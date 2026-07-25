# BAKENYE KIDS PLATFORM
## MVP Implementation Sprint Plan (MISP)
### Engineering Build Order, Backlog & First User Experience Delivery Roadmap

---

## EXECUTIVE DIRECTIVE & BUILD OBJECTIVE

The **Bakenye Kids MVP Implementation Sprint Plan (MISP)** is the actionable engineering roadmap for delivering the first working, beautiful, and testable Android application into the hands of children, parents, and cultural educators.

The primary objective of this sprint plan is to get a **Minimum Lovable Product (MLP)** running reliably on device. The focus is strictly on **execution, speed to user testing, delightful child UX, and rock-solid offline performance**.

---

## 1. MINIMUM LOVABLE PRODUCT (MLP) SCOPE

To deliver a product that is not just functional but genuinely delightful and engaging for children, features are categorized into strict priority buckets:

```
┌─────────────────────────────────────────────────────────────────┐
│                   MINIMUM LOVABLE PRODUCT SCOPE                 │
├───────────────────┬──────────────────────┬──────────────────────┤
│ MUST HAVE (MLP)   │ SHOULD HAVE          │ LATER (Post-Launch)  │
├───────────────────┼──────────────────────┼──────────────────────┤
│ • Offline Room DB │ • Multi-language     │ • Edge AI Speech     │
│ • World Map Path  │   pack selector      │   Recognition        │
│ • Audio Flashcards│ • Elder Audio Studio │ • AR Storytelling    │
│ • Audio Match Game│ • Parent Math Gate   │ • Multiplayer Mode   │
│ • Star Rewards    │ • Detailed Analytics │ • Complex CMS Sync   │
│ • Local Progress  │ • Storybook Viewer   │ • Web/Desktop Ports  │
└───────────────────┴──────────────────────┴──────────────────────┘
```

### Scope Justification
- **Must Have (MLP):** The bare minimum needed for a child to open the app, navigate a world map, listen to native Bakenye pronunciations, complete an activity, receive positive feedback, and persist their progress offline.
- **Should Have:** Enhancements that enrich the experience (storybook viewer, parent portal, elder audio recorder) to be added once the core learning loop is proven.
- **Later:** Complex, infrastructure-heavy features that require cloud connectivity or specialized hardware models.

---

## 2. FIRST USER FLOW IMPLEMENTATION

The engineering team will focus on making a single, end-to-end user journey 100% functional before expanding feature breadth:

```
[1. APP LAUNCH]
       │
       ▼
[2. WELCOME SCREEN] ──(Tap "Start Adventure")──► [3. LANGUAGE SELECT]
                                                          │
                                                          ▼
[6. STORY / AUDIO] ◄── (Select Level) ── [5. HOME MAP DASHBOARD]
       │
       ▼
[7. INTERACTIVE ACTIVITY] ──(Complete)──► [8. REWARD CELEBRATION]
                                                    │
                                                    ▼
                                          [9. PROGRESS PERSISTED]
```

### Component & State Specification for First Journey

#### 1. App Launch & Welcome Screen
- **UI Components:** `MascotIllustration`, `DisplayTitle`, `StartButton`.
- **Data Required:** Pre-seeded initial SQLite database (Room).
- **State Handling:** `UiState.Loading` while Room database verifies seeds ➔ `UiState.Success`.
- **Navigation:** Tapping "Start Adventure" navigates to Language Selection / Home Dashboard.

#### 2. Home Dashboard (World Map)
- **UI Components:** `ComposeCanvasMap`, `MapNodeButton`, `StarCounterBadge`, `CoinBalanceChip`.
- **Data Required:** List of `World` and `Lesson` entities from Room DB.
- **User Action:** Tapping Node 1 launches Lesson 1 popup dialog.

#### 3. Interactive Activity Screen
- **UI Components:** `LessonProgressBar`, `HeroImageCard`, `AudioPlayButton`, `OptionCardGrid`.
- **User Action:** Tap audio speaker to hear Bakenye pronunciation; tap matching picture card.
- **State Handling:** Correct match triggers green glow + sound chime + auto-advance.

#### 4. Reward & Progress Update
- **UI Components:** `CelebrationModal`, `StarParticleFX`, `ContinueButton`.
- **Data Action:** Atomic update to `UserProfileEntity` (Stars +3, Coins +20, Lesson Status = `COMPLETED`).

---

## 3. JETPACK COMPOSE BUILD ORDER (4-SPRINT TIMELINE)

```
┌─────────────────────────────────────────────────────────────────┐
│                    4-SPRINT DEVELOPMENT TIMELINE                │
├─────────────┬───────────────────────────────────────────────────┤
│ Sprint 1    │ Foundation, Theme, Navigation, & App Shell        │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 2    │ Local Room DB, Audio Engine, & World Map Canvas   │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 3    │ Lesson Engine, Minigame Activities, & Rewards     │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 4    │ Polish, Micro-interactions, Accessibility, & QA   │
└─────────────┴───────────────────────────────────────────────────┘
```

### Sprint 1: Foundation & App Shell
- [x] Configure `BakenyeKidsTheme` colors, typography (`Fredoka` & `Plus Jakarta Sans`), and M3 shapes.
- [x] Set up Navigation Compose with type-safe routes (`Screen.Welcome`, `Screen.WorldMap`, `Screen.Lesson`).
- [x] Build reusable Compose components: `BakenyeButton`, `BakenyeCard`, `AudioIconButton`.

### Sprint 2: Core Data & Audio Infrastructure
- [x] Implement Room SQLite Database (`AppDatabase`, `LessonDao`, `PhraseDao`, `UserProgressDao`).
- [x] Create ExoPlayer audio playback manager with pitch-preserved 0.75x slow speed mode.
- [x] Render `WorldMapCanvas` with curved path rendering and dynamic node state calculation.

### Sprint 3: Interactive Learning Loop
- [x] Implement `AudioMatchingMinigame` Composable with tap state feedback.
- [x] Build `CelebrationDialog` with animated star particle effects.
- [x] Connect `LessonViewModel` to Room database for atomic progress state updates.

### Sprint 4: UX Polish & Accessibility Certification
- [x] Audit all clickable elements for WCAG 2.1 AAA touch targets (≥ 48dp x 48dp).
- [x] Add TalkBack `contentDescription` tags to all images, cards, and icons.
- [x] Run Robolectric unit tests and Roborazzi Compose screenshot verification tests.

---

## 4. DESIGN SYSTEM COMPONENT CHECKLIST

| Component Name | Category | Compose Implementation Requirements | Reusability Rules |
| :--- | :--- | :--- | :--- |
| **`BakenyeButton`** | Control | `Button` with 56dp height, M3 elevation, ripple effect, rounded corners (24.dp) | Primary CTA across all screens |
| **`MapNodeComponent`** | Canvas / Map | Custom `Box` + `Canvas` glow, pulsing animation for active node, padlock icon for locked | Used exclusively on World Map |
| **`AudioIconButton`** | Media Control | `IconButton` with speaker vector icon, scale animation on press, sound playing state | Embedded in flashcards and options |
| **`OptionCard`** | Activity | `Surface` with high-contrast border, image avatar, text label, green/red feedback state | Used across all minigame choices |
| **`StarCounterChip`** | Header Badge | `Row` with gold star SVG, bold counter text, spring entry animation | Displayed on TopBar of main screens |

---

## 5. LOCAL DATA ARCHITECTURE (MINIMUM ROOM SCHEMA)

```kotlin
// Room SQLite Entities for Offline MVP

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val worldId: Int,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false,
    val starsEarned: Int = 0
)

@Entity(tableName = "vocabulary_items")
data class VocabularyItemEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val bakenyeText: String,
    val englishTranslation: String,
    val phoneticSpelling: String,
    val audioAssetPath: String,
    val imageAssetPath: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalStars: Int = 0,
    val totalCoins: Int = 0,
    val currentStreakDays: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
```

---

## 6. FIRST PROTOTYPE CONTENT PACKAGE

For the initial MVP test build, a targeted, high-quality content pack focusing on everyday Bakenye life around Lake Kyoga will be seeded:

### Content Statistics
- **Target Language:** Bakenye (Bantu language, Lake Kyoga region, Uganda).
- **Total Worlds:** 1 ("Lake Kyoga Shores").
- **Total Lessons:** 4 structured lessons.
- **Total Vocabulary Terms:** 20 terms with high-resolution vector illustrations and native audio recordings.

```
┌─────────────────────────────────────────────────────────────────┐
│                    FIRST MVP CONTENT PACKAGE                    │
├────────────┬────────────────────┬───────────────────────────────┤
│ Lesson ID  │ Topic / Category   │ Sample Bakenye Terms          │
├────────────┼────────────────────┼───────────────────────────────┤
│ L1_GREET   │ Greetings & Respect│ Yoga (Hello), Webale (Thanks) │
│ L2_FAMILY  │ Family & People    │ Omwana (Child), Omutaka (Elder)│
│ L3_LAKE    │ Lake & Nature      │ Ennyanja (Lake), Enngege (Fish)│
│ L4_ANIMALS │ Birds & Wildlife   │ Enyonyi (Bird), Embeba (Animal)│
└────────────┴────────────────────┴───────────────────────────────┘
```

---

## 7. USER EXPERIENCE TESTING PROTOCOL

The first user testing session will be conducted in a primary school setting near Lake Kyoga with children ages 5–9 and local teachers:

### Key Evaluation Criteria
1. **Unassisted Task Completion:** Can a child start the app, select Node 1, listen to a phrase, and tap the correct matching picture without adult intervention? (Target: > 90% success rate).
2. **Audio Comprehension:** Do children recognize the native Bakenye pronunciations as authentic and clear?
3. **Engagement & Delight:** Do children express joy and smiles during the star reward celebration modal?
4. **Touch Ergonomics:** Are touch targets easily clickable by small fingers on low-cost Android tablets?

---

## 8. DEVELOPER BACKLOG & TASK BOARD

| Task Name | Description | Priority | Dependencies | Definition of Done |
| :--- | :--- | :---: | :--- | :--- |
| **TASK-01: Theme & Typography** | Set up `BakenyeKidsTheme`, M3 color palette, and custom fonts | **P0** | None | App renders high-contrast theme in Compose preview |
| **TASK-02: Room Seed DB** | Implement Room entities and seed initial 20 vocabulary terms | **P0** | TASK-01 | Database initializes and populates on cold start |
| **TASK-03: Audio Playback** | Build `ExoPlayer` manager for phrase playback with slow mode | **P0** | TASK-02 | Tapping phrase triggers instant audio output (<50ms) |
| **TASK-04: World Map Canvas** | Render curved node map with unlocked/locked state visuals | **P0** | TASK-02 | Tapping active node opens lesson launch dialog |
| **TASK-05: Audio Matching Game**| Build picture-sound matching activity with instant feedback | **P0** | TASK-03, TASK-04 | Correct answer advances activity; updates Room DB |
| **TASK-06: Rewards Dialog** | Create celebration modal with animated stars and coin additions | **P1** | TASK-05 | Stars and coins update in user profile atomically |

---

## 9. FIRST RELEASE ACCEPTANCE CRITERIA

The MVP build will be certified for initial user testing when:

- **Technical:** The app compiles without errors (`compile_applet`), installs on API 24+ Android test devices, operates 100% offline in Airplane Mode, and maintains 0% crash rate over 50 test sessions.
- **User Experience:** A child can independently complete the primary user flow (Launch ➔ Map ➔ Lesson ➔ Audio Match ➔ Reward) in under 3 minutes without getting stuck.
- **Cultural Verification:** All 20 initial vocabulary terms and audio recordings are verified for pronunciation accuracy by the Bakenye Language & Culture Council (BLCC).

---

## 10. RECOMMENDED IMMEDIATE BUILD TARGET

> **The next 7 days of development should focus ONLY on:**
> 1. Ensuring the **Room Database** initializes and seeds the 20 primary Bakenye vocabulary items on cold start.
> 2. Connecting the **ExoPlayer Audio Manager** so tapping a word plays its native audio instantly.
> 3. Completing the **Audio Matching Minigame screen** so a child can tap a sound button, select the matching image card, see positive visual feedback, and earn 3 stars saved to local persistence.

---

*Verified & Approved for Sprint Execution by the Technical Lead & Delivery Directorate, 2026.*
