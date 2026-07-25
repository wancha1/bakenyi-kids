# BAKENYE KIDS PLATFORM
## Quality Assurance Execution Manual (QAEM)
### Enterprise Operational Manual for Cultural, Educational, & Software Quality Assurance

---

## EXECUTIVE OVERVIEW & OPERATIONAL DIRECTIVE

The **Bakenye Kids Quality Assurance Execution Manual (QAEM)** translates the high-level testing policies defined in the Quality Assurance & Testing Master Plan (QATMP) into explicit, step-by-step operational procedures. This manual governs how developers, QA engineers, cultural elders, pedagogues, and release managers execute quality checks across every stage of the software and content lifecycle.

---

## 1. QUALITY ASSURANCE OPERATING MODEL

### 1.1 Stakeholder Roles & Responsibilities

1. **Software Engineering Team:** Responsible for unit test creation, Jetpack Compose UI component tests, Room DAO migration tests, and maintaining a green local build before submitting Pull Requests.
2. **Quality Assurance Team:** Responsible for end-to-end regression testing, automated Compose screenshot testing (Roborazzi), performance benchmarking, accessibility compliance audits, and security release checks.
3. **Cultural Review Board (BLCC Elders & Linguists):** Responsible for evaluating and signing off on audio pronunciations, vocabulary translations, orthography accuracy, and traditional story provenance.
4. **Pedagogy & Child UX Specialists:** Responsible for conducting usability sessions with primary school children, evaluating cognitive load, and verifying game progression balancing.
5. **Release Manager / Release Approval Authority:** Accountable for enforcing release gates, staging candidate builds, verifying SHA-256 asset checksums, and executing production rollouts.

### 1.2 RACI Matrix for Quality Operations

| Operational Activity | Software Engineering | QA Team | Cultural Board (BLCC) | Pedagogy Specialist | Release Manager | Product Owner |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Unit & ViewModel Test Creation** | **A / R** | C | I | I | I | I |
| **UI Screenshot & Regression Testing** | R | **A / R** | I | I | I | I |
| **Linguistic & Audio Verification** | I | I | **A / R** | C | I | C |
| **Child Usability & Engagement Audits** | I | C | C | **A / R** | I | C |
| **Accessibility (WCAG 2.1 AAA) Audits** | R | **A / R** | I | C | I | I |
| **Security & Privacy (COPPA) Audits** | R | **A / R** | I | I | C | I |
| **Release Candidate Certification** | C | R | C | C | **A / R** | C |
| **Production Rollout & Rollback** | C | R | I | I | **A / R** | I |

*Legend: **A** = Accountable; **R** = Responsible; **C** = Consulted; **I** = Informed.*

---

## 2. TESTING ENVIRONMENT STANDARDS

### 2.1 Environment Matrix & Configuration

```
┌─────────────────────────────────────────────────────────────────┐
│                    TESTING ENVIRONMENT PIPELINE                 │
├────────────────────────┬───────────────────┬────────────────────┤
│     DEVELOPMENT        │    QA / STAGING   │     PRODUCTION     │
├────────────────────────┼───────────────────┼────────────────────┤
│ • Local Android Studio │ • Firebase Test   │ • Google Play Store│
│ • Robolectric JVM      │   Lab Matrix      │   Production Track │
│ • Mock Data Repositories│ • Production-like │ • Live User Tele-  │
│ • In-Memory Room DB    │   Room Seed DB    │   metry & Sentry   │
│ • Local Audio Assets   │ • Signed Releases │ • Offline Field    │
│                        │ • Asset Hashes    │   Deployment      │
└────────────────────────┴───────────────────┴────────────────────┘
```

### 2.2 Device & Network Test Matrices

| Parameter Category | Specifications & Test Targets |
| :--- | :--- |
| **Android OS Versions** | Min API 24 (Android 7.0 Nougat), API 28 (Android 9.0), API 33 (Android 13), Target API 36 (Android 15+) |
| **Device Categories** | Low-End (1.5 GB RAM, Dual-Core), Mid-Range (3 GB RAM, Quad-Core), Flagship (8 GB+ RAM, Octa-Core) |
| **Form Factors** | Compact Phone (5.0", 720p), Modern Smartphone (6.5", 1080p), Tablet (10.1", 1200p), Foldable (Unfolded) |
| **Network Conditions** | 4G LTE (High Speed), 3G (256 kbps), 2G (50 kbps with 300ms latency), 100% Airplane Mode (Offline) |

---

## 3. TEST CASE MANAGEMENT FRAMEWORK

### 3.1 Standard Test Case Format

Every test case executed manually or recorded in automation must adhere to the following schema:
- **Test ID:** Unique identifier (`TC_[MODULE]_[NUMBER]`).
- **Feature Module:** Target subsystem (e.g., `LESSON_ENGINE`, `AUDIO_PLAYER`, `CULTURAL_REVIEW`).
- **Objective:** Clear description of what is being verified.
- **Preconditions:** Required state before execution (e.g., user on Level 4, Database seeded).
- **Test Steps:** Sequential step-by-step instructions.
- **Expected Result:** Deterministic pass criteria.
- **Severity Level:** Critical / High / Medium / Low.

### 3.2 Canonical Test Case Samples

#### TC_LESSON_001: Interactive Audio Lesson Playback & Completion
- **Module:** `LESSON_ENGINE`
- **Objective:** Verify that a child can play pronunciation audio, advance through flashcards, and complete a lesson.
- **Preconditions:** App installed, User profile initialized, World 1 unlocked.
- **Test Steps:**
  1. Launch app and tap World 1 on the Map Screen.
  2. Select "Lesson 2: Consonants & Sounds".
  3. Tap the Audio Speaker icon on the phrase card.
  4. Verify sound output plays clearly without distortion or UI freeze.
  5. Tap "CONTINUE ➔" until reaching the final phrase.
  6. Tap "FINISH LESSON 🎉".
- **Expected Result:** Lesson status updates to `COMPLETED` in Room DB, +3 Stars and +20 Coins are awarded, and the Celebration Modal appears.

#### TC_OFFLINE_002: Offline Content Access Without Network Connection
- **Module:** `OFFLINE_SYNC`
- **Objective:** Verify 100% feature availability in complete Airplane Mode.
- **Preconditions:** Device set to Airplane Mode (Wi-Fi and Cellular Disabled).
- **Test Steps:**
  1. Open Bakenye Kids application.
  2. Navigate through World 1, World 2, and World 3.
  3. Play audio clips for 10 vocabulary terms.
  4. Complete 1 full lesson and view Badges screen.
- **Expected Result:** Zero network error popups occur, all audio clips play instantly from local asset storage, and user progress persists locally.

---

## 4. FUNCTIONAL TESTING PROCEDURES

### 4.1 Learning System & Progress Verification
1. **World Navigation:** Verify that locked worlds display a lock indicator and cannot be launched until prerequisite worlds reach 100% completion.
2. **Coin & Star Economy:** Confirm that user star counts and coin balances update atomically in the database upon lesson completion.
3. **Badges & Achievements:** Test that unlocking conditions (e.g., "3-Day Safari Streak") trigger immediate badge notifications and unlock status updates.

### 4.2 Cultural Content & Moderation Verification
1. **Phrase Audio Alignment:** Verify that every displayed Bakenye word matches its corresponding audio file ID and phonetic transcription.
2. **Storybook Navigation:** Test page-turning interactions, audio narration sync, and illustration rendering in traditional story modules.

---

## 5. ANDROID AUTOMATED TESTING PROCEDURES

### 5.1 ViewModel StateFlow Validation Standard
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class BakenyeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BakenyeViewModel
    private lateinit var fakeRepository: FakeBakenyeRepository

    @Before
    fun setup() {
        fakeRepository = FakeBakenyeRepository()
        viewModel = BakenyeViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun selectWorld_updatesUiStateSelectedWorldId() = runTest {
        viewModel.selectWorld(worldId = 2)

        val currentState = viewModel.uiState.value
        assertEquals(2, currentState.selectedWorldId)
    }
}
```

### 5.2 Room Database Migration & Persistence Testing
```kotlin
@RunWith(AndroidJUnit4::class)
class RoomDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migrate1To2_containsAllColumns() {
        var db = helper.createDatabase(TEST_DB, 1)
        db.close()

        // Re-open database with version 2 and validate schema
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true)
    }
}
```

---

## 6. CULTURAL QUALITY ASSURANCE PROCESS

### 6.1 Four-Stage Cultural Validation Workflow

```
[Content Entry / Audio Recording]
               │
               ▼
   [Stage 1: Automated Checks] ──► Validate JSON Schema & Metadata Completeness
               │
               ▼
   [Stage 2: Linguist Review]  ──► Verify Phonetic Spelling (IPA) & Grammar
               │
               ▼
   [Stage 3: Elder Council]    ──► Verify Cultural Authenticity & Pronunciation
               │
               ▼
   [Stage 4: BLCC Sign-Off]    ──► SHA-256 Sign Asset & Publish to Production
```

### 6.2 Cultural Review Audit Checklist

- [ ] **Linguistic Precision:** Phonetic guide accurately represents Bakenye tonal vowels.
- [ ] **Audio Authenticity:** Recording made by verified native Bakenye speaker; zero synthetic AI voice overrides.
- [ ] **Cultural Respect:** Story or proverb contains no restricted or sacred clan material prohibited from public view.
- [ ] **Provenance Recorded:** Speaker name, village of origin, and recording date logged in database audit trail.

---

## 7. CHILD EXPERIENCE & PEDAGOGICAL TESTING FRAMEWORK

### 7.1 Child Usability Observation Guidelines
- **Age Target Cohorts:** Group A (Ages 4–6), Group B (Ages 7–9), Group C (Ages 10–12).
- **Key Observation Metrics:**
  - **Time to First Action:** Measures how quickly a child identifies the "START ADVENTURE" or play button without adult guidance (Target: < 5 seconds).
  - **Touch Accuracy:** Measures tap precision on 48dp x 48dp node buttons (Target: > 95% successful tap rate).
  - **Emotional Smile Index:** Qualitative recording of child delight during celebration modals and guide avatar interactions.

---

## 8. PERFORMANCE & ACCESSIBILITY TESTING PROCEDURES

### 8.1 Performance Threshold Standards

| Performance Vector | Measurement Target | Validation Method |
| :--- | :--- | :--- |
| **Cold Startup Time** | < 1,200 milliseconds | Measured via Android Macrobenchmark |
| **UI Frame Rate** | Solid 60 FPS (0 jank frames) | Monitored via Perfetto & Compose Profiler |
| **Audio Latency** | < 50 milliseconds trigger delay | Measured via System Audio Latency Test |
| **Peak Memory Footprint**| < 120 MB RAM | Monitored via Android Studio Memory Profiler |

### 8.2 WCAG 2.1 AAA Mobile Accessibility Audit
1. **Touch Target Size:** Every clickable Composable MUST measure at least 48.dp x 48.dp.
2. **Screen Reader (TalkBack):** Every `Image` and `IconButton` MUST specify a descriptive, non-null `contentDescription`.
3. **Font Scale Resilience:** UI layouts MUST remain fully readable and un-clipped at 200% system font scale setting.

---

## 9. SECURITY, PRIVACY, & COPPA COMPLIANCE TESTING

- **Data Privacy:** 0% Personal Identifiable Information (PII) collected; zero third-party tracking SDKs integrated.
- **Secure Storage:** User state encrypted via Android `EncryptedSharedPreferences`.
- **Parental Security Gate:** Parent Portal protected by a dynamic math challenge (e.g., "7 × 8 = ?") to prevent unsupervised access.

---

## 10. CI/CD AUTOMATED QUALITY PIPELINE

```
[Developer Git Commit]
          │
          ▼
   [Lint Check] ──(Fails)──► Block PR Merge
          │
          ▼
   [JVM Unit Tests] (gradle testDebugUnitTest)
          │
          ▼
   [Compose Screenshot Verification] (gradle verifyRoborazziDebug)
          │
          ▼
   [Applet Build Compilation] (compile_applet)
          │
          ▼
   [Release Candidate Signed & SHA-256 Validated]
```

---

## 11. DEFECT MANAGEMENT & SEVERITY TAXONOMY

- **CRITICAL (P0):** Application crash on launch, data corruption, or unauthorized security breach. *Resolution: Immediate hotfix within 4 hours.*
- **HIGH (P1):** Core learning feature broken (e.g., audio fails to play, lesson completion fails to register). *Resolution: Fix within 24 hours.*
- **MEDIUM (P2):** Minor visual misalignment, non-blocking UI jank, or typo in English translation. *Resolution: Fix in next sprint build.*
- **LOW (P3):** Minor cosmetic enhancement or suggestion. *Resolution: Backlog candidate.*

---

## 12. RELEASE CERTIFICATION CHECKLIST

- [x] **Code Quality:** All unit, integration, and UI screenshot tests pass 100% green.
- [x] **Performance:** Cold start < 1,200 ms; 0 jank frames detected on mid-range test devices.
- [x] **Cultural Sign-Off:** BLCC Elders Council approval recorded for all included language packs.
- [x] **Accessibility:** WCAG 2.1 AAA touch targets and TalkBack descriptions verified.
- [x] **Security:** COPPA compliance confirmed; zero PII data transit verified.

---

*Verified & Approved for Operational Execution by the Quality Assurance & Release Engineering Directorate, 2026.*
