# HabiTrek

**A premium, modern habit tracking Android application built with Jetpack Compose and Material 3.**

HabiTrek helps users build and maintain daily habits through a clean, interactive interface with 3D card animations, calendar-based streak tracking, and a built-in web search feature for habit-related content.

---

## Features

### 🏠 Home Screen
- **Habit List** — All habits displayed in a unified card block with custom rounded corners (top card gets large top rounding, bottom card gets large bottom rounding, middle cards get uniform rounding), creating a cohesive visual stack.
- **3D Tilt Animation** — Each habit card responds to touch with a real-time 3D tilt effect. Using `Animatable`, `graphicsLayer`, and `pointerInput`, the card tilts based on finger position relative to center, creating a tactile, premium feel.
- **Checkmark Toggle** — A circular checkmark button with animated color fill and scale-on-press animation. Toggling marks the habit as completed for today, syncing immediately with the local Room database.
- **Empty State** — When no habits exist, a centered butterfly icon with "No Habits Added Yet!" text provides a friendly, non-empty first impression.
- **Sealed UI State** — The screen uses a sealed interface (`Loading`, `Success`, `Error`) to cleanly handle all possible screen states without ambiguity.

### ➕ Add Habit Screen
- **Habit Name Input** — Outlined text field with keyboard dismiss on Done action.
- **Duration Input** — Numeric keyboard input with automatic validation (capped at 1440 minutes / 24 hours). A companion display box shows the duration converted to a human-readable "Xh Ym" format in real-time.
- **Color Picker** — Horizontally scrollable row of color balls. Users select a color to personalize their habit card. A "Use System Default Color Scheme" button resets to the device's Material You primary color.
- **Live Preview** — A preview card at the bottom shows exactly how the habit will appear on the Home Screen, updating in real-time as the user types.
- **Validation** — The "Create Habit" button is disabled until both name and duration are provided.

### 📊 Review Screen (Habit Detail)
- **Hero Header** — Editable habit name (inline `OutlinedTextField` with transparent borders) paired with a large checkmark button for today's completion toggle.
- **Metric Cards** — Two side-by-side cards displaying "Daily Goal" (duration) and "Total Days" (total completed days count).
- **Interactive Calendar** — A month-by-month calendar grid showing completed days with filled color circles. Users can navigate between months and tap individual days to toggle completion status retroactively.
- **Duration & Color Editing** — The same duration input and color picker UI from the Add screen, allowing users to modify habits after creation.
- **Debounced Saves** — Name, duration, and color changes are debounced (700ms delay) before persisting to the database, preventing excessive writes during rapid typing.
- **Delete Habit** — A destructive action requiring the user to type "delete" (case-insensitive) in a confirmation dialog before permanently removing the habit and all its completions (cascading delete via Room ForeignKey).
- **Assisted Injection** — The ReviewViewModel uses Hilt's `@AssistedInject` to receive the runtime `habitId` parameter from navigation, enabling proper ViewModel scoping per habit.

### 🔍 Web Search Screen
- **GNews API Integration** — Search for habit-related articles and content using the GNews API via Retrofit.
- **Search Bar** — Custom search bar with loading indicator that replaces the search icon during active requests.
- **Result Cards** — Search results displayed in the same rounded card block style as the home screen, maintaining visual consistency.
- **Sealed State** — Uses `Idle`, `Loading`, `Success`, and `Error` sealed states for clean UI handling.

### 🧭 Navigation
- **Navigation 3** — Uses Jetpack's latest Navigation 3 API (`NavDisplay`, `NavEntry`) with a custom `NavigationViewModel` managing an in-memory backstack.
- **Bottom Navigation Bar** — Floating bottom app bar with adaptive padding for both gesture and 3-button navigation modes using `WindowInsets`.
- **Floating Action Button** — Visible only on the Home screen for adding new habits.
- **Dynamic Top Bar** — Top bar content changes based on the current screen/route.

---

## Architecture

HabiTrek follows **Clean Architecture** with a strict unidirectional dependency rule:

```
Presentation → Domain ← Data
```

The Presentation layer depends on Domain. The Data layer depends on Domain. The Domain layer depends on nothing.

### Layer Breakdown

#### Data Layer (`data/`)
| Component | Purpose |
|---|---|
| `HabitDao` | Room DAO for habit CRUD operations |
| `CompletionDao` | Room DAO for completion records (marking days) |
| `HabitEntity` | Room entity for the `habit_table` |
| `CompletionEntity` | Room entity for the `completions` table with ForeignKey cascade |
| `HabitEntityMapper` | Extension functions: `Habit.toEntity()`, `HabitEntity.toDomain()`, `Flow<List<HabitEntity>>.toFlowListHabit()` |
| `CompletionEntityMapper` | Extension functions: `Completion.toEntity()`, `Flow<List<CompletionEntity>>.toFlowCompletionList()` |
| `HabitRepositoryImpl` | Implements domain `HabitRepository` interface, handles entity ↔ domain mapping |
| `CompletionRepositoryImpl` | Implements domain `CompletionRepository` interface |
| `SearchRepositoryImpl` | Implements domain `SearchRepository`, calls GNews API via Retrofit |
| `HabitTrackerAppDatabase` | Room database exposing `habitDao()` and `completionDao()` |
| `SearchApi` | Retrofit interface for GNews API |

#### Domain Layer (`domain/`)
| Component | Purpose |
|---|---|
| `Habit` | Core domain model — `id`, `name`, `color` (as `HabitColor`), `durationMinutes` |
| `HabitColor` | Value wrapper for color as `ULong` |
| `Completion` | Domain model — `id`, `habitId`, `dateMillis` |
| `HabitListWithTodayStatusList` | Bundles a list of habits with a set of completed habit IDs for today |
| `HabitWithTodayStatus` | Bundles completion timestamps with an `isCompletedToday` boolean |
| `HabitRepository` | Interface for habit CRUD |
| `CompletionRepository` | Interface for completion CRUD and queries |
| `SearchRepository` | Interface for web search |
| `GetHabitsWithTodayStatusUseCase` | Combines habits flow + today's completions flow into `HabitListWithTodayStatusList` |
| `GetHabitCompletionsUseCase` | Maps all completions for a habit into `HabitWithTodayStatus` |
| `ToggleHabitCompletionUseCase` | Checks if completion exists → deletes or creates accordingly |
| `CreateHabitUseCase` | Delegates habit creation to repository |

#### Presentation Layer (`presentation/`)
Each feature has its own package with `Screen`, `ViewModel`, `model/` (containing `UiState`, `UiModel`, `Mapper`).

| Feature | Key Components |
|---|---|
| `featureHomeScreen` | `HomScreen`, `HomeViewModel`, `HomeUiState` (sealed), `HomeUiModel`, `HomeUiModelMapper`, `HabitCard`, `HabiTrekEmptyScreen` |
| `featureAddHabitScreen` | `AddHabitScreen`, `AddHabitViewModel`, `AddHabitUiState`, `AddHabitUiModel`, `HabitCardPreview`, `ColorBall` |
| `featureReviewScreen` | `ReviewScreen`, `ReviewViewModel` (AssistedInject), `ReviewUiState`, `ReviewUiModel`, `ReviewUiModelMapper`, `SimpleCalendarGrid`, `MetricCard` |
| `featureWebSearch` | `SearchScreen`, `SearchViewModel`, `SearchScreenState` (sealed), `SearchResult`, `ResultItem`, `SearchResultItem` |
| `navigation` | `HabiTrekNavHost` — Navigation 3 host with `NavDisplay` |

#### Core Layer (`core/`)
| Component | Purpose |
|---|---|
| `di/AppModule` | Hilt module providing Room database, DAOs, Retrofit, and SearchApi |
| `di/RepositoryModule` | Hilt `@Binds` module mapping interfaces to implementations |
| `ui/components/` | Shared composables: `CheckMarkButton`, `HabiTrekSurface`, `HabiTrekSectionThumbnail`, `ExpressiveIconButton`, `HabiTrekNavigationBar`, `HabiTrekFloatingActionButton`, `HabiTrekAppTopBar` |
| `ui/navigation/` | `NavRoutes` (sealed interface), `NavigationViewModel` |
| `theme/` | Material 3 theme: `Color.kt`, `Type.kt`, `Shapes.kt`, `Theme.kt` |

---

## Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | Clean Architecture + MVVM + UDF (Unidirectional Data Flow) |
| **DI** | Hilt (Dagger) |
| **Database** | Room (SQLite) |
| **Networking** | Retrofit + Gson |
| **Navigation** | Jetpack Navigation 3 |
| **Async** | Kotlin Coroutines + Flow |
| **State Management** | StateFlow + MutableStateFlow |
| **Animations** | Animatable, animateColorAsState, animateFloatAsState, graphicsLayer |

---

## Database Schema

### `habit_table`
| Column | Type | Notes |
|---|---|---|
| `id` | `Int` | Primary key, auto-generated |
| `name` | `String` | Habit name |
| `color` | `Long` | Compose Color value stored as Long |
| `duration_minutes` | `Int` | Daily time allocation in minutes |

### `completions`
| Column | Type | Notes |
|---|---|---|
| `id` | `Int` | Primary key, auto-generated |
| `habitId` | `Int` | Foreign key → `habit_table.id` (CASCADE on delete) |
| `dateMillis` | `Long` | UTC midnight timestamp of the completed day |

---

## Color System

Colors are stored as Compose `Color.value` (`ULong`, 64-bit float representation). The app provides a palette of 15 preset colors. Index `0` represents "System Default" (stored as `0UL`), which resolves to `MaterialTheme.colorScheme.primary` at render time. The palette is defined as a `companion object` constant in both `AddHabitViewModel` and `ReviewViewModel`.

---

## Key Design Decisions

1. **`isCompletedToday` is NOT in the domain model.** It is a computed property that exists only in UI models. The domain `Habit` class contains only database-persisted fields. Completion status is computed by UseCases and injected during the domain → UI mapping step.

2. **Debounced persistence for editable fields.** Name, duration, and color changes in the ReviewScreen use a `Job?.cancel()` + `delay(700)` pattern to coalesce rapid user changes into a single database write.

3. **Mutex for toggle operations.** Both HomeViewModel and ReviewViewModel use a `Mutex` around toggle operations to prevent double-click race conditions where a rapid second tap could fire before the first database check completes.

4. **Navigation 3 with custom ViewModel-managed backstack.** Rather than using the older NavController, the app uses Navigation 3's `NavDisplay` with an `SnapshotStateList` backstack managed by a `NavigationViewModel`, enabling programmatic push/pop.

5. **Assisted Injection for ReviewViewModel.** The `habitId` is a runtime navigation argument, not a Hilt-managed dependency. `@AssistedInject` + `@AssistedFactory` bridges this gap cleanly.

---

## Building & Running

```bash
# Clone the repository
git clone <repository-url>

# Open in Android Studio (Ladybug or later recommended)
# Sync Gradle
# Run on emulator or physical device (API 26+)
```

---

## Package Structure

```
com.liftley.habitrek/
├── HabiTrekApplication.kt
├── MainActivity.kt
├── core/
│   ├── di/                          # Hilt modules
│   ├── theme/                       # Material 3 theme files
│   └── ui/
│       ├── components/              # Shared composables
│       └── navigation/              # NavRoutes, NavigationViewModel
├── data/
│   ├── di/                          # (empty, modules in core/di)
│   ├── local/
│   │   ├── dao/                     # HabitDao, CompletionDao
│   │   ├── database/                # RoomDatabase
│   │   └── entity/                  # Entities + Mappers
│   ├── remote/
│   │   ├── api/                     # SearchApi + GNews DTOs
│   │   └── dto/                     # (placeholder for future DTOs)
│   └── repository/                  # Repository implementations
├── domain/
│   ├── model/                       # Habit, Completion, HabitColor, etc.
│   ├── repository/                  # Repository interfaces
│   └── usecase/                     # Business logic use cases
└── presentation/
    ├── featureAddHabitScreen/
    │   ├── components/              # ColorBall, HabitCardPreview
    │   └── model/                   # AddHabitUiState, AddHabitUiModel
    ├── featureHomeScreen/
    │   ├── components/              # HabitCard, HabiTrekEmptyScreen
    │   └── model/                   # HomeUiState, HomeUiModel, Mapper
    ├── featureReviewScreen/
    │   ├── components/              # SimpleCalendarGrid, MetricCard
    │   └── model/                   # ReviewUiState, ReviewUiModel, Mapper
    ├── featureWebSearch/
    │   ├── components/              # SearchResultItem
    │   └── model/                   # SearchScreenState, SearchResult
    ├── navigation/                  # HabiTrekNavHost
    └── util/                        # TimeUtils
```
