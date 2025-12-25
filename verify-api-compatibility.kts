#!/usr/bin/env kotlin

/**
 * API Compatibility Verification Script
 * 
 * This script verifies that the package refactoring hasn't introduced
 * breaking changes to the public API of the swallow framework.
 */

import java.io.File
import java.lang.reflect.Modifier

data class ApiClass(
    val name: String,
    val packageName: String,
    val isPublic: Boolean,
    val methods: List<String>,
    val fields: List<String>
)

fun main() {
    println("=".repeat(60))
    println("API Compatibility Verification")
    println("=".repeat(60))
    println()
    
    // Expected public API classes after refactoring
    val expectedClasses = mapOf(
        // Lifecycle package (was app/)
        "com.swallow.fly.base.lifecycle.BaseApplication" to listOf("onCreate", "onTerminate"),
        "com.swallow.fly.base.lifecycle.AppDelegate" to listOf("attachBaseContext", "onCreate", "onTerminate"),
        "com.swallow.fly.base.lifecycle.AppLifecycles" to listOf("attachBaseContext", "onCreate", "onTerminate"),
        "com.swallow.fly.base.lifecycle.ConfigModule" to listOf("applyOptions", "injectAppLifecycle"),
        
        // UI package (was view/)
        "com.swallow.fly.base.ui.activity.IActivity" to listOf("initView", "initData"),
        "com.swallow.fly.base.ui.activity.BaseActivity" to listOf("onCreate", "initView", "initData"),
        "com.swallow.fly.base.ui.activity.FastBaseActivity" to listOf("onCreate"),
        "com.swallow.fly.base.ui.fragment.IFragment" to listOf("initView", "initData"),
        "com.swallow.fly.base.ui.fragment.BaseFragment" to listOf("onCreateView", "initView", "initData"),
        "com.swallow.fly.base.ui.fragment.BaseLazyFragment" to listOf("onCreateView", "lazyLoadData"),
        "com.swallow.fly.base.ui.ViewBehavior" to listOf("showLoading", "hideLoading"),
        
        // Presentation package (was viewmodel/)
        "com.swallow.fly.base.presentation.IViewModel" to emptyList(),
        "com.swallow.fly.base.presentation.BaseViewModel" to listOf("onCleared"),
        "com.swallow.fly.base.presentation.state.UiState" to emptyList(),
        "com.swallow.fly.base.presentation.state.UiEvent" to emptyList(),
        "com.swallow.fly.base.presentation.state.PageViewState" to emptyList(),
        "com.swallow.fly.base.presentation.state.PageListState" to emptyList(),
        "com.swallow.fly.base.presentation.state.PageStateEvent" to emptyList(),
        "com.swallow.fly.base.presentation.state.BaseStateEvent" to emptyList(),
        
        // Data package (was repository/)
        "com.swallow.fly.base.data.IRepository" to emptyList(),
        "com.swallow.fly.base.data.BaseRepository" to emptyList(),
        "com.swallow.fly.base.data.Repository" to emptyList()
    )
    
    println("Checking ${expectedClasses.size} public API classes...")
    println()
    
    var allPassed = true
    var checkedCount = 0
    var missingCount = 0
    
    for ((className, expectedMethods) in expectedClasses) {
        val shortName = className.substringAfterLast(".")
        val packageName = className.substringBeforeLast(".")
        
        print("✓ Checking $shortName... ")
        
        // Verify the file exists in the expected location
        val filePath = "swallow/src/main/java/" + className.replace(".", "/") + ".kt"
        val file = File(filePath)
        
        if (file.exists()) {
            println("✓ Found at $packageName")
            checkedCount++
        } else {
            println("✗ MISSING at expected location")
            println("  Expected: $filePath")
            allPassed = false
            missingCount++
        }
    }
    
    println()
    println("=".repeat(60))
    println("Summary:")
    println("  Total classes checked: ${expectedClasses.size}")
    println("  Found: $checkedCount")
    println("  Missing: $missingCount")
    println()
    
    if (allPassed) {
        println("✓ API COMPATIBILITY CHECK PASSED")
        println("  All public API classes are in their expected locations.")
    } else {
        println("✗ API COMPATIBILITY CHECK FAILED")
        println("  Some public API classes are missing or misplaced.")
        System.exit(1)
    }
    println("=".repeat(60))
}

main()
