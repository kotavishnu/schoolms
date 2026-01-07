# CLAUDE-GIT.md

Git workflow and commands for the School Management System project.

## Repository Structure

```
school-management-system/
├── backend/           # Spring Boot application
├── frontend/          # React application
├── .github/workflows/ # CI/CD pipelines
├── docs/             # Documentation
├── .gitignore        # Root ignore file
└── README.md         # Project overview
```

## Initial Setup

```bash
# Clone repository
git clone https://github.com/your-org/school-management-system.git
cd school-management-system

# Set user config (if not set globally)
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Create develop branch (if not exists)
git checkout -b develop
git push -u origin develop
```

## .gitignore Configuration

**Root .gitignore**:
```
# IDE
.idea/
.vscode/
*.iml
*.swp

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Environment
.env
.env.local
.env.*.local
```

**backend/.gitignore**:
```
target/
!.mvn/wrapper/maven-wrapper.jar
.mvn/
mvnw
mvnw.cmd

# Application config
application-local.properties
application-dev.properties
```

**frontend/.gitignore**:
```
node_modules/
dist/
build/
.vite/
coverage/

# Production
*.local
```

## Branching Strategy

**Main Branches**:
- `main`: Production-ready code
- `develop`: Integration branch for features

**Feature Branches**:
```bash
# Create feature branch from develop
git checkout develop
git pull origin develop
git checkout -b feature/student-registration

# Work on feature...
git add .
git commit -m "feat: add student registration form"

# Push feature branch
git push -u origin feature/student-registration
```

**Branch Naming Convention**:
- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `hotfix/description` - Production fixes
- `refactor/description` - Code refactoring
- `test/description` - Test additions

## Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `style`: Code style changes (formatting)
- `refactor`: Code restructuring
- `test`: Adding/updating tests
- `chore`: Build tasks, dependencies

**Examples**:
```bash
git commit -m "feat(student): add registration form with validation"
git commit -m "fix(fee): correct monthly payment calculation"
git commit -m "test(student): add service layer unit tests"
git commit -m "docs: update API documentation"
```

## Pre-commit Checklist

Automated via Husky hook, but manual verification:

```bash
# 1. Run backend tests
cd backend
mvn clean test

# 2. Run frontend tests
cd frontend
npm run test

# 3. Check linting
npm run lint

# 4. Build verification
cd backend && mvn clean package -DskipTests
cd frontend && npm run build

# 5. Review changes
git status
git diff

# 6. Stage and commit
git add .
git commit -m "feat: description"
```

## Common Git Workflows

### Feature Development

```bash
# Start feature
git checkout develop
git pull origin develop
git checkout -b feature/fee-receipt

# Make changes, test, commit
git add .
git commit -m "feat(fee): implement receipt generation"
git push -u origin feature/fee-receipt

# Create Pull Request on GitHub
# After review and approval, merge to develop
```

### Sync with Develop

```bash
# Update feature branch with latest develop
git checkout feature/your-feature
git fetch origin
git rebase origin/develop

# Or merge (if rebase conflicts)
git merge origin/develop

# Push updated branch
git push --force-with-lease origin feature/your-feature
```

### Fix Conflicts

```bash
# When merge conflict occurs
git status  # See conflicted files

# Edit conflicted files manually
# Remove conflict markers (<<<<, ====, >>>>)

# Stage resolved files
git add resolved-file.java

# Continue merge/rebase
git rebase --continue  # if rebasing
git merge --continue   # if merging

# Or abort
git rebase --abort
git merge --abort
```

### Release Process

```bash
# Create release branch
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# Version bump, final testing
# Update version in pom.xml and package.json

git commit -m "chore: bump version to 1.0.0"
git push origin release/v1.0.0

# Merge to main and tag
git checkout main
git merge release/v1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# Merge back to develop
git checkout develop
git merge release/v1.0.0
git push origin develop

# Delete release branch
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

## Quick Command Reference

```bash
# Status & Info
git status                          # Check status
git log --oneline --graph --all     # Visual log
git branch -a                       # List all branches
git remote -v                       # Show remotes

# Staging
git add <file>                      # Stage specific file
git add .                           # Stage all changes
git add -p                          # Interactive staging
git reset <file>                    # Unstage file

# Committing
git commit -m "message"             # Commit with message
git commit --amend                  # Amend last commit
git commit --amend --no-edit        # Amend without changing message

# Branching
git checkout -b branch-name         # Create and switch
git branch -d branch-name           # Delete local branch
git push origin --delete branch     # Delete remote branch

# Pulling & Pushing
git pull origin develop             # Pull from remote
git push origin feature-branch      # Push to remote
git push --force-with-lease         # Force push safely

# Stashing
git stash                           # Stash changes
git stash pop                       # Apply and remove stash
git stash list                      # List stashes
git stash apply stash@{0}           # Apply specific stash

# Undoing
git reset --soft HEAD~1             # Undo last commit, keep changes
git reset --hard HEAD~1             # Undo last commit, discard changes
git revert <commit-hash>            # Create revert commit
git clean -fd                       # Remove untracked files

# Remote Management
git remote add origin <url>         # Add remote
git fetch origin                    # Fetch without merge
git fetch --prune                   # Remove deleted remote branches
```

## GitHub Actions Integration

Pushes trigger automated tests (see CLAUDE-TESTING.md).

**Check CI Status**:
```bash
# Via GitHub CLI
gh pr checks

# View workflow runs
gh run list --workflow=test.yml
gh run view <run-id> --log
```

## Pull Request Process

```bash
# 1. Push feature branch
git push -u origin feature/your-feature

# 2. Create PR (GitHub CLI)
gh pr create --base develop --head feature/your-feature \
  --title "feat: add student registration" \
  --body "Implements student registration with validation"

# 3. Check PR status
gh pr status

# 4. After approval, merge
gh pr merge --merge  # or --squash or --rebase

# 5. Delete local branch
git checkout develop
git branch -d feature/your-feature
```

## Troubleshooting

**Large files rejected**:
```bash
# Use Git LFS for large files
git lfs track "*.pdf"
git add .gitattributes
```

**Wrong commit message**:
```bash
git commit --amend -m "corrected message"
git push --force-with-lease
```

**Need to undo pushed commit**:
```bash
git revert HEAD
git push origin branch-name
```

**Accidentally committed to main**:
```bash
git reset --soft HEAD~1
git stash
git checkout -b feature/proper-branch
git stash pop
git add .
git commit -m "message"
```