## Gyps  基于Google 最新AAC架构的移动端开发框架

>The MVVM architecture mobile development framework based on the Kotlin language.Contains Retrofit、Coroutine-Flow、Dagger-Hilt、ViewBinding、Glide、ROOM、ARouter And Other commonly used library of tools.

>全面支持androidX

### Technology
* Retrofit 网络请求
* 协程Coroutine及1.3.2发布后新增Flow库
* ViewBinding 视图绑定
* Dagger-Hilt 依赖注入
* Glide V4版本 （Generated API使用）
* Room 数据库
* ARouter 路由框架（1.0.3版本后已移除，根据需求引入）
* EventBus 事件处理
* immersionbar 状态栏定制
* easypermissions 权限处理
* ...


### Features
* 网络可动态配置，支持域名动态切换、自定义拦截处理及日志打印策略。
* 支持组件化方案，模块化动态配置。
* 根据kotlin语言特性，提供多种扩展函数。
* （呃...暂时就列这么多吧）

### Version

```
implementation 'com.fly:swallow:1.0.3'
```

### Usage

* Step1
1、定义GlobalConfiguration类，并在AndroidManifest.xml清单文件中注册
```
@Singleton
class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
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
```
<meta-data    android:name="com.swallow.gyps.app.GlobalConfiguration"    android:value="ConfigModule" />
```
2、创建AppLifecycleImpl类，绑定应用生命周期。
```
@Singleton
class AppLifecycleImpl : AppLifecycle {
    override fun attachBaseContext(base: Context) {
    }

    override fun onCreate(application: Application) {
    }

    /**
     * 退出APP
     */
    override fun onTerminate(application: Application) {
    }
}
```
3、创建HttpHandleImpl类，配置网络请求参数
```
@Singleton
class HttpHandlerImpl : GlobalHttpHandler {

    override fun onHttpResultResponse(
        httpResult: String?,
        chain: Interceptor.Chain,
        response: Response
    ): Response {
        return response
    }

    override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        return chain.request().newBuilder()
            .header("Content-Type", "application/json;charset=UTF-8")
            .addHeader("dataType", "json")
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
4、继承 BaseApplication 并添加@HiltAndroidApp 注解，如示例中MyApplication实现方式。

