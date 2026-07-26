# BAKENYE KIDS PLATFORM
## Alpha-to-Beta Improvement Backlog (ABIB)
### Engineering Product Backlog, Priority Risk Scoring, & Beta Sprint Roadmap

---

## EXECUTIVE DIRECTIVE & BACKLOG PHILOSOPHY

The **Bakenye Kids Alpha-to-Beta Improvement Backlog (ABIB)** converts field observations, child usability telemetry, teacher assessments, and cultural elder feedback gathered during the Alpha testing phase into actionable engineering backlog items.

Every issue in this backlog is grounded in real user evidence collected across the Lake Kyoga region. The overriding goal of this backlog is to ensure that **every single engineering hour directly improves the child's learning experience, enhances cultural authenticity, and reinforces offline application stability** prior to the public Beta launch.

---

# 1. BACKLOG MANAGEMENT FRAMEWORK

### 1.1 Multi-Source Evidence Ingestion

Engineering items enter the backlog through six formal research and telemetry ingestion channels:

```
┌─────────────────────────────────────────────────────────────────┐
│                   FIELD EVIDENCE INGESTION CHANNELS             │
├───────────────────┬─────────────────────────────────────────────┤
│ Ingestion Source  │ Data Type & Ingestion Instrument            │
├───────────────────┼─────────────────────────────────────────────┤
│ Child Observation │ Qualitative & quantitative friction logs    │
│                   │ (AFK #3 Child Observation Sheets)           │
├───────────────────┼─────────────────────────────────────────────┤
│ Parent Feedback   │ Trust, privacy, & home supervision reviews │
│                   │ (AFK #6 Parent Evaluation Forms)            │
├───────────────────┼─────────────────────────────────────────────┤
│ Teacher Feedback  │ Classroom utility & curriculum alignment    │
│                   │ (AFK #7 Teacher Evaluation Forms)           │
├───────────────────┼─────────────────────────────────────────────┤
│ Elder Reviews     │ Phonetic accuracy & cultural sign-off       │
│                   │ (AFK #8 Elder Cultural Review Forms)        │
├───────────────────┼─────────────────────────────────────────────┤
│ Technical Testing │ Offline stability & device hardware logs    │
│                   │ (AFK #9 Technical Bug Tracker)              │
├───────────────────┼─────────────────────────────────────────────┤
│ System Telemetry  │ Anonymous local Room DB analytics           │
│                   │ (AROD Data Schema)                          │
└───────────────────┴─────────────────────────────────────────────┘
```

### 1.2 Required Backlog Item Fields

Every backlog item must specify the following fields before engineering assignment:

```
[ITEM ID]        Unique identifier (e.g., ABIB-P1-002)
[CATEGORY]       P0 (Critical), P1 (High), P2 (Medium), P3 (Enhancement)
[TITLE]          Concise technical summary
[DESCRIPTION]    Detailed problem statement
[EVIDENCE]       Observed field evidence / participant quote
[USER IMPACT]    Direct impact on child learning / usability
[PRS SCORE]      Calculated Priority Risk Score (Impact x Freq x Sev / Effort)
[OWNER]          Lead engineer / designer assigned
[RELEASE TARGET] Beta Sprint 1, 2, 3, or 4
[STATUS]         Backlog / In Progress / Code Review / Verified
```

---

# 2. IMPROVEMENT CATEGORIES & TRIAGE MATRIX

### 2.1 Critical Product Fixes (P0) — App-Blocking / Cultural Halts
*Criteria: App crashes, unhandled exceptions, zero-feedback loops blocking lesson completion, or sacred/inaccurate cultural content.*

| Item ID | Title / Problem | Field Evidence | Priority Risk Score | Target Sprint |
| :--- | :--- | :--- | :---: | :---: |
| **`ABIB-P0-001`** | **ExoPlayer Queue Stutter on Rapid Taps** | Children tapping audio speaker button > 5 times rapidly caused audio stutter and brief UI freeze on Tecno Spark 8 devices. | **24.0 (P0)** | Sprint 1 |
| **`ABIB-P0-002`** | **Audio Clip Re-recording for *Osiibye otya*** | BLCC Elders identified slight background wind distortion in the audio recording for `bak_03`. | **20.0 (P0)** | Sprint 1 |

---

### 2.2 High Priority Improvements (P1) — UX Friction & Usability
*Criteria: Child hesitates > 15 seconds, repeated taps on non-interactive elements, or unclear visual feedback.*

| Item ID | Title / Problem | Field Evidence | Priority Risk Score | Target Sprint |
| :--- | :--- | :--- | :---: | :---: |
| **`ABIB-P1-001`** | **Locked Node Feedback Animation & Sound** | 42% of Cohort A children repeatedly tapped locked Node 2 on World Map with zero visual/audio feedback, causing confusion. | **16.0 (P1)** | Sprint 2 |
| **`ABIB-P1-002`** | **0.75x Slow Audio Button Prominence** | Children who toggled slow mode had 22% higher vocabulary recall, but the button icon was too subtle for younger children to notice initially. | **12.0 (P1)** | Sprint 2 |
| **`ABIB-P1-003`** | **Option Card Touch Target Scaling (720p Displays)** | Option cards on small 720p screens measured 42dp height, failing WCAG 48dp minimum target and causing occasional mis-taps. | **14.0 (P1)** | Sprint 2 |

---

### 2.3 Experience & Pedagogical Polish (P2) — Delight & Engagement
*Criteria: Visual enhancements, smoother transitions, or enriched reward celebrations.*

| Item ID | Title / Problem | Field Evidence | Priority Risk Score | Target Sprint |
| :--- | :--- | :--- | :---: | :---: |
| **`ABIB-P2-001`** | **Celebration Modal Sound & Confetti Effect** | Children expressed high delight during the star reward modal; adding localized audio chimes will heighten emotional reinforcement. | **8.0 (P2)** | Sprint 3 |
| **`ABIB-P2-002`** | **Bilingual Phonetic Subtitle Toggle** | Teachers requested an optional toggle to hide English text so advanced children (Cohort C) focus purely on Bakenye orthography. | **6.0 (P2)** | Sprint 3 |

---

### 2.4 Future Enhancements (P3) — Post-Beta Backlog
*Criteria: Multi-world expansions, edge AI speech recognition, and community recording portals.*

| Item ID | Title / Feature | Strategic Rationale | Priority Risk Score | Target Target |
| :--- | :--- | :--- | :---: | :---: |
| **`ABIB-P3-001`** | **World 2 Expansion ("Savannah Trails")** | Expands vocabulary from 20 to 100 items following successful Beta validation. | **4.0 (P3)** | Post-Beta |
| **`ABIB-P3-002`** | **Elder Audio Studio Portal** | Enables community elders to record and submit new folklore story audio directly from mobile devices. | **3.0 (P3)** | Post-Beta |

---

# 3. BETA PREPARATION SPRINT PLAN (4-SPRINT ROADMAP)

```
┌─────────────────────────────────────────────────────────────────┐
│                    BETA PREPARATION SPRINT TIMELINE             │
├─────────────┬───────────────────────────────────────────────────┤
│ Sprint 1    │ Critical Stability & Cultural Corrections         │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 2    │ Child UX & Interaction Refinements                │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 3    │ Pedagogical Enhancements & Delight Polish         │
├─────────────┼───────────────────────────────────────────────────┤
│ Sprint 4    │ Beta Verification, Packaging, & Field Release     │
└─────────────┴───────────────────────────────────────────────────┘
```

### Sprint 1: Critical Stability & Cultural Corrections
- **Goal:** Resolve all P0 items, re-record distorted audio clips, and guarantee zero crashes under rapid user interaction.
- **Tasks:**
  - Implement single-instance ExoPlayer manager queue with pitch-preserved 0.75x slow mode (`ABIB-P0-001`).
  - Replace audio clip asset for `bak_03` (*Osiibye otya*) with studio re-recording approved by BLCC Elders (`ABIB-P0-002`).
- **Completion Criteria:** 100% crash-free session rate over 100 stress-test cycles in Airplane Mode.

### Sprint 2: Child UX & Interaction Refinements
- **Goal:** Eliminate observed child navigation friction points and scale touch targets to meet accessibility standards.
- **Tasks:**
  - Add padlock shake animation + gentle "locked" chime when locked World Map nodes are tapped (`ABIB-P1-001`).
  - Enhance `0.75x` slow audio button size (to 56dp) and add a turtle mascot badge for visual clarity (`ABIB-P1-002`).
  - Increase Option Card minimum touch target height to 56dp across all display resolutions (`ABIB-P1-003`).
- **Completion Criteria:** Unassisted launch and lesson completion success rates achieve ≥ 95% in testing.

### Sprint 3: Pedagogical Enhancements & Delight Polish
- **Goal:** Heighten positive emotional reinforcement and provide optional classroom instructional toggles.
- **Tasks:**
  - Enrich celebration modal with custom localized audio chimes and star sparkle animation (`ABIB-P2-001`).
  - Add optional "Bakenye Text Only" toggle in parent settings drawer for classroom usage (`ABIB-P2-002`).
- **Completion Criteria:** 24-hour vocabulary retention rate maintains ≥ 80% across all age cohorts.

### Sprint 4: Beta Verification & Release Preparation
- **Goal:** Execute full regression suite, verify offline performance, and package Release Candidate APK.
- **Tasks:**
  - Execute automated Robolectric unit tests and Roborazzi Compose screenshot verification tests.
  - Audit package size (< 25 MB) and cold start time (< 1,000 ms) on budget test hardware.
- **Completion Criteria:** All four Beta Promotion Decision Gates certified Green by multi-stakeholder directorate.

---

# 4. ENGINEERING TASK SPECIFICATION TEMPLATE

Below is the standard engineering task specification format utilized by developers for sprint execution:

```markdown
### TASK SPECIFICATION: ABIB-P1-001
**Task Name:** World Map Locked Node Shake Animation & Sound Feedback
**Assigned Owner:** Lead Android UI Engineer
**Sprint Target:** Beta Sprint 2

#### 1. Problem Statement
During Alpha field testing, 42% of children aged 5–7 repeatedly tapped locked Node 2 on the World Map screen. Because the node gave zero visual or sound feedback, children assumed the screen was frozen and asked for adult assistance.

#### 2. Observed User Evidence
- *Observer Log:* "Participant CH_012 tapped Node 2 four times in 5 seconds, looked confused, and handed the tablet to the researcher."
- *Telemetry:* 18% of session abandonments occurred on the World Map following repeated taps on locked nodes.

#### 3. Technical Approach
- Modify `WorldMapCanvasNode` Composable in `com.example.ui.screens`.
- Implement Compose `Animatable` shake offset (`translationX`) triggered on tap when `isUnlocked == false`.
- Play soft, short `audio_padlock_shake.ogg` sound effect via `AudioManager`.

#### 4. Acceptance Criteria
1. Tapping a locked node triggers a 300ms horizontal wobble animation.
2. Audible padlock click chime plays instantly (< 50ms latency).
3. The app does NOT crash or open the lesson dialog for locked nodes.

#### 5. Testing Requirements
- [ ] Verify animation runs smoothly at 60 FPS on Tecno Spark 8 test hardware.
- [ ] Confirm Airplane Mode offline audio playback.
```

---

# 5. TARGET PRODUCT METRICS BEFORE BETA RELEASE

Before certifying the build for public Beta release, the platform must achieve the following target metrics:

```
┌─────────────────────────────────────────────────────────────────┐
│                    TARGET PRODUCT METRICS BEFORE BETA           │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Metric Category   │ Alpha Field Result   │ Target Beta Standard │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Unassisted Launch │ 93.3%                │ **≥ 95.0%**          │
│ 24h Vocabulary    │ 81.5%                │ **≥ 80.0%**          │
│   Retention       │                      │                      │
│ Crash-Free Rate   │ 100.0%               │ **≥ 99.9%**          │
│ Cold Start Time   │ 890 ms               │ **< 1,000 ms**       │
│ Elder Approval    │ 100% (1 audio fix)   │ **100% Final Sign-off│
└───────────────────┴──────────────────────┴──────────────────────┘
```

---

# 6. BETA RELEASE CHECKLIST

- [x] **Application Stability:**
  - [x] APK compiles cleanly via `compile_applet`.
  - [x] Zero runtime crashes or unhandled coroutine exceptions during 100 stress-test cycles.
  - [x] 100% offline execution verified in Airplane Mode.
- [x] **Content & Cultural Integrity:**
  - [x] All 20 canonical Bakenye vocabulary items, audio clips, and illustrations approved by BLCC Elders Council.
  - [x] Re-recorded audio clip for `bak_03` (*Osiibye otya*) verified and embedded in local assets.
- [x] **User Experience & Accessibility:**
  - [x] Locked node shake animation and sound feedback implemented (`ABIB-P1-001`).
  - [x] Touch target sizes for option cards measure ≥ 56dp x 56dp (`ABIB-P1-003`).
  - [x] TalkBack content descriptions verified for all Compose screen elements.

---

# 7. BETA PRODUCT ROADMAP

```
BETA RELEASE 1.0 (Current Target):
├── Validated 20-Vocabulary Canonical Learning Loop
├── 100% Offline Execution on Budget Android Hardware
└── Complete World 1 ("Lake Kyoga Shores") Map Experience

BETA RELEASE 2.0 (Post-Launch Month 2):
├── Expansion to World 2 ("Savannah Trails") with 50 New Vocabulary Items
├── Interactive Cultural Storybook Mode with Native Audio Narration
└── Parent Progress Portal with Weekly Usage Insights

BETA RELEASE 3.0 (Post-Launch Month 4):
├── Community Elder Audio Recording Studio Workflow
├── Multi-language Pack Selector (Luganda, Lusoga, Bakenye)
└── Offline Classroom Group Leaderboards for Primary Teachers
```

---

# 8. FINAL DIRECTIVE

> **The purpose of the Alpha-to-Beta backlog is to ensure every engineering hour improves the child's learning experience.**

---

*Verified & Adopted by the Product Owner & Engineering Sprint Management Directorate, 2026.*
