# Technology Stack

## Build System

- **Gradle**: Version Catalog (TOML) for centralized dependency management
- **Gradle Version**: 8.7.3 (AGP)
- **Kotlin**: 2.0.21
- **JDK**: 17 (toolchain)

## SDK Versions

- **compileSdk**: 35
- **targetSdk**: 35
- **minSdk**: 24

## Core Technologies

### Language & Runtime
- **Kotlin**: Primary language with coroutines support
- **Java**: 17 compatibility
- **Kotlin Compiler Options**: `-Xopt-in=kotlin.RequiresOptIn`

### Architecture Components
- **Lifecycle**: 2.8.7 (ViewModel, LiveData, Runtime KTX)
- **Navigation**: 2.8.5 (Fragment, UI, Compose)
- **Room**: 2.6.1 (Database ORM)
- **Paging**: 3.3.5 (Pagination support)
- **ViewBinding**: Enabled across all modules

### Dependency Injection
- **Dagger Hilt**: 2.57.2
- **Annotation Processing**: kapt (Hilt does not support KSP)

### Reactive Programming
- **Kotlin Coroutines**: 1.9.0
- **Flow**: For reactive streams
- **StateFlow/SharedFlow**: For UI state management

### Networking
- **Retrofit**: 2.11.0 (REST client)
- **OkHttp**: 4.12.0 (HTTP client)
- **Gson**: 2.11.0 (JSON serialization)
- **Retrofit URL Manager**: 1.4.0 (Dynamic base URL switching)

### Image Loading
- **Glide**: 5.0.5
- **Annotation Processing**: KSP (not kapt)
- **OkHttp Integration**: For network image loading

### UI Framework
- **Jetpack Compose**: 2024.12.01 BOM (optional support)
- **Compose Compiler**: 1.5.15
- **Material Design**: 1.12.0
- **ConstraintLayout**: 2.2.0
- **RecyclerView**: 1.3.2

### Local Storage
- **Room**: SQLite database abstraction
- **MMKV**: 2.2.4 (Key-value storage)
- **SharedPreferences**: Via custom wrappers

### Utilities
- **Timber**: 5.0.1 (Logging)
- **UtilCodeX**: 1.31.0 (Android utilities)
- **EventBus**: 3.3.1 (Event communication)
- **ImmersionBar**: 3.0.0 (Status bar customization)
- **EasyPermissions**: 3.0.0 (Runtime permissions)

### Third-Party UI
- **PictureSelector**: v2.7.2 (Image picker)
- **BaseRecyclerViewAdapterHelper**: 3.0.4
- **FlycoTabLayout**: 2.1.2
- **Material Dialogs**: 3.3.0

### Routing
- **ARouter**: 1.5.2 (Optional, removed from core in v1.0.3+)

### Testing
- **JUnit**: 4.13.2
- **Mockk**: 1.13.13
- **Espresso**: 3.6.1
- **Turbine**: 1.2.0 (Flow testing)
- **Coroutines Test**: 1.9.0

## Annotation Processing Strategy

- **KSP**: Room, Glide (faster compilation)
- **kapt**: Hilt, ARouter (required by these libraries)

## Common Commands

### Build & Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug on device
./gradlew installDebug

# Run app module
./gradlew :app:run
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run tests for specific module
./gradlew :swallow:test
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

### Dependencies
```bash
# Show dependency tree
./gradlew :app:dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

### Module-Specific
```bash
# Build swallow library
./gradlew :swallow:assembleRelease

# Build base library
./gradlew :base:assembleRelease
```

## Build Configuration Notes

- **ProGuard**: Disabled in debug, optional in release
- **MultiDex**: Enabled for app module
- **NDK**: arm64-v8a, armeabi-v7a filters
- **BuildConfig**: Enabled for modules requiring it
- **Signing**: Debug keystore configured via `debugsigning.properties`
