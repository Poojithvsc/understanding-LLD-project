# Git Workflow & Branching Strategy

## Overview

This document explains the Git workflow and branching strategy we'll use for this project. This is an industry-standard approach used by professional development teams.

---

## Branch Strategy

### Main Branches

```
main (production-ready code)
  ↑
  | Pull Request (after milestone completion)
  |
dev (development branch - active work happens here)
  ↑
  | Pull Request (after feature completion)
  |
feature branches (individual features)
```

### Branch Descriptions

#### 1. `main` Branch
- **Purpose**: Production-ready code only
- **Protection**: Protected branch - no direct commits
- **Updates**: Only through Pull Requests from `dev`
- **When to merge**: After completing major milestones
- **Status**: Should always be stable and deployable

#### 2. `dev` Branch
- **Purpose**: Integration branch for ongoing development
- **Protection**: Protected - no direct commits (use feature branches)
- **Updates**: Through Pull Requests from feature branches
- **When to merge**: After each feature is complete and tested
- **Status**: Should be stable, but can contain work-in-progress features

#### 3. Feature Branches
- **Purpose**: Develop individual features or fixes
- **Naming**: `feature/description` or `fix/description`
- **Lifetime**: Short-lived (delete after merging)
- **Created from**: `dev` branch
- **Merged into**: `dev` branch

---

## Naming Conventions

### Branch Names

**Feature Branches:**
```
feature/order-entity
feature/order-crud-api
feature/redis-caching
feature/kafka-integration
feature/s3-file-storage
```

**Bug Fix Branches:**
```
fix/order-validation-bug
fix/null-pointer-exception
```

**Documentation Branches:**
```
docs/api-documentation
docs/update-readme
```

**Test Branches:**
```
test/order-service-unit-tests
test/integration-tests
```

### Commit Message Format

Follow this format for clear, professional commit messages:

```
<type>: <short summary>

<optional detailed description>

<optional footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `test`: Adding or updating tests
- `refactor`: Code refactoring
- `style`: Code style changes (formatting, etc.)
- `chore`: Maintenance tasks

**Examples:**

```
feat: Add Order entity and repository

- Create Order and OrderItem JPA entities
- Add OrderRepository interface
- Configure H2 database for development

feat: Implement order CRUD API endpoints

- Add POST /api/v1/orders endpoint
- Add GET /api/v1/orders/{id} endpoint
- Add GET /api/v1/orders endpoint with pagination
- Add DELETE /api/v1/orders/{id} endpoint
- Include input validation and error handling

test: Add unit tests for OrderService

- Test order creation with valid input
- Test order retrieval by ID
- Test order not found scenario
- Test validation errors
- Achieve 85% code coverage

fix: Resolve NullPointerException in order retrieval

- Add null check for order items
- Add appropriate error message
- Add test case for null scenario

docs: Update API documentation with new endpoints

- Document order creation endpoint
- Add request/response examples
- Update OpenAPI specification
```

---

## Workflow: Step-by-Step Guide

### Initial Setup (One Time)

#### Step 1: Initialize Local Repository
```bash
# Navigate to project directory
cd "D:\Tinku anna project\project 4"

# Initialize git
git init

# Add all files
git add .

# Create initial commit
git commit -m "docs: Initial project setup with documentation

- Add project structure
- Add HLD and LLD documents
- Add test strategy
- Add API standards
- Add learning path

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>"
```

#### Step 2: Connect to GitHub
```bash
# Add remote repository
git remote add origin https://github.com/Poojithvsc/understanding-LLD-project.git

# Verify remote
git remote -v
```

#### Step 3: Create and Push Main Branch
```bash
# Rename to main (if not already)
git branch -M main

# Push to GitHub
git push -u origin main
```

#### Step 4: Create Dev Branch
```bash
# Create dev branch from main
git checkout -b dev

# Push dev branch to GitHub
git push -u origin dev
```

---

### Daily Development Workflow

#### Starting a New Feature

**Step 1: Ensure you're on dev branch and it's up-to-date**
```bash
# Switch to dev branch
git checkout dev

# Pull latest changes
git pull origin dev
```

**Step 2: Create a new feature branch**
```bash
# Create and switch to feature branch
git checkout -b feature/order-entity

# Example names:
# git checkout -b feature/order-crud-api
# git checkout -b feature/kafka-integration
# git checkout -b test/order-service-tests
```

**Step 3: Work on your feature**
- Write code
- Write tests
- Test locally

**Step 4: Commit your changes**
```bash
# Check what files changed
git status

# Add files to staging
git add .
# Or add specific files:
# git add src/main/java/com/ecommerce/order/entity/Order.java

# Commit with descriptive message
git commit -m "feat: Add Order entity and repository

- Create Order JPA entity with fields
- Create OrderItem JPA entity
- Add OrderRepository interface
- Configure entity relationships"

# Make multiple small commits as you progress
```

**Step 5: Push feature branch to GitHub**
```bash
# First time pushing this branch
git push -u origin feature/order-entity

# Subsequent pushes
git push
```

**Step 6: Create Pull Request (PR)**

On GitHub:
1. Go to https://github.com/Poojithvsc/understanding-LLD-project
2. You'll see a prompt: "Compare & pull request" for your branch
3. Click "Compare & pull request"
4. **Base branch**: `dev` (not main!)
5. **Compare branch**: `feature/order-entity`
6. Fill out the PR template:
   - Title: "Add Order entity and repository"
   - Description: Explain what you did
   - Check all relevant boxes in the template
7. Click "Create pull request"

**Step 7: Review Your Own PR**
- Look at the "Files changed" tab
- Review your code changes
- Check if you missed anything
- Add comments if needed

**Step 8: Merge the PR**

Since you're learning alone, you'll merge your own PRs:
1. Click "Merge pull request"
2. Choose "Squash and merge" (keeps history clean)
3. Confirm merge
4. Delete the feature branch (GitHub will prompt)

**Step 9: Update Local Dev Branch**
```bash
# Switch back to dev
git checkout dev

# Pull the merged changes
git pull origin dev

# Delete local feature branch (already merged)
git branch -d feature/order-entity
```

**Step 10: Repeat for Next Feature**
Start from Step 1 for your next feature!

---

### Merging Dev to Main (Milestone Completion)

Do this after completing a major milestone (e.g., end of Week 2, 6, 10, 12):

**Step 1: Ensure dev is ready**
```bash
# Switch to dev
git checkout dev

# Pull latest
git pull origin dev

# Verify all tests pass
mvn clean verify
```

**Step 2: Create PR from dev to main**

On GitHub:
1. Click "New pull request"
2. **Base branch**: `main`
3. **Compare branch**: `dev`
4. Title: "Release: Order Service MVP" (or appropriate milestone name)
5. Description: Summarize all features included
6. Create pull request

**Step 3: Review thoroughly**
- Review all changes since last merge to main
- Ensure all tests pass
- Check documentation is updated

**Step 4: Merge to main**
1. Click "Merge pull request"
2. Choose "Create a merge commit" (for releases)
3. Confirm merge

**Step 5: Tag the release (optional)**
```bash
# Switch to main
git checkout main

# Pull the merge
git pull origin main

# Create a tag
git tag -a v0.1.0 -m "Release: Order Service MVP completed"

# Push the tag
git push origin v0.1.0
```

---

## What is a Pull Request? (Detailed Explanation)

### Definition
A Pull Request (PR) is a way to propose changes to a codebase. It's called a "pull request" because you're asking the repository maintainers to "pull" your changes into their branch.

### Why Use Pull Requests?

1. **Code Review**: Others can review your code before it's merged
2. **Discussion**: Team can discuss the changes
3. **Automated Testing**: Run tests automatically before merge
4. **Documentation**: Creates a record of what changed and why
5. **Quality Control**: Ensures code meets standards

### Pull Request Lifecycle

```
1. Create feature branch
   ↓
2. Write code and tests
   ↓
3. Commit changes
   ↓
4. Push branch to GitHub
   ↓
5. Open Pull Request
   ↓
6. Automated tests run (CI/CD)
   ↓
7. Code review (by you or others)
   ↓
8. Address feedback (if any)
   ↓
9. Approval
   ↓
10. Merge to target branch
   ↓
11. Delete feature branch
```

### Anatomy of a Good Pull Request

**Title**: Clear, concise summary
```
✅ Add order CRUD API endpoints with validation
❌ Update code
```

**Description**: Explain what and why
```
✅ This PR implements the order management API with the following endpoints:
- POST /api/v1/orders - Create new order
- GET /api/v1/orders/{id} - Get order by ID
- GET /api/v1/orders - List orders with pagination
- DELETE /api/v1/orders/{id} - Cancel order

Also includes:
- Input validation using Bean Validation
- Unit tests with 90% coverage
- Integration tests with Testcontainers
- API documentation with Swagger

Closes #5

❌ Added some stuff
```

**Changes**: Keep it focused
```
✅ One feature per PR (~200-400 lines)
❌ Multiple unrelated changes (~2000 lines)
```

---

## Common Git Commands Reference

### Branch Management
```bash
# List all branches
git branch -a

# Create new branch
git checkout -b branch-name

# Switch to existing branch
git checkout branch-name

# Delete local branch
git branch -d branch-name

# Delete remote branch
git push origin --delete branch-name

# Rename current branch
git branch -m new-name
```

### Committing Changes
```bash
# Check status
git status

# See what changed
git diff

# Add all changes
git add .

# Add specific file
git add path/to/file.java

# Commit with message
git commit -m "feat: your message"

# Amend last commit (before pushing)
git commit --amend -m "feat: updated message"
```

### Syncing with Remote
```bash
# Fetch changes (doesn't merge)
git fetch origin

# Pull changes (fetch + merge)
git pull origin dev

# Push changes
git push origin branch-name

# Push and set upstream
git push -u origin branch-name
```

### Viewing History
```bash
# View commit history
git log

# View compact history
git log --oneline

# View history with graph
git log --oneline --graph --all

# View changes in a commit
git show commit-hash
```

### Undoing Changes
```bash
# Discard changes in working directory
git checkout -- file.java

# Unstage file (undo git add)
git reset HEAD file.java

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes) - USE CAREFULLY!
git reset --hard HEAD~1
```

---

## Workflow Timeline for This Project

### Week 1: Getting Comfortable with Git

**Commits you'll make:**
```
Day 1-2:
- feat: Add Spring Boot Order Service skeleton
- feat: Add hello world endpoint
- test: Add test for hello endpoint

Day 3-4:
- feat: Add Order and OrderItem entities
- feat: Add OrderRepository interface
- test: Add repository tests

Day 5-6:
- feat: Implement OrderService
- feat: Implement OrderController with CRUD endpoints
- test: Add service layer unit tests
- test: Add controller integration tests

Day 7:
- docs: Update API documentation
- refactor: Code cleanup and improvements
```

**Pull Requests you'll create:**
```
PR #1: feature/hello-endpoint → dev
PR #2: feature/order-entity → dev
PR #3: feature/order-crud-api → dev
PR #4: test/order-service-tests → dev
```

**Milestone PR:**
```
PR #5: dev → main (after Week 2 completion)
Title: "Release: Order Service MVP"
```

---

## Best Practices

### DO:
✅ Create small, focused feature branches
✅ Write descriptive commit messages
✅ Commit often (after each logical change)
✅ Pull before pushing to avoid conflicts
✅ Delete feature branches after merging
✅ Write tests before creating PR
✅ Fill out PR template completely
✅ Review your own PR before asking others

### DON'T:
❌ Commit directly to main or dev
❌ Create huge PRs with many changes
❌ Write vague commit messages like "fix" or "update"
❌ Commit sensitive data (passwords, API keys)
❌ Commit broken code
❌ Skip writing tests
❌ Force push to shared branches (git push -f)
❌ Leave feature branches unmerged for long

---

## Handling Merge Conflicts (You'll Learn This Later)

If you get a merge conflict:

```bash
# 1. Pull latest changes
git pull origin dev

# 2. Git will tell you which files have conflicts

# 3. Open the conflicting files in your IDE
# You'll see something like:
<<<<<<< HEAD
Your changes
=======
Conflicting changes
>>>>>>> dev

# 4. Manually resolve conflicts
# Keep what you need, remove conflict markers

# 5. Add resolved files
git add resolved-file.java

# 6. Complete the merge
git commit -m "Merge dev into feature/your-feature"

# 7. Push
git push
```

**Pro Tip**: Keep your feature branches short-lived and sync with dev often to minimize conflicts!

---

## Git Workflow Checklist

Use this checklist for every feature:

**Before Starting:**
- [ ] On dev branch
- [ ] Pulled latest changes (`git pull origin dev`)

**During Development:**
- [ ] Created feature branch
- [ ] Making small, focused commits
- [ ] Writing tests alongside code

**Before Creating PR:**
- [ ] All tests passing locally
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] Pushed feature branch to GitHub

**Creating PR:**
- [ ] PR title is descriptive
- [ ] PR description filled out
- [ ] Base branch is `dev` (not main)
- [ ] All checkboxes in template addressed

**After Merge:**
- [ ] Switched back to dev
- [ ] Pulled merged changes
- [ ] Deleted local feature branch

---

## Visual Workflow Summary

```
START
  ↓
[On dev] git checkout dev
  ↓
[On dev] git pull origin dev
  ↓
[Create feature branch] git checkout -b feature/my-feature
  ↓
[Work] Write code + tests
  ↓
[Commit] git commit -m "feat: description"
  ↓
[Push] git push -u origin feature/my-feature
  ↓
[GitHub] Create Pull Request: feature/my-feature → dev
  ↓
[GitHub] Review changes
  ↓
[GitHub] Merge Pull Request
  ↓
[GitHub] Delete feature branch
  ↓
[Local] git checkout dev
  ↓
[Local] git pull origin dev
  ↓
[Local] git branch -d feature/my-feature
  ↓
REPEAT for next feature
```

---

## Quick Start Commands for Your First Feature

```bash
# 1. Initialize and push to GitHub (one time)
cd "D:\Tinku anna project\project 4"
git init
git add .
git commit -m "docs: Initial project setup"
git branch -M main
git remote add origin https://github.com/Poojithvsc/understanding-LLD-project.git
git push -u origin main

# 2. Create dev branch (one time)
git checkout -b dev
git push -u origin dev

# 3. Start first feature
git checkout -b feature/spring-boot-setup

# 4. After making changes
git add .
git commit -m "feat: Add Spring Boot Order Service skeleton"
git push -u origin feature/spring-boot-setup

# 5. Create PR on GitHub: feature/spring-boot-setup → dev
# 6. Merge PR on GitHub
# 7. Clean up
git checkout dev
git pull origin dev
git branch -d feature/spring-boot-setup
```

---

## Resources

- [GitHub Flow Guide](https://guides.github.com/introduction/flow/)
- [Git Documentation](https://git-scm.com/doc)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Pull Request Best Practices](https://github.blog/2015-01-21-how-to-write-the-perfect-pull-request/)

---

**Remember**: The goal of this workflow is to maintain clean, reviewable, and stable code. It might seem like overhead at first, but it's how professional teams work and it will become second nature!

**You'll learn by doing**: Don't worry if this seems complex. You'll create your first PR in Week 1 and it will make much more sense then!
