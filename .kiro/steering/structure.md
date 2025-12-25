# Project Structure

## Module Organization

The project follows a multi-module architecture with clear separation of concerns:

```
gyps/
├── app/                    # Sample application module
├── swallow/                # Core framework library
├── base/                   # UI resources and common widgets
├── msc/                    # Voice input SDK integration
├── gradle/                 # Gradle wrapper and version catalog
└── buildScripts/           # Build configuration scripts
```

## Module Dependencies

```
app → base → swallow
app → msc
```

- **app** depends on **base** and **msc**
- **base** depends on **swallow**
- **swallow** is the foundation module with no internal dependencies

## Module Details

### swallow (Core Framework)

**Purpose**: Foundation library containing base classes, networking, database, and utilities

**Package Structure**:
```
com.swallow.fly/
├── base/
│   ├── lifecycle/          # Application lifecycle and configuration (formerly app/)
│   │   ├── config/         # Global configuration builders
│   │   └── parse/          # Data parsing utilities
│   ├── ui/                 # UI layer components (formerly view/)
│   │   ├── activity/       # Activity base classes and interfaces
│   │   ├── fragment/       # Fragment base classes and interfaces
│   │   └── ViewBehavior.kt # UI behavior interface
│   ├── presentation/       # Presentation layer (formerly viewmodel/)
│   │   ├── BaseViewModel.kt
│   │   ├── IViewModel.kt
│   │   └── state/          # State management (merged viewmodel, viewstate, event)
│   │       ├── UiState.kt
│   │       ├── UiEvent.kt
│   │       ├── PageViewState.kt
│   │       ├── PageListState.kt
│   │       ├── PageStateEvent.kt
│   │       └── BaseStateEvent.kt  # @Deprecated
│   └── data/               # Data layer (formerly repository/)
│       ├── IRepository.kt
│       ├── BaseRepository.kt
│       └── Repository.kt
├── compose/                # Jetpack Compose utilities
├── db/                     # Room database configuration
│   ├── bean/               # Database entities
│   └── dao/                # Data access objects
├── ext/                    # Kotlin extension functions
│   ├── paging/             # Paging extensions
│   └── prefs/              # SharedPreferences extensions
├── glide/                  # Glide configuration
├── http/                   # Networking layer
│   ├── cache/              # HTTP caching
│   ├── di/                 # Network DI modules
│   ├── exception/          # Error handling
│   ├── interceptor/        # OkHttp interceptors
│   ├── manager/            # Network managers
│   ├── printer/            # Request/response logging
│   └── result/             # API response wrappers
├── image/                  # Image loading configuration
├── log/                    # Logging configuration
├── service/                # Background services
├── utils/                  # Utility classes
└── widget/                 # Custom views
```

**Key Classes**:
- `BaseActivity<VM, VB>`: Activity base with ViewModel and ViewBinding (in `ui.activity`)
- `BaseFragment<VM, VB>`: Fragment base with ViewModel and ViewBinding (in `ui.fragment`)
- `BaseViewModel`: ViewModel with StateFlow/SharedFlow support (in `presentation`)
- `BaseRepository`: Repository pattern implementation (in `data`)
- `BaseApplication`: Application class with lifecycle hooks (in `lifecycle`)

**Package Responsibilities**:

- **lifecycle/**: Manages application lifecycle, initialization, and framework configuration
  - Contains `BaseApplication`, `AppDelegate`, `AppLifecycles`, configuration modules
  - Replaces the former `app/` package with a more descriptive name

- **ui/**: UI layer components following MVVM architecture
  - `activity/`: Activity interfaces (`IActivity`) and base classes (`BaseActivity`, `FastBaseActivity`)
  - `fragment/`: Fragment interfaces (`IFragment`) and base classes (`BaseFragment`, `BaseLazyFragment`)
  - `ViewBehavior`: Common UI behavior interface
  - Replaces the former `view/` package with clearer organization

- **presentation/**: Presentation layer managing UI state and business logic
  - Contains `BaseViewModel` and `IViewModel`
  - `state/`: Unified state management (modern `UiState`/`UiEvent` and legacy `BaseStateEvent`)
  - Replaces the former `viewmodel/`, `viewstate/`, and `event/` packages
  - Follows standard MVVM naming conventions

- **data/**: Data layer providing data access abstractions
  - Contains `BaseRepository`, `IRepository`, and `Repository`
  - Replaces the former `repository/` package with standard Clean Architecture naming

### Package Migration Reference

The base package structure was refactored to improve clarity and follow standard architecture naming conventions. Use this table when updating imports:

| Old Package | New Package | Notes |
|-------------|-------------|-------|
| `com.swallow.fly.base.app` | `com.swallow.fly.base.lifecycle` | More descriptive of lifecycle management purpose |
| `com.swallow.fly.base.app.config` | `com.swallow.fly.base.lifecycle.config` | Moved with parent package |
| `com.swallow.fly.base.app.parse` | `com.swallow.fly.base.lifecycle.parse` | Moved with parent package |
| `com.swallow.fly.base.view` | `com.swallow.fly.base.ui.activity` or `com.swallow.fly.base.ui.fragment` | Split by component type |
| `com.swallow.fly.base.viewmodel` | `com.swallow.fly.base.presentation` | Standard MVVM naming |
| `com.swallow.fly.base.viewstate` | `com.swallow.fly.base.presentation.state` | Unified state management |
| `com.swallow.fly.base.event` | `com.swallow.fly.base.presentation.state` | Merged into state package |
| `com.swallow.fly.base.repository` | `com.swallow.fly.base.data` | Standard Clean Architecture naming |

**Import Update Examples**:

```kotlin
// Old imports
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.base.repository.BaseRepository
import com.swallow.fly.base.app.BaseApplication

// New imports
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.data.BaseRepository
import com.swallow.fly.base.lifecycle.BaseApplication
```

### base (UI Resources)

**Purpose**: Reusable UI components, adapters, and resource files

**Package Structure**:
```
com.hsp.resource/
├── adapter/                # RecyclerView adapters
│   ├── BaseVBAdapter       # ViewBinding adapter base
│   └── VBViewHolder        # ViewBinding ViewHolder
├── dialog/                 # Custom dialogs
├── ext/                    # UI extension functions
├── router/                 # Navigation utilities
└── widget/                 # Custom widgets
    └── drop/               # Drag-and-drop components
```

**Resources**:
- Animations (`anim/`)
- Color selectors (`color/`)
- Drawable shapes and backgrounds (`drawable/`)
- Common layouts (`layout/`)
- Styles and themes (`values/`)

### msc (Voice Input)

**Purpose**: iFlytek MSC SDK integration for voice recognition

**Structure**:
- Native libraries in `jniLibs/` (arm64-v8a, armeabi-v7a)
- SDK JAR in `libs/Msc.jar`
- Assets for voice recognition models

### app (Sample Application)

**Purpose**: Demonstrates framework usage and serves as integration testing

**Package Structure**:
```
com.swallow.gyps/
├── app/                    # Application configuration
│   ├── AppLifecycleImpl    # Lifecycle implementation
│   ├── GlobalConfiguration # Global config module
│   └── HttpHandlerImpl     # HTTP interceptor implementation
├── common/                 # Common utilities
├── glide/                  # Glide module configuration
├── main/                   # Main feature
│   ├── models/             # Data models
│   ├── MainActivity        # Main screen
│   ├── MainRepository      # Data layer
│   └── MainViewModel       # Presentation layer
├── msc/                    # Voice input features
│   └── viewmodel/          # ViewModels
├── service/                # App services
├── test/                   # Test/demo features
└── widget/                 # App-specific widgets
```

## Architecture Patterns

### MVVM Structure

Each feature follows this structure with the refactored base packages:
```
feature/
├── FeatureActivity.kt      # View layer (extends base.ui.activity.BaseActivity)
├── FeatureViewModel.kt     # ViewModel layer (extends base.presentation.BaseViewModel)
├── FeatureRepository.kt    # Repository layer (extends base.data.BaseRepository)
└── models/
    └── FeatureModel.kt     # Data models
```

**Architecture Layers**:
- **UI Layer** (`base.ui`): Activities and Fragments handle user interaction
- **Presentation Layer** (`base.presentation`): ViewModels manage UI state and business logic
- **Data Layer** (`base.data`): Repositories provide data access abstraction

### Dependency Injection

- `@HiltAndroidApp` on Application class
- `@AndroidEntryPoint` on Activities/Fragments
- `@HiltViewModel` on ViewModels
- `@Inject` constructor for dependencies
- Module classes for providing dependencies

### ViewBinding Pattern

```kotlin
// Using the refactored base packages
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.base.presentation.BaseViewModel

class MyActivity : BaseActivity<MyViewModel, ActivityMyBinding>() {
    override val viewModel: MyViewModel by viewModels()
    override val bindingInflater: (LayoutInflater) -> ActivityMyBinding
        get() = ActivityMyBinding::inflate
    
    // Access views via binding.viewId
}
```

### StateFlow/SharedFlow Pattern

```kotlin
// Using the refactored base packages
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.base.presentation.state.UiEvent

class MyViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
}
```

## Resource Organization

### Naming Conventions

**Layouts**: `activity_*.xml`, `fragment_*.xml`, `item_*.xml`, `dialog_*.xml`

**Drawables**: 
- Shapes: `shape_*.xml`, `bg_*.xml`
- Selectors: `*_selector.xml`
- Icons: `ic_*.png/xml`

**Colors**: Descriptive names in `colors.xml` (e.g., `toolbar_blue`, `text_gray`)

**Strings**: Feature-prefixed keys (e.g., `main_title`, `login_button`)

**Dimensions**: `dimens.xml` for reusable spacing values

### Density-Specific Resources

- `drawable-hdpi/`, `drawable-xhdpi/`, `drawable-xxhdpi/`, `drawable-xxxhdpi/`
- `mipmap-*dpi/` for launcher icons

## Configuration Files

- **gradle/libs.versions.toml**: Centralized dependency versions
- **build.gradle.kts**: Root project configuration
- **settings.gradle.kts**: Module inclusion and repository configuration
- **local.properties**: Local SDK paths (not in version control)
- **gradle.properties**: Gradle JVM settings and Android build properties

## Code Organization Principles

1. **Package by Feature**: Group related classes by feature, not by type
2. **Single Responsibility**: Each class has one clear purpose
3. **Dependency Rule**: Dependencies point inward (View → ViewModel → Repository)
4. **Separation of Concerns**: UI, business logic, and data access are separate
5. **Reusability**: Common code in `swallow`, UI components in `base`
