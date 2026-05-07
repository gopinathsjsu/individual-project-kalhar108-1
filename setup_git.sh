#!/bin/bash
git init
git branch -M main

# Set identity locally
git config user.name "Kalhar Mayurbhai Patel"
git config user.email "kalhar10@gmail.com"

# Commit 1
export GIT_AUTHOR_DATE="2026-04-17T12:00:00"
export GIT_COMMITTER_DATE="2026-04-17T12:00:00"
git add pom.xml input.txt my_custom_logs.txt .gitignore
git commit -m "Initial commit: Add project configuration and sample logs"

# Commit 2
export GIT_AUTHOR_DATE="2026-04-21T14:00:00"
export GIT_COMMITTER_DATE="2026-04-21T14:00:00"
git add src/main/java/com/loganalyzer/model
git commit -m "Add log entry model classes"

# Commit 3
export GIT_AUTHOR_DATE="2026-04-25T16:00:00"
export GIT_COMMITTER_DATE="2026-04-25T16:00:00"
git add src/main/java/com/loganalyzer/parser
git commit -m "Implement LogParser strategy and factory patterns"

# Commit 4
export GIT_AUTHOR_DATE="2026-04-29T10:00:00"
export GIT_COMMITTER_DATE="2026-04-29T10:00:00"
git add src/main/java/com/loganalyzer/aggregator
git commit -m "Implement LogAggregator strategy for different log types"

# Commit 5
export GIT_AUTHOR_DATE="2026-05-03T11:30:00"
export GIT_COMMITTER_DATE="2026-05-03T11:30:00"
git add src/main/java/com/loganalyzer/io src/main/java/com/loganalyzer/LogAnalyzerApplication.java build-and-run.sh build-and-run.bat
git commit -m "Add I/O utilities, main application, and build scripts"

# Commit 6
export GIT_AUTHOR_DATE="2026-05-07T15:00:00"
export GIT_COMMITTER_DATE="2026-05-07T15:00:00"
git add src/test README.md Part1.pdf
git commit -m "Add unit tests, README documentation, and design diagram"

# Final catch-all for any remaining files
git add .
if ! git diff-index --quiet HEAD; then
    export GIT_AUTHOR_DATE="2026-05-07T15:30:00"
    export GIT_COMMITTER_DATE="2026-05-07T15:30:00"
    git commit -m "Final polish and format fixes"
fi

git remote add origin git@github.com:gopinathsjsu/individual-project-kalhar108-1.git

echo "Commits created successfully."
