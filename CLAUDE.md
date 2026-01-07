# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

**School Management System** - Full-stack web application for school administration.

## Overview

This system manages student registration, class organization, fee structures, payment tracking, and receipt generation for educational institutions.

## Current Status

**⚠️ This repository currently contains architectural planning and documentation only.**

- ✅ **Implemented**: Comprehensive documentation structure in `docs/` folder
- 📋 **Planned**: Backend (Spring Boot + Java 25) and Frontend (React 18 + Vite) implementation
- 📖 **Purpose**: This CLAUDE.md and associated docs serve as implementation blueprints for future development

The architecture, tech stack, and workflows described below represent the planned system design. Use the detailed guides in `docs/` when beginning implementation.

## Core Features

1. **School Configuration**: Name, address, fee frequency (Monthly/Quarterly/Yearly)
2. **Student Registration**: Complete enrollment with personal details, family info, class assignment
3. **Class Management**: Pre-configured Classes 1-10
4. **Fee Master**: Configure fee structures per class
5. **Fee Journal**: Track monthly payments, pending dues per student
6. **Fee Receipt**: Search students, auto-calculate fees, generate receipts
7. **Parent Portal**: Fee payment interface for parents

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Vite, Axios, Next.js patterns |
| Backend | Spring Boot 3.5, Java 25 |
| Database | PostgreSQL 18+ |
| ORM | Spring Data JPA / Hibernate |
| Rules Engine | Drools (fee calculation) |
| Build Tools | Maven, npm |
| Testing | JUnit 5, Mockito, React Testing Library |
| CI/CD | GitHub Actions |


## Development Methodology

**Test-Driven Development (TDD)**: Write tests before implementation
- Write failing test → Implement code → Refactor → Repeat

## Architecture

**Backend**: Layered architecture
- Controller → Service → Repository → Database
- DTOs for API, Entities for persistence
- Global exception handling with @ControllerAdvice
- Drools for complex fee calculations

**Frontend**: Component-based architecture
- Pages for major features
- Reusable components
- Service layer for API calls
- Context API for global state


## Fee Calculation Rules

Drools engine calculates fees based on:
- Student's class (base fee varies by class)
- Library fee
- Computer fee
- Special fee (first month only)
- School-configured frequency (monthly/quarterly/yearly)

## Documentation

For detailed guidance, see specialized docs:

- **[CLAUDE-BACKEND.md](docs/CLAUDE-BACKEND.md)**: Spring Boot development, Drools setup, testing patterns
- **[CLAUDE-FRONTEND.md](docs/CLAUDE-FRONTEND.md)**: React components, API integration, state management
- **[CLAUDE-TESTING.md](docs/CLAUDE-TESTING.md)**: TDD approach, unit/integration tests, CI/CD automation
- **[CLAUDE-GIT.md](docs/CLAUDE-GIT.md)**: Git workflow, commit conventions, PR process

## Context Optimization Notes

This documentation is split into focused files to optimize Claude Code's context window:
- **CLAUDE.md**: Project overview and quick reference
- **CLAUDE-BACKEND.md**: Backend-specific patterns (~2K tokens)
- **CLAUDE-FRONTEND.md**: Frontend-specific patterns (~2K tokens)
- **CLAUDE-TESTING.md**: Testing strategies (~2.5K tokens)
- **CLAUDE-GIT.md**: Git operations (~2K tokens)

Load only the relevant file for your current task to conserve context.

## Getting Help

- **Backend issues**: Reference CLAUDE-BACKEND.md
- **Frontend issues**: Reference CLAUDE-FRONTEND.md
- **Testing questions**: Reference CLAUDE-TESTING.md
- **Git operations**: Reference CLAUDE-GIT.md
- **API documentation**: Run backend and visit `/swagger-ui` (if configured)

## Common Workflows

**Adding a new entity**:
1. Create Entity class (backend/model/)
2. Create Repository interface
3. Create DTOs for request/response
4. Write service tests (TDD)
5. Implement service
6. Create controller
7. Add frontend service method
8. Create/update React components
9. Test integration

## Quick Start

**Note**: These commands will be available once backend and frontend implementation begins.

```bash
# Backend (Terminal 1)
cd backend
mvn spring-boot:run  # http://localhost:8080

# Frontend (Terminal 2)
cd frontend
npm install
npm run dev          # http://localhost:3000
```

## Project Structure

```
school-management-system/
├── backend/          # Spring Boot (Java 25) + PostgreSQL (Planned)
├── frontend/         # React 18 + Vite (Planned)
└── docs/             # Implementation guides (Current)
    ├── CLAUDE-BACKEND.md   # Backend development guide
    ├── CLAUDE-FRONTEND.md  # Frontend development guide
    ├── CLAUDE-TESTING.md   # Testing & automation
    └── CLAUDE-GIT.md       # Git workflow & commands
```

**Deploying to production**:
1. Run full test suite
2. Create release branch: `git checkout -b release/v1.0.0`
3. Update version numbers
4. Merge to main: `git checkout main && git merge release/v1.0.0`
5. Tag release: `git tag -a v1.0.0 -m "Release 1.0.0"`
6. Push: `git push origin main --tags`
7. CI/CD deploys automatically

## Contributing

1. Follow TDD methodology
2. Write meaningful commit messages (Conventional Commits)
3. Ensure all tests pass before pushing
4. Create PR with description
5. Address review comments
6. Squash commits if requested

## License

[Your License Here]

---

**For Claude Code**: Load the specific CLAUDE-*.md file relevant to your current task to optimize context usage.