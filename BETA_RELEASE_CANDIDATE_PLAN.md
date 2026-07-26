# BAKENYE KIDS PLATFORM
## Beta Release Candidate Plan (BRC)
### Release Management Specification, Deployment Protocols, Quality Gates, & Incident Response Playbook

---

## EXECUTIVE DIRECTIVE & RELEASE PHILOSOPHY

The **Bakenye Kids Beta Release Candidate Plan (BRC)** governs the final technical build preparation, quality gate verification, field distribution strategy, real-time monitoring, and incident management procedures for deploying the Beta Release Candidate of Bakenye Kids.

Following the successful field validation of the Alpha MVP in the Lake Kyoga region, the Beta release transitions the application from a controlled research prototype to a scalable, field-tested educational product. This document ensures that every release candidate build is deterministic, secure, culturally verified, 100% offline-functional, and ready for deployment across primary schools and households in Uganda.

---

# 1. BETA RELEASE OBJECTIVES

### 1.1 Core Validation Goals
The Beta deployment phase must prove product reliability at scale across expanded user cohorts without compromising the core child learning experience.

```
┌─────────────────────────────────────────────────────────────────┐
│                    BETA RELEASE CORE OBJECTIVES                 │
├───────────────────┬─────────────────────────────────────────────┤
│ Objective Domain  │ Validation Target Benchmark                 │
├───────────────────┼─────────────────────────────────────────────┤
│ 1. Stable Learning│ ≥ 99.9% crash-free session rate across all  │
│    Experience     │ low-end target devices (1.5GB–3GB RAM)      │
├───────────────────┼─────────────────────────────────────────────┤
│ 2. Independent    │ ≥ 95.0% of child participants complete      │
│    Navigation     │ lessons without requiring adult help        │
├───────────────────┼─────────────────────────────────────────────┤
│ 3. Vocabulary     │ ≥ 80.0% 24-hour memory recall gain verified │
│    Retention      │ across 300+ expanded child participants     │
├───────────────────┼─────────────────────────────────────────────┤
│ 4. Cultural       │ 100% BLCC Elders Council sign-off on all    │
│    Authenticity   │ expanded vocabulary items and audio clips   │
├───────────────────┼─────────────────────────────────────────────┤
│ 5. Offline        │ 100% feature functionality in zero-connectivity│
│    Resilience     │ rural classroom settings                    │
└───────────────────┴─────────────────────────────────────────────┘
```

---

# 2. BETA SCOPE DEFINITION

To ensure complete stability and prevent feature creep, the Beta release scope is strictly bounded:

```
┌─────────────────────────────────────────────────────────────────┐
│                    BETA RELEASE SCOPE BOUNDARIES                │
├───────────────────────────────────┬─────────────────────────────┤
│ INCLUDED IN BETA SCOPE            │ EXCLUDED FROM BETA SCOPE    │
├───────────────────────────────────┼─────────────────────────────┤
│ • World 1 ("Lake Kyoga Shores")   │ • Online Multi-player Games │
│ • 20 Canonical Bakenye Vocabulary  │ • Cloud User Account Sync   │
│ • Interactive World Map Canvas    │ • In-App Purchases / Ads    │
│ • Audio Flashcards & 0.75x Slow   │ • Complex Admin CMS Modules │
│ • Picture-Sound Matching Minigame │ • Unvalidated Extra Languages│
│ • Star Reward Celebration Modal   │ • Background Web Servers    │
│ • Local Room SQLite Persistence   │ • Heavy ML Speech Recog.    │
└───────────────────────────────────┴─────────────────────────────┘
```

---

# 3. RELEASE CANDIDATE BUILD PROCESS

### 3.1 Versioning & Branching Strategy

The build pipeline enforces strict Semantic Versioning (`MAJOR.MINOR.PATCH`):
- **Version Name:** `1.0.0-beta.1`
- **Version Code:** `100010`

```
  [main]  ───────────────────────────────────────────► (Production V1.0)
    ▲
    │ (PR Merge after QA Verification)
  [release/v1.0.0-beta.1] ───► [compile_applet] ───► [Signed APK / AAB]
    ▲
    │ (Feature Branches)
  [feature/abib-p1-locked-node]
```

### 3.2 Build Verification & Signing Checklist
1. **Compilation Check:** Run `compile_applet` to verify zero syntax or import errors.
2. **APK Optimization:** Verify final APK bundle size is **< 25 MB** with R8 code shrinking enabled.
3. **Keystore Integrity:** Sign APK using official release keystore (`debug.keystore` maintained without modification).
4. **Pre-seed Database Verification:** Verify Room SQLite pre-seeded database initializes clean state with 20 canonical items on cold start.

---

# 4. BETA TESTING GROUPS & ONBOARDING

### 4.1 Participant Demographics & Distribution

```
                           ┌───────────────────────────┐
                           │    BETA TESTING GROUPS    │
                           └─────────────┬─────────────┘
                                         │
        ┌───────────────────┬────────────┴──────┬───────────────────┐
        ▼                   ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  CHILDREN    │    │   PARENTS    │    │  TEACHERS    │    │ CULTURAL     │
│ (Ages 5–13)  │    │ (n = 100)    │    │  (n = 30)    │    │ ELDERS (n=15)│
│ (n = 300)    │    └──────────────┘    └──────────────┘    └──────────────┘
└──────────────┘
```

### 4.2 Onboarding Protocol
- **School Pilot Program:** Deploy pre-loaded offline tablets to 5 partner primary schools in the Lake Kyoga basin.
- **Parent Orientation:** Conduct 15-minute community briefings explaining privacy protections (COPPA compliance, 0% internet required).
- **Teacher Briefings:** Provide educators with simple 1-page instructional guides on integrating Bakenye Kids into daily morning reading circles.

---

# 5. BETA DISTRIBUTION STRATEGY

### 5.1 Multi-Channel Offline Distribution

Because target learning environments frequently lack stable internet, distribution combines digital and offline channels:

```
┌─────────────────────────────────────────────────────────────────┐
│                    DISTRIBUTION CHANNELS                        │
├───────────────────┬─────────────────────────────────────────────┤
│ Channel           │ Execution Methodology                       │
├───────────────────┼─────────────────────────────────────────────┤
│ 1. School Hubs    │ Direct USB/OTG flash drive installation     │
│                   │ on school tablet inventories                │
├───────────────────┼─────────────────────────────────────────────┤
│ 2. Community SDs  │ Pre-loaded MicroSD cards distributed to    │
│                   │ local teachers and community centers        │
├───────────────────┼─────────────────────────────────────────────┤
│ 3. Closed Digital │ Google Play Store Closed Beta track for     │
│    Track          │ connected urban parents and educators       │
└───────────────────┴─────────────────────────────────────────────┘
```

---

# 6. FINAL QUALITY GATES (GO / NO-GO CRITERIA)

Before certifying any build as **Beta Release Candidate 1 (v1.0.0-beta.1)**, all four Quality Gates must be signed off:

```
┌─────────────────────────────────────────────────────────────────┐
│                    FINAL BETA QUALITY GATES                     │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Quality Gate      │ Required Standard    │ Verification Method  │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Gate 1: Technical │ ≥ 99.9% Crash-Free;  │ Automated stress test│
│    Reliability    │ Cold Start < 1,000ms │ + Android Vitals     │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Gate 2: Child UX  │ ≥ 95.0% Unassisted   │ Field observation    │
│    Autonomy       │ Navigation Success   │ checklist            │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Gate 3: Learning  │ ≥ 80.0% 24-Hour      │ Pre/post vocabulary  │
│    Effectiveness  │ Retention Gain       │ testing              │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Gate 4: Cultural  │ 100% BLCC Elder      │ Formal Council       │
│    Approval       │ Sign-off             │ Certificate          │
└───────────────────┴──────────────────────┴──────────────────────┘
```

---

# 7. BETA MONITORING DASHBOARD

### 7.1 Real-Time Analytics & Telemetry Matrix

Anonymized local telemetry is stored in encrypted SQLite tables on test devices and aggregated during weekly coordinator check-ins:

```
┌─────────────────────────────────────────────────────────────────┐
│                   BETA MONITORING TELEMETRY                     │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Telemetry Vector  │ Measured Metric      │ Alert Threshold      │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Technical         │ App Crashes / ANRs   │ > 0.1% of sessions   │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Usage             │ Lessons Completed    │ < 80% completion rate│
├───────────────────┼──────────────────────┼──────────────────────┤
│ Pedagogical       │ Slow Mode Toggles    │ < 30% adoption rate  │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Cultural          │ Errata Reports       │ Any reported error   │
└───────────────────┴──────────────────────┴──────────────────────┘
```

---

# 8. BETA FEEDBACK LOOP

The Beta feedback pipeline runs on a 7-day iterative cycle:

```
[Field Feedback Captured] ──► [Weekly Review Meeting]
                                      │
                                      ▼
                        [Prioritization & PRS Score]
                                      │
                                      ▼
                        [Patch Development Sprint]
                                      │
                                      ▼
                        [Verification & OTA Update]
```

---

# 9. BETA INCIDENT RESPONSE PLAYBOOK

When issues arise in the field, they are escalated according to three strict severity tiers:

### Severity Level Specifications

| Severity Level | Definition | Response SLA | Action Required |
| :--- | :--- | :---: | :--- |
| **SEV-1 (CRITICAL)** | Application crash on launch, data corruption, or severe cultural error | **< 4 Hours** | Immediate build rollback; release hotfix patch within 24 hours. |
| **SEV-2 (MAJOR)** | UX friction point causing > 10% session abandonments | **< 24 Hours** | Priority fix scheduled for next weekly Beta patch. |
| **SEV-3 (MINOR)** | Minor visual alignment or cosmetic text spacing glitch | **< 72 Hours** | Logged in backlog for post-Beta refinement. |

---

# 10. BETA EXIT CRITERIA (VERSION 1.0 GRADUATION)

The Beta phase formally concludes and Bakenye Kids graduates to **Version 1.0 Production** when:

1. **Scale Validation:** 300+ children across 5 schools complete World 1 with > 90% unassisted navigation success.
2. **Proven Pedagogical Benefit:** 24-hour vocabulary retention rates consistently exceed 80%.
3. **Uncompromising Technical Stability:** Zero SEV-1 crashes or database corruptions reported over 30 consecutive days of field usage.
4. **Cultural Certification:** BLCC Elders Council grants formal written authorization for national public distribution.

---

# FINAL DIRECTIVE

> **"The Beta phase exists to prove reliability at scale. The goal is not to add everything possible; the goal is to deliver the strongest validated learning experience."**

---

*Certified & Issued by the Beta Release Management & Product Delivery Directorate, 2026.*
