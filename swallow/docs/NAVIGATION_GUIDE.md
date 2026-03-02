# Navigation API 使用指南

本文档介绍如何使用 Swallow 框架中的导航相关 API，包括页面跳转、返回、对话框和 SnackBar 等功能。

## 目录

- [基础导航](#基础导航)
- [Activity 启动模式](#activity-启动模式)
- [带参数导航](#带参数导航)
- [返回上一页](#返回上一页)
- [显示对话框](#显示对话框)
- [显示 SnackBar](#显示-snackbar)
- [自定义导航处理](#自定义导航处理)

---

## 基础导航

### 1. 简单页面跳转

在 ViewModel 中调用 `navigate()` 方法：

```kotlin
class MainViewModel : BaseViewModel() {
    
    fun navigateToDetail() {
        navigate("app://detail")
    }
}
```

### 2. 清空返回栈

跳转到新页面并清空之前的页面栈：

```kotlin
fun navigateToLogin() {
    navigate(
        route = "app://login",
        popUpTo = "app://main",
        inclusive = true
    )
}
```

---

## Activity 启动模式

Swallow 框架支持 Android 的四种启动模式，通过 `LaunchMode` 枚举类型指定。

### 1. STANDARD（标准模式）- 默认

每次启动都会创建新的 Activity 实例。

```kotlin
fun navigateToDetail() {
    // 默认就是 STANDARD 模式，可以省略
    navigate("app://detail")
    
    // 或者显式指定
    navigate("app://detail", launchMode = LaunchMode.STANDARD)
}
```

### 2. SINGLE_TOP（栈顶复用模式）

如果目标 Activity 已经在栈顶，则复用该实例，调用 `onNewIntent()`。

```kotlin
fun navigateToHome() {
    navigate(
        route = "app://home",
        launchMode = LaunchMode.SINGLE_TOP
    )
}
```

**使用场景**：
- 搜索页面：避免重复创建搜索页面
- 通知跳转：点击通知跳转到已打开的页面

### 3. SINGLE_TASK（栈内复用模式）

如果目标 Activity 已经在栈中，则复用该实例，并清除其上的所有 Activity。

```kotlin
fun backToMain() {
    navigate(
        route = "app://main",
        launchMode = LaunchMode.SINGLE_TASK
    )
}
```

**使用场景**：
- 返回首页：清除首页之上的所有页面
- 重置导航栈：回到某个关键页面

### 4. SINGLE_INSTANCE（单实例模式）

目标 Activity 会在新的任务栈中创建，且该任务栈中只有这一个 Activity。

```kotlin
fun openCamera() {
    navigate(
        route = "app://camera",
        launchMode = LaunchMode.SINGLE_INSTANCE
    )
}
```

**使用场景**：
- 相机页面：独立的任务栈
- 分享页面：可以被其他应用调用

### 启动模式对比表

| 启动模式 | Intent Flag | 说明 | 典型场景 |
|---------|------------|------|---------|
| STANDARD | 无 | 每次都创建新实例 | 普通页面跳转 |
| SINGLE_TOP | FLAG_ACTIVITY_SINGLE_TOP | 栈顶复用 | 搜索页、通知跳转 |
| SINGLE_TASK | FLAG_ACTIVITY_CLEAR_TOP + SINGLE_TOP | 栈内复用，清除上层 | 返回首页 |
| SINGLE_INSTANCE | FLAG_ACTIVITY_NEW_TASK | 独立任务栈 | 相机、分享 |

---

## 带参数导航

### 1. 传递简单参数

```kotlin
fun navigateToUserDetail(userId: String, userName: String) {
    val args = Bundle().apply {
        putString("userId", userId)
        putString("userName", userName)
    }
    navigate("app://user/detail", args)
}
```

### 2. 传递复杂对象

```kotlin
fun navigateToOrderDetail(order: Order) {
    val args = Bundle().apply {
        putParcelable("order", order)  // Order 需要实现 Parcelable
    }
    navigate("app://order/detail", args)
}
```

### 3. 在目标页面接收参数

```kotlin
class UserDetailActivity : BaseActivity<UserDetailViewModel, ActivityUserDetailBinding>() {
    
    override fun initView() {
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")
        
        mViewModel?.loadUserDetail(userId, userName)
    }
}
```

---

## 返回上一页

### 1. 简单返回

```kotlin
fun goBack() {
    navigateBack()
}
```

### 2. 返回并传递结果

```kotlin
fun saveAndGoBack(data: String) {
    val result = Bundle().apply {
        putString("result", data)
        putBoolean("success", true)
    }
    navigateBack(result)
}
```

### 3. 在调用页面接收返回结果

```kotlin
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringExtra("result")
            val success = result.data?.getBooleanExtra("success", false) ?: false
            // 处理返回结果
        }
    }
    
    override fun initView() {
        binding.btnOpenDetail.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, "app://detail".toUri())
            detailLauncher.launch(intent)
        }
    }
}
```

---

## 显示对话框

### 1. 简单提示对话框

```kotlin
fun showSimpleDialog() {
    showDialog(
        message = "操作成功",
        positiveButton = "确定"
    )
}
```

### 2. 确认对话框

```kotlin
fun showConfirmDialog() {
    showDialog(
        title = "删除确认",
        message = "确定要删除这条记录吗？",
        positiveButton = "删除",
        negativeButton = "取消",
        tag = "delete_confirm"
    )
}
```

### 3. 处理对话框按钮点击

在 Activity/Fragment 中重写回调方法：

```kotlin
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onDialogPositiveClick(tag: String?) {
        when (tag) {
            "delete_confirm" -> {
                mViewModel?.deleteRecord()
            }
        }
    }
    
    override fun onDialogNegativeClick(tag: String?) {
        when (tag) {
            "delete_confirm" -> {
                // 用户取消删除
            }
        }
    }
}
```

---

## 显示 SnackBar

### 1. 简单提示

```kotlin
fun showSimpleSnackBar() {
    showSnackBar("操作成功")
}
```

### 2. 带操作按钮的 SnackBar

```kotlin
fun showUndoSnackBar() {
    showSnackBar(
        message = "已删除",
        actionText = "撤销",
        duration = 1  // 1=LONG, 0=SHORT, -1=INDEFINITE
    )
}
```

### 3. 处理 SnackBar 操作按钮点击

在 Activity/Fragment 中重写回调方法：

```kotlin
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onSnackBarAction(message: String) {
        when (message) {
            "已删除" -> {
                mViewModel?.undoDelete()
            }
        }
    }
}
```

---

## 自定义导航处理

### 1. 自定义导航逻辑

如果你使用的是 Navigation Component 或其他导航框架，可以在 Activity/Fragment 中重写导航处理方法：

```kotlin
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onNavigate(event: UiEvent.Navigate) {
        // 使用 Navigation Component
        val navController = findNavController(R.id.nav_host_fragment)
        
        try {
            val destination = when (event.route) {
                "app://home" -> R.id.homeFragment
                "app://profile" -> R.id.profileFragment
                else -> return super.onNavigate(event)
            }
            
            navController.navigate(destination, event.args)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("导航失败")
        }
    }
}
```

### 2. 自定义对话框样式

```kotlin
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onShowDialog(event: UiEvent.ShowDialog) {
        // 使用 Material Design 对话框
        MaterialAlertDialogBuilder(this)
            .setTitle(event.title)
            .setMessage(event.message)
            .setPositiveButton(event.positiveButton) { dialog, _ ->
                onDialogPositiveClick(event.tag)
                dialog.dismiss()
            }
            .setNegativeButton(event.negativeButton) { dialog, _ ->
                onDialogNegativeClick(event.tag)
                dialog.dismiss()
            }
            .show()
    }
}
```

### 3. 自定义 SnackBar 样式

```kotlin
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onShowSnackBar(event: UiEvent.ShowSnackBar) {
        val snackbar = Snackbar.make(
            binding.root,
            event.message,
            Snackbar.LENGTH_LONG
        )
        
        // 自定义样式
        snackbar.setBackgroundTint(getColor(R.color.primary))
        snackbar.setTextColor(getColor(R.color.white))
        
        event.actionText?.let { actionText ->
            snackbar.setAction(actionText) {
                onSnackBarAction(event.message)
            }
            snackbar.setActionTextColor(getColor(R.color.accent))
        }
        
        snackbar.show()
    }
}
```

---

## 完整示例

### ViewModel 层

```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    fun loadProducts() {
        viewModelScope.launch {
            setState(UiState.Loading("加载中..."))
            
            repository.getProducts()
                .onSuccess { products ->
                    _products.value = products
                    setState(UiState.Success(products))
                }
                .onFailure { error ->
                    setState(UiState.Error(error.message ?: "加载失败"))
                }
        }
    }
    
    fun navigateToProductDetail(productId: String) {
        val args = Bundle().apply {
            putString("productId", productId)
        }
        // 使用 SINGLE_TOP 避免重复创建详情页
        navigate("app://product/detail", args, LaunchMode.SINGLE_TOP)
    }
    
    fun backToHome() {
        // 使用 SINGLE_TASK 返回首页并清除上层页面
        navigate("app://home", launchMode = LaunchMode.SINGLE_TASK)
    }
    
    fun deleteProduct(productId: String) {
        showDialog(
            title = "删除确认",
            message = "确定要删除这个商品吗？",
            positiveButton = "删除",
            negativeButton = "取消",
            tag = "delete_$productId"
        )
    }
    
    fun confirmDelete(productId: String) {
        viewModelScope.launch {
            setState(UiState.Loading("删除中..."))
            
            repository.deleteProduct(productId)
                .onSuccess {
                    setState(UiState.Idle)
                    showSnackBar("已删除", "撤销")
                    loadProducts()  // 重新加载列表
                }
                .onFailure { error ->
                    setState(UiState.Error(error.message ?: "删除失败"))
                }
        }
    }
    
    fun undoDelete(productId: String) {
        viewModelScope.launch {
            repository.restoreProduct(productId)
                .onSuccess {
                    showSnackBar("已恢复")
                    loadProducts()
                }
                .onFailure { error ->
                    showSnackBar("恢复失败: ${error.message}")
                }
        }
    }
}
```

### Activity 层

```kotlin
@AndroidEntryPoint
class ProductListActivity : BaseActivity<ProductViewModel, ActivityProductListBinding>() {
    
    override val viewModel: ProductViewModel by viewModels()
    
    override val bindingInflater: (LayoutInflater) -> ActivityProductListBinding
        get() = ActivityProductListBinding::inflate
    
    private val adapter = ProductAdapter { product ->
        mViewModel?.navigateToProductDetail(product.id)
    }
    
    override fun initView() {
        binding.recyclerView.adapter = adapter
        
        binding.btnAdd.setOnClickListener {
            mViewModel?.navigate("app://product/add")
        }
        
        // 加载数据
        mViewModel?.loadProducts()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 观察商品列表
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel?.products?.collect { products ->
                    adapter.submitList(products)
                }
            }
        }
    }
    
    override fun onDialogPositiveClick(tag: String?) {
        // 处理删除确认
        if (tag?.startsWith("delete_") == true) {
            val productId = tag.removePrefix("delete_")
            mViewModel?.confirmDelete(productId)
        }
    }
    
    override fun onSnackBarAction(message: String) {
        // 处理撤销删除
        if (message == "已删除") {
            // 这里需要保存被删除的商品 ID
            // 实际项目中可以在 ViewModel 中维护一个临时变量
            // mViewModel?.undoDelete(lastDeletedProductId)
        }
    }
}
```

---

## 最佳实践

### 1. 路由命名规范

建议使用统一的路由命名规范：

```kotlin
object Routes {
    const val HOME = "app://home"
    const val PRODUCT_LIST = "app://product/list"
    const val PRODUCT_DETAIL = "app://product/detail"
    const val USER_PROFILE = "app://user/profile"
    const val SETTINGS = "app://settings"
}

// 使用
mViewModel?.navigate(Routes.PRODUCT_DETAIL, args)
```

### 2. 参数封装

对于复杂的参数传递，建议封装成扩展函数：

```kotlin
object NavigationArgs {
    fun createProductDetailArgs(productId: String, source: String): Bundle {
        return Bundle().apply {
            putString("productId", productId)
            putString("source", source)
        }
    }
    
    fun Bundle.getProductId(): String? = getString("productId")
    fun Bundle.getSource(): String? = getString("source")
}

// 使用
mViewModel?.navigate(
    Routes.PRODUCT_DETAIL,
    NavigationArgs.createProductDetailArgs(productId, "list")
)
```

### 3. 对话框标识管理

使用常量管理对话框标识：

```kotlin
object DialogTags {
    const val DELETE_CONFIRM = "delete_confirm"
    const val LOGOUT_CONFIRM = "logout_confirm"
    const val NETWORK_ERROR = "network_error"
}

// 使用
mViewModel?.showDialog(
    message = "确定要退出登录吗？",
    positiveButton = "退出",
    negativeButton = "取消",
    tag = DialogTags.LOGOUT_CONFIRM
)
```

### 4. 避免内存泄漏

在 ViewModel 中不要持有 Context、Activity、Fragment 等引用，所有 UI 操作都通过 UiEvent 发送：

```kotlin
// ❌ 错误示例
class MyViewModel(private val context: Context) : BaseViewModel() {
    fun showMessage() {
        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
    }
}

// ✅ 正确示例
class MyViewModel : BaseViewModel() {
    fun showMessage() {
        showSnackBar("Hello")
    }
}
```

---

## 注意事项

1. **Deep Link 配置**：使用 `navigate()` 方法需要在 AndroidManifest.xml 中配置对应的 Deep Link

2. **线程安全**：所有导航方法都是线程安全的，可以在任何线程调用

3. **生命周期**：UiEvent 只在 Activity/Fragment 处于 STARTED 状态时才会被处理

4. **事件消费**：UiEvent 是一次性事件，不会重复触发（使用 SharedFlow 实现）

5. **自定义处理**：如果默认实现不满足需求，可以在 Activity/Fragment 中重写对应的处理方法

---

## 相关文档

- [UI State 和 Event 使用指南](UI_STATE_EVENT_GUIDE.md)
- [BaseViewModel API 文档](../src/main/java/com/swallow/fly/base/presentation/BaseViewModel.kt)
- [UiEvent API 文档](../src/main/java/com/swallow/fly/base/presentation/state/UiEvent.kt)
