# Swallow 核心库现代化升级任务列表

## 阶段 1: 构建工具和配置升级

- [x] 1. 升级 Gradle 和 AGP 版本





  - 升级 Gradle Wrapper 到 8.9 或更高版本
  - 升级 AGP 到 8.7.3
  - 升级 Kotlin 到 2.0.21
  - 升级 JDK 到 17
  - 更新 gradle-wrapper.properties
  - 更新 gradle.properties 配置
  - _需求: 1.1, 1.2, 1.3, 1.4_

- [x] 2. 创建 Version Catalog 配置





  - 创建 gradle/libs.versions.toml 文件
  - 定义所有版本号（AGP、Kotlin、AndroidX、第三方库）
  - 定义所有库依赖
  - 定义所有插件
  - 创建常用的 bundles（lifecycle、compose 等）
  - _需求: 9.6_

- [x] 3. 迁移根目录 build.gradle 到 Kotlin DSL


  - 创建 build.gradle.kts
  - 使用 Version Catalog 引用插件
  - 配置所有子项目的通用设置
  - 删除旧的 build.gradle
  - _需求: 9.1, 1.5_

- [x] 4. 迁移 settings.gradle 到 Kotlin DSL


  - 创建 settings.gradle.kts
  - 配置 pluginManagement
  - 配置 dependencyResolutionManagement
  - 启用 Version Catalog
  - 删除旧的 settings.gradle
  - _需求: 9.2_

- [x] 5. 验证构建配置



  - 执行 gradlew clean
  - 执行 gradlew build --dry-run
  - 检查是否有配置错误
  - 确认 Version Catalog 正常工作
  - _需求: 13.1, 13.5_

## 阶段 2: Swallow 模块构建脚本迁移

- [x] 6. 迁移 swallow/build.gradle 到 Kotlin DSL



- [x] 6.1 创建 swallow/build.gradle.kts 基础结构


  - 配置插件（android-library, kotlin-android, kotlin-parcelize）
  - 设置 namespace
  - 配置 compileSdk 和 targetSdk 为 35
  - 配置 minSdk 为 24
  - _需求: 2.1, 2.2, 2.3, 9.3_

- [x] 6.2 配置编译选项

  - 设置 Java 17 兼容性
  - 配置 Kotlin JVM target 为 17
  - 添加 Kotlin 编译器参数（opt-in）
  - 启用 ViewBinding
  - _需求: 1.3_

- [x] 6.3 配置 Compose 支持

  - 启用 Compose 构建特性
  - 配置 Compose 编译器版本
  - _需求: 10.1, 10.2_

- [x] 6.4 配置 ProGuard 和发布设置

  - 配置 buildTypes
  - 配置 consumerProguardFiles
  - 配置 publishing 块
  - _需求: 9.3_


## 阶段 3: 依赖库升级和 KSP 迁移

- [x] 7. 升级 AndroidX 核心库


- [x] 7.1 升级 AndroidX 基础库依赖

  - 使用 Version Catalog 引用所有 AndroidX 库
  - 升级 core-ktx 到 1.15.0+
  - 升级 appcompat 到 1.7.0+
  - 升级 material 到 1.12.0+
  - 升级 constraintlayout 到 2.2.0+
  - 升级 recyclerview 到 1.3.2+
  - _需求: 3.7_

- [x] 7.2 升级 Lifecycle 组件

  - 升级到 2.8.7+
  - 更新所有 lifecycle 相关依赖
  - 移除 lifecycle-compiler（使用 common-java8）
  - _需求: 3.1_

- [x] 7.3 升级 Navigation 组件

  - 升级到 2.8.5+
  - 更新 navigation-fragment-ktx 和 navigation-ui-ktx
  - _需求: 3.3_

- [x] 7.4 升级 Paging 库

  - 升级到 3.3.5+
  - 更新为 paging-runtime-ktx
  - _需求: 3.4_

- [x] 8. 迁移 Hilt 到 KSP


- [x] 8.1 添加 KSP 插件和依赖

  - 在 build.gradle.kts 中添加 KSP 插件
  - 添加 KSP 版本到 Version Catalog
  - _需求: 4.1_

- [x] 8.2 升级 Hilt 到 2.54+

  - 更新 Hilt 版本
  - 将 kapt(hilt-compiler) 改为 ksp(hilt-compiler)
  - _需求: 4.1_

- [x] 8.3 移除 kapt 配置块

  - 删除 kapt { correctErrorTypes = true }
  - _需求: 4.3_

- [x] 9. 迁移 Room 到 KSP


- [x] 9.1 升级 Room 到 2.6.1+

  - 更新 Room 版本
  - 将 kapt(room-compiler) 改为 ksp(room-compiler)
  - _需求: 3.2, 4.2_

- [x] 9.2 验证 Room 代码生成

  - 检查生成的代码路径
  - 确认 DAO 和 Database 类正常工作
  - _需求: 4.2_

- [x] 10. 升级网络库


- [x] 10.1 升级 Retrofit 和 OkHttp

  - 升级 Retrofit 到 2.11.0+
  - 升级 OkHttp 到 4.12.0+
  - 升级 Gson 到 2.11.0+
  - 更新 retrofit-url-manager（如果有新版本）
  - _需求: 5.1, 5.2, 5.3_

- [x] 10.2 验证网络库兼容性

  - 检查 API 变更
  - 测试网络请求
  - 确认协程支持正常
  - _需求: 5.4, 5.5_

- [x] 11. 升级 Glide 到 5.x 并迁移到 KSP


- [x] 11.1 升级 Glide 到 5.0.5+

  - 更新 Glide 版本
  - 将 kapt(glide-compiler) 改为 ksp(glide-ksp)
  - 添加 glide-okhttp3-integration
  - _需求: 6.1, 6.2_

- [x] 11.2 适配 Glide 5.x API 变更

  - 检查 API 变更文档
  - 更新 GlideExtension 代码
  - 更新图片加载相关代码
  - _需求: 6.3, 6.4_

- [x] 12. 升级协程库

  - 升级 kotlinx-coroutines 到 1.9.0+
  - 更新 coroutines-android 和 coroutines-core
  - _需求: 7.1, 7.2, 7.3, 7.4_

- [x] 13. 升级工具类库


- [x] 13.1 升级 Timber 到 5.0.1+

  - 更新 Timber 版本
  - _需求: 8.1_


- [x] 13.2 升级 MMKV 到 2.0+


  - 更新 MMKV 版本
  - 检查 API 变更
  - _需求: 8.4_

- [x] 13.3 评估和升级 ImmersionBar

  - 检查是否有新版本
  - 评估是否需要替代方案
  - 如需替代，实现新的状态栏管理方案
  - _需求: 8.2_

- [x] 13.4 升级权限处理库

  - 评估 EasyPermissions 对 Android 14+ 的支持
  - 如不支持，考虑使用 Accompanist Permissions 或 Activity Result API
  - _需求: 8.3_

- [x] 13.5 升级其他工具库

  - 升级 UtilCodeX（如有新版本）
  - 升级 EventBus 到 3.3.1
  - _需求: 8.5_

- [x] 14. 验证所有依赖升级



  - 执行 gradlew dependencies 检查依赖树
  - 解决所有依赖冲突
  - 确认没有重复的类
  - 执行 gradlew build 验证编译
  - _需求: 13.1, 13.2, 13.3, 13.4_


## 阶段 4: 核心代码重构

- [x] 15. 重构 BaseActivity



- [x] 15.1 添加 ViewModel 委托支持


  - 移除 modelClass 抽象属性
  - 添加 viewModel 抽象属性（使用 by viewModels()）
  - 更新 onCreate 中的 ViewModel 初始化逻辑
  - _需求: 12.1_

- [x] 15.2 实现 Activity Result API

  - 添加 permissionLauncher 属性
  - 使用 registerForActivityResult 注册权限请求
  - 添加 requestPermissions 方法
  - 添加 onPermissionsResult 回调方法
  - 标记 onRequestPermissionsResult 为 @Deprecated
  - _需求: 12.2_

- [x] 15.3 替换 ProgressDialog

  - 移除 ProgressDialog 相关代码
  - 实现 Material Design 进度指示器
  - 创建自定义加载对话框布局
  - 更新 showLoading 和 hideLoading 方法
  - _需求: 12.3_

- [x] 15.4 优化生命周期观察

  - 添加 observeViewModel 方法
  - 使用 repeatOnLifecycle 观察 Flow
  - 添加 handleUiState 和 handleUiEvent 抽象方法
  - 移除旧的 LiveData 观察代码
  - _需求: 12.4, 12.5_

- [x] 15.5 清理和优化

  - 移除不必要的代码
  - 优化导入语句
  - 添加 KDoc 注释
  - 确保 Kotlin 代码风格一致
  - _需求: 12.6_

- [x] 16. 重构 BaseViewModel


- [x] 16.1 使用 StateFlow 替代 LiveData

  - 添加 _uiState: MutableStateFlow
  - 添加 uiState: StateFlow 公开属性
  - 移除 _pageStateEvent: MutableLiveData
  - _需求: 12.4_

- [x] 16.2 添加 SharedFlow 处理一次性事件

  - 添加 _uiEvent: MutableSharedFlow
  - 添加 uiEvent: SharedFlow 公开属性
  - _需求: 12.4_

- [x] 16.3 创建 UiState 和 UiEvent 密封类

  - 创建 UiState 密封类（Idle, Loading, Success, Error）
  - 创建 UiEvent 密封类（ShowToast, ShowError, Navigate）
  - _需求: 12.4_

- [x] 16.4 重构状态管理方法

  - 更新 showLoading 使用 StateFlow
  - 更新 showError 使用 SharedFlow
  - 更新 showToast 使用 SharedFlow
  - 添加 handleErrors Flow 扩展函数
  - _需求: 12.4_

- [x] 16.5 添加 Compose 支持

  - 确保 ViewModel 可在 Compose 中使用
  - 添加必要的注解和配置
  - _需求: 10.3_

- [x] 17. 重构 BaseRepository


- [x] 17.1 创建 IRepository 接口


  - 定义平台无关的接口
  - 添加 executeRequest 方法签名
  - _需求: 11.2_

- [x] 17.2 实现 BaseRepository


  - 实现 IRepository 接口
  - 保留 repositoryManager 注入
  - 实现 executeRequest 方法（返回 Result）
  - _需求: 11.2_

- [x] 17.3 添加 Flow 支持

  - 添加 flowRequest 方法
  - 添加 cachedFlowRequest 方法
  - 使用 Dispatchers.IO 作为默认调度器
  - _需求: 11.5_

- [x] 17.4 优化错误处理

  - 集成 GlobalExceptionHandler
  - 统一异常转换逻辑
  - _需求: 11.3_

- [x] 18. 创建全局异常处理器



- [x] 18.1 创建 AppException 密封类


  - 定义各种异常类型（NetworkException, HttpException 等）
  - _需求: 错误处理_

- [x] 18.2 实现 GlobalExceptionHandler


  - 创建异常处理类
  - 实现 handleException 方法
  - 注入到 Repository 中
  - _需求: 错误处理_

- [x] 19. 更新网络层配置 ✅ **已完成 - NetworkModule 和 GlobalConfigModule 整合**




- [x] 19.1 重构 NetworkModule（整合版）


  - ✅ 使用 Kotlin DSL 语法
  - ✅ 整合 GlobalConfigModule 的网络配置部分
  - ✅ 按职责拆分为 4 个独立模块：NetworkModule、DatabaseModule、LogModule、ImageModule
  - ✅ 保留 RetrofitUrlManager（动态切换 BaseUrl）
  - ✅ 保留灵活配置接口（GsonConfiguration、OkhttpConfiguration、RetrofitConfiguration）
  - ✅ 保留 ManifestParser + meta-data 配置方式
  - ✅ 移除旧的 Dagger 代码，统一使用 Hilt
  - ✅ 删除冗余文件：GlobalConfigModule.kt、AppConfigModule.kt、NetworkConfigProvider.kt、AppComponent.kt
  - ✅ 更新 app 模块的 GlobalConfiguration 适配新的配置方式
  - ✅ 编译成功：swallow 模块和 app 模块均编译通过
  - _需求: 5.4_

- [x] 19.2 优化拦截器



  - ✅ 更新 GlobalHttpHandler 实现
  - ✅ 优化日志拦截器配置
  - ✅ 添加超时配置
  - ✅ 支持自定义拦截器配置
  - _需求: 5.5_

- [x] 20. 更新数据库层配置


- [x] 20.1 重构 DatabaseModule


  - 使用 Kotlin DSL 语法
  - 更新 Room 配置
  - 添加数据库迁移策略
  - _需求: 3.2_

- [x] 20.2 更新 DAO 使用 Flow


  - 将返回类型改为 Flow
  - 使用 suspend 函数
  - 移除回调方式
  - _需求: 3.2_


## 阶段 5: Compose 支持和扩展功能

- [x] 21. 添加 Compose 依赖和配置


- [x] 21.1 添加 Compose BOM 到 Version Catalog


  - 在 libs.versions.toml 中添加 Compose BOM 版本
  - 添加 Compose 相关库定义
  - _需求: 10.1_

- [x] 21.2 在 swallow 模块中启用 Compose


  - 在 build.gradle.kts 中启用 compose 构建特性
  - 配置 composeOptions
  - 添加 Compose 依赖
  - _需求: 10.1, 10.2_

- [x] 22. 创建 Compose 基础组件


- [x] 22.1 创建 LoadingContent Composable


  - 实现通用的加载状态组件
  - 支持 Loading、Error、Success 状态
  - _需求: 10.3_

- [x] 22.2 创建 ViewModel Compose 集成


  - 添加 hiltViewModel 扩展函数
  - 确保 StateFlow 可在 Compose 中使用
  - _需求: 10.3_

- [x] 22.3 创建 Navigation Compose 支持


  - 添加 navigation-compose 依赖
  - 创建导航辅助函数
  - _需求: 10.4_

- [x] 22.4 创建 Hilt Compose 集成


  - 添加 hilt-navigation-compose 依赖
  - 测试依赖注入在 Compose 中的工作
  - _需求: 10.5_

- [x] 23. 创建 Kotlin 扩展函数


- [x] 23.1 创建 Flow 扩展函数



  - 添加常用的 Flow 操作符扩展
  - 添加错误处理扩展
  - _需求: 7.4_

- [x] 23.2 创建 LiveData 扩展函数


  - 添加 LiveData 到 Flow 的转换
  - 添加观察辅助函数
  - _需求: 12.4_

- [x] 23.3 创建 Context 扩展函数



  - 添加常用的 Context 操作扩展
  - 优化现有扩展函数
  - _需求: 8.5, 12.6_

- [x] 23.4 创建 View 扩展函数


  - 添加 View 可见性扩展
  - 添加点击事件扩展
  - _需求: 12.6_

- [x] 24. 优化图片加载扩展



- [x] 24.1 创建 ImageView 扩展函数



  - 实现 loadImage 扩展函数
  - 支持占位符和错误图片
  - 支持圆角和圆形裁剪
  - _需求: 6.3_

- [x] 24.2 创建 Glide 配置扩展



  - 更新 GlideExtension
  - 添加常用的图片变换
  - _需求: 6.4_


## 阶段 6: 其他模块迁移

- [x] 25. 迁移 app 模块构建脚本


- [x] 25.1 迁移 app/build.gradle 到 Kotlin DSL


  - 创建 app/build.gradle.kts
  - 使用 Version Catalog 引用所有依赖
  - 配置 applicationId、versionCode、versionName
  - 配置签名
  - _需求: 9.4_

- [x] 25.2 更新 app 模块依赖


  - 将所有 kapt 改为 ksp
  - 更新测试依赖
  - _需求: 4.3_

- [x] 26. 迁移 base 模块构建脚本

- [x] 26.1 迁移 base/build.gradle 到 Kotlin DSL



  - 创建 base/build.gradle.kts
  - 使用 Version Catalog 引用依赖
  - 更新第三方库版本
  - _需求: 9.4_

- [x] 26.2 评估 base 模块依赖



  - 检查 PictureSelector 兼容性
  - 检查 BaseRecyclerViewAdapterHelper 兼容性
  - 检查 FlycoTabLayout 兼容性
  - 更新或替换不兼容的库
  - _需求: 13.4_

- [x] 27. 迁移 msc 模块构建脚本

- [x] 27.1 迁移 msc/build.gradle 到 Kotlin DSL


  - 创建 msc/build.gradle.kts
  - 使用 Version Catalog 引用依赖
  - 保持 native 库配置
  - _需求: 9.4_

- [x] 27.2 更新 msc 模块依赖



  - 升级 AndroidX 依赖
  - 确保与 swallow 模块兼容
  - _需求: 13.4_

- [x] 28. 更新 app 模块代码


- [x] 28.1 更新 MainActivity




  - 使用新的 BaseActivity API
  - 使用 by viewModels() 委托
  - 使用 repeatOnLifecycle 观察 Flow
  - _需求: 12.1, 12.4, 12.5_

- [x] 28.2 更新 MainViewModel


  - 使用新的 BaseViewModel API
  - 使用 StateFlow 和 SharedFlow
  - _需求: 12.4_

- [x] 28.3 更新 MainRepository


  - 使用新的 BaseRepository API
  - 返回 Flow<Result<T>>
  - _需求: 11.2, 11.5_

- [x] 28.4 更新 Glide 配置



  - 更新 AppGlideModule
  - 适配 Glide 5.x API
  - _需求: 6.2_

- [x] 28.5 更新权限请求代码


  - 使用 Activity Result API
  - 移除废弃的权限请求方法
  - _需求: 12.2_


## 阶段 7: 测试和验证

- [ ] 29. 编译验证
- [ ] 29.1 清理构建缓存
  - 执行 gradlew clean
  - 删除 .gradle 和 build 目录
  - _需求: 13.5_

- [ ] 29.2 执行完整构建
  - 执行 gradlew build
  - 解决所有编译错误
  - 解决所有编译警告
  - _需求: 13.5, 14.1_

- [ ] 29.3 检查依赖冲突
  - 执行 gradlew dependencies
  - 检查是否有版本冲突
  - 检查是否有重复的类
  - 使用依赖约束解决冲突
  - _需求: 13.1, 13.2, 13.3_

- [ ] 29.4 运行 Lint 检查
  - 执行 gradlew lint
  - 修复所有 Lint 错误
  - 评估并修复重要的 Lint 警告
  - _需求: 13.5_

- [ ] 30. 功能测试
- [ ] 30.1 测试应用启动
  - 在真机或模拟器上安装 app
  - 验证应用正常启动
  - 检查启动时间
  - _需求: 14.2, 17.2_

- [ ] 30.2 测试 MVVM 架构
  - 测试 Activity 和 ViewModel 交互
  - 测试数据绑定
  - 测试生命周期管理
  - _需求: 14.3, 14.8_

- [ ] 30.3 测试网络功能
  - 测试 Retrofit 网络请求
  - 测试错误处理
  - 测试超时处理
  - _需求: 14.4_

- [ ] 30.4 测试数据库功能
  - 测试 Room 数据库操作
  - 测试 CRUD 操作
  - 测试 Flow 查询
  - _需求: 14.5_

- [ ] 30.5 测试依赖注入
  - 测试 Hilt 注入
  - 测试 ViewModel 注入
  - 测试 Repository 注入
  - _需求: 14.6_

- [ ] 30.6 测试图片加载
  - 测试 Glide 图片加载
  - 测试占位符和错误图片
  - 测试缓存功能
  - _需求: 14.7_

- [ ] 30.7 测试权限处理
  - 测试权限请求
  - 测试权限回调
  - 测试 Android 14+ 权限
  - _需求: 2.4, 12.2_

- [ ] 31. 性能测试
- [ ] 31.1 测试编译性能
  - 记录 kapt 编译时间（作为基准）
  - 记录 KSP 编译时间
  - 计算性能提升百分比
  - 验证提升 ≥ 25%
  - _需求: 4.4, 17.1_

- [ ] 31.2 测试应用启动性能
  - 使用 Android Profiler 测量启动时间
  - 验证框架初始化时间 ≤ 100ms
  - 对比升级前后的启动时间
  - _需求: 17.2_

- [ ] 31.3 测试内存性能
  - 使用 Android Profiler 监控内存使用
  - 检查是否有内存泄漏
  - 对比升级前后的内存占用
  - _需求: 17.3_

- [ ] 31.4 测试网络性能
  - 测试网络请求响应时间
  - 对比升级前后的性能
  - _需求: 17.5_


## 阶段 8: 文档更新和发布准备

- [ ] 32. 更新 README.md
- [ ] 32.1 更新版本信息
  - 更新所有库的版本号
  - 更新 AGP、Gradle、Kotlin 版本
  - 更新 SDK 版本信息
  - _需求: 15.1_

- [ ] 32.2 添加 KSP 配置说明
  - 说明如何配置 KSP
  - 提供配置示例
  - 说明 KSP 的优势
  - _需求: 15.2_

- [ ] 32.3 添加 Kotlin DSL 配置示例
  - 提供完整的 build.gradle.kts 示例
  - 说明 Version Catalog 的使用
  - 提供常见配置示例
  - _需求: 15.3_

- [ ] 32.4 编写迁移指南
  - 列出所有破坏性变更
  - 提供迁移步骤
  - 提供代码对比示例
  - 说明最低要求（JDK 17, minSdk 24）
  - _需求: 15.4, 16.1, 16.2, 16.4, 16.5_

- [ ] 32.5 更新使用示例
  - 更新 BaseActivity 使用示例
  - 更新 BaseViewModel 使用示例
  - 更新 Repository 使用示例
  - 添加 Compose 使用示例
  - _需求: 15.1_

- [ ] 33. 更新技术栈文档
- [ ] 33.1 更新 .kiro/steering/tech.md
  - 更新所有版本号
  - 更新构建命令
  - 添加 KSP 说明
  - 添加 Version Catalog 说明
  - _需求: 15.5_

- [ ] 33.2 更新 .kiro/steering/structure.md
  - 更新架构说明（如有变化）
  - 更新代码组织模式
  - 添加 Compose 支持说明
  - _需求: 15.5_

- [ ] 34. 创建版本说明文档
- [ ] 34.1 创建 CHANGELOG.md
  - 列出所有新特性
  - 列出所有破坏性变更
  - 列出所有 bug 修复
  - 列出所有依赖升级
  - _需求: 16.1_

- [ ] 34.2 创建 MIGRATION_GUIDE.md
  - 详细的迁移步骤
  - 代码迁移示例
  - 常见问题解答
  - 故障排除指南
  - _需求: 16.2, 16.3, 16.4_

- [ ] 35. 代码质量检查
- [ ] 35.1 统计 Kotlin 代码比例
  - 使用工具统计代码行数
  - 计算 Kotlin 代码比例
  - 验证 ≥ 95%
  - _需求: 12.6_

- [ ] 35.2 代码格式化
  - 运行 Kotlin 代码格式化工具
  - 统一代码风格
  - 检查导入语句
  - _需求: 12.6_

- [ ] 35.3 添加 KDoc 注释
  - 为所有公开 API 添加 KDoc
  - 为复杂逻辑添加注释
  - 更新过时的注释
  - _需求: 12.6_

- [ ] 36. 最终验收检查
- [ ] 36.1 功能验收检查清单
  - ✓ 所有模块成功编译
  - ✓ app 模块成功运行
  - ✓ MVVM 架构正常工作
  - ✓ 网络请求正常
  - ✓ 数据库操作正常
  - ✓ 图片加载正常
  - ✓ 依赖注入正常
  - ✓ 生命周期管理正常
  - _需求: 14.1-14.8_

- [ ] 36.2 性能验收检查清单
  - ✓ KSP 编译速度提升 ≥ 25%
  - ✓ 应用启动时间 ≤ 原有时间
  - ✓ 内存占用 ≤ 原有水平
  - ✓ 网络请求响应时间 ≤ 原有时间
  - _需求: 17.1-17.5_

- [ ] 36.3 代码质量验收检查清单
  - ✓ Kotlin 代码比例 ≥ 95%
  - ✓ 无编译警告
  - ✓ 无 Lint 错误
  - ✓ 代码格式统一
  - _需求: 12.6, 13.5_

- [ ] 36.4 文档验收检查清单
  - ✓ README 更新完成
  - ✓ 迁移指南编写完成
  - ✓ API 文档更新完成
  - ✓ 版本说明编写完成
  - _需求: 15.1-15.5, 16.1-16.5_

## 总结

本任务列表包含 36 个主要任务，分为 8 个阶段：

1. **阶段 1**: 构建工具和配置升级（任务 1-5）
2. **阶段 2**: Swallow 模块构建脚本迁移（任务 6）
3. **阶段 3**: 依赖库升级和 KSP 迁移（任务 7-14）
4. **阶段 4**: 核心代码重构（任务 15-20）
5. **阶段 5**: Compose 支持和扩展功能（任务 21-24）
6. **阶段 6**: 其他模块迁移（任务 25-28）
7. **阶段 7**: 测试和验证（任务 29-31）
8. **阶段 8**: 文档更新和发布准备（任务 32-36）

每个任务都明确引用了相关的需求，确保完整覆盖所有升级目标。任务按照依赖关系排序，可以按顺序执行，也可以在某些阶段并行执行独立的任务。
