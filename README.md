## Gyps  基于Google 最新AAC架构的移动端开发框架

>The MVVM architecture mobile development framework based on the Kotlin language.Contains Retrofit、Coroutine-Flow、Dagger-Hilt、ViewBinding、Glide、ROOM、ARouter And Other commonly used library of tools.


[![Jitpack](https://jitpack.io/v/hpuhsp/gyps.svg)](https://jitpack.io/#hpuhsp/gyps)

### 主要技术实现

* Retrofit 网络请求
* 协程Coroutine及1.3.2发布后新增Flow库
* ViewBinding 视图绑定
* Hilt 依赖注入
* Glide V4版本 （Generated API使用）
* Room 数据库
* ARouter 路由框架（1.0.3版本后已移除，根据需要自行引入）
* immersionbar 状态栏定制
* easypermissions 权限处理
* ...


### Usage
#### 1、接入方式

1、直接将swallow模块作为Module引入。

2、添加远程依赖
 Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
 Add the dependency
```groovy
dependencies {
	        implementation 'com.github.hpuhsp:gyps:latestVersion' // latestVersion 替换为上方最新版本
	}
```

#### 2、配置build.gradle文件

* 项目根目录下build.gradle下引入Dagger Hilt插件，gradle工具版本推荐4.0+

```groovy
    // 1.5.0之前版本
    dependencies {
        classpath "com.android.tools.build:gradle:4.0.1"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        // Dagger-Hilt
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'
    }
	// 1.5.0之后版本
	plugins {
    ...
    id 'com.google.dagger.hilt.android' version '2.44' apply false
    ...
	}

```

* app或Module下build.gradle文件配置：

```groovy
// 1.5.0之前版本
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-android-extensions'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}
android {
    ...
    // ViewBinding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    //=========== Dagger Hilt 依赖注入 ============//
    implementation rootProject.ext.dependencies["hilt-android"]
    implementation rootProject.ext.dependencies["hilt-lifecycle-viewmodel"]
    kapt rootProject.ext.dependencies["hilt-android-compiler"]
    kapt rootProject.ext.dependencies["hilt-compiler"]
    // Glide
    kapt rootProject.ext.dependencies["glide-compiler"]
    ....
}
// 1.5.0之后版本示例
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'kotlin-parcelize'
    id 'com.google.dagger.hilt.android'
}
android {
    ...
    // ViewBinding
    buildFeatures {
        viewBinding  true
    }
}
dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    // ARouter
    kapt 'com.alibaba:arouter-compiler:1.2.2'
    // Room
    kapt 'androidx.room:room-compiler:2.5.1'
    // Glide
    implementation "com.github.bumptech.glide:okhttp3-integration:4.11.0"
    kapt 'com.github.bumptech.glide:compiler:4.11.0'
    // Hilt
    implementation 'com.google.dagger:hilt-android:2.44'
    kapt 'com.google.dagger:hilt-compiler:2.44'
    ...
}
```

#### 3、Dagger Hilt依赖注入

* 程序入口处添加@HiltAndroidApp注解

```kotlin
@HiltAndroidApp
class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        // 可根据编译环境选择日志打印策略
        initLogger(true)
    }
}
```

* 1.5.0之后版本由于Hilt版本变动，注解配置有所调整

```kotlin
// 1.5.0之后
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel() {

}
// 1.5.0之前版本
class MainViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    BaseViewModel() {

}
```

#### 4、MVVM模式下进行开发

![1b622821b9f386ba76924d2a3d551fe7.png](images/54432gg.png)
基于Dagger-Hilt依赖注入实现标准化、松耦合等特性MVVM架构下进行开发。如：Activity/Fragment 需要添加@AndroidEntryPoint注解。本地化存储及注入类全局初始化策略可以参考设备项目‘包名.app/’目录下具体实现类。功能页面开发构成方式可以参见Gyps项目主页健康上报功能实现方式。

#### 5、全局文件配置（可选）

![ac5806ded0961f148766fb21ad406b23.png](images/231134.jpg)

* AppLifecycleImpl生命周期实现类

```kotlin
@Singleton
class AppLifecycleImpl : AppLifecycle {
    override fun attachBaseContext(base: Context) {

    }

    override fun onCreate(application: Application) {
        // Module下应用级初始化操作
        // initARouter()
    }

    /**
     * 退出APP
     */
    override fun onTerminate(application: Application) {
    }
}
```

* GlobalConfiguration 全局配置文件+AndroidManifest中注册

```kotlin
@Singleton
class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
        // App域名、网络参数配置
        builder.baseurl(object : BaseUrl {
            override fun url(): HttpUrl? {
                return "http://www.github.com/".toHttpUrlOrNull()
            }
        })
        builder.globalHttpHandler(HttpHandlerImpl())
    }

    /**
     * 添加子Module的生命周期监听
     */
    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        lifecycleList.add(AppLifecycleImpl())
    }
}
```
```xml
// "包名"替换为项目实际包名
<meta-data
    android:name="{包名}.app.GlobalConfiguration"
    android:value="ConfigModule" />
```

* HttpHandlerImpl 网络请求参数配置及拦截处理

```kotlin
@Singleton
class HttpHandlerImpl : GlobalHttpHandler {

    override fun onHttpResultResponse(
        httpResult: String?,
        chain: Interceptor.Chain,
        response: Response
    ): Response {
        // 此处可以进行token校验并进行相关处理
        return response
    }

    override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        return chain.request().newBuilder()
            .header("Content-Type", "application/json;charset=UTF-8")
            .addHeader("platform", "Android")
            .addHeader("token","xxxxxxxxxxxxxxxxx")
            .addHeader("udid","231ddsad1421x2312sdmdsads22")
            .build()
    }

    /**
     * 动态切换域名访问
     */
    override fun redirectRequest(
        chain: Interceptor.Chain,
        request: Request,
        exception: Exception
    ): Response? {

        return null
    }
}
```

* Glide 图片加载配置

  * 引入方式

    推荐使用Generated API方式进行Glide的项目配置，为实现灵活性，此库已移除AppGlideModule及LibraryGlideModule实现，接入时请在app模块下进行AppGlideModule配置，需要注意两者的使用场景及区别。具体可参考

    [官方文档]: https://muyangmin.github.io/glide-docs-cn/doc/configuration.html#avoid-appglidemodule-in-libraries

    

  * 扩展配置

```kotlin
@GlideExtension
class MyGlideExtension private constructor() {
    companion object {
        private const val USER_AVATAR_SIZE = 168

        /**
         * 加载用户头像
         */
        @JvmStatic
        @GlideOption
        fun userAvatar(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
            return options
                .centerCrop()
                .override(USER_AVATAR_SIZE)
        }
    }
}
```
