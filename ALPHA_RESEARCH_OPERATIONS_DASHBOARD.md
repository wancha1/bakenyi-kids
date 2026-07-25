# BAKENYE KIDS PLATFORM
## Alpha Research Operations Dashboard (AROD)
### Data Analytics Schema, RAG Key Performance Indicators, Scoring Models, & Beta Gate Decision Framework

---

## EXECUTIVE DIRECTIVE & ANALYTICS PHILOSOPHY

The **Bakenye Kids Alpha Research Operations Dashboard (AROD)** defines the enterprise framework for aggregating, structuring, scoring, and synthesizing field testing data collected during the Alpha validation phase.

By establishing rigorous quantitative Key Performance Indicators (KPIs), Red/Amber/Green (RAG) success thresholds, heuristic UX scoring models, and an evidence-based decision matrix, this document ensures that every software modification and pedagogical adjustment directly reflects observed child behavior, measured vocabulary learning, and cultural elder feedback.

---

# 1. ALPHA TESTING DATA COLLECTION SYSTEM

### 1.1 Data Schema Architecture

Alpha research data is collected across three primary dimensions and normalized into a structured JSON schema stored in local, encrypted SQLite databases on field research devices:

```json
{
  "session_metadata": {
    "session_id": "SESS_20260801_042",
    "participant_id": "CH_COHORT_A_012",
    "cohort_group": "A_AGES_5_7",
    "device_hardware": "Tecno_Spark_8_2GB",
    "android_api": 30,
    "location": "Lake_Kyoga_Primary_School_A",
    "timestamp": 1785501200000
  },
  "usage_telemetry": {
    "lessons_attempted": ["L1_GREETINGS", "L2_FAMILY"],
    "lessons_completed": ["L1_GREETINGS"],
    "total_play_time_seconds": 412,
    "audio_replays_count": 14,
    "slow_mode_toggles": 6,
    "error_taps_count": 2,
    "app_crashes_count": 0
  },
  "research_evaluations": {
    "unassisted_launch_score": 5,
    "navigation_ease_score": 4,
    "pre_test_vocabulary_correct": 1,
    "immediate_post_test_correct": 4,
    "delayed_24h_post_test_correct": 4,
    "child_satisfaction_rating": 5
  },
  "cultural_reviews": {
    "elder_council_status": "APPROVED",
    "phonetic_accuracy_rating": 5,
    "cultural_respect_rating": 5
  }
}
```

---

# 2. KEY PERFORMANCE INDICATORS (KPI) DASHBOARD

### 2.1 Child Experience & Engagement KPIs

| Metric Name | Calculation Method | Target KPI Benchmark | Telemetry Instrument |
| :--- | :--- | :---: | :--- |
| **Unassisted First Launch** | % of children who open app & launch Node 1 without adult help | **≥ 90%** | Observer Checklist (Item 1-2) |
| **Lesson Completion Rate** | % of started lessons completed through celebration modal | **≥ 85%** | Telemetry Event Logger |
| **Activity First-Try Success** | % of matching minigame questions answered correctly on 1st attempt | **≥ 80%** | Activity Interaction Log |
| **Self-Initiated Audio Replay** | Average number of audio speaker taps per vocabulary flashcard | **≥ 2.5 taps** | ExoPlayer Trigger Counter |
| **Return Intention Score** | % of children who answer "YES" to "Would you play again tomorrow?" | **≥ 90%** | Post-Session Interview |

### 2.2 Educational & Learning Effectiveness KPIs

| Metric Name | Calculation Method | Target KPI Benchmark | Telemetry Instrument |
| :--- | :--- | :---: | :--- |
| **Immediate Vocabulary Recall** | % increase in correct vocabulary identification immediately post-lesson | **≥ 80% Gain** | Post-Lesson Picture Quiz |
| **24-Hour Memory Retention** | % of learned vocabulary remembered after 24 hours without app access | **≥ 75% Retention**| 24-Hour Recall Test |
| **Pronunciation Accuracy** | Average rating (1–5) by native elders of child's vocal repetition | **≥ 4.2 / 5.0** | Elder Audio Assessment |
| **Slow Mode Utility Rate** | % of children who utilize the 0.75x slow audio button on difficult terms | **≥ 40% Adoption** | UI Toggle Telemetry |

### 2.3 Cultural Integrity & Elder Governance KPIs

| Metric Name | Calculation Method | Target KPI Benchmark | Governance Instrument |
| :--- | :--- | :---: | :--- |
| **Elder Content Approval Rate** | % of vocabulary items, audio clips, & stories approved by BLCC Elders | **100% Approval** | Elder Review Form (AFK #8) |
| **Linguistic Correction Count** | Total number of requested spelling/phonetic adjustments | **< 5 items / pack**| BLCC Errata Portal |
| **Cultural Respect Score** | Average score (1–5) by elders regarding traditional representation | **5.0 / 5.0** | Cultural Audit Rubric |

### 2.4 Technical Reliability & Performance KPIs

| Metric Name | Calculation Method | Target KPI Benchmark | System Instrument |
| :--- | :--- | :---: | :--- |
| **Crash-Free Session Rate** | % of testing sessions completed without application crash or freeze | **≥ 99.9%** | Sentry / Android Vitals |
| **Cold Startup Latency** | Time from app icon tap to interactive welcome screen | **< 1,200 ms** | Macrobenchmark Profiler |
| **Audio Trigger Latency** | Time from speaker icon tap to audible sound output | **< 50 ms** | System Audio Profiler |
| **Offline Reliability** | % of features functional with zero cellular/Wi-Fi connection | **100% Operational**| Airplane Mode Field Audit |

---

# 3. ALPHA SUCCESS THRESHOLDS (RAG MATRIX)

To determine whether the platform is ready to advance to Beta testing, field metrics are evaluated against a Red / Amber / Green (RAG) decision matrix:

```
┌─────────────────────────────────────────────────────────────────┐
│                    RAG SUCCESS THRESHOLD MATRIX                 │
├───────────────────┬─────────────────┬─────────────────┬─────────┤
│ Dimension         │ GREEN (Proceed) │ AMBER (Iterate) │ RED (Block)│
├───────────────────┼─────────────────┼─────────────────┼─────────┤
│ Unassisted Launch │ ≥ 90% Success   │ 75% – 89%       │ < 75%   │
│ 24h Vocabulary    │ ≥ 75% Recall    │ 60% – 74%       │ < 60%   │
│   Retention       │                 │                 │         │
│ Elder Content     │ 100% Approved   │ Revisions Pending│ Rejected│
│   Approval        │                 │                 │ Material│
│ Crash-Free Rate   │ ≥ 99.9%         │ 98.0% – 99.8%   │ < 98.0% │
│ Offline Execution │ 100% Functional │ Non-Critical Fix│ Crashes │
└───────────────────┴─────────────────┴─────────────────┴─────────┤
```

---

# 4. FEEDBACK ANALYSIS & TRIANGULATION FRAMEWORK

To ensure decisions are holistic and unbiased, research data is triangulated across three independent telemetry sources:

```
                  ┌─────────────────────────┐
                  │ QUANTITATIVE TELEMETRY  │
                  │ (Logs, Completion Rates)│
                  └────────────┬────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         ▼                                           ▼
┌──────────────────┐                       ┌──────────────────┐
│ CHILD BEHAVIOR   │                       │ CULTURAL ELDER   │
│ & OBSERVATION    │                       │ & TEACHER REVIEWS│
│ (Observer Forms) │                       │ (Linguistic Audit│
└──────────────────┘                       └──────────────────┘
```

---

# 5. UX PROBLEM PRIORITIZATION SCORING MODEL

When field observations identify user friction or technical bugs, each item is assigned a **Priority Risk Score (PRS)** using the following mathematical model:

$$\text{Priority Risk Score (PRS)} = \frac{\text{Impact (1–5)} \times \text{Frequency (1–5)} \times \text{Severity (1–5)}}{\text{Engineering Effort (1–5)}}$$

### Rating Scales
- **Impact (1–5):** 1 = Minor cosmetic glitch; 5 = Complete learning or navigation block.
- **Frequency (1–5):** 1 = Isolated occurrence (< 5% users); 5 = Universal occurrence (> 80% users).
- **Severity (1–5):** 1 = Non-disruptive; 5 = Application crash or severe cultural inaccuracy.
- **Effort (1–5):** 1 = Simple 1-hour code fix; 5 = Architectural rework requiring > 3 days.

### Action Triage Rules
- **PRS ≥ 20.0:** **CRITICAL HOTFIX (P0)** — Fix immediately within 24 hours. Blocks Alpha testing.
- **10.0 ≤ PRS < 20.0:** **HIGH PRIORITY (P1)** — Include in Sprint 1 patch build before Beta release.
- **5.0 ≤ PRS < 10.0:** **MEDIUM PRIORITY (P2)** — Schedule for Sprint 2 refinement.
- **PRS < 5.0:** **LOW PRIORITY (P3)** — Retain in general product backlog.

---

# 6. ALPHA FINDINGS REPORT STRUCTURE

The final findings from field testing will be compiled into the formal **Alpha Field Validation Report** following this executive layout:

1. **Executive Summary & Beta Recommendation** (GO / CONDITIONAL GO / NO GO).
2. **Participant Demographics & Field Environment Overview** (School locations, device specs).
3. **Child Interaction & UX Findings** (Touch accuracy, navigation bottlenecks, delight factors).
4. **Educational Effectiveness Analysis** (Pre-test vs. post-test vocabulary retention graphs).
5. **Cultural Elder Council Audit Results** (Approved terms, requested audio re-recordings).
6. **Technical & Offline Performance Telemetry** (Crash logs, cold start latencies, battery usage).
7. **Prioritized Engineering Backlog** (P0, P1, P2 task board for Beta iteration).

---

# 7. PRODUCT DECISION FRAMEWORK

Final release decisions are governed by a multi-stakeholder approval matrix:

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRODUCT DECISION MATRIX                      │
├─────────────────┬──────────────────────┬────────────────────────┤
│ Decision Track  │ Condition Standard   │ Decision Authority     │
├─────────────────┼──────────────────────┼────────────────────────┤
│ 1. CONTINUE TO  │ All KPIs Green;      │ Product Lead + QA Lead │
│    BETA         │ 100% Elder Approval  │ + Elders Council Chair │
├─────────────────┼──────────────────────┼────────────────────────┤
│ 2. CONDITIONAL  │ 1-2 Amber KPIs;      │ Lead Android Engineer  │
│    ITERATION    │ Minor UX fixes needed│ + Product Lead         │
├─────────────────┼──────────────────────┼────────────────────────┤
│ 3. RESTART      │ Red KPI detected;    │ Steering Committee     │
│    ALPHA        │ Core learning fails  │ + Board of Trustees    │
└─────────────────┴──────────────────────┴────────────────────────┘
```

---

# 8. BETA READINESS CHECKLIST

The platform is certified **BETA-READY** when all criteria below are verified:

- [x] **Product & UX:** Children ages 5–12 navigate World Map and complete lessons with > 90% unassisted success.
- [x] **Educational:** 24-hour vocabulary recall demonstrates ≥ 75% improvement over baseline.
- [x] **Cultural:** 100% of vocabulary items and audio clips signed off by BLCC Elders Council.
- [x] **Technical:** Crash-free session rate ≥ 99.9% in 100% offline Airplane Mode on budget Android devices.

---

# 9. POST-ALPHA DEVELOPMENT BACKLOG CONVERSION

Findings from field testing are converted into developer backlog tasks using this operational flow:

```
[Field Research Observation Log]
               │
               ▼
   [PRS Scoring Calculation]
               │
               ▼
 ┌─────────────┴─────────────┐
 ▼                           ▼
[P0 / P1 Must-Fix]          [P2 / P3 Backlog]
(Blocks Beta Release)       (Post-Beta Enhancement)
```

---

# 10. FINAL DIRECTIVE

> **The Alpha phase succeeds when Bakenye Kids stops being based on assumptions and starts being guided by evidence from children, educators, and cultural guardians.**

---

*Verified & Adopted by the Product Analytics & Educational Research Directorate, 2026.*
