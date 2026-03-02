# Activity 过渡动画迁移指南

## 问题背景

`overridePendingTransition()` 在 Android 13 (API 33) 中被标记为废弃，在 Android 14 (API 34) 中被新的 `overrideActivityTransition()` API 替代。

## 解决方案

Swallow 框架提供了兼容新旧 API 的扩展函数，自动根据系统版本选择合适的 API。

## 使用方法

### 1. 基本用法 - finish 时添加动画

**旧代码（已废弃）：**
```kotlin
finish()
overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
```

**新代码（推荐）：**
```kotlin
// 方式 1：使用便捷方法
finishWithTransition(R.anim.fade_in, R.anim.fade_out)

// 方式 2：分开调用
finish()
overrideTransition(R.anim.fade_in, R.anim.fade_out)
```

### 2. startActivity 时添加动画

**旧代码（已废弃）：**
```kotlin
startActivity(intent)
overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
```

**新代码（推荐）：**
```kotlin
startActivity(intent)
overrideOpenTransition(R.anim.slide_in_right, R.anim.slide_out_left)
```

## API 说明

### overrideTransition()

用于 `finish()` 之后设置关闭动画。

```kotlin
fun Activity.overrideTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int)
```

**参数：**
- `enterAnim`: 进入动画资源 ID（当前 Activity 下面的 Activity 的进入动画）
- `exitAnim`: 退出动画资源 ID（当前 Activity 的退出动画）

**示例：**
```kotlin
finish()
overrideTransition(R.anim.fade_in, R.anim.fade_out)
```

### overrideOpenTransition()

用于 `startActivity()` 之后设置打开动画。

```kotlin
fun Activity.overrideOpenTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int)
```

**参数：**
- `enterAnim`: 进入动画资源 ID（新 Activity 的进入动画）
- `exitAnim`: 退出动画资源 ID（当前 Activity 的退出动画）

**示例：**
```kotlin
startActivity(intent)
overrideOpenTransition(R.anim.slide_in_right, R.anim.slide_out_left)
```

### finishWithTransition()

便捷方法，finish 并应用过渡动画。

```kotlin
fun Activity.finishWithTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int)
```

**示例：**
```kotlin
finishWithTransition(R.anim.fade_in, R.anim.fade_out)
```

## 兼容性说明

这些扩展函数会自动根据系统版本选择合适的 API：

- **Android 14+ (API 34+)**: 使用 `overrideActivityTransition()`
- **Android 13 及以下**: 使用 `overridePendingTransition()`（已抑制废弃警告）

## 常见动画资源

框架内置了常用的过渡动画：

```xml
<!-- res/anim/fade_in.xml -->
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="300"
    android:fromAlpha="0.0"
    android:toAlpha="1.0" />

<!-- res/anim/fade_out.xml -->
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="300"
    android:fromAlpha="1.0"
    android:toAlpha="0.0" />
```

## 迁移检查清单

- [ ] 搜索项目中所有 `overridePendingTransition` 的使用
- [ ] 根据使用场景选择合适的替代方法：
  - `finish()` 后使用 → `finishWithTransition()` 或 `overrideTransition()`
  - `startActivity()` 后使用 → `overrideOpenTransition()`
- [ ] 导入扩展函数：`import com.swallow.fly.ext.*`
- [ ] 测试在不同 Android 版本上的动画效果

## 注意事项

1. **导入扩展函数**：确保导入了 `com.swallow.fly.ext` 包中的扩展函数
2. **动画资源**：确保动画资源文件存在于 `res/anim/` 目录
3. **测试覆盖**：建议在 Android 13 及以下和 Android 14+ 设备上都进行测试
4. **性能考虑**：过渡动画会略微增加内存使用，建议使用简单的动画效果

## 参考资料

- [Android 官方文档 - overrideActivityTransition](https://developer.android.com/reference/android/app/Activity#overrideActivityTransition(int,%20int,%20int))
- [Android 官方文档 - overridePendingTransition (已废弃)](https://developer.android.com/reference/android/app/Activity#overridePendingTransition(int,%20int))
