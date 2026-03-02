# Activity 启动模式详解

本文档详细介绍 Swallow 框架中 Activity 启动模式的使用方法、原理和最佳实践。

## 目录

- [启动模式概述](#启动模式概述)
- [STANDARD - 标准模式](#standard---标准模式)
- [SINGLE_TOP - 栈顶复用模式](#single_top---栈顶复用模式)
- [SINGLE_TASK - 栈内复用模式](#single_task---栈内复用模式)
- [SINGLE_INSTANCE - 单实例模式](#single_instance---单实例模式)
- [启动模式对比](#启动模式对比)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

---

## 启动模式概述

Android 提供了四种 Activity 启动模式，用于控制 Activity 的创建和复用行为。Swallow 框架通过 `LaunchMode` 枚举类型封装了这四种模式，并提供了简洁的 API。

```kotlin
enum class LaunchMode {
    STANDARD,       // 标准模式
    SINGLE_TOP,     // 栈顶复用模式
    SINGLE_TASK,    // 栈内复用模式
    SINGLE_INSTANCE // 单实例模式
}
```

---

## STANDARD - 标准模式

### 特点

- 每次启动都会创建新的 Activity 实例
- 新实例会被添加到当前任务栈的栈顶
- 这是默认的启动模式

### 任务栈变化

```
启动前: [A, B, C]
启动 D: [A, B, C, D]
再次启动 D: [A, B, C, D, D]  // 创建新实例
```

### 使用场景

- 普通页面跳转
- 详情页面（每次查看不同内容）
- 表单页面（每次填写新表单）

### 代码示例

```kotlin
class ProductViewModel : BaseViewModel() {
    
    // 默认就是 STANDARD 模式
    fun navigateToDetail(productId: String) {
        val args = Bundle().apply {
            putString("productId", productId)
        }
        navigate("app://product/detail", args)
    }
    
    // 显式指定 STANDARD 模式
    fun navigateToDetailExplicit(productId: String) {
        navigate(
            route = "app://product/detail",
            args = Bundle().apply { putString("productId", productId) },
            launchMode = LaunchMode.STANDARD
        )
    }
}
```

### 注意事项

- 可能导致同一个 Activity 的多个实例存在
- 需要注意内存占用
- 适合大多数场景

---

## SINGLE_TOP - 栈顶复用模式

### 特点

- 如果目标 Activity 已经在栈顶，则复用该实例
- 复用时会调用 `onNewIntent()` 方法，而不是 `onCreate()`
- 如果目标 Activity 不在栈顶，则创建新实例

### 任务栈变化

```
启动前: [A, B, C]
启动 C (SINGLE_TOP): [A, B, C]  // 复用栈顶的 C，调用 onNewIntent()
启动 D (SINGLE_TOP): [A, B, C, D]  // C 不在栈顶，创建新的 D
```

### 使用场景

- 搜索页面：避免重复创建搜索页面
- 通知跳转：点击通知跳转到已打开的页面
- 扫码页面：避免重复打开扫码界面
- 消息列表：从通知栏打开消息列表

### 代码示例

```kotlin
class SearchViewModel : BaseViewModel() {
    
    fun navigateToSearch(keyword: String) {
        val args = Bundle().apply {
            putString("keyword", keyword)
        }
        navigate(
            route = "app://search",
            args = args,
            launchMode = LaunchMode.SINGLE_TOP
        )
    }
}
```

### Activity 中处理 onNewIntent

```kotlin
@AndroidEntryPoint
class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)  // 更新 intent
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val keyword = intent.getStringExtra("keyword")
        mViewModel?.search(keyword)
    }
}
```

### 注意事项

- 必须在 Activity 中重写 `onNewIntent()` 方法
- 需要调用 `setIntent(intent)` 更新 intent
- 只有在栈顶时才会复用

---

## SINGLE_TASK - 栈内复用模式

### 特点

- 如果目标 Activity 已经在栈中，则复用该实例
- 复用时会清除该 Activity 之上的所有 Activity
- 会调用 `onNewIntent()` 方法
- 如果目标 Activity 不在栈中，则创建新实例

### 任务栈变化

```
启动前: [A, B, C, D]
启动 B (SINGLE_TASK): [A, B]  // 复用 B，清除 C 和 D
启动 E (SINGLE_TASK): [A, B, E]  // E 不在栈中，创建新实例
```

### 使用场景

- 返回首页：清除首页之上的所有页面
- 重置导航栈：回到某个关键页面
- 登录页面：登录后清除之前的页面
- 主界面：从任何页面返回主界面

### 代码示例

```kotlin
class MainViewModel : BaseViewModel() {
    
    // 返回首页，清除所有中间页面
    fun backToHome() {
        navigate(
            route = "app://home",
            launchMode = LaunchMode.SINGLE_TASK
        )
    }
    
    // 登录成功后跳转到主页，清除登录流程的所有页面
    fun navigateToMainAfterLogin() {
        navigate(
            route = "app://main",
            launchMode = LaunchMode.SINGLE_TASK
        )
    }
}
```

### Activity 中处理 onNewIntent

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // 处理从其他页面返回的情况
        val fromPage = intent.getStringExtra("from")
        when (fromPage) {
            "login" -> {
                // 登录成功返回，刷新用户信息
                mViewModel?.refreshUserInfo()
            }
            "settings" -> {
                // 从设置返回，可能需要刷新配置
                mViewModel?.refreshSettings()
            }
        }
    }
}
```

### 注意事项

- 会清除目标 Activity 之上的所有 Activity
- 可能导致用户数据丢失（如未保存的表单）
- 适合用于"返回关键页面"的场景
- 需要在 Activity 中处理 `onNewIntent()`

---

## SINGLE_INSTANCE - 单实例模式

### 特点

- 目标 Activity 会在新的任务栈中创建
- 该任务栈中只有这一个 Activity
- 系统中只会存在一个该 Activity 的实例
- 其他 Activity 不能与它共存于同一个任务栈

### 任务栈变化

```
任务栈 1: [A, B, C]
启动 D (SINGLE_INSTANCE):
  任务栈 1: [A, B, C]
  任务栈 2: [D]  // D 在独立的任务栈中

从 D 启动 E:
  任务栈 1: [A, B, C, E]  // E 回到原任务栈
  任务栈 2: [D]  // D 仍然独立
```

### 使用场景

- 相机页面：独立的拍照界面
- 分享页面：可以被其他应用调用
- 视频播放器：独立的播放界面
- 地图导航：独立的导航界面

### 代码示例

```kotlin
class CameraViewModel : BaseViewModel() {
    
    fun openCamera() {
        navigate(
            route = "app://camera",
            launchMode = LaunchMode.SINGLE_INSTANCE
        )
    }
}
```

### Activity 配置

使用 SINGLE_INSTANCE 模式时，建议在 AndroidManifest.xml 中也配置：

```xml
<activity
    android:name=".camera.CameraActivity"
    android:launchMode="singleInstance"
    android:taskAffinity="com.example.camera" />
```

### 注意事项

- 会创建独立的任务栈
- 从该 Activity 启动其他 Activity 时，新 Activity 会回到原任务栈
- 按返回键时，可能会跳转到其他应用
- 适合需要独立运行的功能模块
- 使用较少，需谨慎使用

---

## 启动模式对比

### 对比表

| 特性 | STANDARD | SINGLE_TOP | SINGLE_TASK | SINGLE_INSTANCE |
|-----|----------|------------|-------------|-----------------|
| 创建新实例 | 总是 | 不在栈顶时 | 不在栈中时 | 不存在时 |
| 复用实例 | 不复用 | 栈顶复用 | 栈内复用 | 全局复用 |
| 清除上层 Activity | 否 | 否 | 是 | 否 |
| 独立任务栈 | 否 | 否 | 否 | 是 |
| 调用 onNewIntent | 否 | 是 | 是 | 是 |
| Intent Flag | 无 | SINGLE_TOP | CLEAR_TOP + SINGLE_TOP | NEW_TASK |
| 使用频率 | 最高 | 高 | 中 | 低 |

### 生命周期对比

#### STANDARD 模式
```
启动新实例: onCreate() -> onStart() -> onResume()
```

#### SINGLE_TOP / SINGLE_TASK / SINGLE_INSTANCE 模式（复用时）
```
复用实例: onPause() -> onNewIntent() -> onResume()
```

---

## 最佳实践

### 1. 选择合适的启动模式

```kotlin
class NavigationViewModel : BaseViewModel() {
    
    // ✅ 普通页面使用 STANDARD（默认）
    fun navigateToDetail() {
        navigate("app://detail")
    }
    
    // ✅ 搜索页面使用 SINGLE_TOP
    fun navigateToSearch() {
        navigate("app://search", launchMode = LaunchMode.SINGLE_TOP)
    }
    
    // ✅ 返回首页使用 SINGLE_TASK
    fun backToHome() {
        navigate("app://home", launchMode = LaunchMode.SINGLE_TASK)
    }
    
    // ✅ 相机页面使用 SINGLE_INSTANCE
    fun openCamera() {
        navigate("app://camera", launchMode = LaunchMode.SINGLE_INSTANCE)
    }
}
```

### 2. 正确处理 onNewIntent

```kotlin
@AndroidEntryPoint
class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)  // ⚠️ 重要：更新 intent
        processIntent(intent)
    }
    
    private fun processIntent(intent: Intent) {
        // 统一处理 intent 数据
        val data = intent.getStringExtra("data")
        mViewModel?.loadData(data)
    }
}
```

### 3. 结合 popUpTo 使用

```kotlin
class LoginViewModel : BaseViewModel() {
    
    // 登录成功后跳转到主页，清除登录流程
    fun navigateToMainAfterLogin() {
        navigate(
            route = "app://main",
            launchMode = LaunchMode.SINGLE_TASK,
            popUpTo = "app://login",
            inclusive = true
        )
    }
}
```

### 4. 避免过度使用 SINGLE_TASK

```kotlin
// ❌ 错误：不要对所有页面都使用 SINGLE_TASK
fun navigateToDetail() {
    navigate("app://detail", launchMode = LaunchMode.SINGLE_TASK)
}

// ✅ 正确：只对需要清除返回栈的页面使用
fun backToHome() {
    navigate("app://home", launchMode = LaunchMode.SINGLE_TASK)
}
```

### 5. SINGLE_INSTANCE 的谨慎使用

```kotlin
// ⚠️ 注意：SINGLE_INSTANCE 会创建独立任务栈
// 只在真正需要独立运行的功能中使用

// ✅ 适合：相机、分享等独立功能
fun openCamera() {
    navigate("app://camera", launchMode = LaunchMode.SINGLE_INSTANCE)
}

// ❌ 不适合：普通业务页面
fun navigateToProfile() {
    // 不要这样做
    navigate("app://profile", launchMode = LaunchMode.SINGLE_INSTANCE)
}
```

---

## 常见问题

### Q1: 什么时候使用 SINGLE_TOP？

A: 当你希望避免在栈顶重复创建相同的 Activity 时使用。典型场景：
- 搜索页面：用户多次点击搜索按钮
- 通知跳转：点击通知打开已经在前台的页面
- 扫码页面：避免重复打开扫码界面

### Q2: SINGLE_TASK 会清除哪些 Activity？

A: SINGLE_TASK 会清除目标 Activity 之上的所有 Activity。例如：
```
栈: [A, B, C, D]
启动 B (SINGLE_TASK): [A, B]  // C 和 D 被清除
```

### Q3: 如何在 onNewIntent 中获取新的参数？

A: 需要调用 `setIntent(intent)` 更新 intent，然后从新 intent 中获取参数：

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)  // 更新 intent
    
    // 现在可以获取新参数
    val newData = intent.getStringExtra("data")
}
```

### Q4: SINGLE_INSTANCE 和 SINGLE_TASK 的区别？

A: 主要区别：
- SINGLE_TASK：在当前任务栈中复用，清除上层 Activity
- SINGLE_INSTANCE：在独立任务栈中运行，不与其他 Activity 共存

### Q5: 启动模式可以动态改变吗？

A: 可以。Swallow 框架通过 Intent Flags 实现启动模式，每次导航时都可以指定不同的模式：

```kotlin
// 第一次使用 STANDARD
navigate("app://detail")

// 第二次使用 SINGLE_TOP
navigate("app://detail", launchMode = LaunchMode.SINGLE_TOP)
```

### Q6: 如何测试启动模式是否生效？

A: 可以通过以下方式测试：

1. 在 Activity 的 `onCreate()` 和 `onNewIntent()` 中打印日志
2. 使用 `adb shell dumpsys activity activities` 查看任务栈
3. 观察 Activity 的创建和复用行为

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("LaunchMode", "onCreate: ${this.hashCode()}")
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    Log.d("LaunchMode", "onNewIntent: ${this.hashCode()}")
}
```

---

## 相关文档

- [Navigation API 使用指南](NAVIGATION_GUIDE.md)
- [UI State 和 Event 使用指南](UI_STATE_EVENT_GUIDE.md)
- [BaseViewModel API 文档](../src/main/java/com/swallow/fly/base/presentation/BaseViewModel.kt)
- [UiEvent API 文档](../src/main/java/com/swallow/fly/base/presentation/state/UiEvent.kt)
