# BAKENYE KIDS PLATFORM
## Quality Assurance & Testing Master Plan (QATMP)
### Enterprise-Grade Operational Standard for Cultural, Educational, & Software Quality

---

## Executive Summary & Quality Vision

The **Bakenye Kids Quality Assurance & Testing Master Plan (QATMP)** establishes an institutional quality framework for the Bakenye Digital Language Platform. Designed for longevity, cultural fidelity, pedagogical efficacy, and technical stability, this master plan governs all verification, validation, performance benchmarking, accessibility testing, and cultural review workflows across the platform's lifecycle.

---

## 1. QUALITY PHILOSOPHY & OBJECTIVES

### 1.1 Quality Objectives
1. **Zero Defect Cultural Integrity:** 0% tolerance for unverified, mispronounced, or culturally inaccurate language content.
2. **Pedagogical Efficacy:** 100% of published lessons verified for age appropriateness, positive reinforcement, and engaging micro-interactions.
3. **Flawless Offline Reliability:** 99.99% crash-free session rate across offline Android environments without cellular or Wi-Fi dependency.
4. **Universal Accessibility:** Full compliance with WCAG 2.1 AAA accessibility standards (touch targets ≥ 48dp, screen reader descriptions, dyslexia mode).

### 1.2 Quality Pyramid & Shift-Left Testing Model
```
                  ┌───────────────────────┐
                  │   CULTURAL & ELDER    │
                  │   VALIDATION GATE     │
                  └───────────┬───────────┘
                              │
                  ┌───────────┴───────────┐
                  │   E2E & UI AUTOMATION │
                  │   (Roborazzi / Compose)│
                  └───────────┬───────────┘
                              │
                  ┌───────────┴───────────┐
                  │ INTEGRATION & ROOM DB │
                  │      TESTING          │
                  └───────────┬───────────┘
                              │
                  ┌───────────┴───────────┐
                  │  UNIT & SCHEMAS TESTS │
                  │    (Robolectric/JUnit)│
                  └───────────────────────┘
```

---

## 2. TESTING STRATEGY & TEST ARCHITECTURE

### 2.1 Test Execution Spectrum

| Test Level | Scope & Framework | Execution Frequency | Target Coverage |
| :--- | :--- | :--- | :--- |
| **Unit Testing** | ViewModels, Repositories, Domain UseCases (JUnit 4/5, MockK) | Every Commit (PR Gate) | ≥ 85% Code Coverage |
| **Integration Testing** | Room DAOs, SQLite Migrations, Asset Loading (Robolectric) | Daily Nightly Builds | ≥ 90% DB / Data Coverage |
| **UI Screenshot Testing** | Jetpack Compose Rendering & Visual Regression (Roborazzi) | Every PR Merge | 100% Core Screen Coverage |
| **E2E Critical Path** | Full Lesson Flow, Audio Playback, Rewards Modal | Pre-Release Candidate | 100% CUJ Pathways |
| **Cultural Validation** | Pronunciation audio, orthography, elder consent records | Pre-Publication Gate | 100% Verified Content |

### 2.2 Mocking & Fake Repository Strategy
- **`FakeBakenyeRepository`:** In-memory DAO implementation populated with canonical test datasets (`TestPhrases.kt`, `TestLessons.kt`) to isolate ViewModel testing from Room I/O overhead.
- **`FakeAudioPlayer`:** Simulates sound playback latencies and audio state callbacks without requiring hardware audio device drivers during JVM unit tests.

---

## 3. ANDROID TESTING STANDARDS

### 3.1 Jetpack Compose UI Testing Pattern
```kotlin
@RunWith(AndroidJUnit4::class)
class LessonScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lessonNode_click_launchesInteractiveDialog() {
        composeTestRule.setContent {
            BakenyeKidsTheme {
                MainAppScreen(viewModel = fakeViewModel)
            }
        }

        // Verify lesson node exists and click
        composeTestRule.onNodeWithTag("lesson_node_L1_1")
            .assertExists()
            .performClick()

        // Verify interactive audio dialog appears
        composeTestRule.onNodeWithTag("audio_play_btn")
            .assertIsDisplayed()
    }
}
```

### 3.2 Visual Screenshot Verification (Roborazzi Standard)
```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainAppScreenshotTest {

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputDirectoryPath = "src/test/screenshots"
        )
    )

    @Test
    fun capture_main_app_screen_light_theme() {
        captureRoboImage("src/test/screenshots/main_app_screen.png") {
            BakenyeKidsTheme {
                MainAppScreen(viewModel = fakeViewModel)
            }
        }
    }
}
```

---

## 4. CONTENT VALIDATION & CULTURAL REVIEW TESTING

### 4.1 Four-Stage Content Approval Pipeline

```
[Raw Audio / Vocabulary Submission]
               │
               ▼
   [Automated JSON Schema Test] ──(Fails)──► Reject to Contributor
               │
               ▼ (Passes)
  [Linguist Phonetic Verification]
               │
               ▼ (Approved)
  [Cultural Elders Council Review] ──(Disputed)──► BLCC Arbitration
               │
               ▼ (75% Elder Approval)
 [SHA-256 Sign & Publish Release Candidate]
```

---

## 5. ACCESSIBILITY, PERFORMANCE, & COMPATIBILITY MATRIX

### 5.1 Performance Targets & Benchmarks

| Metric | Target Standard | Verification Tool |
| :--- | :--- | :--- |
| **Cold Start Time** | < 1,200 ms | Macrobenchmark / Perfetto |
| **Frame Rate** | Constant 60 FPS (Zero dropped frames during scroll) | Compose Frame Inspector |
| **Memory Footprint** | Peak RAM < 120 MB | Android Studio Profiler |
| **Audio Playback Latency** | < 50 ms trigger-to-sound | System Audio Latency Profiler |
| **APK Package Size** | Base APK < 25 MB | App Bundle Analyzer |

### 5.2 Device Compatibility Matrix
- **Android Versions:** Min API 24 (Android 7.0 Nougat) to Target API 36 (Android 15+).
- **RAM Profiles:** Tested on 1GB, 2GB, 4GB, and 8GB RAM devices.
- **Form Factors:** Compact Phones (4.7" - 6.5"), Foldables (Unfolded & Flex modes), Tablets (10.1" - 12.9").

---

## 6. AUTOMATED CI/CD QUALITY PIPELINE

```
[Git Commit / PR]
       │
       ▼
[Lint & Static Code Analysis] (ktlint / Android Lint)
       │
       ▼
[JVM Unit Tests] (gradle testDebugUnitTest)
       │
       ▼
[Roborazzi Screenshot Verification] (gradle verifyRoborazziDebug)
       │
       ▼
[Compilation & Build APK] (compile_applet)
       │
       ▼
[Release Candidate APK Signed & Hash Verified]
```

---

## 7. DISASTER RECOVERY & ROLLBACK VERIFICATION

### 7.1 Automated Database Recovery Verification
1. **Corrupted Database Simulation:** Test suite injects invalid byte sequences into the `.db` file on startup.
2. **Recovery Validation:** Verifies that `AppDatabase` catches `SQLiteCorruptException`, safely triggers a clean rebuild, re-seeds default offline worlds, and recovers user state without crashing the user interface.

---

*Verified & Adopted by the Quality Assurance & Testing Board, 2026.*
