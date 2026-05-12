# Utils 工具类优化分析报告

## 执行日期
2026-03-13

## 当前状态

### 工具类列表

```
swallow/src/main/java/com/swallow/fly/utils/
├── AppManager.kt                    # ✅ 已迁移到 Kotlin
├── DateUtils.kt                     # ⚠️ 需要优化
├── DialogUtils.kt                   # ⚠️ 需要优化
├── DoubleClickExitHelper.kt         # ✅ 良好
├── DownloadFileManager.kt           # ✅ 已优化
├── FastUtils.kt                     # ⚠️ 需要重构
├── FileUtils.kt                     # ⚠️ 需要优化
├── KeyBordStateUtil.kt              # ⚠️ 需要检查
├── NetWorkHelper.kt                 # ✅ 已使用新 API
├── SingletonHolderDoubleArg.kt      # ✅ 良好
├── SingletonHolderNoneArg.kt        # ✅ 良好
├── SingletonHolderSingleArg.kt      # ✅ 良好
├── StringUtils.kt                   # ⚠️ 功能太少
├── UrlEncoderUtils.kt               # ⚠️ 需要检查
└── ZipHelper.kt                     # ⚠️ 需要检查
```

---

## 需要优化的工具类

### 🔴 高优先级优化

#### 1. FastUtils.kt - 需要拆分和重构

**问题**:
- 功能过于杂乱,包含 UI、转换、资源、Toast 等多种功能
- 部分方法使用了废弃的 API
- 缺少现代化的 Kotlin 扩展函数
- 代码重复（与扩展函数重复）

**具体问题**:

1. **使用废弃的 API**
   ```kotlin
   // scaledDensity 已废弃
   val fontScale = getResources(context).displayMetrics.scaledDensity
   ```

2. **Toast 单例实现不够优雅**
   ```kotlin
   var mToast: Toast? = null  // 全局可变变量
   ```

3. **功能混杂**
   - dp/px 转换
   - 资源获取
   - Toast 显示
   - RecyclerView 配置
   - 软键盘控制
   - MD5 加密
   - 全屏设置

**优化方案**:

```kotlin
// 1. 拆分为多个专门的工具类

// DimensionUtils.kt - 尺寸转换
object DimensionUtils {
    fun Context.dp2px(dp: Float): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
    
    fun Context.px2dp(px: Int): Float {
        return px / resources.displayMetrics.density + 0.5f
    }
    
    fun Context.sp2px(sp: Float): Int {
        return (sp * resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }
    
    fun Context.px2sp(px: Float): Int {
        return (px / resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }
}

// ToastUtils.kt - Toast 工具
object ToastUtils {
    private var toast: Toast? = null
    
    fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(this, message, duration).apply { show() }
    }
    
    fun Context.showLongToast(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }
}

// ResourceUtils.kt - 资源获取
object ResourceUtils {
    fun Context.getString(resId: Int): String = resources.getString(resId)
    
    fun Context.getColor(resId: Int): Int = 
        ResourcesCompat.getColor(resources, resId, theme)
    
    fun Context.getDrawable(resId: Int): Drawable? = 
        ResourcesCompat.getDrawable(resources, resId, theme)
    
    fun Context.getDimen(resId: Int): Int = 
        resources.getDimension(resId).toInt()
}

// CryptoUtils.kt - 加密工具
object CryptoUtils {
    fun String.md5(): String {
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
```

**收益**:
- 职责单一,易于维护
- 使用扩展函数,更符合 Kotlin 风格
- 避免使用废弃 API
- 减少代码重复

---

#### 2. DateUtils.kt - 需要现代化

**问题**:
- 使用 SimpleDateFormat（线程不安全）
- 缺少 Kotlin 扩展函数
- 方法命名不统一
- 缺少时区处理
- 代码重复

**具体问题**:

1. **SimpleDateFormat 线程不安全**
   ```kotlin
   val format = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
   ```

2. **方法命名不一致**
   ```kotlin
   fun getDateFromStamp(timeMill: Long): String
   fun getCnDateFromStamp(timeMill: Long): String
   fun getDateMillisFromStamp(timeMill: Long): String
   fun getDateFromStampE(timeMill: Long): String  // 命名混乱
   ```

3. **缺少现代化 API**
   - 未使用 java.time (API 26+)
   - 未使用 kotlinx-datetime

**优化方案**:

```kotlin
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

object DateUtils {
    // 线程安全的 DateTimeFormatter 缓存
    private val formatterCache = ConcurrentHashMap<String, DateTimeFormatter>()
    
    private fun getFormatter(pattern: String): DateTimeFormatter {
        return formatterCache.getOrPut(pattern) {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    
    // 扩展函数方式
    fun Long.toDateString(pattern: String = "yyyy-MM-dd"): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        )
        return dateTime.format(getFormatter(pattern))
    }
    
    fun String.toTimestamp(pattern: String = "yyyy-MM-dd"): Long {
        val dateTime = LocalDateTime.parse(this, getFormatter(pattern))
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    fun Long.isToday(): Boolean {
        val today = LocalDateTime.now().toLocalDate()
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        ).toLocalDate()
        return today == date
    }
    
    fun Long.isYesterday(): Boolean {
        val yesterday = LocalDateTime.now().minusDays(1).toLocalDate()
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.systemDefault()
        ).toLocalDate()
        return yesterday == date
    }
    
    // 兼容旧代码的方法（标记为废弃）
    @Deprecated("Use Long.toDateString() instead")
    fun getDateFromStamp(timeMill: Long): String {
        return timeMill.toDateString("yyyy-MM-dd")
    }
}
```

**收益**:
- 线程安全
- 使用现代化 API
- 扩展函数更易用
- 统一命名规范

---

#### 3. FileUtils.kt - 需要简化和模块化

**问题**:
- 代码过长（800+ 行）
- 功能混杂（文件操作、Uri 处理、图片处理）
- 部分方法过时
- 缺少协程支持

**优化方案**:

```kotlin
// 拆分为多个文件

// FileUtils.kt - 基础文件操作
object FileUtils {
    fun File.copyTo(target: File, overwrite: Boolean = false): Boolean {
        return try {
            this.copyTo(target, overwrite)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun File.copyToSuspend(target: File): Result<File> = withContext(Dispatchers.IO) {
        try {
            this@copyToSuspend.copyTo(target, overwrite = true)
            Result.success(target)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun File.deleteRecursively(): Boolean {
        return this.deleteRecursively()
    }
    
    fun File.sizeInMB(): Double {
        return length() / (1024.0 * 1024.0)
    }
}

// UriUtils.kt - Uri 处理
object UriUtils {
    fun Context.getPathFromUri(uri: Uri): String? {
        // 简化的 Uri 转路径逻辑
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10+ 使用 MediaStore API
                getPathFromUriQ(uri)
            }
            else -> {
                // Android 10 以下使用传统方法
                getPathFromUriLegacy(uri)
            }
        }
    }
    
    private fun Context.getPathFromUriQ(uri: Uri): String? {
        // 实现 Android 10+ 的 Uri 处理
        TODO()
    }
    
    private fun Context.getPathFromUriLegacy(uri: Uri): String? {
        // 实现传统的 Uri 处理
        TODO()
    }
}

// ImageUtils.kt - 图片处理
object ImageUtils {
    fun Bitmap.toCircle(): Bitmap {
        // 圆形图片处理
        TODO()
    }
    
    fun Bitmap.rotate(degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
    
    suspend fun Bitmap.saveTo(file: File, quality: Int = 100): Result<File> = 
        withContext(Dispatchers.IO) {
            try {
                FileOutputStream(file).use { fos ->
                    compress(Bitmap.CompressFormat.JPEG, quality, fos)
                }
                Result.success(file)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
```

**收益**:
- 代码模块化
- 职责单一
- 支持协程
- 更易维护

---

### 🟡 中优先级优化

#### 4. StringUtils.kt - 功能太少

**问题**:
- 只有 2 个方法
- 缺少常用的字符串处理功能

**优化方案**:

```kotlin
object StringUtils {
    // 现有方法
    fun String.isValidPassword(): Boolean {
        val pattern = Pattern.compile("^(?![A-Z]+\$)(?![a-z]+\$)(?!\\d+\$)(?![\\W_]+\$)\\S{8,16}\$")
        return pattern.matcher(this).matches()
    }
    
    fun String.isValidIpAddress(): Boolean {
        val pattern = Pattern.compile(
            "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\$"
        )
        return pattern.matcher(this).matches()
    }
    
    // 新增常用方法
    fun String.isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    
    fun String.isValidPhone(): Boolean {
        return android.util.Patterns.PHONE.matcher(this).matches()
    }
    
    fun String.isValidUrl(): Boolean {
        return android.util.Patterns.WEB_URL.matcher(this).matches()
    }
    
    fun String.isNumeric(): Boolean {
        return this.matches(Regex("\\d+"))
    }
    
    fun String.isAlphabetic(): Boolean {
        return this.matches(Regex("[a-zA-Z]+"))
    }
    
    fun String.isAlphanumeric(): Boolean {
        return this.matches(Regex("[a-zA-Z0-9]+"))
    }
    
    fun String.truncate(maxLength: Int, suffix: String = "..."): String {
        return if (length <= maxLength) this
        else substring(0, maxLength - suffix.length) + suffix
    }
    
    fun String.toTitleCase(): String {
        return split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }
    
    fun String.removeWhitespace(): String {
        return replace("\\s+".toRegex(), "")
    }
    
    fun String.maskEmail(): String {
        val parts = split("@")
        if (parts.size != 2) return this
        val username = parts[0]
        val domain = parts[1]
        val maskedUsername = if (username.length > 2) {
            username.substring(0, 2) + "***"
        } else {
            "***"
        }
        return "$maskedUsername@$domain"
    }
    
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
- 提供常用字符串处理功能
- 减少重复代码
- 提高开发效率

---

#### 5. DialogUtils.kt - 需要现代化

**问题**:
- 使用传统的 DatePickerDialog
- 缺少 Material Design 风格
- 缺少其他常用对话框

**优化方案**:

```kotlin
object DialogUtils {
    // 现有方法保持兼容
    
    // 新增 Material Design 风格的对话框
    fun Context.showMaterialDatePicker(
        selection: Long = System.currentTimeMillis(),
        onDateSelected: (Long) -> Unit
    ) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(selection)
            .build()
        
        picker.addOnPositiveButtonClickListener { timestamp ->
            onDateSelected(timestamp)
        }
        
        if (this is AppCompatActivity) {
            picker.show(supportFragmentManager, "DATE_PICKER")
        }
    }
    
    fun Context.showConfirmDialog(
        title: String,
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onConfirm() }
            .setNegativeButton(negativeText) { _, _ -> onCancel() }
            .show()
    }
    
    fun Context.showLoadingDialog(message: String = "加载中..."): AlertDialog {
        return MaterialAlertDialogBuilder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()
            .apply { show() }
    }
}
```

**收益**:
- Material Design 风格
- 更现代化的 API
- 更多常用对话框

---

### 🟢 低优先级优化

#### 6. 其他工具类检查

需要检查以下工具类:
- KeyBordStateUtil.kt
- UrlEncoderUtils.kt
- ZipHelper.kt

---

## 优化优先级总结

### 🔴 立即实施（本周，约 8 小时）

1. **FastUtils.kt 拆分重构**（3 小时）
   - 拆分为 DimensionUtils、ToastUtils、ResourceUtils、CryptoUtils
   - 使用扩展函数
   - 移除废弃 API

2. **DateUtils.kt 现代化**（2 小时）
   - 使用 DateTimeFormatter
   - 添加扩展函数
   - 统一命名规范

3. **FileUtils.kt 模块化**（3 小时）
   - 拆分为 FileUtils、UriUtils、ImageUtils
   - 添加协程支持
   - 简化代码

### 🟡 近期实施（下周，约 4 小时）

4. **StringUtils.kt 增强**（1.5 小时）
   - 添加常用字符串处理方法
   - 添加验证方法
   - 添加格式化方法

5. **DialogUtils.kt 现代化**（1.5 小时）
   - 添加 Material Design 对话框
   - 添加常用对话框
   - 保持向后兼容

6. **其他工具类检查**（1 小时）
   - 检查 KeyBordStateUtil.kt
   - 检查 UrlEncoderUtils.kt
   - 检查 ZipHelper.kt

---

## 预期收益

### 代码质量
- 代码模块化,职责单一
- 使用现代化 API
- 避免废弃 API
- 线程安全

### 开发效率
- 扩展函数更易用
- 减少样板代码
- 提供常用功能
- 统一命名规范

### 可维护性
- 代码结构清晰
- 易于理解和修改
- 便于测试
- 便于扩展

---

## 总结

Utils 工具类的主要问题:

1. ❌ **FastUtils.kt 功能混杂** - 需要拆分
2. ❌ **DateUtils.kt 使用旧 API** - 需要现代化
3. ❌ **FileUtils.kt 代码过长** - 需要模块化
4. ❌ **StringUtils.kt 功能太少** - 需要增强
5. ❌ **部分工具类使用废弃 API** - 需要更新

建议优先实施高优先级优化,这些改进能显著提升代码质量和开发效率。
