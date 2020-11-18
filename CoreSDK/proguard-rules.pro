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
#=================================默认配置==========================================================#
#指定压缩级别，0-7之间，默认为5
-optimizationpasses 5
#包明不混合大小写,不使用大小写混合类名,windows平台必须指定
-dontusemixedcaseclassnames
#不跳过非公共的库类的成员
-dontskipnonpubliclibraryclassmembers
#不跳过非公共的库类,不去忽略非公共的库类,是否混淆第三方jar
-dontskipnonpubliclibraryclasses
#优化 不优化输入的类文件
-dontoptimize
#指定不执行预检,去掉预校验可以加快混淆速度
-dontpreverify
#混淆时是否记录日志
-verbose
#输出类名->混淆后类名的映射关系
-printmapping priguardMapping.txt
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
#JS调用原生的方法不能被混淆
-keepattributes *JavascriptInterface*
#不混淆Annotation
-keepattributes Annotation,InnerClasses
#不混淆泛型
-keepattributes Signature
#保留行号
-keepattributes SourceFile,LineNumberTable

#=================================通用配置(保持哪些类不被混淆)==========================================================#
-keep class android.** {*; }
-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
################ support ###############
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.**
################ androidX ###############
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
################ Kotlin ###############
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#=================================App框架通用常规混淆配置============================================#
-keep public class * implements com.hnsh.core.base.app.ConfigModule
#基类中反射用到的类
# keep the class and specified members from being removed or renamed
-keep class com.hnsh.core.base.view.BaseActivity { *; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class com.hnsh.core.base.view.BaseActivity { *; }

# keep the class and specified members from being renamed only
-keepnames class com.hnsh.core.base.view.BaseActivity { *; }

# keep the specified class members from being renamed only
-keepclassmembernames class com.hnsh.core.base.view.BaseActivity { *; }

#ViewBinding混淆
-keep public class * implements androidx.viewbinding.ViewBinding{*;}
# 基类包
-keep class com.hnsh.core.base.** { *; }
#自定义控件不参与混淆
-keep class com.hnsh.core.widget.** { *; }
#保留一个完整的包
-keep  class **.bean.* {
    *;
}

#保留一个完整的包
-keep  class **.model.* {
    *;
}
-keep  class **.models.* {
    *;
}

#保留一个完整的包
-keep  class **.entitys.* {
    *;
}

-keepattributes Signature
-keep class **.R$* {*;}
-ignorewarnings
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 保持native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 使用enum类型时需要注意避免以下两个方法混淆，因为enum类的特殊性，以下两个方法会被反射调用，
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#自定义View
-keep public class * extends android.view.View {
     public <init>(android.content.Context);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
     public void set*(...);
 }

-keep public class * extends android.view.View
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#################保留序列化###############
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
############### 删除Log ###############
-assumenosideeffects class android.util.Log { *; }
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
     public static *** i(...);
}
################annotation###############
-keep class android.support.annotation.** { *; }
-keep interface android.support.annotation.** { *; }
#保留核心网络库
-keep class com.hnsh.core.http.** {
    *;
}
################Timber#################
-dontwarn org.jetbrains.annotations.**
#================================= Retrofit =======================================================#
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

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
############ 依赖的okio ##############
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
#=================================== okhttp =======================================================#
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.ConscryptHostnameVerifier
#=================================ARouter==========================================================#
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

#=================================Glide============================================================#
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#=================================EventBus=========================================================#
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#==================================== Gson ========================================================#
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.sunloto.shandong.bean.** { *; }

#==================================== EasyPermission ==============================================#
-keepclassmembers class * {
    @pub.devrel.easypermissions.AfterPermissionGranted <methods>;
}