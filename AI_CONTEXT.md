<HABITREK_PROJECT_CONTEXT>

<IDENTITY>
  App: HabiTrek
  Package: com.liftley.habitrek
  Platform: Android (Kotlin, Jetpack Compose, Material 3)
  Min SDK: 26
  Architecture: Clean Architecture (3 layers) + MVVM + Unidirectional Data Flow
  DI: Hilt (Dagger)
  Database: Room (SQLite)
  Networking: Retrofit + Gson (GNews API)
  Navigation: Jetpack Navigation 3 (NavDisplay, NavEntry)
  State: Kotlin StateFlow + MutableStateFlow
</IDENTITY>

<ARCHITECTURE_RULES>
  STRICT DEPENDENCY RULE:
    Presentation → Domain ← Data
    Domain depends on NOTHING.
    Presentation NEVER imports from data/.
    Data NEVER imports from presentation/.
  
  KNOWN VIOLATION (AS OF 2026-04-15):
    domain/repository/SearchRepository.kt imports presentation.featureWebSearch.model.SearchResult
    data/repository/SearchRepositoryImpl.kt imports presentation.featureWebSearch.model.ResultItem
    STATUS: Known, not yet fixed. Fix requires creating domain/model/SearchResult and domain/model/SearchResultItem, mapping in SearchRepositoryImpl.

  LAYER RESPONSIBILITIES:
    data/:    Room entities, DAOs, database, repository implementations, Retrofit API, entity↔domain mappers
    domain/:  Pure Kotlin data classes, repository interfaces, use cases (business logic)
    presentation/: Composable screens, ViewModels, UI state (sealed interfaces), UI models, UI model mappers
    core/:    Hilt DI modules, shared composables, theme, navigation primitives
</ARCHITECTURE_RULES>

<FILE_MAP>
  ROOT: app/src/main/java/com/liftley/habitrek/

  ENTRY_POINTS:
    HabiTrekApplication.kt          → @HiltAndroidApp Application class
    MainActivity.kt                  → @AndroidEntryPoint, calls setContent { HabiTrekNavHost() }

  DATA_LAYER:
    data/local/dao/HabitDao.kt       → @Dao: upsertHabit, getAllHabits (Flow), deleteHabit, getHabitWithId
    data/local/dao/CompletionDao.kt  → @Dao: addCompletion, getIdOfAllHabitsCompletedForDate (Flow), deleteCompletionWithIdAndDate, checkIfCompletionExistsWithHabitIdAndDate, getAllCompletionsForHabitWithId (Flow)
    data/local/entity/HabitEntity.kt → @Entity("habit_table"): id (PK auto), name, color (Long), durationMinutes
    data/local/entity/CompletionEntity.kt → @Entity("completions"): id (PK auto), habitId (FK→habit_table.id CASCADE), dateMillis
    data/local/entity/HabitEntityMapper.kt → Habit.toEntity(), HabitEntity.toDomain(), Flow<List<HabitEntity>>.toFlowListHabit()
    data/local/entity/CompletionEntityMapper.kt → Completion.toEntity(), Flow<List<CompletionEntity>>.toFlowCompletionList()
    data/local/database/HabitTrackerAppDatabase.kt → @Database(entities=[HabitEntity, CompletionEntity], version=1): habitDao(), completionDao()
    data/repository/HabitRepositoryImpl.kt → implements HabitRepository, @Singleton
    data/repository/CompletionRepositoryImpl.kt → implements CompletionRepository, @Singleton
    data/repository/SearchRepositoryImpl.kt → implements SearchRepository, @Singleton, calls SearchApi, API key hardcoded
    data/remote/api/SearchApi.kt → Retrofit interface (GNews /search endpoint), GNewsResponse, GNewsArticle DTOs
    data/remote/dto/SearchResultDto.kt → EMPTY (placeholder)
    data/remote/dto/SearchDtoMapper.kt → EMPTY (placeholder)

  DOMAIN_LAYER:
    domain/model/Habit.kt            → data class: id (Int=0), name (String), color (HabitColor=0UL), durationMinutes (Int=0). NOTE: NO isCompletedToday field.
    domain/model/HabitColor.kt       → data class: color (ULong)
    domain/model/Completion.kt       → data class: id (Int), habitId (Int), dateMillis (Long)
    domain/model/HabitListWithTodayStatusList.kt → data class: habits (List<Habit>), completedIdSet (Set<Int>). Used by GetHabitsWithTodayStatusUseCase.
    domain/model/HabitWithTodayStatus.kt → data class: completedTimestamps (Set<Long>), isCompletedToday (Boolean). Used by GetHabitCompletionsUseCase.
    domain/repository/HabitRepository.kt → interface: getAllHabits() Flow, upsertHabit(Habit), deleteHabit(Int), getHabitWithId(Int)
    domain/repository/CompletionRepository.kt → interface: getIdOfAllHabitsCompletedForDate(Long) Flow, addCompletion(Completion), checkIfCompletionExistsWithHabitIdAndDate(Int,Long), getAllCompletionsForHabitWithId(Int) Flow, deleteCompletionWithIdAndDate(Int,Long)
    domain/repository/SearchRepository.kt → interface: search(String): SearchResult [VIOLATION: imports from presentation]
    domain/usecase/CreateHabitUseCase.kt → suspend invoke(Habit), delegates to HabitRepository.upsertHabit
    domain/usecase/GetHabitsWithTodayStatusUseCase.kt → @Singleton, invoke(): Flow<HabitListWithTodayStatusList>, uses combine(allHabits, todayCompletions)
    domain/usecase/GetHabitCompletionsUseCase.kt → invoke(habitId): Flow<HabitWithTodayStatus>, maps completions to timestamps set + isCompletedToday
    domain/usecase/ToggleHabitCompletionUseCase.kt → suspend invoke(habitId, dateMillis), checks exists → delete or create

  PRESENTATION_LAYER:
    FEATURE: HOME
      presentation/featureHomeScreen/HomeScreen.kt → HomScreen composable (note: typo "HomScreen" is intentional/existing), uses when(val state = ...) on sealed HomeUiState
      presentation/featureHomeScreen/HomeViewModel.kt → @HiltViewModel, injects GetHabitsWithTodayStatusUseCase + ToggleHabitCompletionUseCase, uses .collect to update MutableStateFlow<HomeUiState>, Mutex for toggle
      presentation/featureHomeScreen/model/HomeUiState.kt → sealed interface: Loading (data object), Success(habits: List<HomeUiModel>), Error(message: String)
      presentation/featureHomeScreen/model/HomeUiModel.kt → data class: id, name, color (Compose Color), isCompletedToday, durationMinutes
      presentation/featureHomeScreen/model/HomeUiModelMapper.kt → HabitListWithTodayStatusList.toHomeUiModelList(), computes isCompletedToday = id in completedIdSet
      presentation/featureHomeScreen/components/HabitCard.kt → 3D tilt animation card with pointerInput + graphicsLayer
      presentation/featureHomeScreen/components/HabiTrekEmptyScreen.kt → centered butterfly icon + text

    FEATURE: ADD HABIT
      presentation/featureAddHabitScreen/AddHabitScreen.kt → form with name, duration, color picker, preview
      presentation/featureAddHabitScreen/AddHabitViewModel.kt → @HiltViewModel, injects CreateHabitUseCase, companion object habitPalette (List<ULong>), constructs domain Habit in createHabit()
      presentation/featureAddHabitScreen/model/AddHabitUiState.kt → data class: habitUiModel (AddHabitUiModel)
      presentation/featureAddHabitScreen/model/AddHabituiModel.kt → data class: id=0, name="", color=Color(0UL), durationMinutes=0
      presentation/featureAddHabitScreen/components/ColorBall.kt → color selector circle
      presentation/featureAddHabitScreen/components/HabitCardPreview.kt → preview card for habit creation

    FEATURE: REVIEW (HABIT DETAIL)
      presentation/featureReviewScreen/ReviewScreen.kt → detail screen with editable name, checkmark, calendar, duration/color editing, delete
      presentation/featureReviewScreen/ReviewViewModel.kt → @HiltViewModel(assistedFactory), @AssistedInject with habitId, injects HabitRepository + ToggleHabitCompletionUseCase + GetHabitCompletionsUseCase, debounced saves (700ms), Mutex for toggle, companion object habitPalette
      presentation/featureReviewScreen/model/ReviewUiState.kt → data class: habitCompletions (Set<Long>), habitUiModel (ReviewUiModel), currentYearMonth (YearMonth)
      presentation/featureReviewScreen/model/ReviewUiModel.kt → data class: id, name, color (Compose Color), isCompletedToday, durationMinutes
      presentation/featureReviewScreen/model/ReviewUiModelMapper.kt → Habit.toReviewUiModel() (isCompletedToday=false default), ReviewUiModel.toDomain()
      presentation/featureReviewScreen/components/SimpleCalendarGrid.kt → calendar grid composable
      presentation/featureReviewScreen/components/MetricCard.kt → stat display card

    FEATURE: WEB SEARCH
      presentation/featureWebSearch/SearchScreen.kt → search bar + results, called SearchScreen() (note: file is WebSearchScreen.kt but composable is SearchScreen)
      presentation/featureWebSearch/SearchViewModel.kt → @HiltViewModel, injects SearchRepository, separate _queryFlow for search text
      presentation/featureWebSearch/model/SearchUiState.kt → SearchScreenState sealed interface (Idle, Loading, Success, Error) + SearchResult data class + ResultItem data class + toSearchScreenState() mapper
      presentation/featureWebSearch/components/SearchResultItem.kt → result card composable

    NAVIGATION:
      presentation/navigation/HabiTrekNavHost.kt → root composable, Scaffold + NavDisplay, routes: Home, AddHabit, SearchScreen, ReviewHabit(habitId)

  CORE_LAYER:
    core/di/AppModule.kt → @Module: provideDatabase, providesHabitsDao, providesCompletionDao, provideRetrofit (gnews.io), provideSearchApi
    core/di/RepositoryModule.kt → @Module abstract: @Binds bindHabitRepository, bindCompletionRepository, bindSearchRepository
    core/ui/components/CheckMarkButton.kt → animated circular toggle button with scale + color animation
    core/ui/components/HabiTrekSurface.kt → reusable surface wrapper
    core/ui/components/HabiTrekSectionThumbnail.kt → icon thumbnail for section headers
    core/ui/components/ExpressiveIconButton.kt → styled icon button
    core/ui/components/HabiTrekAppTopBar.kt → dynamic top bar
    core/ui/components/HabiTrekNavigationBar.kt → floating bottom nav bar with WindowInsets support
    core/ui/components/HabiTrekFloatingActionButton.kt → FAB for adding habits
    core/ui/navigation/NavRoutes.kt → sealed interface: Home, AddHabit, ReviewHabit(habitId), SearchScreen
    core/ui/navigation/NavigationViewModel.kt → manages SnapshotStateList backstack
    core/theme/Theme.kt → LiftleyTheme with dynamic Material You colors
    core/theme/Color.kt, Type.kt, Shapes.kt → design tokens
</FILE_MAP>

<DATA_FLOW_PATTERNS>
  HOME_SCREEN_DATA_FLOW:
    1. Room emits Flow<List<HabitEntity>> from HabitDao.getAllHabits()
    2. HabitRepositoryImpl maps it via .toFlowListHabit() → Flow<List<Habit>>
    3. GetHabitsWithTodayStatusUseCase combines(allHabits, todayCompletedIds) → Flow<HabitListWithTodayStatusList>
    4. HomeViewModel .collect{} on this flow
    5. HomeUiModelMapper.toHomeUiModelList() maps domain → UI, computing isCompletedToday = habit.id in completedIdSet
    6. HomeViewModel emits HomeUiState.Success(habits)
    7. HomScreen composable reads via collectAsState()

  REVIEW_SCREEN_DATA_FLOW:
    1. ReviewViewModel init: fetches single Habit via habitRepository.getHabitWithId(habitId) → maps to ReviewUiModel
    2. ReviewViewModel init: starts collecting getHabitCompletionsUseCase(habitId) → Flow<HabitWithTodayStatus>
    3. Each emission updates habitCompletions (Set<Long>) and habitUiModel.isCompletedToday in ReviewUiState
    4. Edits (name/duration/color) update local MutableStateFlow immediately for responsive UI, then debounce 700ms before calling habitRepository.upsertHabit(reviewUiModel.toDomain())

  TOGGLE_COMPLETION_FLOW:
    1. User taps checkmark → ViewModel calls toggleHabitCompletionUseCase(habitId, dateMillis) inside Mutex.withLock
    2. UseCase checks completionRepository.checkIfCompletionExistsWithHabitIdAndDate()
    3. If exists → deleteCompletionWithIdAndDate(). If not → addCompletion(Completion(id=0, habitId, dateMillis))
    4. Room automatically emits updated data through existing Flow observers
    5. UI updates reactively through the collect chains

  COLOR_ENCODING:
    Storage: Compose Color.value (ULong, 64-bit) → .toLong() → stored in Room as Long
    Retrieval: Long → .toULong() → HabitColor(ULong) → Color(habitColor.color) in UI mapper
    Default Color: 0UL means "use system MaterialTheme.colorScheme.primary" — resolved at render time in composables, NOT stored as actual primary color value
    CRITICAL: Color(Long) constructor expects 32-bit ARGB. Color(value: ULong) constructor expects 64-bit Compose internal. Always use the ULong path.
</DATA_FLOW_PATTERNS>

<CRITICAL_PATTERNS>
  PATTERN: SEALED_UI_STATE
    HomeUiState: sealed interface with Loading (data object), Success(habits), Error(message)
    SearchScreenState: sealed interface with Idle, Loading, Success(searchResult), Error(error)
    ReviewUiState: currently a data class (NOT sealed). Potential future migration target.
    AddHabitUiState: currently a data class (NOT sealed). Simple form state, sealed not necessary.
    USAGE: when (val state = uiState) { is Success -> state.habits } — local val enables smart-cast.

  PATTERN: COMPANION_OBJECT_PALETTE
    Both AddHabitViewModel and ReviewViewModel define habitPalette as companion object val.
    Type: List<ULong> where index 0 = 0UL (system default), indices 1-14 = Color(0xFFXXXXXX).value
    Compared in UI via: habit.color == Color(colorULong)

  PATTERN: DEBOUNCED_SAVE
    Private Job variable (nameUpdateJob, colorUpdateJob, dateUpdateJob)
    On change: cancel previous Job, launch new with delay(700), then upsertHabit
    Ensures rapid keystrokes don't cause N database writes

  PATTERN: MUTEX_TOGGLE
    Private Mutex (clickMutex)
    Toggle function: viewModelScope.launch { clickMutex.withLock { useCase(...) } }
    Prevents double-tap race conditions

  PATTERN: ASSISTED_INJECTION
    ReviewViewModel uses @AssistedInject constructor with @Assisted habitId
    Inner @AssistedFactory interface with create(habitId: Int): ReviewViewModel
    @HiltViewModel(assistedFactory = ReviewViewModel.Factory::class)
    Created in composable via: hiltViewModel<ReviewViewModel, ReviewViewModel.Factory>(creationCallback = { factory -> factory.create(habitId) })

  PATTERN: ENTITY_MAPPER_EXTENSIONS
    Located in data/local/entity/ alongside entities
    Extension functions on both domain models (toEntity) and entities (toDomain)
    Flow mappers: Flow<List<Entity>>.toFlowDomainList() using .map { list -> list.map { it.toDomain() } }
</CRITICAL_PATTERNS>

<NAMING_CONVENTIONS>
  FILES:
    Composable screens: FeatureName + "Screen.kt" (e.g., HomeScreen.kt, ReviewScreen.kt)
    ViewModels: FeatureName + "ViewModel.kt"
    UI State: FeatureName + "UiState.kt"
    UI Model: FeatureName + "UiModel.kt"
    Mappers: FeatureName + "UiModelMapper.kt" (presentation) or EntityName + "EntityMapper.kt" (data)
    Composable components: PascalCase descriptive name (e.g., CheckMarkButton.kt, MetricCard.kt)
    Shared components: prefixed with "HabiTrek" (e.g., HabiTrekSurface, HabiTrekNavigationBar)

  FUNCTIONS:
    ViewModel actions: verbNoun (e.g., toggleHabitCompletion, updateHabitName, changeYearMonth)
    Mapper extensions: to + TargetType (e.g., toEntity, toDomain, toReviewUiModel, toHomeUiModelList)
    UseCase invoke: operator fun invoke(...) for callable-class syntax

  PACKAGES:
    Features: "feature" + ScreenName (e.g., featureHomeScreen, featureAddHabitScreen)
    Sub-packages: "components/" for composables, "model/" for UiState/UiModel/Mapper

  KNOWN TYPOS IN CODEBASE:
    HomScreen (missing 'e' in Home) — composable function name, intentional/existing
    AddHabituiModel.kt (lowercase 'u' in Ui) — filename
</NAMING_CONVENTIONS>

<KNOWN_ISSUES>
  1. SearchRepository imports presentation layer models (layer violation)
  2. SearchResultDto.kt and SearchDtoMapper.kt are empty placeholder files
  3. data/di/ directory exists but is empty (all DI is in core/di/)
  4. HomScreen typo in function name
  5. Color palette is duplicated in AddHabitViewModel.companion and ReviewViewModel.companion
  6. ReviewUiState is a data class, not a sealed interface (no Loading/Error handling)
</KNOWN_ISSUES>

<MODIFICATION_GUIDELINES>
  WHEN ADDING A NEW SCREEN:
    1. Create presentation/featureXxxScreen/ package
    2. Create XxxScreen.kt (composable), XxxViewModel.kt (@HiltViewModel)
    3. Create model/ sub-package with XxxUiState.kt (sealed interface), XxxUiModel.kt, XxxUiModelMapper.kt
    4. Add NavRoute to core/ui/navigation/NavRoutes.kt
    5. Add NavEntry case to presentation/navigation/HabiTrekNavHost.kt
    6. If new data needed: add to existing DAOs/Repositories or create new ones following existing pattern

  WHEN ADDING A NEW DATABASE TABLE:
    1. Create entity in data/local/entity/
    2. Create DAO in data/local/dao/
    3. Register both in HabitTrackerAppDatabase
    4. Create domain model in domain/model/
    5. Create repository interface in domain/repository/
    6. Create repository impl in data/repository/
    7. Create entity mapper in data/local/entity/
    8. Add @Binds in core/di/RepositoryModule
    9. Add @Provides for DAO in core/di/AppModule
    10. INCREMENT database version and add migration

  WHEN MODIFYING HABIT DATA MODEL:
    - Update HabitEntity (data layer) + migration
    - Update Habit (domain layer)
    - Update HabitEntityMapper (both directions)
    - Update ALL UI models that consume Habit: HomeUiModel, ReviewUiModel, AddHabitUiModel
    - Update ALL mappers: HomeUiModelMapper, ReviewUiModelMapper
    - Update ALL ViewModels that construct Habit objects: AddHabitViewModel.createHabit(), ReviewViewModel update functions

  CRITICAL DO-NOTS:
    - Do NOT put isCompletedToday in domain Habit.kt — it is computed, not persisted
    - Do NOT use Color(Long) constructor for Compose colors — use Color(value: ULong) or Color(0xFFXXXXXX)
    - Do NOT read StateFlow.value once and discard in a ViewModel — use .collect{} for continuous observation
    - Do NOT create CompletionEntity directly in a ViewModel — go through the UseCase/Repository
    - Do NOT store actual primary color value for "system default" — store 0UL and resolve at render time
</MODIFICATION_GUIDELINES>

</HABITREK_PROJECT_CONTEXT>
