# Project Structure

## Multi-Module Architecture

The project follows a modular architecture with clear separation of concerns:

```
Gyps/
├── app/                    # Main application module
├── swallow/                # Core framework library (MVVM base classes)
├── base/                   # UI resources and widgets library
├── msc/                    # iFlytek voice SDK integration module
└── buildScripts/           # Build configuration scripts
```

## Module Responsibilities

### app/
Main application module containing:
- Application entry point (`MyApplication`)
- Feature implementations (main, test, msc integrations)
- App-level configuration (`GlobalConfiguration`, `AppLifecycleImpl`, `HttpHandlerImpl`)
- Glide configuration (`GypsGlideModule`, `AppGlideExtension`)

**Package Structure:**
```
com.swallow.gyps/
├── app/                    # App lifecycle and global config
├── common/                 # App-level common utilities
├── glide/                  # Glide configuration
├── main/                   # Main feature (Activity, ViewModel, Repository)
├── msc/                    # Voice input features
├── service/                # Background services
└── test/                   # Test/demo features
```

### swallow/
Core framework library providing MVVM foundation:
- Base classes for Activity, Fragment, ViewModel
- Repository pattern implementation
- HTTP client configuration with Retrofit/OkHttp
- Dependency injection setup with Hilt
- Database abstraction with Room
- Extension functions and utilities

**Package Structure:**
```
com.swallow.fly/
├── base/
│   ├── app/                # Application component, lifecycle, config
│   ├── view/               # BaseActivity, BaseFragment, BaseLazyFragment
│   ├── viewmodel/          # BaseViewModel, IViewModel
│   ├── viewstate/          # ViewState patterns
│   ├── repository/         # Repository interfaces and base classes
│   └── event/              # Event handling
├── db/                     # Room database setup
├── ext/                    # Kotlin extensions
├── http/                   # Networking layer (Retrofit, interceptors)
├── service/                # Framework services
├── utils/                  # Utility classes
└── widget/                 # Custom widgets
```

### base/
UI resources and reusable components:
- Custom adapters (BaseVBAdapter with ViewBinding)
- Custom dialogs and widgets
- Common layouts and drawables
- Animation resources
- Extension functions for UI components

**Package Structure:**
```
com.hsp.resource/
├── adapter/                # RecyclerView adapters
├── dialog/                 # Custom dialogs
├── ext/                    # UI extensions
├── router/                 # Routing utilities
└── widget/                 # Custom views (BadgeView, SignatureView, etc.)
```

### msc/
iFlytek voice recognition SDK integration:
- Native libraries (.so files for ARM architectures)
- Voice recognition assets
- MSC SDK wrapper (Msc.jar)

## Code Organization Patterns

### MVVM Pattern
Each feature follows this structure:
```
feature/
├── FeatureActivity.kt      # View layer with ViewBinding
├── FeatureViewModel.kt     # ViewModel with business logic
├── FeatureRepository.kt    # Data layer
└── models/                 # Data models for the feature
    └── FeatureModel.kt
```

### Naming Conventions

**Files:**
- Activities: `*Activity.kt` (e.g., `MainActivity.kt`)
- Fragments: `*Fragment.kt`
- ViewModels: `*ViewModel.kt`
- Repositories: `*Repository.kt`
- Services: `*Service.kt`
- Models/Entities: `*Model.kt`, `*Entity.kt`

**Classes:**
- Base classes: `Base*` prefix
- Interfaces: `I*` prefix
- Extensions: `*Ext.kt` suffix

**Resources:**
- Layouts: `activity_*.xml`, `fragment_*.xml`, `dialog_*.xml`, `item_*.xml`
- Drawables: `ic_*` (icons), `bg_*` (backgrounds), `shape_*` (shapes)
- Colors: Defined in `values/colors.xml`
- Strings: Defined in `values/strings.xml`

### Dependency Flow
```
app → base → swallow
app → msc
```

- `app` depends on `base` and `msc`
- `base` depends on `swallow`
- `swallow` is the foundation with no internal dependencies

## Resource Organization

### Layouts
- Organized by type in `res/layout/`
- Include files: `include_*.xml`
- Item layouts: `item_*.xml`

### Drawables
- Multiple density folders: hdpi, xhdpi, xxhdpi, xxxhdpi
- Vector drawables in `drawable/`
- Bitmap resources in density-specific folders

### Animations
- Activity transitions in `res/anim/`
- Standard animations: fade, slide, translate

## Configuration Files

- **Global config**: `app/src/main/java/*/app/GlobalConfiguration.kt`
- **HTTP config**: `app/src/main/java/*/app/HttpHandlerImpl.kt`
- **Lifecycle**: `app/src/main/java/*/app/AppLifecycleImpl.kt`
- **Manifest meta-data**: Register `GlobalConfiguration` in `AndroidManifest.xml`

## Testing Structure

- Unit tests: `src/test/java/`
- Instrumented tests: `src/androidTest/java/`
- Test naming: `*Test.kt` (e.g., `ExampleUnitTest.kt`)
