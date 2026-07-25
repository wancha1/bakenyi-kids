# BAKENYE KIDS PLATFORM
## Master Content Production Guide (CPG) & Institutional Preservation Architecture
### World-Class Standard for Indigenous Language Revitalization, Digital Preservation, and Multi-Language Platforms

---

## Executive Summary & Institutional Vision

The **Bakenye Kids Platform** is designed as a national and continental digital infrastructure for indigenous language preservation, education, and cultural revitalisation. Spearheaded by Bakenye cultural elders, linguists, child development specialists, and software architects, this platform bridges ancestral oral heritage with modern Android technology, offline-first execution, and scalable cloud-edge distribution.

This document serves as the **Master Content Production Guide (CPG) & Institutional Preservation Architecture**, establishing an enterprise-grade operational manual suitable for review and adoption by UNESCO, ministries of education, academic research institutions, cultural heritage foundations, and software development teams worldwide.

---

## 1. Language Governance & Orthography Standardisation

### 1.1 Governance Body Structure
Language decisions are adjudicated by the **Bakenye Language & Culture Council (BLCC)**, comprised of:
- **Elders Board (3-5 members):** Custodians of oral history, proverbs, and traditional folklore.
- **Linguist Committee (2 members):** Academic specialists in Bantu languages, phonetics, and orthography.
- **Pedagogy Officers (2 members):** Primary education teachers specializing in early childhood language acquisition.

### 1.2 Orthography Dispute Adjudication Workflow
```
[Contested Term / Spelling Identified]
              │
              ▼
    [Linguistic Analysis] ──(IPA & Etymology)──► [Elders Council Hearing]
              │                                           │
              ├───────────────────────────────────────────┘
              ▼
   [BLCC Formal Resolution]
              │
      ┌───────┴────────────────────────┐
      ▼                                ▼
[Standard Form Adopted]     [Variant Variant Form Logged]
      │                                │
      ▼                                ▼
(Primary Database ID)        (Accepted Synonym / Dialect)
```

### 1.3 Dialectal Variation & Variant Spelling Policy
- **Primary Canonical Form:** Standardised spelling accepted for school curricula and primary app display.
- **Accepted Dialectal Variants:** Stored in metadata and indexed in search and speech recognition engines so children pronouncing or selecting regional variants receive positive reinforcement with an explanation (e.g., *"In Lake Kyoga North, this is also called..."*).

---

## 2. Knowledge Classification & Cultural Access Control

### 2.1 Content Sensitivity Tiers
To respect indigenous cultural protocols regarding sacred or restricted knowledge:

| Tier Level | Classification | Target Audience | Access Policy |
| :--- | :--- | :--- | :--- |
| **Tier 0 (Public)** | Everyday Vocabulary, Animals, Alphabet, Basic Conversations | All Children & Public Users | Unrestricted |
| **Tier 1 (Educational)** | Folk Stories, Traditional Songs, Proverbs, Crafts | Registered Learners | Unrestricted in App |
| **Tier 2 (Community)** | Clan Genealogies, Ritual Songs, Deep Historical Records | Clan Members & Schools | Requires Elder Verification Code |
| **Tier 3 (Restricted)** | Sacred Burial Customs, Esoteric Rituals | Cultural Historians & Elders | Restricted from Public App |

---

## 3. Content Reliability, Evidence Tracking & Audit Trails

### 3.1 Confidence Scoring Algorithm
Every language entry is assigned a **Reliability Confidence Score (RCS)** calculated as:

$$\text{RCS} = (E \times 0.40) + (L \times 0.30) + (H \times 0.20) + (A \times 0.10)$$

Where:
- $E$ = Number of Elder Approvals (Max 5)
- $L$ = Linguist Review Verification (0 or 1)
- $H$ = Historical Text / Archive Reference Count (Max 3)
- $A$ = Audio Pronunciation Verification (0 or 1)

Only entries with an **RCS ≥ 0.85** are published to the production Android release.

### 3.2 Audit Log Schema
```json
{
  "phrase_id": "bak_phr_0042",
  "bakenye_text": "Omwana w'ennyanja",
  "english_translation": "Child of the lake",
  "confidence_score": 0.95,
  "verifications": [
    {"role": "ELDER", "verifier": "Elder Mukasa Kato", "timestamp": "2026-05-12T10:30:00Z", "status": "APPROVED"},
    {"role": "LINGUIST", "verifier": "Dr. A. Nalubega", "timestamp": "2026-05-14T14:15:00Z", "status": "APPROVED"}
  ],
  "provenance": {
    "source_village": "Buhuka",
    "historical_archive_ref": "UG-LKY-2024-REC-019"
  }
}
```

---

## 4. Artificial Intelligence Governance & Ethical Framework

### 4.1 Permitted vs. Prohibited AI Utilities

| Category | Permitted Use Cases | Strictly Prohibited Use Cases |
| :--- | :--- | :--- |
| **Audio** | Audio noise reduction, speech synthesis testing in QA | Synthetic AI voice replacement for official elder pronunciations |
| **Illustration** | Vector style layout drafting, asset background cleanup | Unchecked AI generation of sacred cultural artifacts or symbols |
| **Pedagogy** | Generating candidate quiz distractor options | Automatic publishing of unreviewed translations or proverbs |

---

## 5. Knowledge Graph Architecture & Semantic Linking

### 5.1 Graph Schema Overview
Entities within the Bakenye Kids ecosystem are linked semantically:
- **Phrase** ──(`BELONGS_TO`)──► **Lesson** ──(`PART_OF`)──► **World**
- **Phrase** ──(`FEATURED_IN`)──► **Story** ──(`ORIGINATES_FROM`)──► **Geographic Location**
- **Proverb** ──(`USES_VOCABULARY`)──► **Phrase**

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "BakenyeKnowledgeGraphNode",
  "type": "object",
  "properties": {
    "node_id": { "type": "string" },
    "entity_type": { "type": "string", "enum": ["WORD", "PROVERB", "STORY", "SONG", "LOCATION"] },
    "canonical_label": { "type": "string" },
    "related_nodes": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "target_id": { "type": "string" },
          "relationship_type": { "type": "string" }
        }
      }
    }
  }
}
```

---

## 6. Research Archive & Ethno-Linguistic Repository

### 6.1 Metadata Standards
All raw field recordings, video oral histories, and historical documents are cataloged adhering to **OLAC (Open Language Archives Community)** and **Dublin Core** metadata extensions.

---

## 7. Internationalization & Multi-Language Platform Strategy

### 7.1 Multi-Language Engine Architecture
The core engine separates the UI layer from the language content pack:
- `core-app`: Android Jetpack Compose UI components, game logic, Room database manager.
- `content-pack-bakenye.json`: Bakenye language assets, audio references, world definitions.
- `content-pack-luganda.json`: Future expansion pack.
- `content-pack-runyoro.json`: Future expansion pack.

---

## 8. Digital Preservation & Data Integrity

### 8.1 Open Archival Formats
- **Audio:** Lossless 24-bit/96kHz FLAC (Archival Master), 128kbps AAC/MP3 (App Runtime).
- **Graphics:** Scalable Vector Graphics (SVG) and PNG (WebP compressed for app runtime).
- **Data:** UTF-8 encoded JSON schemas with SQLite database exports.
- **Checksum Integrity:** Every content pack is hashed using **SHA-256** and verified upon download in the app.

---

## 9. Universal Content Engine & Platform Vision

### 9.1 Ecosystem Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│               BAKENYE UNIVERSAL CONTENT ENGINE                   │
├─────────────────────────────────────────────────────────────────┤
│  SQLite / Room DB │  FLAC Audio Engine  │  JSON Schema API       │
└────────┬────────────────────────┬──────────────────────┬────────┘
         │                        │                      │
         ▼                        ▼                      ▼
┌─────────────────┐      ┌─────────────────┐    ┌─────────────────┐
│  Bakenye Kids   │      │  Digital Audio  │    │  Bakenye Web    │
│  Android App    │      │  Dictionary     │    │  Keyboard & AI  │
└─────────────────┘      └─────────────────┘    └─────────────────┘
```

---

*Verified & Approved by the Bakenye Cultural Preservation & Engineering Board, 2026.*
