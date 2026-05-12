# Utils 工具类向后兼容优化方案

## 执行日期
2026-03-13

## 核心原则

**100% 向后兼容 - 渐进式增强**

所有优化都采用"新增而非替换"的策略，确保：
1. ✅ 旧代码无需修改，继续正常工作
2. ✅ 新代码可以使用更优雅的 API
3. ✅ 使用 `@Deprecated` 标记旧方法，提供迁移指引
4. ✅ 保持包名和类名不变

---

## 使用情况分析

### DateUtils 使用情况
- `DateUtils.getDateFromStamp()` - 1 处使用（DialogUtils.kt）
- `DateUtils.isMonthLastDay()` - 1 处使用（DialogUtils.kt）
- **影响范围**: 极小，仅框架内部使用

### FastUtils 使用情况
- `FastUtils.makeText()` - 5 处使用
- `FastUtils.getScreenWidth()` - 1 处使用
- `FastUtils.collapseSoftInputMethod()` - 1 处使用
- **影响范围**: 中等，框架内部和 base 模块使用

### FileUtils 使用情况
- **影响范围**: 无，项目中未使用

---

## 向后兼容优化方案

### 方案 1: FastUtils - 保留原类 + 新增扩展函数

**策略**: 保留 FastUtils.kt，标记部分方法为 @Deprecated，同时新增扩展函数

```kotlin
// FastUtils.kt - 保持原有方法，添加 @Deprecated
object FastUtils {
    var mToast: Toast? = null

    /**
     * 单例 toast
     * @deprecated 使用 Context.showToast() 扩展函数替代
     */
    @Deprecated(
        message = "Use Context.showToast() extension function instead",
        replaceWith = ReplaceWith(
            "context.showToast(string)",
            "com.swallow.fly.ext.showToast"
        )
    )
    @SuppressLint("ShowToast")
    fun makeText(context: Context?, string: String?) {
        if (mToast == null) {
            mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        }
        mToast?.setText(string)
        mToast?.show()
    }

    /**
     * 获得屏幕的宽度
     * @deprecated 使用 Context.screenWidth 扩展属性替代
     */
    @Deprecated(
        message = "Use Context.screenWidth extension property instead",
        replaceWith = ReplaceWith(
            "context.screenWidth",
            "com.swallow.fly.ext.screenWidth"
        )
    )
    fun getScreenWidth(context: Context): Int {
        return getResources(context).displayMetrics.widthPixels
    }

    /**
     * dp 转 px
     * @deprecated 使用 Context.dp2px() 或 Int.dp 扩展函数替代
     */
    @Deprecated(
        message = "Use Context.dp2px() or Int.dp extension function instead",
        replaceWith = ReplaceWith(
            "context.dp2px(dpValue)",
            "com.swallow.fly.ext.dp2px"
        )
    )
    fun dip2px(@NonNull context: Context, dpValue: Float): Int {
        val scale = getResources(context).displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    // ... 其他方法保持不变
}
```

**新增扩展函数文件**:

```kotlin
// ext/ToastExt.kt - 新增文件
package com.swallow.fly.ext

import android.content.Context
import android.widget.Toast

private var toast: Toast? = null

/**
 * 显示 Toast（新方法）
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, message, duration).apply { show() }
}

fun Context.showLongToast(message: String) {
    showToast(message, Toast.LENGTH_LONG)
}
```

```kotlin
// ext/DimensionExt.kt - 新增文件
package com.swallow.fly.ext

import android.content.Context

/**
 * dp 转 px（新方法）
 */
fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.dp2px(dp: Int): Int = dp2px(dp.toFloat())

/**
 * Int 扩展属性，直接转换 dp
 */
val Int.dp: Int
    get() = (this * android.content.res.Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * android.content.res.Resources.getSystem().displayMetrics.density + 0.5f).toInt()

/**
 * 屏幕宽度扩展属性（新方法）
 */
val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels
```

**迁移示例**:

```kotlin
// 旧代码（继续工作，但有警告）
FastUtils.makeText(context, "Hello")
val width = FastUtils.getScreenWidth(context)
val px = FastUtils.dip2px(context, 16f)

// 新代码（推荐使用）
context.showToast("Hello")
val width = context.screenWidth
val px = context.dp2px(16)
// 或者更简洁
val px = 16.dp
```

**收益**:
- ✅ 旧代码无需修改
- ✅ 新代码更简洁优雅
- ✅ IDE 会提示迁移建议
- ✅ 可以逐步迁移

---

### 方案 2: DateUtils - 保留原方法 + 新增扩展函数

**策略**: 保留所有原有方法，新增扩展函数作为补充

```kotlin
// DateUtils.kt - 保持原有方法
object DateUtils {
    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    // ... 其他常量

    /**
     * 日期字符串转时间戳（保留原方法）
     */
    fun dateToStamp(dateStr: String): Long {
        val format = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
        try {
            return format.parse(dateStr)?.time ?: Date().time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date().time
    }

    /**
     * YY/MM/dd（保留原方法）
     */
    fun getDateFromStamp(timeMill: Long): String {
        val d = Date(timeMill)
        val format = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.CHINA)
        return format.format(d)
    }

    // ... 其他原有方法保持不变

    // ========== 新增扩展函数（推荐使用） ==========

    /**
     * 时间戳转日期字符串（新方法，线程安全）
     */
    fun Long.toDateString(pattern: String = DEFAULT_DATE_FORMAT): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0+ 使用新 API
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(this),
                ZoneId.systemDefault()
            )
            dateTime.format(DateTimeFormatter.ofPattern(pattern))
        } else {
            // 兼容旧版本
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.format(Date(this))
        }
    }

    /**
     * 日期字符串转时间戳（新方法，线程安全）
     */
    fun String.toTimestamp(pattern: String = DEFAULT_DATE_FORMAT): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.parse(this)?.time ?: System.currentTimeMillis()
        }
    }

    /**
     * 判断是否是今天（新方法）
     */
    fun Long.isToday(): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val date = Calendar.getInstance().apply {
            timeInMillis = this@isToday
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return today.timeInMillis == date.timeInMillis
    }
}
```

**迁移示例**:

```kotlin
// 旧代码（继续工作）
val dateStr = DateUtils.getDateFromStamp(timestamp)
val timestamp = DateUtils.dateToStamp("2024-01-01")

// 新代码（推荐使用，更简洁）
val dateStr = timestamp.toDateString()
val timestamp = "2024-01-01".toTimestamp()

// 新增功能
if (timestamp.isToday()) {
    // 是今天
}
```

**收益**:
- ✅ 100% 向后兼容
- ✅ 新方法线程安全
- ✅ 扩展函数更易用
- ✅ 可选择性迁移

---

### 方案 3: FileUtils - 仅新增，不修改

**策略**: FileUtils 项目中未使用，可以安全地新增功能

```kotlin
// FileUtils.kt - 保留所有原有方法

object FileUtils {
    // ... 所有原有方法保持不变

    // ========== 新增协程支持（推荐使用） ==========

    /**
     * 复制文件（协程版本，新方法）
     */
    suspend fun File.copyToSuspend(
        target: File,
        overwrite: Boolean = false
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            this@copyToSuspend.copyTo(target, overwrite)
            Result.success(target)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 读取文件内容（协程版本，新方法）
     */
    suspend fun File.readTextSuspend(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Result.success(readText())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 写入文件内容（协程版本，新方法）
     */
    suspend fun File.writeTextSuspend(text: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            writeText(text)
            Result.success(this@writeTextSuspend)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**收益**:
- ✅ 完全向后兼容
- ✅ 新增协程支持
- ✅ 无需迁移旧代码

---

### 方案 4: StringUtils - 仅新增功能

**策略**: 保留原有方法，新增常用功能

```kotlin
// StringUtils.kt
object StringUtils {
    // ========== 原有方法保持不变 ==========
    
    fun passwordAvailable(@NonNull str: String): Boolean {
        val pattern = Pattern.compile("^(?![A-Z]+\$)(?![a-z]+\$)(?!\\d+\$)(?![\\W_]+\$)\\S{8,16}\$")
        return pattern.matcher(str).matches()
    }

    fun checkIpAddress(@NonNull str: String): Boolean {
        val pattern = Pattern.compile(
            "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\$"
        )
        return pattern.matcher(str).matches()
    }

    // ========== 新增扩展函数（推荐使用） ==========

    /**
     * 验证邮箱（新方法）
     */
    fun String.isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    /**
     * 验证手机号（新方法）
     */
    fun String.isValidPhone(): Boolean {
        return android.util.Patterns.PHONE.matcher(this).matches()
    }

    /**
     * 验证 URL（新方法）
     */
    fun String.isValidUrl(): Boolean {
        return android.util.Patterns.WEB_URL.matcher(this).matches()
    }

    /**
     * 截断字符串（新方法）
     */
    fun String.truncate(maxLength: Int, suffix: String = "..."): String {
        return if (length <= maxLength) this
        else substring(0, maxLength - suffix.length) + suffix
    }

    /**
     * 手机号脱敏（新方法）
     */
    fun String.maskPhone(): String {
        return if (length >= 11) {
            substring(0, 3) + "****" + substring(7)
        } else {
            this
        }
    }
}
```

**收益**:
- ✅ 完全向后兼容
- ✅ 新增常用功能
- ✅ 扩展函数更易用

---

## 迁移策略

### 阶段 1: 新增功能（当前版本 - 无破坏性）

1. **新增扩展函数文件**
   - `ext/ToastExt.kt`
   - `ext/DimensionExt.kt`
   - `ext/ScreenExt.kt`

2. **在原有工具类中新增方法**
   - DateUtils 新增扩展函数
   - StringUtils 新增扩展函数
   - FileUtils 新增协程方法

3. **标记旧方法为 @Deprecated**
   - 提供 `replaceWith` 迁移建议
   - 不强制迁移

**影响**: ✅ 零影响，完全向后兼容

---

### 阶段 2: 推荐迁移（下一个版本 - 可选）

1. **文档更新**
   - 更新 README，推荐使用新 API
   - 提供迁移指南

2. **示例代码更新**
   - app 模块使用新 API
   - 提供迁移示例

**影响**: ✅ 零影响，旧代码继续工作

---

### 阶段 3: 移除旧方法（未来版本 - 可选）

1. **提前通知**
   - 在文档中说明计划移除的方法
   - 提供充足的迁移时间（至少 2 个大版本）

2. **逐步移除**
   - 先移除使用率低的方法
   - 保留核心方法

**影响**: ⚠️ 需要迁移，但有充足时间准备

---

## 实际改动影响分析

### 对使用方的影响

#### 当前项目（app 模块）
- **FastUtils.makeText()**: 5 处使用
  - 可选择性迁移到 `context.showToast()`
  - 旧代码继续工作，只有编译警告

- **FastUtils.getScreenWidth()**: 1 处使用
  - 可选择性迁移到 `context.screenWidth`
  - 旧代码继续工作

- **DateUtils**: 2 处使用
  - 可选择性迁移到扩展函数
  - 旧代码继续工作

#### 外部项目（引入 swallow 库的项目）
- **影响**: ✅ 零影响
- **原因**: 所有旧 API 保持不变
- **选择**: 可以选择使用新 API，也可以继续使用旧 API

---

## 推荐的优化顺序

### 第 1 步: 新增扩展函数（本周，2 小时）

**只新增，不修改**

1. 创建 `ext/ToastExt.kt`
2. 创建 `ext/DimensionExt.kt`
3. 在 DateUtils 中新增扩展函数
4. 在 StringUtils 中新增扩展函数

**影响**: ✅ 零影响

---

### 第 2 步: 标记废弃方法（下周，1 小时）

**添加 @Deprecated 注解**

1. FastUtils 部分方法添加 @Deprecated
2. 提供 replaceWith 迁移建议

**影响**: ⚠️ 编译警告（可忽略）

---

### 第 3 步: 更新示例代码（按需）

**app 模块使用新 API**

1. 更新 app 模块代码
2. 提供迁移示例

**影响**: ✅ 零影响（仅示例）

---

## 总结

### 核心策略

**渐进式增强 + 100% 向后兼容**

1. ✅ **新增而非替换** - 保留所有旧方法
2. ✅ **扩展而非修改** - 使用扩展函数补充功能
3. ✅ **标记而非删除** - 使用 @Deprecated 提示迁移
4. ✅ **可选而非强制** - 用户自主选择迁移时机

### 对使用方的影响

- **当前版本**: ✅ 零影响，完全兼容
- **下一版本**: ✅ 零影响，可选迁移
- **未来版本**: ⚠️ 可能移除旧方法（提前通知）

### 推荐做法

1. **立即执行**: 新增扩展函数（零影响）
2. **逐步迁移**: 在新代码中使用新 API
3. **保持兼容**: 旧代码无需修改

这样既能享受新 API 的优势，又不会给使用方带来任何负担。
