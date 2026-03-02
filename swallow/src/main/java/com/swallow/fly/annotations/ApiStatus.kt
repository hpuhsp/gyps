package com.swallow.fly.annotations

/**
 * API 稳定性标记
 * 
 * 标记为 Stable 的 API 保证在主版本内不会有破坏性变更
 * 
 * @since 2.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS
)
annotation class Stable

/**
 * 实验性 API 标记
 * 
 * 标记为实验性的 API 可能会在未来版本中变更或移除
 * 使用时会产生编译警告
 * 
 * 使用示例：
 * ```kotlin
 * @OptIn(ExperimentalSwallowApi::class)
 * fun useExperimentalFeature() {
 *     DeepLinkNavigator.navigate(...)
 * }
 * ```
 * 
 * @since 2.0.0
 */
@RequiresOptIn(
    message = "This API is experimental and may change in future versions without notice",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS
)
annotation class ExperimentalSwallowApi

/**
 * 内部 API 标记
 * 
 * 标记为内部的 API 仅供框架内部使用，不应被外部调用
 * 使用时会产生编译错误
 * 
 * @since 2.0.0
 */
@RequiresOptIn(
    message = "This is an internal Swallow API and should not be used outside of the framework. " +
            "It may be changed or removed without notice.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS
)
annotation class InternalSwallowApi

/**
 * 计划移除标记
 * 
 * 标记计划在指定版本移除的 API
 * 应与 @Deprecated 一起使用
 * 
 * @param version 计划移除的版本号
 * @param replaceWith 替代方案说明
 * 
 * @since 2.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS
)
annotation class ScheduledForRemoval(
    val version: String,
    val replaceWith: String = ""
)
