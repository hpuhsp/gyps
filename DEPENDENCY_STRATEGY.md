# 依赖配置策略文档

本文档说明 Gyps 框架各模块的依赖配置策略，确保合理使用 `api` 和 `implementation`。

## 模块依赖关系

```
app → base → swallow
app → msc
```

## Swallow 模块（核心框架库）

### 使用 `api` 的依赖（暴露给 base/app）

| 依赖 | 原因 | 使用场景 |
|------|------|----------|
| **androidx.core** | BaseActivity/BaseFragment 基础 | Activity、Fragment、ViewBinding |
| **lifecycle** | MVVM 核心组件 | ViewModel、LiveData、LifecycleOwner |
| **navigation** | 导航组件 | Fragment 导航、深链接 |
| **paging** | 分页功能 | PagingData、PagingSource |
| **coroutines** | 协程和 Flow | 异步操作、状态管理 |
| **hilt** | 依赖注入 | @Inject、@HiltViewModel、@AndroidEntryPoint |

**为什么暴露？**
- base/app 模块需要继承 BaseActivity、BaseViewModel
- 需要使用 Flow、StateFlow、LiveData 等类型
- 需要使用 @HiltViewModel、@Inject 等注解

### 使用 `implementation` 的依赖（内部实现）

| 依赖 | 原因 | 封装方式 |
|------|------|----------|
| **retrofit + okhttp** | 网络层实现 | 通过 BaseRepository 封装 |
| **glide** | 图片加载实现 | 通过 ImageLoader 工具类封装 |
| **room** | 数据库实现 | 通过 Repository 层封装 |
| **eventbus** | 事件总线 | 通过 BaseViewModel/BaseActivity 封装 |
| **therouter** | 路由实现 | 通过路由工具类封装 |
| **timber** | 日志工具 | 通过 Log 工具类封装 |
| **mmkv** | KV 存储 | 通过 Prefs 工具类封装 |
| **immersionbar** | 状态栏工具 | 通过 BaseActivity 封装 |
| **easypermissions** | 权限工具 | 通过 BaseActivity/BaseFragment 封装 |
| **utilcodex** | Android 工具 | 内部使用 |
| **multidex** | 分包配置 | 内部配置 |
| **legacy support** | 兼容性支持 | 内部使用 |

**为什么隐藏？**
- 这些是实现细节，不应该暴露给上层
- 强制 app 模块通过封装的 API 使用
- 避免版本冲突和依赖爆炸
- 提高编译速度

## Base 模块（UI 资源库）

### 使用 `api` 的依赖（暴露给 app）

| 依赖 | 原因 | 使用场景 |
|------|------|----------|
| **swallow** | 核心框架 | app 需要使用 BaseActivity、BaseViewModel 等 |
| **pictureselector** | 图片选择器 | app 直接调用图片选择功能 |
| **baserecyclerviewadapterhelper** | RecyclerView Adapter | app 继承 BaseVBAdapter |
| **recyclerview-flexibledivider** | 分割线 | app 直接使用分割线 |
| **flyco-tablayout** | TabLayout | app 直接使用 Tab 组件 |
| **material-dialogs** | Material 对话框 | app 直接使用对话框 |
| **本地 JAR** | 第三方库 | app 可能需要使用 |

**为什么暴露？**
- 这些是 UI 组件，app 需要直接使用
- app 会继承 base 提供的 Adapter 基类
- app 会直接调用图片选择、对话框等功能

### 使用 `implementation` 的依赖（内部实现）

| 依赖 | 原因 |
|------|------|
| **legacy support** | 内部兼容性支持，app 不需要直接访问 |
| **vectordrawable** | 矢量图支持，内部使用 |
| **swiperefreshlayout** | 下拉刷新，通过封装使用 |
| **viewpager** | ViewPager，通过封装使用 |

**为什么隐藏？**
- 这些是 base 内部使用的兼容性库
- app 不需要直接访问

## App 模块可见的依赖

通过上述配置，app 模块可以访问：

### 直接可用的类型和注解
```kotlin
// 来自 swallow 的 api 依赖
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.paging.PagingData

// 来自 swallow 的 public 类
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.data.BaseRepository

// 来自 base 的 UI 组件
import com.hsp.resource.adapter.BaseVBAdapter
import com.luck.picture.lib.PictureSelector
import com.flyco.tablayout.CommonTabLayout
```

### 不可直接访问（需要通过封装）
```kotlin
// ❌ 编译错误 - 这些是 implementation 依赖
import retrofit2.Retrofit
import com.bumptech.glide.Glide
import androidx.room.Room
import org.greenrobot.eventbus.EventBus
import com.jakewharton.timber.log.Timber

// ✅ 正确做法 - 通过封装使用
class MyViewModel @Inject constructor(
    private val repository: MyRepository  // 通过 Repository 访问网络
) : BaseViewModel() {
    fun loadData() = repository.getData()  // Repository 内部使用 Retrofit
}

class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    fun loadImage(url: String) {
        ImageLoader.load(this, url, binding.imageView)  // 通过工具类使用 Glide
    }
}
```

## 配置优势

### 1. 架构清晰
- 强制分层：UI → ViewModel → Repository → Network
- app 无法绕过架构直接访问底层实现

### 2. 编译效率
- swallow 内部改动不触发 base/app 重新编译
- 依赖变更影响范围小

### 3. 版本管理
- 减少版本冲突
- swallow 可以自由升级内部依赖

### 4. Maven 发布友好
```xml
<!-- swallow 发布到 Maven 后的 POM -->
<dependencies>
    <!-- compile scope - 必须传递 -->
    <dependency>
        <groupId>androidx.lifecycle</groupId>
        <artifactId>lifecycle-viewmodel-ktx</artifactId>
        <scope>compile</scope>
    </dependency>
    
    <!-- runtime scope - 运行时提供 -->
    <dependency>
        <groupId>com.squareup.retrofit2</groupId>
        <artifactId>retrofit</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 5. 代码质量
- 新成员无法写出违反架构的代码
- 编译器强制使用正确的 API

## 依赖更新指南

### 何时使用 `api`？
1. 依赖的类型出现在 public API 中（方法参数、返回值、泛型参数）
2. 依赖的注解需要被上层模块使用
3. 依赖是框架的核心组件（如 Lifecycle、Coroutines）

### 何时使用 `implementation`？
1. 依赖只在内部实现中使用
2. 依赖通过工具类或封装暴露功能
3. 依赖是具体实现（如 Retrofit、Glide、Room）

### 判断标准
问自己：**"如果移除这个依赖，上层模块的代码会编译失败吗？"**
- 是 → 使用 `api`
- 否 → 使用 `implementation`

## 示例场景

### 场景 1：添加新的网络库
```kotlin
// swallow/build.gradle.kts
implementation("com.squareup.moshi:moshi:1.15.0")  // ✅ implementation

// 原因：通过 Repository 封装，app 不直接使用
```

### 场景 2：添加新的 UI 组件库
```kotlin
// base/build.gradle.kts
api("com.github.bumptech.glide:glide:5.0.5")  // ❌ 错误

implementation("com.github.bumptech.glide:glide:5.0.5")  // ✅ 正确
// 应该在 swallow 中添加，并通过工具类封装
```

### 场景 3：添加新的 Jetpack 组件
```kotlin
// swallow/build.gradle.kts
api("androidx.datastore:datastore-preferences:1.0.0")  // ✅ api

// 原因：app 需要使用 DataStore 的 Flow API
```

## 验证配置

### 编译测试
```bash
# 清理构建
./gradlew clean

# 构建所有模块
./gradlew build

# 检查依赖树
./gradlew :app:dependencies
./gradlew :base:dependencies
./gradlew :swallow:dependencies
```

### 代码测试
在 app 模块中尝试导入 implementation 依赖，应该编译失败：
```kotlin
// app 模块
import retrofit2.Retrofit  // ❌ 应该编译失败
import com.bumptech.glide.Glide  // ❌ 应该编译失败
```

## 总结

当前配置遵循以下原则：
1. **最小暴露原则**：只暴露必要的 API
2. **封装原则**：实现细节通过封装暴露
3. **分层原则**：强制遵循架构分层
4. **性能原则**：优化编译速度
5. **维护原则**：便于版本升级和依赖管理

这种配置方式是 Android 官方推荐的最佳实践，适合发布到 Maven 的库项目。
