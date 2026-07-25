# BAKENYE KIDS PLATFORM
## Technical Design Specification (TDS)
### Master Engineering Implementation Reference Manual for Senior Android & Platform Engineers

---

## EXECUTIVE OVERVIEW & ARCHITECTURAL DIRECTIVE

This **Technical Design Specification (TDS)** is the authoritative implementation reference manual for the **Bakenye Kids Android Application & Engine**. It specifies the exact technical design, module boundaries, data pipelines, state machines, database schemas, audio engines, and performance budgets required to build, test, deploy, and maintain the production system.

---

## 1. ANDROID APPLICATION ARCHITECTURAL SPECIFICATION

### 1.1 Module Structure & Dependency Graph

The application follows a **Modular Clean Architecture** pattern designed to separate domain logic, UI frameworks, local data persistence, and platform integrations.

```
                     ┌───────────────────────────────────┐
                     │            :app (UI)              │
                     └─┬───────────────┬───────────────┬─┘
                       │               │               │
                       ▼               ▼               ▼
             ┌──────────────────┐ ┌─────────┐ ┌─────────────────┐
             │ :feature:lesson  │ │:feature:│ │ :feature:map    │
             │     engine       │ │minigame │ │   & world       │
             └────────┬─────────┘ └────┬────┘ └────────┬────────┘
                      │                │               │
                      └────────────────┼───────────────┘
                                       │
                                       ▼
                             ┌──────────────────┐
                             │  :core:domain    │
                             └────────┬─────────┘
                                      │
                                      ▼
                             ┌──────────────────┐
                             │   :core:data     │
                             └────────┬─────────┘
                                      │
                                      ▼
                             ┌──────────────────┐
                             │ :core:database   │
                             │    (Room DB)     │
                             └──────────────────┘
```

### 1.2 Unidirectional Data Flow (UDF) & State Management

Each screen feature manages state using Kotlin `StateFlow` and immutable UI state representations:

```
[User Action / UI Event] ──► ViewModel.onEvent(Intent) ──► UseCase / Repository
                                                                │
                                                                ▼
[UI View Component] ◄── collectAsStateWithLifecycle() ◄── MutableStateFlow<UiState>
```

```kotlin
// Immutable State Model Example
data class LessonUiState(
    val isLoading: Boolean = false,
    val currentActivity: LessonActivity? = null,
    val progressFraction: Float = 0f,
    val isAudioPlaying: Boolean = false,
    val userScore: Int = 0,
    val errorMessage: String? = null
)
```

---

## 2. LESSON ENGINE SPECIFICATION

### 2.1 Lesson Execution Lifecycle & State Machine

```
[IDLE / LOADED] ──(Start Event)──► [ACTIVITY_ACTIVE] ──(Submit Answer)──► [EVALUATING]
      ▲                                                                          │
      │                                                                          ├──(Correct)──► [CELEBRATION] ──(Next)──┐
      │                                                                          └──(Incorrect)─► [HINT_RETRY] ──────────┤
      │                                                                                                                  │
      └──────────────────────────── Check Remaining Activities ◄────────────────────────────────────────────────────────┘
```

### 2.2 Lesson Definition JSON Schema
```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "BakenyeLessonSchema",
  "type": "object",
  "properties": {
    "lesson_id": { "type": "string" },
    "world_id": { "type": "integer" },
    "title": { "type": "string" },
    "star_reward": { "type": "integer" },
    "activities": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "activity_id": { "type": "string" },
          "type": { "type": "string", "enum": ["LISTEN_REPEAT", "TAP_MATCH", "MEMORY_FLIP", "DRAG_SORT", "AUDIO_QUIZ"] },
          "prompt_phrase_id": { "type": "string" },
          "options": { "type": "array", "items": { "type": "string" } },
          "correct_option_index": { "type": "integer" }
        }
      }
    }
  }
}
```

---

## 3. REUSABLE MINIGAME ENGINE SPECIFICATION

The Minigame Engine provides 5 primary reusable template classes supporting over 25 distinct learning minigame variants:

| Minigame Engine Class | Primary Pedagogy Goal | Input Interaction | Mechanics & Scoring |
| :--- | :--- | :--- | :--- |
| **`AudioMatchingEngine`** | Sound-to-Symbol Association | Tap / Touch | Plays Bakenye audio; child taps matching card. +10 pts per match. |
| **`MemoryCardEngine`** | Visual-Linguistic Memory | Card Flip | 3x4 grid of hidden cards (word + image). +15 pts per pair. |
| **`DragDropSorterEngine`** | Categorization & Spelling | Drag & Drop | Drag letters or categories into target baskets. +20 pts. |
| **`PronunciationEchoEngine`** | Tonal & Vocal Practice | Microphone / Tap Repeat | Child listens to audio clip and repeats phrase. +25 pts. |
| **`StorySequenceEngine`** | Narrative Comprehension | Sequence Reordering | Place story illustration panels in chronological order. +30 pts. |

---

## 4. AUDIO ENGINE & PLAYBACK PIPELINE SPECIFICATION

### 4.1 Audio Playback Architecture

```
[App / Composable Request] ──► AudioPlaybackManager.play(soundId, speedRate = 1.0f)
                                       │
                                       ▼
                       [ExoPlayer / SoundPool Cache]
                                       │
                ┌──────────────────────┴──────────────────────┐
                ▼                                             ▼
     [RAM Asset Cache Hit]                       [Disk Storage / OGG Load]
                │                                             │
                └──────────────────────┬──────────────────────┘
                                       │
                                       ▼
                         [AudioTrack Output Stream]
```

### 4.2 Playback Configuration Parameters
- **Sample Rate:** 44.1 kHz / 16-bit PCM Audio
- **Normal Playback Speed:** `1.0f`
- **Slow Pronunciation Speed:** `0.75f` (Pitch preserved via ExoPlayer Sonic audio processor)
- **Audio Interruption Handling:** Focus loss automatically pauses background music; voice prompts override background music using `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`.

---

## 5. WORLD MAP & PROGRESS NAVIGATION ENGINE

### 5.1 Node Map Rendering & Path Calculation

The World Map renders a dynamic node trail calculated through Compose Canvas curves:

```kotlin
// Bezier Curve World Path Calculation
fun Path.drawWorldPath(nodes: List<MapNode>) {
    if (nodes.isEmpty()) return
    moveTo(nodes[0].x, nodes[0].y)
    for (i in 0 until nodes.size - 1) {
        val current = nodes[i]
        val next = nodes[i + 1]
        val controlX = (current.x + next.x) / 2f
        cubicTo(controlX, current.y, controlX, next.y, next.x, next.y)
    }
}
```

---

## 6. ADAPTIVE LEARNING & SPACED REPETITION ENGINE

### 6.1 Modified SuperMemo 2 (SM-2) Spaced Repetition Algorithm

To ensure long-term retention of Bakenye vocabulary without overwhelming young learners, items are scheduled according to:

$$I(n) = I(n-1) \times EF$$

Where:
- $I(n)$ = Next review interval in days.
- $EF$ = Ease Factor (Default = 2.5; modified based on performance scores 1 to 5).
- If score < 3, item resets to $I(1) = 1$ day.

---

## 7. LOCAL ROOM DATABASE SCHEMA SPECIFICATION

### 7.1 Database ERD & Entity Relationships

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│   UserProfile   │       │      World      │       │     Lesson      │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ worldId (PK)    │◄──────┤ lessonId (PK)   │
│ stars           │       │ title           │       │ worldId (FK)    │
│ coins           │       │ iconEmoji       │       │ title           │
│ streakDays      │       │ isUnlocked      │       │ isCompleted     │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                                             │
                                                             ▼
                                                    ┌─────────────────┐
                                                    │     Phrase      │
                                                    ├─────────────────┤
                                                    │ id (PK)         │
                                                    │ worldId (FK)    │
                                                    │ bakenyeText     │
                                                    │ englishText     │
                                                    │ pronunciation   │
                                                    └─────────────────┘
```

---

## 8. SYNCHRONIZATION ENGINE & OFFLINE QUEUE

### 8.1 Offline Sync State Machine

```
[Offline Activity Completed] ──► Write to Room Local DB
                                        │
                                        ▼
                         Enqueue WorkManager Task
                                        │
                      ┌─────────────────┴─────────────────┐
                      ▼                                   ▼
          [No Network Connection]                [Network Available]
                      │                                   │
             Wait for Network Event              POST Sync Payload to Edge API
                                                          │
                                                          ▼
                                              Update Sync Status Flag = 1
```

---

## 9. ASSET PIPELINE & OPTIMIZATION

- **Image Assets:** WebP format (lossless compression, 80% size reduction vs PNG).
- **Audio Assets:** OGG Vorbis 128kbps for audio phrases; stereo 192kbps OGG for cultural songs.
- **Vector Graphics:** Native Android Vector Drawables for UI controls and iconography.

---

## 10. PARENT PORTAL & CHILD SAFETY GATE

### 10.1 Mathematical Verification Security Gate
To prevent children from accessing parent settings or external links, the portal is protected by an interactive gesture or math challenge:

```
[Tap Parents Portal] ──► Display Math Problem: "8 × 7 = ?"
                                  │
                  ┌───────────────┴───────────────┐
                  ▼                               ▼
          [Correct Entry: 56]           [Incorrect Entry]
                  │                               │
       Open Parents Dashboard           Block Access & Reset
```

---

## 11. ACCESSIBILITY & INCLUSIVITY ENGINE

- **Touch Target Size:** Minimum 48dp x 48dp on all clickable elements.
- **High-Contrast Theme:** WCAG AAA compliance (Contrast ratio > 7:1 for text/backgrounds).
- **TalkBack Integration:** Descriptive `contentDescription` on all interactive images and emojis.
- **Left-Handed Mode Toggle:** Swaps control button layouts horizontally.

---

## 12. SECURITY ARCHITECTURE & DATA PRIVACY

- **Storage:** Sensitive local state encrypted via Android `EncryptedSharedPreferences` & Room SQLite Encryption (SQLCipher).
- **COPPA Compliance:** Zero personal identifiable information (PII) collected or transmitted.
- **Asset Verification:** Content updates validated using SHA-256 signatures prior to ingestion.

---

## 13. PERFORMANCE BUDGET & METRICS

| Performance Metric | Target Threshold | Monitoring Tool |
| :--- | :--- | :--- |
| **Cold Startup Time** | < 1,200 ms | Android Vitals / Macrobenchmark |
| **Frame Render Time** | < 16ms (60 FPS solid) | Perfetto / Compose Inspector |
| **Max Memory Usage** | < 120 MB RAM | Android Studio Profiler |
| **Download APK Size** | < 25 MB base | App Bundle Optimization |
| **Audio Playback Latency** | < 50 ms | ExoPlayer Audio Profiler |

---

## 14. ERROR RECOVERY & GRACEFUL FALLBACKS

- **Missing Audio File:** Plays standard phonetic fallback TTS tone and logs non-blocking error.
- **Corrupted Room Database:** Triggers `fallbackToDestructiveMigration()` and re-seeds canonical initial offline assets.
- **Failed Sync:** Retries via WorkManager exponential backoff without interrupting active gameplay.

---

## 15. FUTURE EXTENSION INTERFACES

The codebase defines clean abstract interfaces for future platform capabilities:

```kotlin
// Interface for Future Edge Speech Recognition
interface SpeechRecognitionEngine {
    suspend fun evaluatePronunciation(
        targetPhraseId: String,
        recordedAudioUri: String
    ): PronunciationResult
}

data class PronunciationResult(
    val accuracyScore: Float, // 0.0f to 1.0f
    val feedbackMessage: String
)
```

---

*Engineered & Verified by the Senior Android & Platform Engineering Team, 2026.*
