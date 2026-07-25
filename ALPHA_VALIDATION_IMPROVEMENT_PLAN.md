# BAKENYE KIDS PLATFORM
## Alpha Validation & Improvement Plan (AVIP)
### User Research, Educational Testing, UX Evaluation, & Product Iteration Framework

---

## Executive Overview & Testing Philosophy

The **Bakenye Kids Alpha Validation & Improvement Plan (AVIP)** defines the operational framework for validating the functional Android MVP with real children, parents, teachers, and cultural elders in the Lake Kyoga region of Uganda.

Rather than adding new software features or background modules, the Alpha testing phase focuses strictly on **evaluating user behavior, measuring vocabulary retention, uncovering UX friction points, verifying cultural authenticity, and converting field observations into targeted product refinements**.

---

## 1. ALPHA TESTING OBJECTIVES

### 1.1 Core Learning & Usability Benchmarks

```
┌─────────────────────────────────────────────────────────────────┐
│                    ALPHA RESEARCH BENCHMARKS                    │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Metric Category   │ Primary Target       │ Validation Method    │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Unassisted Entry  │ ≥ 90% of children    │ Direct observation   │
│                   │ start without adult  │ during initial app   │
│                   │ intervention         │ launch               │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Audio Comprehens- │ ≥ 95% recognize      │ Immediate vocal      │
│ ion               │ native pronunciations│ repetition & picture │
│                   │ as natural & clear   │ identification       │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Word Retention    │ ≥ 80% recall after   │ 24-hour delayed      │
│ (24-Hour Recall)  │ single 10-min session│ vocabulary quiz      │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Self-Initiated    │ ≥ 85% ask to play    │ Post-session child   │
│ Replay            │ another lesson       │ interview & telemetry│
└───────────────────┴──────────────────────┴──────────────────────┘
```

---

## 2. TARGET TESTING GROUPS

### 2.1 Cohort Definitions & Sample Sizes

```
                           ┌───────────────────────────┐
                           │   ALPHA USER COHORTS      │
                           └─────────────┬─────────────┘
                                         │
        ┌───────────────────┬────────────┴──────┬───────────────────┐
        ▼                   ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  CHILDREN    │    │   PARENTS    │    │  TEACHERS    │    │ CULTURAL     │
│ (Ages 5–13)  │    │  (n = 25)    │    │  (n = 15)    │    │ ELDERS (n=10)│
│  (n = 60)    │    └──────────────┘    └──────────────┘    └──────────────┘
└───────┬──────┘
        │
        ├─► Cohort A (Ages 5–7): Pre-literate / Early childhood (n = 20)
        ├─► Cohort B (Ages 8–10): Primary school learners (n = 20)
        └─► Cohort C (Ages 11–13): Fluent reading / Youth ambassadors (n = 20)
```

- **Children (n = 60):** Stratified across three age groups to evaluate touch precision, literacy reliance, and gamification appeal.
- **Parents (n = 25):** Evaluated on trust, ease of supervision, privacy confidence (COPPA), and offline convenience.
- **Teachers & Educators (n = 15):** Evaluated on classroom utility, curriculum alignment, and student engagement monitoring.
- **Cultural Elders & Linguists (n = 10):** Evaluated on phonetic accuracy, orthography standards, tonal naturalness, and traditional storytelling integrity.

---

## 3. USER TESTING SESSION PROCEDURES

### 3.1 Standardized 45-Minute Session Protocol

```
┌─────────────────────────────────────────────────────────────────┐
│              STANDARDIZED USER TESTING SESSION                  │
├───────────────────┬─────────────────────────────────────────────┤
│ Phase / Duration  │ Protocol & Execution Steps                  │
├───────────────────┼─────────────────────────────────────────────┤
│ 1. Pre-Session    │ • Sanitize tablet & pre-load Alpha build    │
│    (10 Mins)      │ • Verify offline mode (Airplane mode ON)    │
│                   │ • Obtain parent/guardian informed consent   │
├───────────────────┼─────────────────────────────────────────────┤
│ 2. Unassisted     │ • Place tablet in front of child            │
│    Discovery      │ • Prompt: "Explore the app however you like"│
│    (15 Mins)      │ • Observer logs taps, pauses, & hesitations │
├───────────────────┼─────────────────────────────────────────────┤
│ 3. Guided Tasks   │ • Prompt child to complete Node 1 & Node 2  │
│    (10 Mins)      │ • Record time-on-task & error occurrences   │
├───────────────────┼─────────────────────────────────────────────┤
│ 4. Post-Session   │ • Child Smiley-Scale interview (1–5)        │
│    Interview      │ • 24-hour delayed recall follow-up scheduled │
│    (10 Mins)      │ • Teacher/Parent observation form logged    │
└───────────────────┴─────────────────────────────────────────────┘
```

---

## 4. CORE USER TASKS & MEASUREMENT MATRIX

### 4.1 Task Completion Benchmarks

| Task ID | Task Description | Target Metric | Failure / Friction Trigger |
| :--- | :--- | :--- | :--- |
| **TASK-01** | Open app & tap "START ADVENTURE" | < 5 seconds to action | Child asks "What do I press?" |
| **TASK-02** | Select Node 1 on World Map | < 10 seconds to launch | Taps locked nodes repeatedly |
| **TASK-03** | Listen to Bakenye phrase audio | 100% audio triggered | Fails to recognize speaker icon |
| **TASK-04** | Complete picture-sound matching | ≥ 90% first-try accuracy | Taps wrong image > 3 times |
| **TASK-05** | Claim 3-star reward in modal | High emotional delight | Ignores reward continue button |
| **TASK-06** | Return to map & select Node 2 | Self-initiated continuation | Exits app after single lesson |

---

## 5. UX RESEARCH EVALUATION FRAMEWORK

### 5.1 Heuristic & Interaction Scoring System

```
┌─────────────────────────────────────────────────────────────────┐
│                    UX RESEARCH EVALUATION MATRIX                │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Evaluation Dimension│ Rating Criteria      │ Field Diagnostic     │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Ergonomics & Touch│ Touch Target Size    │ Taps hit target on   │
│                   │ (≥ 48dp x 48dp)      │ first attempt without│
│                   │                      │ accidental neighbor  │
│                   │                      │ triggers             │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Visual Hierarchy  │ High Contrast &      │ Children eyes follow │
│                   │ Clear Imagery        │ hero image ➔ audio   │
│                   │                      │ button ➔ option grid │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Audio Feedback    │ Low Latency & Clear  │ Sound plays < 50ms;  │
│                   │ Tonal Distinction    │ zero clipping or     │
│                   │                      │ distortion           │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Emotional Delight │ Gamified Sparkle &   │ Child smiles, claps, │
│                   │ Positive Sounds      │ or verbalizes joy    │
│                   │                      │ upon earning stars   │
└───────────────────┴──────────────────────┴──────────────────────┘
```

---

## 6. EDUCATIONAL EFFECTIVENESS TESTING

### 6.1 Three-Phase Vocabulary Retention Protocol

To verify that Bakenye Kids delivers genuine learning outcomes rather than passive screen time, a pre/post-test recall methodology is enforced:

1. **Phase 1: Pre-Test Baseline (Day 0):** Show child 10 flashcard illustrations (e.g., *Enngege*, *Eryato*, *Omwana*) without text/audio and ask for native Bakenye names.
2. **Phase 2: Learning Intervention (Day 0):** Child completes Lesson 1 and Lesson 2 in Bakenye Kids (10 minutes of interactive play).
3. **Phase 3: Immediate & Delayed Post-Test (Day 1):** Measure vocabulary recall immediately after the session and 24 hours later.

$$\text{Vocabulary Retention Rate (\%)} = \left( \frac{\text{Post-Test Correct Words} - \text{Pre-Test Baseline}}{\text{Total New Vocabulary Words Introduced}} \right) \times 100$$

*Target Benchmark:* **≥ 75% Retention Rate** across all cohorts after 24 hours.

---

## 7. CULTURAL VALIDATION PROCESS FOR ELDERS

### 7.1 Elder Review Decision Matrix

Every vocabulary term, audio pronunciation clip, and traditional story must receive formal evaluation from the **Bakenye Language & Culture Council (BLCC)** elders:

```
                  ┌─────────────────────────┐
                  │ ELDER COUNCIL EVALUATION│
                  └────────────┬────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         ▼                     ▼                     ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ 1. APPROVED      │  │ 2. REVISION REQ. │  │ 3. REJECTED      │
│ (100% Consensus) │  │ (Minor Accent /  │  │ (Inaccurate or   │
│ ➔ Publish to App │  │   Spelling Fix)  │  │  Sacred Material)│
└──────────────────┘  └────────┬─────────┘  └────────┬─────────┘
                               │                     │
                               ▼                     ▼
                      Re-record / Edit       Permanently Delete
                      & Resubmit             from Repository
```

---

## 8. TELEMETRY, ANALYTICS, & FEEDBACK SYSTEM

### 8.1 Offline Privacy-Preserving Telemetry Schema

The app logs anonymous local analytics stored securely in Room SQLite to identify drop-off points without collecting any Personal Identifiable Information (PII):

```json
{
  "session_id": "anon_session_89231",
  "device_model": "Tecno_Spark_8",
  "android_api": 30,
  "lesson_id": "L1_GREETINGS",
  "time_spent_seconds": 184,
  "attempts_count": 5,
  "error_clicks_count": 1,
  "slow_audio_used_count": 2,
  "completed_successfully": true
}
```

---

## 9. IMPROVEMENT PRIORITIZATION FRAMEWORK

### 9.1 Defect & Enhancement Classification Matrix

```
┌─────────────────────────────────────────────────────────────────┐
│               DEFECT PRIORITIZATION TAXONOMY                    │
├─────────────┬─────────────────────────┬─────────────────────────┤
│ Severity    │ Problem Definition      │ Resolution Timeline     │
├─────────────┼─────────────────────────┼─────────────────────────┤
│ CRITICAL    │ Child cannot complete   │ Fixed within 24 hours;  │
│ (P0)        │ activity, app crashes,  │ blocks testing continuation
│             │ or audio fails to play  │                         │
├─────────────┼─────────────────────────┼─────────────────────────┤
│ HIGH        │ Child hesitates > 15s   │ Fixed in Sprint 1 patch │
│ (P1)        │ due to unclear icon or  │                         │
│             │ confusing navigation    │                         │
├─────────────┼─────────────────────────┼─────────────────────────┤
│ MEDIUM      │ Visual layout jitter or │ Scheduled for Sprint 2  │
│ (P2)        │ non-optimal font size   │ refinement              │
├─────────────┼─────────────────────────┼─────────────────────────┤
│ LOW         │ Cosmetic enhancement or │ Added to post-Beta      │
│ (P3)        │ minor particle effect   │ backlog                 │
└─────────────┴─────────────────────────┴─────────────────────────┘
```

---

## 10. ALPHA RELEASE CHECKLIST

- [x] **Technical Readiness:**
  - [x] Stable APK compiled (`compile_applet`) without syntax or runtime crashes.
  - [x] 100% offline functionality verified in Airplane Mode.
  - [x] "Reset Local Progress" developer action accessible in debug settings.
- [x] **Cultural & Educational Readiness:**
  - [x] All 20 initial vocabulary items signed off by BLCC elders.
  - [x] Native audio clips verified for natural tone and clarity.
- [x] **Operational Readiness:**
  - [x] Paper-based observation sheets printed for research observers.
  - [x] Parent informed consent forms translated into Luganda and Bakenye.

---

## 11. FIRST 30-DAY IMPROVEMENT ROADMAP

```
WEEK 1: Field Data Collection
├── Conduct 60 child testing sessions in Lake Kyoga primary schools
├── Gather parent and elder observation logs
└── Aggregate anonymous local telemetry metrics

WEEK 2: Major UX & Interaction Fixes
├── Address P0 and P1 touch target and navigation friction points
├── Optimize audio playback latency and button responsiveness
└── Refine option card visual feedback animations

WEEK 3: Learning & Pedagogical Enhancements
├── Adjust lesson difficulty curve based on activity error rates
├── Enhance reward celebration modal with louder audio cues and particle effects
└── Integrate 0.75x slow pronunciation toggle directly on activity cards

WEEK 4: Beta Preparation & Final Sign-Off
├── Perform full regression test suite across low-end target devices
├── Obtain final sign-off from BLCC Elders Council on updated content
└── Package Release Candidate APK for Beta deployment
```

---

> **The next objective is not adding more systems. The next objective is proving that a child can learn, enjoy, and return to Bakenye Kids.**

---

*Verified & Adopted by the Product Management, UX Research, & Educational Testing Directorate, 2026.*
