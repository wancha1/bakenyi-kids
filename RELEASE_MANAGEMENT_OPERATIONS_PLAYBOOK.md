# BAKENYE KIDS PLATFORM
## Release Management & Operations Playbook (RMOP)
### Enterprise Operational Playbook for Release Engineering, Deployment, Monitoring, and Digital Heritage Operations

---

## Executive Overview & Operational Directive

The **Bakenye Kids Release Management & Operations Playbook (RMOP)** establishes the operational framework for building, testing, signing, publishing, monitoring, securing, and maintaining the Bakenye Kids platform across its multi-decade operational lifecycle.

Designed for mission-critical educational software and cultural digital preservation, this playbook bridges software engineering best practices with indigenous language governance, ensuring zero downtime, 100% offline reliability, COPPA child privacy compliance, and atomic content updates.

---

## 1. RELEASE MANAGEMENT PHILOSOPHY

### 1.1 Core Release Principles

1. **Cultural Integrity First:** No software feature or deadline takes precedence over linguistic accuracy and elder approval. A release candidate containing unverified audio or disputed orthography MUST be halted immediately.
2. **Deterministic & Immutable Artifacts:** Every build artifact (Android App Bundle, SQLite database, language pack JSON) is deterministically generated, cryptographically signed, and assigned an immutable SHA-256 hash.
3. **Zero-Downtime Offline-First Deployment:** The Android application operates 100% offline. Content releases and application updates sync incrementally without interrupting active learning sessions.
4. **Strict Environmental Segregation:** Production signing keys and live database publishing credentials are stored in secure key management systems (KMS) with access restricted to certified Release Operations Engineers.

### 1.2 Release Categories & Approval Matrix

| Release Category | Scope & Triggers | Approval Authority | Testing Gate Requirement |
| :--- | :--- | :--- | :--- |
| **Major Release (`vX.0.0`)** | New architectural modules, major UI redesigns, new game engines | Board of Trustees + Lead Architect + BLCC Elders | Full E2E Regression + 100% QA + Elder Sign-Off |
| **Minor Release (`v1.X.0`)** | New world additions, minigame templates, feature extensions | Lead Architect + QA Lead | Automated CI Pipeline + Full QA Suite |
| **Patch Release (`v1.0.X`)** | Non-breaking bug fixes, performance optimizations, UI tweaks | QA Lead + DevOps Engineer | Automated Unit & Compose Screenshot Tests |
| **Content Pack Release (`cX.Y`)** | New vocabulary, stories, audio clips, proverbs (No code change) | BLCC Elders Council + Content Manager | Schema Validation + Linguist & Elder Verification |
| **Emergency Patch (`v1.0.X-hotfix`)** | Critical security patch or app-blocking crash | Technical Steering Committee + Release Manager | Accelerated Smoke Suite + Security Scan |

---

## 2. SOFTWARE RELEASE LIFECYCLE

### 2.1 End-to-End Release Workflow

```
┌───────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 1.PLAN    │───►│ 2.DEV & CODE│───►│ 3.AUTOMATED │───►│ 4.QA & UI   │───►│ 5.CULTURAL  │
│ PLANNING  │    │   REVIEW    │    │ CI TESTING  │    │ VALIDATION  │    │  APPROVAL   │
└───────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └──────┬──────┘
                                                                                 │
┌───────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐           │
│ 10. POST  │◄───│ 9. LIVE     │◄───│ 8. STAGED   │◄───│ 7. RELEASE  │◄──────────┘
│  REVIEW   │    │ MONITORING  │    │ PRODUCTION  │    │ CANDIDATE   │
└───────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### 2.2 Stage Exit Criteria

- **Stage 3 (Automated CI):** 100% unit tests pass, 100% Compose screenshot tests pass (Roborazzi), zero static analysis warnings.
- **Stage 5 (Cultural Approval):** Formal digital signature recorded from BLCC Elders Council for all new language pack content.
- **Stage 7 (Release Candidate):** Signed Android App Bundle (`.aab`) generated with SHA-256 checksum, cold startup verified < 1,200 ms.
- **Stage 8 (Staged Rollout):** 10% ➔ 25% ➔ 50% ➔ 100% Play Store rollout over 7 days with crash-free rate > 99.9%.

---

## 3. VERSIONING STRATEGY

### 3.1 Dual Semantic Versioning Standard

The platform maintains two independent versioning tracks: **Software Engine Versioning** and **Language Content Pack Versioning**.

#### Software Engine Versioning (`MAJOR.MINOR.PATCH`)
Example: `v1.2.4`
- `MAJOR` (`1`): Fundamental platform overhaul or major architecture change.
- `MINOR` (`2`): New feature module, world engine, or parent portal extension.
- `PATCH` (`4`): Backward-compatible bug fix or performance optimization.

#### Content Pack Versioning (`LANG-YEAR.RELEASE.REVISION`)
Example: `BAK-2026.1.0`
- `LANG`: Three-letter ISO code (`BAK` = Bakenye).
- `YEAR.RELEASE`: Year and annual publication batch (`2026.1` = First release of 2026).
- `REVISION`: Errata or minor audio correction (`0` = Initial publication).

---

## 4. DEPLOYMENT ARCHITECTURE

### 4.1 Multi-Tier Deployment Topology

```
┌─────────────────────────────────────────────────────────────────┐
│                    DEPLOYMENT TOPOLOGY MATRIX                   │
├────────────────────────┬───────────────────┬────────────────────┤
│   DEVELOPMENT (DEV)    │   STAGING (STG)   │  PRODUCTION (PROD) │
├────────────────────────┼───────────────────┼────────────────────┤
│ • Local Android Build  │ • Firebase Test   │ • Google Play Store│
│ • Mock Local Server    │   Lab Matrix      │   Production Track │
│ • Debug Keystore       │ • Production-like │ • Production Release│
│ • SQLite In-Memory DB  │   Room Seed DB    │   Keystore (KMS)   │
│ • Verbose Debug Logs   │ • Signed Candidate│ • Live Content CDN │
│                        │   AAB Artifacts   │ • SHA-256 Signature│
└────────────────────────┴───────────────────┴────────────────────┘
```

---

## 5. CI/CD PRODUCTION PIPELINE SPECIFICATION

### 5.1 Pipeline Execution Stages

```
[Git Commit / PR to main]
          │
          ▼
   [Ktlint & Android Lint Check]
          │
          ▼
   [JUnit Unit Tests] (gradle testDebugUnitTest)
          │
          ▼
   [Roborazzi Screenshot Tests] (gradle verifyRoborazziDebug)
          │
          ▼
   [Release Compilation] (compile_applet)
          │
          ▼
   [AAB Artifact Generation & SHA-256 Signing]
          │
          ▼
   [Upload to Play Store Internal Track]
```

---

## 6. ANDROID APPLICATION RELEASE PROCESS

### 6.1 Play Store Staged Rollout Schedule

| Phase | Target Audience | Duration | Success Threshold / Halt Criteria |
| :--- | :--- | :--- | :--- |
| **Internal Test Track** | Dev & QA Team (20 users) | 24 Hours | 0 crashes; 100% CUJ verification |
| **Closed Beta Track** | Teachers & Elders (100 users) | 3 Days | > 99.5% crash-free rate; positive UX feedback |
| **Staged Production (10%)**| Public Users (10% random) | 48 Hours | Crash-free rate ≥ 99.9%; ANR rate < 0.05% |
| **Staged Production (50%)**| Public Users (50% random) | 48 Hours | No SEV-1 or SEV-2 incidents reported |
| **Full Production (100%)**| All Users Worldwide | Perpetual | Continuous Sentry & Android Vitals monitoring |

---

## 7. CULTURAL CONTENT RELEASE OPERATIONS

### 7.1 Content Publishing & Governance Pipeline

```
[Content Creator Submission]
             │
             ▼
   [Automated JSON Schema Check] ──(Invalid)──► Return to Creator
             │
             ▼ (Valid)
   [Linguist Phonetic Verification]
             │
             ▼ (Verified)
   [BLCC Elders Council Review] ──(Disputed)──► Return to Revision
             │
             ▼ (75% Approval)
   [Cryptographic Digital Signature Applied]
             │
             ▼
   [Published to Language Pack CDN & Seed DB]
```

---

## 8. DATABASE & DATA OPERATIONS

### 8.1 Database Migration & Backup Policy
- **Database Engine:** Android Room (SQLite).
- **Migration Policy:** Every schema update MUST include an explicit `Migration(startVersion, endVersion)` implementation tested via `MigrationTestHelper`. Destructive fallback migration is strictly prohibited in production releases.
- **Recovery Point Objective (RPO):** < 24 Hours (User local progress backed up to encrypted local Room state).
- **Recovery Time Objective (RTO):** < 1 Hour (Instant database recovery via seed asset restoration upon corruption).

---

## 9. MONITORING, OBSERVABILITY, & METRICS

### 9.1 Core Platform Health Metrics

| Vector Category | Target KPI Benchmark | Telemetry Source | Action Threshold |
| :--- | :--- | :--- | :--- |
| **Crash-Free Sessions** | ≥ 99.9% Crash-Free Users | Sentry / Android Vitals | < 99.5% triggers automatic rollout pause |
| **Application Not Responding (ANR)** | < 0.05% of Total Sessions | Android Vitals | > 0.1% triggers immediate engineering triage |
| **Cold Startup Latency** | < 1,200 ms (p95) | Android Macrobenchmark | > 1,800 ms blocks release promotion |
| **Frame Jank Rate** | < 0.5% dropped frames | Perfetto / RenderThread | > 1.5% triggers UI optimization review |

---

## 10. INCIDENT RESPONSE FRAMEWORK

### 10.1 Severity Classification & Emergency Playbooks

#### SEV-1: Critical Platform Failure
- **Definition:** App crashes continuously on launch, database corruption affects > 1% of users, or severe security breach.
- **Response Time:** < 15 minutes.
- **Action:** Halt Play Store rollout immediately; revert to previous stable release via Play Console roll-back; notify Technical Steering Committee.

#### SEV-2: Major Feature Degradation
- **Definition:** Core learning feature broken (e.g., audio playback fails, lesson completion fails to persist).
- **Response Time:** < 2 hours.
- **Action:** Issue hotfix release (`vX.Y.Z-hotfix`) within 24 hours.

#### SEV-3: Minor Non-Blocking Issue
- **Definition:** Minor visual layout glitch or localized English translation typo.
- **Response Time:** < 24 hours.
- **Action:** Schedule fix for next scheduled minor patch build.

---

## 11. SECURITY OPERATIONS & CHILD PRIVACY (COPPA)

- **Zero PII Policy:** No child names, email addresses, phone numbers, or precise location coordinates collected or transmitted.
- **Key Management:** Production signing keys stored in hardware-backed Google Cloud KMS with multi-factor biometric approval required for release signing.
- **Dependency Auditing:** Automated OWASP Dependency Check executed on every build to prevent supply chain vulnerabilities.

---

## 12. BACKUP & DISASTER RECOVERY PLAN

### 12.1 Disaster Scenarios & Recovery SOPs

```
                       ┌─────────────────────────┐
                       │   DISASTER RECOVERY     │
                       └────────────┬────────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         ▼                          ▼                          ▼
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│ CORRUPTED ROOM   │      │ DAMAGED LANGUAGE │      │ COMPROMISED CDN  │
│    DATABASE      │      │    PACK ASSETS   │      │  RELEASE ASSETS  │
└────────┬─────────┘      └────────┬─────────┘      └────────┬─────────┘
         │                         │                         │
         ▼                         ▼                         ▼
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│ Trigger SQLite   │      │ Fallback to      │      │ Invalidate SHA   │
│ Corruption Catch │      │ Embedded Local   │      │ Hashes & Redeploy│
│ & Re-seed Assets │      │ Raw Asset Cache  │      │ Signed Build     │
└──────────────────┘      └──────────────────┘      └──────────────────┘
```

---

## 13. COMMUNITY & STAKEHOLDER OPERATIONS

- **Community Feedback Channels:** Monthly feedback forums with Lake Kyoga school teachers and community elders.
- **Cultural Errata Portal:** Formal process for elders or community members to report pronunciation errors or suggest traditional story additions.

---

## 14. OPERATIONAL MAINTENANCE SCHEDULE

| Frequency | Target Operational Tasks | Responsible Lead |
| :--- | :--- | :--- |
| **Daily** | Android Vitals crash review, CI/CD pipeline check | DevOps Lead |
| **Weekly** | Content approval queue review, Sentry error triage | QA Automation Lead |
| **Monthly** | Dependency security vulnerability scan, Play Console metrics | Security Lead |
| **Quarterly** | Disaster recovery simulation, database migration benchmark | Lead Architect |
| **Yearly** | Multi-year platform sustainability audit, BLCC charter review | Operations Director |

---

## 15. OPERATIONAL METRICS DASHBOARD

- **System Availability:** 100% offline-first application execution.
- **Crash-Free Rate:** Target 99.9%.
- **Cultural Content Coverage:** Total approved words (1,000+), stories (25+), proverbs (50+).
- **Elder Review Turnaround:** Average time from submission to approval (< 7 days).

---

## 16. LONG-TERM PLATFORM SUSTAINABILITY

To ensure the platform remains functional for 50–100 years:
1. **Open Standard Archival Formats:** All audio saved in open FLAC/OGG; data stored in human-readable UTF-8 JSON schemas.
2. **Framework Decoupling:** Core domain logic decoupled from UI frameworks, enabling future porting to modern display systems without re-recording language content.
3. **Institutional Documentation:** Complete documentation preserved in public repositories and archived with university research libraries.

---

*Verified & Adopted by the Release Engineering & Platform Operations Directorate, 2026.*
