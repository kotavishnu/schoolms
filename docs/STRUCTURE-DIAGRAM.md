# Documentation Structure Diagram

## Visual Representation of 3-Tier Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                       │
│                      TIER 1: STRATEGIC OVERVIEW                      │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                                                               │   │
│  │                        CLAUDE.md                              │   │
│  │                        (~14 KB)                               │   │
│  │                                                               │   │
│  │  • Business & Technical Goals                                │   │
│  │  • Tech Stack Rationale                                      │   │
│  │  • Core Features Summary                                     │   │
│  │  • Development Methodology                                   │   │
│  │  • Architecture Principles                                   │   │
│  │  • Navigation Hub                                            │   │
│  │                                                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                       │
└───────────────────────────────┬───────────────────────────────────┘
                                │
                                │
┌───────────────────────────────┴───────────────────────────────────┐
│                                                                       │
│                   TIER 2: COMPONENT AGENTS                           │
│                                                                       │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐ │
│  │                  │  │                  │  │                  │ │
│  │  CLAUDE-BACKEND  │  │ CLAUDE-FRONTEND  │  │ CLAUDE-TESTING   │ │
│  │      .md         │  │      .md         │  │      .md         │ │
│  │    (~34 KB)      │  │    (~23 KB)      │  │    (Existing)    │ │
│  │                  │  │                  │  │                  │ │
│  │ • Spring Boot    │  │ • React 18       │  │ • TDD Approach   │ │
│  │ • JPA Entities   │  │ • Vite Config    │  │ • Unit Tests     │ │
│  │ • Repositories   │  │ • Components     │  │ • Integration    │ │
│  │ • Services       │  │ • API Layer      │  │ • CI/CD          │ │
│  │ • Controllers    │  │ • State Mgmt     │  │                  │ │
│  │ • Drools Setup   │  │ • Hooks          │  │                  │ │
│  │ • Testing        │  │ • Testing        │  │                  │ │
│  │                  │  │                  │  │                  │ │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘ │
│                                                                       │
└───────────────────────────────┬───────────────────────────────────┘
                                │
                                │
┌───────────────────────────────┴───────────────────────────────────┐
│                                                                       │
│                   TIER 3: FEATURE AGENTS                             │
│                         (docs/features/)                             │
│                                                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │   STUDENT    │  │    CLASS     │  │  FEE-MASTER  │             │
│  │   (~26 KB)   │  │   (~16 KB)   │  │   (~6.5 KB)  │             │
│  │              │  │              │  │              │             │
│  │ • Data Model │  │ • Data Model │  │ • Data Model │             │
│  │ • All Fields │  │ • Capacity   │  │ • Fee Types  │             │
│  │ • Validation │  │ • Sections   │  │ • Frequency  │             │
│  │ • API Specs  │  │ • API Specs  │  │ • API Specs  │             │
│  │ • UI Design  │  │ • UI Cards   │  │ • UI Config  │             │
│  │ • Workflows  │  │ • Seeding    │  │ • Rules      │             │
│  └──────────────┘  └──────────────┘  └──────────────┘             │
│                                                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │ FEE-RECEIPT  │  │ FEE-JOURNAL  │  │PARENT-PORTAL │             │
│  │   (~13 KB)   │  │    (~2 KB)   │  │   (~2.8 KB)  │             │
│  │              │  │              │  │              │             │
│  │ • Drools     │  │ • Tracking   │  │ • Login      │             │
│  │ • Auto-calc  │  │ • Pending    │  │ • Payment    │             │
│  │ • PDF Gen    │  │ • History    │  │ • Gateway    │             │
│  │ • Search     │  │ • Status     │  │ • Receipts   │             │
│  └──────────────┘  └──────────────┘  └──────────────┘             │
│                                                                       │
│  ┌──────────────┐                                                   │
│  │SCHOOL-CONFIG │                                                   │
│  │   (~2.1 KB)  │                                                   │
│  │              │                                                   │
│  │ • Settings   │                                                   │
│  │ • Frequency  │                                                   │
│  │ • Logo       │                                                   │
│  └──────────────┘                                                   │
│                                                                       │
└───────────────────────────────────────────────────────────────────┘
```

## Information Flow

```
┌─────────────────────────────────────────────────────────────┐
│                                                               │
│  DEVELOPER WORKFLOW                                           │
│                                                               │
│  1. Start Task                                                │
│     └─> Load CLAUDE.md (Tier 1)                             │
│         • Understand: Overall architecture                    │
│         • Identify: Which feature to implement                │
│         • Reference: Tech stack decisions                     │
│                                                               │
│  2. Feature Specification                                     │
│     └─> Load CLAUDE-FEATURE-XXX.md (Tier 3)                 │
│         • Read: Complete data model                          │
│         • Review: API endpoints                              │
│         • Study: UI/UX specifications                        │
│         • Understand: Business rules                         │
│                                                               │
│  3. Backend Implementation                                    │
│     └─> Reference CLAUDE-BACKEND.md (Tier 2)                │
│         • Follow: Entity pattern                             │
│         • Use: Repository template                           │
│         • Apply: Service pattern                             │
│         • Create: Controller                                 │
│                                                               │
│  4. Frontend Implementation                                   │
│     └─> Reference CLAUDE-FRONTEND.md (Tier 2)               │
│         • Follow: Component structure                        │
│         • Use: API service pattern                           │
│         • Apply: Form validation                             │
│         • Implement: UI design                               │
│                                                               │
│  5. Testing                                                   │
│     └─> Reference CLAUDE-TESTING.md (Tier 2)                │
│         • Write: Unit tests                                  │
│         • Create: Integration tests                          │
│         • Verify: Test coverage                              │
│                                                               │
│  6. Commit & Deploy                                           │
│     └─> Reference CLAUDE-GIT.md (Tier 2)                    │
│         • Follow: Commit conventions                         │
│         • Create: Pull request                               │
│         • Merge: After review                                │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Context Loading Strategy

```
┌────────────────────────────────────────────────────────┐
│                                                          │
│  OPTIMAL CONTEXT LOADING                                 │
│                                                          │
│  Scenario 1: New Feature (Student Registration)         │
│  ┌────────────────────────────────────────────────┐    │
│  │ Load Order:                                     │    │
│  │ 1. CLAUDE.md                    (~3,500 tokens) │    │
│  │ 2. CLAUDE-FEATURE-STUDENT.md    (~6,500 tokens) │    │
│  │ 3. CLAUDE-BACKEND.md            (~8,500 tokens) │    │
│  │                                                  │    │
│  │ Total: ~18,500 tokens (well within budget)     │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  Scenario 2: Frontend Bug Fix                           │
│  ┌────────────────────────────────────────────────┐    │
│  │ Load Order:                                     │    │
│  │ 1. CLAUDE-FRONTEND.md           (~5,700 tokens) │    │
│  │ 2. CLAUDE-FEATURE-XXX.md        (~6,500 tokens) │    │
│  │                                                  │    │
│  │ Total: ~12,200 tokens (efficient)              │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  Scenario 3: Backend Testing                            │
│  ┌────────────────────────────────────────────────┐    │
│  │ Load Order:                                     │    │
│  │ 1. CLAUDE-TESTING.md            (~2,500 tokens) │    │
│  │ 2. CLAUDE-BACKEND.md            (~8,500 tokens) │    │
│  │                                                  │    │
│  │ Total: ~11,000 tokens (optimal)                │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
└────────────────────────────────────────────────────────┘
```

## Data Synchronization

```
┌──────────────────────────────────────────────────────────┐
│                                                            │
│  FRONTEND ↔ BACKEND DATA SYNC                             │
│                                                            │
│  Example: Student Entity                                   │
│                                                            │
│  ┌──────────────────────┐    ┌──────────────────────┐   │
│  │  BACKEND (Java)       │    │  FRONTEND (React)     │   │
│  ├──────────────────────┤    ├──────────────────────┤   │
│  │ @Entity              │    │ const formData = {    │   │
│  │ class Student {      │◄──►│   firstName: '',      │   │
│  │   Long id;           │    │   lastName: '',       │   │
│  │   String firstName;  │    │   dateOfBirth: '',    │   │
│  │   String lastName;   │    │   address: '',        │   │
│  │   LocalDate dob;     │    │   mobile: '',         │   │
│  │   String address;    │    │   motherName: '',     │   │
│  │   String mobile;     │    │   fatherName: '',     │   │
│  │   String motherName; │    │   classId: ''         │   │
│  │   String fatherName; │    │ }                     │   │
│  │   Long classId;      │    │                       │   │
│  │ }                    │    │                       │   │
│  └──────────────────────┘    └──────────────────────┘   │
│           │                            │                  │
│           └────────────────────────────┘                  │
│                        │                                   │
│                        ▼                                   │
│          ┌─────────────────────────────┐                  │
│          │ CLAUDE-FEATURE-STUDENT.md   │                  │
│          │                              │                  │
│          │ • Defines all fields         │                  │
│          │ • Specifies data types       │                  │
│          │ • Sets validation rules      │                  │
│          │ • Documents UI components    │                  │
│          └─────────────────────────────┘                  │
│                                                            │
│  This ensures frontend and backend stay synchronized!     │
│                                                            │
└──────────────────────────────────────────────────────────┘
```

## File Size Distribution

```
Total Documentation: ~100 KB

┌────────────────────────────────────┐
│ Tier 1 (14%)                       │
│ ████████████                       │  14 KB
│ CLAUDE.md                          │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ Tier 2 (57%)                       │
│ ██████████████████████████████████ │  57 KB
│ Backend (34K) + Frontend (23K)     │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ Tier 3 (68%)                       │
│ ████████████████████████████████████│  68 KB
│ All Feature Agents                 │
│ (Student, Class, Fees, etc.)       │
└────────────────────────────────────┘
```

---

**Note**: Load documents progressively based on your current task to optimize Claude Code's context window and performance.
