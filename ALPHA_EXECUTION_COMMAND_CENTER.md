# BAKENYE KIDS PLATFORM
## Alpha Execution Command Center (AECC)
### Operational Field Operations Manual & Field Execution Playbook

---

## EXECUTIVE DIRECTIVE

The **Bakenye Kids Alpha Execution Command Center (AECC)** serves as the operational field execution manual for the Alpha deployment team in the Lake Kyoga region of Uganda.

This playbook governs day-to-day field operations, device preparation, research protocols, team roles, data collection workflows, daily triage meetings, and issue escalation pathways to guarantee that the Alpha testing phase is conducted safely, ethically, reliably, and efficiently.

---

# 1. ALPHA LAUNCH PREPARATION CHECKLIST

Before launching field testing sessions, the team must complete and sign off on all preparation items:

### 1.1 Technical Readiness Checklist
- [ ] **APK Build Verification:** Stable Alpha APK (`com.aistudio.bakenyekids.app`) built via `compile_applet` and verified on test hardware.
- [ ] **Offline Execution Verification:** App tested in 100% Airplane Mode on 5 test devices; zero network exception popups.
- [ ] **Database Pre-seeding:** SQLite database verified pre-seeded with 20 canonical Bakenye vocabulary items on initial cold launch.
- [ ] **Data Reset Procedure:** "Reset Local Progress" action verified in debug drawer to allow rapid session resets between participants.
- [ ] **Device Provisioning:** 10 test tablets/phones fully charged, sanitized, updated to target brightness, and loaded with the Alpha APK.

### 1.2 Content Verification Checklist
- [ ] **Vocabulary Pack:** 20 Bakenye vocabulary items (Greetings, Family, Lake/Nature, Animals) confirmed in app assets.
- [ ] **Audio Assets:** High-quality native audio recordings verified for all 20 terms with zero distortion or truncation.
- [ ] **Illustrations & Vector Graphics:** Vector illustrations rendered properly on various screen resolutions (720p to 1200p).

### 1.3 Research & Compliance Checklist
- [ ] **Informed Consent Forms:** 100 printed copies of Parental Consent Forms (AFK #2) ready in Luganda and Bakenye.
- [ ] **Observation Sheets:** 100 printed copies of Child Observation Sheets (AFK #3) and Vocabulary Tests (AFK #5).
- [ ] **Sanitization Kits:** Microfiber cloths, alcohol cleaning wipes, and hand sanitizer stocked for field devices.

---

# 2. FIELD TESTING SCHEDULE (30-DAY TIMELINE)

```
┌─────────────────────────────────────────────────────────────────┐
│                    30-DAY FIELD OPERATIONS SCHEDULE             │
├─────────────┬───────────────────────────────────────────────────┤
│ Day 0       │ Field Team Onboarding, Device Provisioning & Setup │
├─────────────┼───────────────────────────────────────────────────┤
│ Days 1 – 7  │ Child Testing Sessions (60 Participants, Schools) │
├─────────────┼───────────────────────────────────────────────────┤
│ Days 8 – 10 │ Parent & Teacher Feedback Sessions (40 Adults)     │
├─────────────┼───────────────────────────────────────────────────┤
│ Days 11 – 14│ Elder Cultural Review Sessions (10 BLCC Elders)   │
├─────────────┼───────────────────────────────────────────────────┤
│ Days 15 – 21│ Data Aggregation, Analysis, & PRS Issue Scoring   │
├─────────────┼───────────────────────────────────────────────────┤
│ Days 22 – 30│ Engineering Improvement Sprint & Beta Readiness    │
└─────────────┴───────────────────────────────────────────────────┘
```

---

# 3. FIELD TEAM ROLES & RESPONSIBILITIES

```
┌─────────────────────────────────────────────────────────────────┐
│                    FIELD OPERATIONS TEAM ROLES                  │
├───────────────────┬─────────────────────────────────────────────┤
│ Role              │ Primary Responsibilities                    │
├───────────────────┼─────────────────────────────────────────────┤
│ Alpha Coordinator │ Oversees field schedule, manages school     │
│                   │ relationships, enforces safety protocols    │
├───────────────────┼─────────────────────────────────────────────┤
│ Test Facilitator  │ Welcomes children, delivers scripts, guides │
│                   │ sessions, ensures child comfort and agency │
├───────────────────┼─────────────────────────────────────────────┤
│ Observer          │ Quietly logs touch interactions, time-on-   │
│                   │ task, error taps, and emotional delight     │
├───────────────────┼─────────────────────────────────────────────┤
│ Technical Support │ Manages hardware, battery levels, app resets│
│                   │ APK updates, and local device telemetry logs│
├───────────────────┼─────────────────────────────────────────────┤
│ Cultural Reviewer │ Coordinates BLCC Elder sessions, logs audio │
│                   │ re-recording requests and spelling fixes    │
├───────────────────┼─────────────────────────────────────────────┤
│ Data Analyst      │ Aggregates daily telemetry, calculates PRS  │
│                   │ scores, and builds daily triage reports     │
└───────────────────┴─────────────────────────────────────────────┘
```

---

# 4. DEVICE TESTING MATRIX

Field testing utilizes a representative hardware matrix of entry-level, mid-range, and tablet Android devices typical of rural and urban Ugandan households and primary schools:

| Device Category | Hardware Specification | Test Focus Area | Performance Target |
| :--- | :--- | :--- | :--- |
| **Entry Phone** | Tecno Spark 8 / Itel A58 (1.5GB RAM, Quad-Core, Android 11 Go) | Low-end performance, startup time, touch target scaling | Cold start < 1,500ms; smooth touch response |
| **Mid-Range Phone** | Samsung Galaxy A14 / Xiaomi Redmi 10 (3GB RAM, Android 13) | Standard UX flow, audio latency, battery consumption | Cold start < 1,000ms; audio latency < 50ms |
| **Android Tablet** | Lenovo Tab M10 / Nokia T20 (10.1", 3GB RAM, Android 12) | Large display scaling, multi-touch ergonomics, classroom usage | 60 FPS Canvas rendering; 0 jank frames |

---

# 5. TESTING SESSION WORKFLOW (45-MINUTE PROTOCOL)

Every testing session follows a strict, child-friendly 45-minute operational timeline:

```
[00:00 - 00:05] Phase 1: Welcome & Warm-up (Deliver Script, Confirm Agency)
       │
       ▼
[00:05 - 00:20] Phase 2: Independent Exploration (Unassisted Child Play)
       │
       ▼
[00:20 - 00:30] Phase 3: Guided Learning Tasks (Nodes 1 & 2 Completion)
       │
       ▼
[00:30 - 00:40] Phase 4: Child Interview & Delayed Assessment Follow-up
       │
       ▼
[00:40 - 00:45] Phase 5: Device Sanitization & Progress Reset
```

### Facilitator Instructions
- **Phase 1 (Min 0-5):** Deliver the welcoming script (AFK #1). Ensure the child knows they are not being graded.
- **Phase 2 (Min 5-20):** Place tablet in front of child. Observe where their eyes and fingers go. Do not interrupt unless child expresses distress.
- **Phase 3 (Min 20-30):** Ask child to select Node 1 on the map, play audio, and complete the picture-sound matching activity.
- **Phase 4 (Min 30-40):** Conduct the age-appropriate interview (AFK #4) and score vocabulary recall (AFK #5).
- **Phase 5 (Min 40-45):** Thank child, provide a small sticker or reward, sanitize device, and execute progress reset.

---

# 6. DATA COLLECTION & PRIVACY PROCEDURE

1. **Offline-First Storage:** All interaction logs, time-on-task metrics, and observation forms are stored locally on the research device in encrypted SQLite tables.
2. **Zero PII Policy:** Participants are identified solely by anonymous codes (e.g., `CH_COHORT_A_012`). No full names, photos, or location GPS coordinates are stored.
3. **Daily Export:** At the end of each field day, research devices export JSON logs via encrypted USB transfer to the Lead Analyst's secure laptop.

---

# 7. DAILY ALPHA REVIEW MEETING

At 17:00 at the end of each field testing day, the team conducts a mandatory 30-minute daily review:

```
┌─────────────────────────────────────────────────────────────────┐
│                    DAILY ALPHA REVIEW PROTOCOL                  │
├───────────────────┬─────────────────────────────────────────────┤
│ Agenda Step       │ Action Required                             │
├───────────────────┼─────────────────────────────────────────────┤
│ 1. Telemetry Sync │ Combine exported daily JSON logs into AROD  │
│                   │ dashboard                                   │
├───────────────────┼─────────────────────────────────────────────┤
│ 2. Issue Triage   │ Review logged bugs and child friction points│
├───────────────────┼─────────────────────────────────────────────┤
│ 3. PRS Calculation│ Assign Impact, Frequency, Severity, Effort  │
│                   │ scores                                      │
├───────────────────┼─────────────────────────────────────────────┤
│ 4. Decision      │ Assign tasks: Fix Immediately (P0), Patch   │
│    Assignment     │ Sprint (P1), or Backlog (P2/P3)             │
└───────────────────┴─────────────────────────────────────────────┘
```

---

# 8. ALPHA ISSUE RESPONSE WORKFLOW

```
[Issue Discovered in Field] ──► [Logged in AFK Bug Tracker]
                                        │
                                        ▼
                           [PRS Scoring Calculation]
                                        │
       ┌────────────────────────────────┼────────────────────────────────┐
       ▼                                ▼                                ▼
[P0: CRITICAL (PRS ≥ 20)]    [P1: HIGH (10 ≤ PRS < 20)]    [P2/P3: MEDIUM/LOW (PRS < 10)]
• Halt testing               • Include in Sprint 1 patch   • Add to post-Beta
• Engineering hotfix < 24h   • Deploy before Beta release  • backlog
```

---

# 9. ALPHA COMPLETION REPORT STRUCTURE

Upon concluding the field sessions, the Alpha Coordinator prepares the final report:

1. **Executive Summary:** Overall Alpha trial outcomes and GO / NO GO decision for Beta.
2. **Participant Demographics:** Summary of 60 children, 25 parents, 15 teachers, and 10 elders tested.
3. **KPI Performance Metrics:** RAG scorecards for UX, Learning, Technical, and Cultural indicators.
4. **Child Usability Findings:** Detailed analysis of touch accuracy, map navigation, and activity completion.
5. **Cultural Elder Sign-Off:** Formal record of approved language assets and required corrections.
6. **Engineering Action Board:** Final list of P0/P1 fixes completed prior to Beta release.

---

# 10. BETA RELEASE DECISION CRITERIA

```
┌─────────────────────────────────────────────────────────────────┐
│                    BETA PROMOTION DECISION GATES                │
├───────────────────┬──────────────────────┬──────────────────────┤
│ Gate Domain       │ Criteria Standard    │ Promotion Threshold  │
├───────────────────┼──────────────────────┼──────────────────────┤
│ UX & Independence │ Child can complete   │ ≥ 90% Unassisted     │
│                   │ lesson independently │ Success              │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Educational       │ Vocabulary recall    │ ≥ 75% 24-Hour        │
│ Effectiveness     │ verified             │ Retention Gain       │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Cultural          │ Language content     │ 100% BLCC Elder      │
│ Governance        │ approved             │ Sign-off             │
├───────────────────┼──────────────────────┼──────────────────────┤
│ Technical         │ Offline stability    │ ≥ 99.9% Crash-Free   │
│ Reliability       │ verified             │ Sessions             │
└───────────────────┴──────────────────────┴──────────────────────┘
```

- **PROCEED TO BETA:** All four gates meet or exceed promotion thresholds.
- **DELAY BETA:** One or more gates fall into Amber/Red RAG status; mandatory 1-week iteration sprint triggered.

---

> **The Alpha phase is complete only when real user evidence has replaced assumptions.**

---

*Verified & Approved for Operational Field Execution by the Bakenye Kids Alpha Program Directorate, 2026.*
