# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Swallow Framework ProGuard Rules
# Version: 2.0.0
# Last Updated: 2024-12

#=====================================核心框架混淆配置================================================#

# 保留所有公开 API（标记为 @Stable 的类和方法）
-keep @com.swallow.fly.annotations.Stable class * { *; }
-keep class * {
    @com.swallow.fly.annotations.Stable *;
}

# 保留配置接口实现
-keep class * implements com.swallow.fly.base.lifecycle.config.FrameworkConfigProvider { *; }
-keep class * implements com.swallow.fly.base.lifecycle.config.ModuleConfigProvider { *; }

# 保留 Activity/Fragment 基类（反射和继承使用）
-keep public class * extends com.swallow.fly.base.ui.activity.BaseActivity { *; }
-keep public class * extends com.swallow.fly.base.ui.fragment.BaseFragment { *; }
-keep public class * extends com.swallow.fly.base.ui.fragment.BaseLazyFragment { *; }

# 保留 ViewModel 基类
-keep public class * extends com.swallow.fly.base.presentation.BaseViewModel { *; }

# 保留 Repository 基类
-keep public class * extends com.swallow.fly.base.data.BaseRepository { *; }
-keep public class * extends com.swallow.fly.base.data.BaseRepositoryBoth { *; }
-keep public class * extends com.swallow.fly.base.data.BaseRepositoryLocal { *; }
-keep public class * extends com.swallow.fly.base.data.BaseRepositoryRemote { *; }
-keep public class * extends com.swallow.fly.base.data.BaseRepositoryNothing { *; }

# 保留 UseCase 接口实现
-keep class * implements com.swallow.fly.domain.usecase.UseCase { *; }
-keep class * implements com.swallow.fly.domain.usecase.FlowUseCase { *; }

# 保留数据模型（用于序列化）
-keep class com.swallow.fly.domain.model.** { *; }
-keep class com.swallow.fly.http.result.** { *; }
-keep class com.swallow.fly.db.bean.** { *; }

# 保留 HTTP 相关接口和类
-keep interface com.swallow.fly.http.** { *; }
-keep class com.swallow.fly.http.manager.** { *; }
-keep class com.swallow.fly.http.engine.** { *; }

# 保留自定义 View
-keep public class com.swallow.fly.widget.** { *; }

# 保留扩展函数（可能被反射调用）
-keep class com.swallow.fly.ext.**Kt { *; }

# 保留数据类（通用规则）
-keep class **.bean.** { *; }
-keep class **.model.** { *; }
-keep class **.models.** { *; }
-keep class **.entity.** { *; }
-keep class **.entities.** { *; }

#=======================================三方库混淆配置================================================#
############ ViewBinding混淆 ##############
-keep public class * implements androidx.viewbinding.ViewBinding{*;}
############ Timber ##############
-dontwarn org.jetbrains.annotations.**

############ Retrofit ##############
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

############ 依赖的okio ##############
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

############ okhttp ##############
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.ConscryptHostnameVerifier

############ ARouter ##############
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

############ Glide ##############
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
############ EventBus ##############
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

############ Gson ##############
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.sunloto.shandong.bean.** { *; }

############ EasyPermission ##############
-keepclassmembers class * {
    @pub.devrel.easypermissions.AfterPermissionGranted <methods>;
}


#=====================================Hilt 混淆配置================================================#

# 保留 Hilt 生成的代码
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltComponents$* { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# 保留 @HiltViewModel 注解的类
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# 保留 Hilt 注入的构造函数
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

#=====================================Room 混淆配置================================================#

# 保留 Room 数据库
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }

# 保留 Room 实体
-keep @androidx.room.Entity class * { *; }
-keepclassmembers class * {
    @androidx.room.* *;
}

# 保留 Room DAO
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Dao class * { *; }

#=====================================Kotlin 协程混淆配置================================================#

# 保留协程相关类
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# 保留 Flow 相关
-keep class kotlinx.coroutines.flow.** { *; }
