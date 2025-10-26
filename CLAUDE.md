# CLAUDE.md

**3-Tier Documentation Structure for Claude Code**

This file provides **Tier 1: High-Level Overview** for the School Management System. For detailed component and feature information, refer to specialized agent files.

---

## Overview

**School Management System** - Full-stack web application for comprehensive school administration, managing the complete lifecycle of student enrollment, class organization, fee structures, payment tracking, and receipt generation.

## Current Status

**⚠️ This repository currently contains architectural planning and documentation only.**

- ✅ **Implemented**: Comprehensive 3-tier documentation structure
- 📋 **Planned**: Backend (Spring Boot + Java 25) and Frontend (React 18 + Vite) implementation
- 📖 **Purpose**: Complete implementation blueprints with feature-level specifications

## 3-Tier Documentation Architecture

### Tier 1: High-Level Goals (This File)
Strategic overview, tech stack decisions, and documentation navigation

### Tier 2: Component Agents
Detailed component-level guidance and patterns:
- **[CLAUDE-FRONTEND.md](docs/CLAUDE-FRONTEND.md)**: React architecture, state management, API integration patterns
- **[CLAUDE-BACKEND.md](docs/CLAUDE-BACKEND.md)**: Spring Boot architecture, layered design, Drools engine, database patterns
- **[CLAUDE-TESTING.md](docs/CLAUDE-TESTING.md)**: TDD methodology, test automation, CI/CD pipelines
- **[CLAUDE-GIT.md](docs/CLAUDE-GIT.md)**: Git workflow, commit conventions, PR process

### Tier 3: Feature Agents
Granular feature specifications with UI/UX details, data models, and implementation guides:
- **[CLAUDE-FEATURE-STUDENT.md](docs/features/CLAUDE-FEATURE-STUDENT.md)**: Student registration and management
- **[CLAUDE-FEATURE-CLASS.md](docs/features/CLAUDE-FEATURE-CLASS.md)**: Class structure and enrollment
- **[CLAUDE-FEATURE-FEE-MASTER.md](docs/features/CLAUDE-FEATURE-FEE-MASTER.md)**: Fee structure configuration
- **[CLAUDE-FEATURE-FEE-JOURNAL.md](docs/features/CLAUDE-FEATURE-FEE-JOURNAL.md)**: Payment tracking and dues
- **[CLAUDE-FEATURE-FEE-RECEIPT.md](docs/features/CLAUDE-FEATURE-FEE-RECEIPT.md)**: Receipt generation workflow
- **[CLAUDE-FEATURE-PARENT-PORTAL.md](docs/features/CLAUDE-FEATURE-PARENT-PORTAL.md)**: Parent-facing payment interface
- **[CLAUDE-FEATURE-SCHOOL-CONFIG.md](docs/features/CLAUDE-FEATURE-SCHOOL-CONFIG.md)**: School setup and configuration

---

## Strategic Goals

### Business Objectives
1. **Streamline Administration**: Reduce manual paperwork by 80% through digital workflows
2. **Financial Transparency**: Real-time fee tracking with automated calculations
3. **Parent Engagement**: Self-service portal for fee payments and student information
4. **Data Integrity**: Single source of truth with PostgreSQL backend
5. **Scalability**: Support 1000+ students with sub-second response times

### Technical Objectives
1. **Modern Stack**: Leverage latest Java 25 features and React 18 patterns
2. **Test-Driven Quality**: 80%+ code coverage with automated testing
3. **Rule-Based Logic**: Drools engine for complex fee calculations
4. **API-First Design**: RESTful APIs enabling future mobile app integration
5. **Developer Experience**: Comprehensive documentation for efficient onboarding

---

## Tech Stack Rationale

| Layer | Technology | Justification |
|-------|-----------|---------------|
| **Frontend** | React 18 + Vite | Component reusability, fast HMR, modern DX |
| **Styling** | Tailwind CSS | Utility-first, rapid prototyping, consistent design |
| **Backend** | Spring Boot 3.5 + Java 25 | Enterprise-grade, robust ecosystem, virtual threads |
| **Database** | PostgreSQL 18+ | ACID compliance, JSON support, proven reliability |
| **ORM** | Spring Data JPA | Reduces boilerplate, type-safe queries, caching |
| **Rules Engine** | Drools | Business rule externalization, maintainability |
| **Testing** | JUnit 5 + Mockito + RTL | Comprehensive test coverage, BDD support |
| **CI/CD** | GitHub Actions | Native integration, cost-effective, flexible workflows |

---

## Core Features Summary

### 1. School Configuration
Set foundational parameters: name, address, fee frequency (Monthly/Quarterly/Yearly)

### 2. Student Registration
Complete enrollment workflow with personal details, family information, and class assignment

### 3. Class Management
Pre-configured Classes 1-10 with section management and capacity tracking

### 4. Fee Master
Configure fee structures per class with multiple fee types and frequencies

### 5. Fee Journal
Track payment history, pending dues, and monthly payment status per student

### 6. Fee Receipt
Search-driven workflow with auto-calculation and instant receipt generation

### 7. Parent Portal
Secure parent interface for viewing dues and making online payments

---

## Development Methodology

**Test-Driven Development (TDD)**:
```
Write Failing Test → Implement Code → Refactor → Repeat
```

**Conventional Commits**:
```
feat: Add student autocomplete search
fix: Correct fee calculation for quarterly payments
docs: Update API documentation
test: Add integration tests for receipt generation
```

**Branching Strategy**:
```
main (production) ← develop ← feature/student-registration
                              ← feature/fee-calculation
                              ← bugfix/payment-validation
```

---

## Architecture Principles

### Backend: Layered Architecture
```
Controller (REST API)
    ↓
Service (Business Logic + @Transactional)
    ↓
Repository (Data Access + JPA)
    ↓
Database (PostgreSQL)
```

**Cross-Cutting Concerns**:
- DTOs for API contracts (request/response)
- Entities for persistence (JPA-mapped)
- Global exception handling (@ControllerAdvice)
- Drools integration for complex calculations

### Frontend: Component-Based Architecture
```
Pages (Routes)
    ↓
Components (Reusable UI)
    ↓
Services (API Layer)
    ↓
Backend REST API
```

**State Management**:
- Context API for global state (auth, school config)
- Local state with useState for component data
- Custom hooks for shared logic

---

## Fee Calculation Logic

**Drools Rules Engine** calculates fees based on:
- **Base Fee**: Varies by class (Class 1-5 vs 6-10)
- **Library Fee**: Fixed add-on
- **Computer Fee**: Fixed add-on
- **Special Fee**: Applied first month only
- **Frequency Multiplier**: Monthly/Quarterly/Yearly adjustments

**Example Calculation**:
```
Class 3 Student, Monthly Frequency, First Month:
Base: ₹1000 + Library: ₹200 + Computer: ₹300 + Special: ₹500 = ₹2000

Same Student, Second Month:
Base: ₹1000 + Library: ₹200 + Computer: ₹300 = ₹1500
```

---

## Getting Started

### Prerequisites
- **Java 21+** (JDK 25 recommended)
- **Node.js 20+** with npm
- **PostgreSQL 18+**
- **Git 2.40+**
- **Maven 3.9+**

### Quick Start Commands

**Note**: These commands will be available once implementation begins.

```bash
# Clone repository
git clone <repository-url>
cd school-management-system

# Backend Setup (Terminal 1)
cd backend
mvn clean install
mvn spring-boot:run
# Running at http://localhost:8080

# Frontend Setup (Terminal 2)
cd frontend
npm install
npm run dev
# Running at http://localhost:3000
```

### Database Setup
```sql
-- Create database
CREATE DATABASE school_management_db;

-- Tables auto-created by Hibernate
-- Seed data loaded via DataInitializer
```

---

## Project Structure

```
school-management-system/
├── backend/                    # Spring Boot application (Planned)
│   ├── src/
│   │   ├── main/java/com/school/management/
│   │   │   ├── controller/    # REST endpoints
│   │   │   ├── service/       # Business logic
│   │   │   ├── repository/    # Data access
│   │   │   ├── model/         # JPA entities
│   │   │   ├── dto/           # Request/Response objects
│   │   │   ├── config/        # Configuration classes
│   │   │   └── exception/     # Error handling
│   │   ├── main/resources/
│   │   │   ├── rules/         # Drools rule files
│   │   │   └── application.properties
│   │   └── test/              # Unit & integration tests
│   └── pom.xml
│
├── frontend/                   # React application (Planned)
│   ├── src/
│   │   ├── components/        # Reusable UI components
│   │   ├── pages/             # Route-level pages
│   │   ├── services/          # API integration
│   │   ├── hooks/             # Custom React hooks
│   │   ├── contexts/          # Global state providers
│   │   ├── utils/             # Helper functions
│   │   └── assets/            # Static files
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
└── docs/                       # 3-Tier Documentation (Current)
    ├── CLAUDE-FRONTEND.md      # Tier 2: Frontend agent
    ├── CLAUDE-BACKEND.md       # Tier 2: Backend agent
    ├── CLAUDE-TESTING.md       # Tier 2: Testing agent
    ├── CLAUDE-GIT.md           # Tier 2: Git workflow agent
    └── features/               # Tier 3: Feature agents
        ├── CLAUDE-FEATURE-STUDENT.md
        ├── CLAUDE-FEATURE-CLASS.md
        ├── CLAUDE-FEATURE-FEE-MASTER.md
        ├── CLAUDE-FEATURE-FEE-JOURNAL.md
        ├── CLAUDE-FEATURE-FEE-RECEIPT.md
        ├── CLAUDE-FEATURE-PARENT-PORTAL.md
        └── CLAUDE-FEATURE-SCHOOL-CONFIG.md
```

---

## Navigation Guide for Claude Code

### When to Load Which Documentation

**Starting a new feature?**
→ Load Tier 3 feature agent (e.g., CLAUDE-FEATURE-STUDENT.md)

**Working on frontend architecture?**
→ Load CLAUDE-FRONTEND.md for patterns and setup

**Implementing backend logic?**
→ Load CLAUDE-BACKEND.md for layered architecture guidance

**Writing tests?**
→ Load CLAUDE-TESTING.md for TDD approach

**Git operations?**
→ Load CLAUDE-GIT.md for workflow commands

**High-level planning?**
→ Stay on this file (CLAUDE.md)

### Context Optimization Strategy

Load documentation incrementally to conserve context:
1. **Always start with Tier 1** (this file) for orientation
2. **Load Tier 2** component agent when working on that layer
3. **Load Tier 3** feature agent only when implementing specific feature
4. **Never load all docs simultaneously** - reference cross-links as needed

---

## Common Workflows

### Adding a New Feature
1. Read **Tier 3 feature agent** (e.g., CLAUDE-FEATURE-STUDENT.md)
2. Create backend entity, repository, DTOs (follow CLAUDE-BACKEND.md)
3. Write service tests (TDD from CLAUDE-TESTING.md)
4. Implement service and controller
5. Create frontend service and components (follow CLAUDE-FRONTEND.md)
6. Write component tests
7. Integration test with both layers running
8. Commit with conventional commits (CLAUDE-GIT.md)

### Debugging an Issue
1. Identify layer: Frontend or Backend?
2. Load relevant **Tier 2 agent** for debugging patterns
3. Load **Tier 3 feature agent** if issue is feature-specific
4. Check test coverage in affected area
5. Add regression test before fixing

### Deploying to Production
1. Run full test suite: `mvn test && npm run test`
2. Create release branch: `git checkout -b release/v1.0.0`
3. Update version numbers in pom.xml and package.json
4. Merge to main: `git merge --no-ff release/v1.0.0`
5. Tag release: `git tag -a v1.0.0 -m "Release 1.0.0"`
6. Push: `git push origin main --tags`
7. CI/CD pipeline deploys automatically

---

## Quality Standards

### Code Coverage Targets
- Backend Services: **80%+**
- Frontend Components: **70%+**
- Critical Paths (fee calculation, payments): **100%**

### Performance Benchmarks
- API Response Time: **< 200ms** (P95)
- Page Load: **< 2s** (First Contentful Paint)
- Database Queries: **< 50ms** (indexed lookups)

### Code Review Checklist
- [ ] All tests passing (backend + frontend)
- [ ] No console errors or warnings
- [ ] Following TDD methodology
- [ ] Conventional commit messages
- [ ] Documentation updated if API changes
- [ ] No hardcoded credentials or secrets

---

## Support & Resources

### Documentation
- **API Docs**: http://localhost:8080/swagger-ui (when backend running)
- **Component Library**: Storybook (planned)
- **Database Schema**: Generate with `mvn hibernate5:schema-export`

### Getting Help
| Issue Type | Reference Document |
|-----------|-------------------|
| Backend patterns | CLAUDE-BACKEND.md |
| Frontend architecture | CLAUDE-FRONTEND.md |
| Testing strategy | CLAUDE-TESTING.md |
| Git operations | CLAUDE-GIT.md |
| Feature specifications | docs/features/CLAUDE-FEATURE-*.md |
| Product questions | https://support.claude.com |

### External Resources
- Spring Boot: https://docs.spring.io/spring-boot/docs/current/reference/html/
- React: https://react.dev/
- Drools: https://docs.drools.org/
- PostgreSQL: https://www.postgresql.org/docs/

---

## Contributing Guidelines

1. **Follow TDD**: Tests before implementation
2. **Commit Conventions**: Use conventional commits format
3. **Code Style**: Follow ESLint (frontend) and Checkstyle (backend)
4. **Documentation**: Update relevant Tier 2/3 docs when adding features
5. **Pull Requests**: Include description, screenshots (UI changes), test results
6. **Review**: Address all comments before merging
7. **Squash**: Squash commits if requested by maintainer

---

## License

[Your License Here]

---

**For Claude Code Agents**: 
- **Tier 1 (This File)**: Strategic overview and navigation
- **Tier 2 (Component Agents)**: Architecture and patterns
- **Tier 3 (Feature Agents)**: Implementation specifications

Load documentation progressively to optimize context usage. Start here, then drill down to relevant Tier 2 and Tier 3 agents as needed.
