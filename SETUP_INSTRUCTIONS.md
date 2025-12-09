# Setup Instructions - Start Here!

## ⚠️ IMPORTANT: Complete These Steps Before Starting Development

You're almost ready to start! Follow these steps in order:

---

## Step 1: Configure Git with Your GitHub Email

**Find your GitHub email:**
1. Go to https://github.com/settings/emails
2. Copy your primary email address OR use your GitHub no-reply email:
   `<username>@users.noreply.github.com`

**Configure Git:**
```bash
# Replace with your actual GitHub email
git config --global user.email "your-actual-email@example.com"

# Verify it's set correctly
git config --global user.email
git config --global user.name
```

**Expected output:**
```
your-actual-email@example.com
Poojithvsc
```

---

## Step 2: Create Initial Commit

After configuring your email, create the first commit:

```bash
# Navigate to project directory (if not already there)
cd "D:\Tinku anna project\project 4"

# Create the commit
git commit -m "docs: Initial project setup with comprehensive documentation

- Add project structure and documentation framework
- Add High-Level Design (HLD) document
- Add Low-Level Design (LLD) document
- Add test strategy and guidelines
- Add API standards and best practices
- Add code review guidelines
- Add 12-week learning path
- Add progress tracking system
- Add Git workflow and branching strategy
- Add pull request template

This is the foundation for learning microservices architecture
with Java, Spring Boot, Kafka, PostgreSQL, Redis, and AWS S3.

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## Step 3: Verify Your GitHub Repository

**Go to GitHub:**
1. Visit: https://github.com/Poojithvsc/understanding-LLD-project
2. Verify the repository exists and is empty (or has just a README)

**If repository doesn't exist:**
1. Go to https://github.com/new
2. Repository name: `understanding-LLD-project`
3. Description: "Learning microservices architecture with Java, Spring Boot, Kafka, and more"
4. Choose: Public (recommended for learning) or Private
5. **IMPORTANT**: Do NOT initialize with README, .gitignore, or license (we already have these)
6. Click "Create repository"

---

## Step 4: Connect Local Repository to GitHub

```bash
# Rename branch to main (if needed)
git branch -M main

# Add GitHub remote
git remote add origin https://github.com/Poojithvsc/understanding-LLD-project.git

# Verify remote is set
git remote -v
```

**Expected output:**
```
origin  https://github.com/Poojithvsc/understanding-LLD-project.git (fetch)
origin  https://github.com/Poojithvsc/understanding-LLD-project.git (push)
```

---

## Step 5: Push to GitHub

**First time pushing (you may need to authenticate):**

```bash
# Push main branch
git push -u origin main
```

**If you see authentication error:**

### Option A: Using Personal Access Token (Recommended)

1. Go to: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Note: "LLD Project Access"
4. Expiration: 90 days (or custom)
5. Select scopes:
   - ✅ `repo` (Full control of private repositories)
6. Click "Generate token"
7. **COPY THE TOKEN IMMEDIATELY** (you won't see it again)

**Use token when pushing:**
```bash
# When prompted for password, paste your token (not your GitHub password)
git push -u origin main
```

### Option B: Using GitHub Desktop (Easier)

1. Download: https://desktop.github.com/
2. Install and sign in with GitHub account
3. File → Add Local Repository
4. Choose: `D:\Tinku anna project\project 4`
5. Click "Publish repository"
6. Done! GitHub Desktop handles authentication

### Option C: Using SSH (For Advanced Users)

Follow GitHub's SSH setup guide: https://docs.github.com/en/authentication/connecting-to-github-with-ssh

---

## Step 6: Create Dev Branch

After successfully pushing to main:

```bash
# Create dev branch
git checkout -b dev

# Push dev branch to GitHub
git push -u origin dev
```

---

## Step 7: Verify Everything is on GitHub

1. Go to: https://github.com/Poojithvsc/understanding-LLD-project
2. You should see:
   - All your files and folders
   - Initial commit in history
   - Two branches: `main` and `dev`

**Check branches:**
- Click the branch dropdown (says "main" by default)
- You should see both `main` and `dev` branches

---

## Step 8: Set Dev as Default Branch (Optional but Recommended)

This makes `dev` the default branch you see when visiting the repo:

1. Go to: https://github.com/Poojithvsc/understanding-LLD-project/settings
2. Click "Branches" in the left sidebar
3. Under "Default branch", click the switch icon
4. Select `dev`
5. Click "Update"
6. Confirm by clicking "I understand, update the default branch"

**Why?** Most of your work will be on `dev`, so it's convenient to see it first.

---

## Step 9: Update PROGRESS.md with Session 1 Completion

Update your progress file to reflect what you've accomplished:

```bash
# Open PROGRESS.md and update:
# - Session 1 status to "Completed"
# - Check off the git-related tasks
# - Update "Last Session" date

# Commit the update
git add PROGRESS.md
git commit -m "docs: Update progress tracking for Session 1"
git push origin dev
```

---

## Step 10: Verify Your Setup

Run these commands to verify everything:

```bash
# Check Git config
git config --list

# Check current branch
git branch

# Check remote
git remote -v

# Check status
git status

# View commit history
git log --oneline
```

**Expected results:**
- Config shows your name and email
- Two branches: main and dev
- Remote points to your GitHub repo
- Status shows "nothing to commit, working tree clean"
- Log shows your initial commit

---

## Troubleshooting

### Issue: "Permission denied" or authentication error

**Solution 1:** Use Personal Access Token (see Step 5, Option A)
**Solution 2:** Use GitHub Desktop (see Step 5, Option B)
**Solution 3:** Set up SSH keys (see Step 5, Option C)

### Issue: "Repository not found"

**Check:**
1. Repository name is exactly: `understanding-LLD-project`
2. Repository exists at: https://github.com/Poojithvsc/understanding-LLD-project
3. You're signed in to the correct GitHub account

**Fix:**
```bash
# Remove wrong remote
git remote remove origin

# Add correct remote
git remote add origin https://github.com/Poojithvsc/understanding-LLD-project.git
```

### Issue: "Failed to push some refs"

**This means GitHub has commits you don't have locally.**

**Fix:**
```bash
# Pull first, then push
git pull origin main --rebase
git push -u origin main
```

### Issue: Line ending warnings (LF will be replaced by CRLF)

**This is normal on Windows and safe to ignore.** Git is converting line endings to Windows format.

**To suppress warnings (optional):**
```bash
git config --global core.autocrlf true
```

---

## What's Next?

After completing all these steps, you're ready to start development!

**Next actions:**
1. ✅ Read `NEXT_STEPS.md` for your Week 1 plan
2. ✅ Install Java 21, Maven, Docker Desktop
3. ✅ Create your first Spring Boot project
4. ✅ Make your first feature branch and PR!

---

## Quick Reference - Commands You'll Use Daily

```bash
# Start new feature
git checkout dev
git pull origin dev
git checkout -b feature/feature-name

# During development
git status
git add .
git commit -m "feat: description"
git push -u origin feature/feature-name

# After creating and merging PR on GitHub
git checkout dev
git pull origin dev
git branch -d feature/feature-name
```

---

## Need Help?

**Git Issues:**
- GitHub Documentation: https://docs.github.com/
- Git Documentation: https://git-scm.com/doc
- GitHub Community: https://github.community/

**Project Issues:**
- Review `GIT_WORKFLOW.md` for detailed Git workflow
- Review `NEXT_STEPS.md` for development steps
- Review `PROGRESS.md` to track your learning

---

## Summary Checklist

Complete this checklist before starting development:

- [ ] Git configured with your name and email
- [ ] Initial commit created locally
- [ ] GitHub repository exists
- [ ] Local repository connected to GitHub
- [ ] Main branch pushed to GitHub
- [ ] Dev branch created and pushed
- [ ] Both branches visible on GitHub
- [ ] Progress file updated
- [ ] Understood Git workflow basics

**Once all checked:** You're ready to start Week 1! 🚀

---

**Remember:** These are one-time setup steps. After this, you'll follow the simple daily workflow described in `GIT_WORKFLOW.md`.
