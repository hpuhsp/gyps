# UiState 和 UiEvent 使用指南

## 概述

`UiState` 和 `UiEvent` 是 Swallow 框架中用于管理 UI 状态和事件的核心机制：

- **UiState**: 使用 `StateFlow` 管理持久化的 UI 状态（如加载中、成功、失败）
- **UiEvent**: 使用 `SharedFlow` 管理一次性事件（如 Toast 提示、导航跳转）

## 协程作用域说明

在 ViewModel 中启动协程时，应该使用 `viewModelScope.launch`：

```kotlin
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ViewModel 中启动协程
viewModelScope.launch {
    // 协程代码
}
```

**为什么使用 viewModelScope？**
- 自动绑定 ViewModel 生命周期
- ViewModel 销毁时自动取消协程，避免内存泄漏
- 默认在主线程（Main Dispatcher）执行

## 核心概念

### UiState - 状态管理

```kotlin
sealed class UiState {
    object Init : UiState()                          // 初始状态
    object Idle : UiState()                          // 空闲状态
    data class Loading(val message: String?) : UiState()  // 加载中
    data class Success<T>(val data: T) : UiState()   // 成功
    data class Error(val message: String) : UiState() // 错误
}
```

**特点**：
- StateFlow 会保留最新状态
- 自动去重（相同状态不会重复发射）
- 适合表示页面的整体状态

### UiEvent - 事件管理

```kotlin
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}
```

**特点**：
- SharedFlow 不保留历史事件
- 每次发射都会被消费
- 适合一次性操作（如 Toast、导航）

## 场景一：单个网络请求

### ViewModel

```kotlin
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : BaseViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            // 显示加载状态
            showLoading("加载用户信息...")
            
            repository.getUserProfile(userId).collect { result ->
                result.onSuccess { user ->
                    _userProfile.value = user
                    hideLoading()
                }.onFailure { error ->
                    showError(error?.message ?: "加载失败")
                }
            }
        }
    }
}
```

### Activity/Fragment

```kotlin
@AndroidEntryPoint
class UserProfileActivity : BaseActivity<UserProfileViewModel, ActivityUserProfileBinding>() {

    override val modelClass: Class<UserProfileViewModel> = UserProfileViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityUserProfileBinding
        get() = ActivityUserProfileBinding::inflate

    override fun initView() {
        // 加载数据
        mViewModel?.loadUserProfile("user123")
        
        // 观察用户信息
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel?.userProfile?.collect { user ->
                    user?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(user: User) {
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
    }
}
```

## 场景二：多个独立的网络请求

当页面有多个独立的网络请求时，为每个请求创建独立的 StateFlow。

### ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    // 用户信息
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    // 新闻列表
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    // 天气信息
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    // 加载状态（可选：为每个请求单独管理加载状态）
    private val _userLoading = MutableStateFlow(false)
    val userLoading: StateFlow<Boolean> = _userLoading.asStateFlow()

    private val _newsLoading = MutableStateFlow(false)
    val newsLoading: StateFlow<Boolean> = _newsLoading.asStateFlow()

    private val _weatherLoading = MutableStateFlow(false)
    val weatherLoading: StateFlow<Boolean> = _weatherLoading.asStateFlow()

    /**
     * 加载所有数据
     */
    fun loadAllData() {
        loadUserInfo()
        loadNews()
        loadWeather()
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        viewModelScope.launch {
            _userLoading.value = true
            
            userRepository.getUserInfo().collect { result ->
                _userLoading.value = false
                
                result.onSuccess { user ->
                    _userInfo.value = user
                }.onFailure { error ->
                    // 只显示错误提示，不影响其他请求
                    showToast("用户信息加载失败")
                }
            }
        }
    }

    /**
     * 加载新闻列表
     */
    fun loadNews() {
        viewModelScope.launch {
            _newsLoading.value = true
            
            newsRepository.getNewsList().collect { result ->
                _newsLoading.value = false
                
                result.onSuccess { news ->
                    _newsList.value = news
                }.onFailure { error ->
                    showToast("新闻加载失败")
                }
            }
        }
    }

    /**
     * 加载天气信息
     */
    fun loadWeather() {
        viewModelScope.launch {
            _weatherLoading.value = true
            
            weatherRepository.getWeather().collect { result ->
                _weatherLoading.value = false
                
                result.onSuccess { weather ->
                    _weather.value = weather
                }.onFailure { error ->
                    showToast("天气信息加载失败")
                }
            }
        }
    }

    /**
     * 刷新所有数据
     */
    fun refreshAll() {
        showLoading("刷新中...")
        
        viewModelScope.launch {
            // 并发执行所有请求
            val userDeferred = async { userRepository.getUserInfo().first() }
            val newsDeferred = async { newsRepository.getNewsList().first() }
            val weatherDeferred = async { weatherRepository.getWeather().first() }

            // 等待所有请求完成
            val userResult = userDeferred.await()
            val newsResult = newsDeferred.await()
            val weatherResult = weatherDeferred.await()

            hideLoading()

            // 处理结果
            userResult.onSuccess { _userInfo.value = it }
            newsResult.onSuccess { _newsList.value = it }
            weatherResult.onSuccess { _weather.value = it }

            // 显示刷新完成提示
            showToast("刷新完成")
        }
    }
}
```

### Activity/Fragment

```kotlin
@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override val modelClass: Class<HomeViewModel> = HomeViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    override fun initView() {
        setupRefresh()
        observeData()
        
        // 加载所有数据
        mViewModel?.loadAllData()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mViewModel?.refreshAll()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察用户信息
                launch {
                    mViewModel?.userInfo?.collect { user ->
                        user?.let { updateUserUI(it) }
                    }
                }

                // 观察新闻列表
                launch {
                    mViewModel?.newsList?.collect { news ->
                        updateNewsList(news)
                    }
                }

                // 观察天气信息
                launch {
                    mViewModel?.weather?.collect { weather ->
                        weather?.let { updateWeatherUI(it) }
                    }
                }

                // 观察加载状态
                launch {
                    mViewModel?.userLoading?.collect { loading ->
                        binding.userLoadingIndicator.isVisible = loading
                    }
                }

                launch {
                    mViewModel?.newsLoading?.collect { loading ->
                        binding.newsLoadingIndicator.isVisible = loading
                    }
                }

                launch {
                    mViewModel?.weatherLoading?.collect { loading ->
                        binding.weatherLoadingIndicator.isVisible = loading
                    }
                }
            }
        }
    }

    private fun updateUserUI(user: User) {
        binding.tvUserName.text = user.name
        binding.ivAvatar.load(user.avatar)
    }

    private fun updateNewsList(news: List<News>) {
        newsAdapter.submitList(news)
    }

    private fun updateWeatherUI(weather: Weather) {
        binding.tvTemperature.text = "${weather.temperature}°C"
        binding.tvWeatherDesc.text = weather.description
    }

    override fun handleUiState(state: UiState) {
        when (state) {
            is UiState.Loading -> {
                // 全局加载状态（如刷新所有数据时）
                binding.swipeRefresh.isRefreshing = true
            }
            is UiState.Idle -> {
                binding.swipeRefresh.isRefreshing = false
            }
            else -> super.handleUiState(state)
        }
    }
}
```

## 场景三：有依赖关系的多个请求

当请求之间有依赖关系时（如先获取用户信息，再根据用户 ID 获取订单列表）。

### ViewModel

```kotlin
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    /**
     * 加载用户信息和订单（有依赖关系）
     */
    fun loadUserAndOrders() {
        viewModelScope.launch {
            showLoading("加载中...")

            // 第一步：获取用户信息
            userRepository.getUserInfo().collect { userResult ->
                userResult.onSuccess { user ->
                    _userInfo.value = user

                    // 第二步：根据用户 ID 获取订单
                    orderRepository.getOrders(user.id).collect { orderResult ->
                        hideLoading()

                        orderResult.onSuccess { orders ->
                            _orders.value = orders
                        }.onFailure { error ->
                            showError("订单加载失败: ${error?.message}")
                        }
                    }
                }.onFailure { error ->
                    hideLoading()
                    showError("用户信息加载失败: ${error?.message}")
                }
            }
        }
    }
}
```

## 场景四：分页加载

### ViewModel

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private var currentPage = 1
    private var isLoadingMore = false
    private var hasMore = true

    /**
     * 加载第一页
     */
    fun loadFirstPage() {
        currentPage = 1
        hasMore = true
        
        viewModelScope.launch {
            showLoading("加载中...")

            repository.getProducts(currentPage).collect { result ->
                hideLoading()

                result.onSuccess { response ->
                    _products.value = response.data
                    hasMore = response.hasMore
                    currentPage++
                }.onFailure { error ->
                    showError(error?.message ?: "加载失败")
                }
            }
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (isLoadingMore || !hasMore) return

        isLoadingMore = true

        viewModelScope.launch {
            // 不显示全局 loading，使用列表底部的加载指示器
            repository.getProducts(currentPage).collect { result ->
                isLoadingMore = false

                result.onSuccess { response ->
                    // 追加数据
                    _products.value = _products.value + response.data
                    hasMore = response.hasMore
                    currentPage++
                }.onFailure { error ->
                    showToast("加载更多失败")
                }
            }
        }
    }

    /**
     * 刷新
     */
    fun refresh() {
        loadFirstPage()
    }
}
```

### Activity/Fragment

```kotlin
@AndroidEntryPoint
class ProductListActivity : BaseActivity<ProductListViewModel, ActivityProductListBinding>() {

    override val modelClass: Class<ProductListViewModel> = ProductListViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityProductListBinding
        get() = ActivityProductListBinding::inflate

    private val adapter by lazy { ProductAdapter() }

    override fun initView() {
        setupRecyclerView()
        setupRefresh()
        observeData()

        // 加载第一页
        mViewModel?.loadFirstPage()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = this@ProductListActivity.adapter

            // 监听滚动，实现加载更多
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    // 滚动到倒数第 3 个时触发加载更多
                    if (lastVisiblePosition >= totalItemCount - 3) {
                        mViewModel?.loadMore()
                    }
                }
            })
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mViewModel?.refresh()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel?.products?.collect { products ->
                    adapter.submitList(products)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }
}
```

## 场景五：使用 UiEvent 处理一次性事件

### ViewModel

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel() {

    fun login(username: String, password: String) {
        // 验证输入
        if (username.isBlank()) {
            showToast("请输入用户名")
            return
        }

        if (password.isBlank()) {
            showToast("请输入密码")
            return
        }

        viewModelScope.launch {
            showLoading("登录中...")

            repository.login(username, password).collect { result ->
                hideLoading()

                result.onSuccess { user ->
                    // 保存用户信息
                    saveUserInfo(user)

                    // 发送导航事件
                    sendEvent(UiEvent.Navigate("/home"))

                    // 显示成功提示
                    showToast("登录成功")
                }.onFailure { error ->
                    showError(error?.message ?: "登录失败")
                }
            }
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
```

### Activity/Fragment

```kotlin
@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val modelClass: Class<LoginViewModel> = LoginViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun initView() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            mViewModel?.login(username, password)
        }
    }

    override fun handleUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Navigate -> {
                // 导航到主页
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else -> super.handleUiEvent(event)
        }
    }
}
```

## 最佳实践

### 1. 何时使用 UiState

- 页面的整体加载状态
- 需要在配置变更后保留的状态
- 需要去重的状态更新

### 2. 何时使用 UiEvent

- Toast 提示
- Snackbar 提示
- 导航跳转
- 对话框显示
- 一次性操作

### 3. 何时使用独立的 StateFlow

- 多个独立的数据源
- 需要单独控制加载状态
- 数据更新频率不同

### 4. 性能优化建议

```kotlin
// ✅ 推荐：使用 distinctUntilChanged 避免重复更新
lifecycleScope.launch {
    mViewModel?.products
        ?.distinctUntilChanged()
        ?.collect { products ->
            updateUI(products)
        }
}

// ✅ 推荐：使用 debounce 处理高频更新
lifecycleScope.launch {
    mViewModel?.searchQuery
        ?.debounce(300)
        ?.collect { query ->
            performSearch(query)
        }
}

// ✅ 推荐：在正确的生命周期收集
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        // 只在 STARTED 状态收集，避免内存泄漏
        mViewModel?.uiState?.collect { state ->
            handleState(state)
        }
    }
}
```

## 常见问题

### Q1: 多个请求都需要显示 loading，如何处理？

**方案 1**：为每个请求创建独立的 loading 状态
```kotlin
private val _userLoading = MutableStateFlow(false)
private val _newsLoading = MutableStateFlow(false)
```

**方案 2**：使用计数器管理全局 loading
```kotlin
private var loadingCount = 0

private fun showLoadingInternal() {
    loadingCount++
    if (loadingCount == 1) {
        showLoading()
    }
}

private fun hideLoadingInternal() {
    loadingCount--
    if (loadingCount == 0) {
        hideLoading()
    }
}
```

### Q2: 如何处理请求失败但不影响其他请求？

使用 `showToast()` 而不是 `showError()`，避免改变全局 UiState：

```kotlin
result.onFailure { error ->
    showToast("加载失败")  // 只显示提示，不改变状态
}
```

### Q3: 如何避免重复请求？

```kotlin
private var isLoading = false

fun loadData() {
    if (isLoading) return
    
    isLoading = true
    viewModelScope.launch {
        // 执行请求
        isLoading = false
    }
}
```

## 总结

- **UiState** 用于管理页面整体状态，适合持久化状态
- **UiEvent** 用于一次性事件，适合 Toast、导航等操作
- **独立 StateFlow** 用于多个独立数据源，提供更精细的控制
- **viewModelScope** 是 ViewModel 中启动协程的标准方式，自动管理生命周期
- 根据业务场景选择合适的方案，保持代码清晰和可维护性

## 附录：协程作用域对比

### viewModelScope vs lifecycleScope

| 特性 | viewModelScope | lifecycleScope |
|------|----------------|----------------|
| 使用位置 | ViewModel 中 | Activity/Fragment 中 |
| 生命周期 | 绑定 ViewModel | 绑定 Lifecycle |
| 取消时机 | ViewModel.onCleared() | Lifecycle.ON_DESTROY |
| 默认调度器 | Dispatchers.Main.immediate | Dispatchers.Main.immediate |
| 配置变更 | 不受影响（ViewModel 存活） | 重新创建（Activity/Fragment 重建） |

### 为什么在 ViewModel 中使用 viewModelScope？

```kotlin
// ✅ 推荐：使用 viewModelScope
class MyViewModel : BaseViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // 协程代码
            // ViewModel 销毁时自动取消
        }
    }
}

// ❌ 不推荐：使用 GlobalScope
class MyViewModel : BaseViewModel() {
    fun loadData() {
        GlobalScope.launch {
            // 协程代码
            // 永远不会自动取消，可能导致内存泄漏
        }
    }
}

// ❌ 不推荐：手动创建 CoroutineScope
class MyViewModel : BaseViewModel() {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun loadData() {
        scope.launch {
            // 协程代码
            // 需要手动取消，容易遗漏
        }
    }
    
    override fun onCleared() {
        scope.cancel()  // 容易忘记
    }
}
```

### 切换调度器

如果需要在 IO 线程执行，使用 `withContext`：

```kotlin
viewModelScope.launch {
    // 主线程
    showLoading()
    
    // 切换到 IO 线程
    val result = withContext(Dispatchers.IO) {
        // IO 操作
        repository.loadData()
    }
    
    // 自动切回主线程
    hideLoading()
    updateUI(result)
}
```

或者在 Repository 层使用 `flowOn`：

```kotlin
// Repository
fun loadData(): Flow<Result> = flow {
    // IO 操作
    emit(fetchData())
}.flowOn(Dispatchers.IO)  // 在 IO 线程执行

// ViewModel
viewModelScope.launch {
    // 主线程收集
    repository.loadData().collect { result ->
        // 主线程处理结果
        updateUI(result)
    }
}
```
