# Technology Stack

## Build System

- **Gradle**: 7.3.0
- **Android Gradle Plugin**: 7.3.0
- **Kotlin**: 1.8.10

## SDK Versions

- **compileSdk**: 33
- **minSdk**: 21
- **targetSdk**: 33
- **Java Version**: 1.8

## Core Libraries

### Architecture Components
- **Lifecycle**: 2.6.1 (LiveData, ViewModel, Runtime)
- **Paging**: 3.1.1
- **Room**: 2.5.1
- **Navigation**: 2.5.3

### Dependency Injection
- **Dagger Hilt**: 2.44

### Networking
- **Retrofit**: 2.9.0
- **OkHttp**: 4.3.0
- **Gson Converter**: 2.9.0
- **Retrofit URL Manager**: 1.4.0 (dynamic base URL switching)

### Asynchronous Programming
- **Kotlin Coroutines**: 1.6.4
- **Coroutines Android**: 1.6.4

### UI & Image Loading
- **Glide**: 4.11.0
- **Material Components**: 1.8.0
- **ViewBinding**: Enabled
- **ConstraintLayout**: 2.1.4

### Utilities
- **Timber**: 4.7.1 (logging)
- **ImmersionBar**: 3.0.0 (status bar)
- **EasyPermissions**: 3.0.0
- **UtilCodeX**: 1.31.0
- **MMKV**: 1.2.10 (key-value storage)
- **EventBus**: 3.2.0

### Routing
- **ARouter**: 1.5.0 (optional, removed in 1.0.3+)

### Third-Party Integrations
- **PictureSelector**: v2.7.2 (image picker)
- **BaseRecyclerViewAdapterHelper**: 3.0.4
- **FlycoTabLayout**: 2.1.2

## Common Commands

### Build
```bash
# Clean build
gradlew clean

# Build debug APK
gradlew assembleDebug

# Build release APK
gradlew assembleRelease

# Build all variants
gradlew build
```

### Testing
```bash
# Run unit tests
gradlew test

# Run instrumented tests
gradlew connectedAndroidTest
```

### Code Quality
```bash
# Run lint checks
gradlew lint

# Generate lint report
gradlew lintDebug
```

### Dependencies
```bash
# View dependency tree
gradlew dependencies

# Check for dependency updates
gradlew dependencyUpdates
```

## Annotation Processing

The project uses `kapt` for annotation processing:
- Hilt compiler
- Room compiler
- Glide compiler
- ARouter compiler (if used)

## ProGuard

- **Debug**: minifyEnabled = false
- **Release**: minifyEnabled = true (configurable per module)
- ProGuard rules defined in `proguard-rules.pro` files

## Multi-Dex

Enabled for applications with method count exceeding 64K limit.
