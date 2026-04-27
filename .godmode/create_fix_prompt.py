from pathlib import Path

build_error = Path(".godmode/build_error.log").read_text(errors="ignore") if Path(".godmode/build_error.log").exists() else ""
build_log = Path(".godmode/build.log").read_text(errors="ignore") if Path(".godmode/build.log").exists() else ""

files = []
for p in Path(".").rglob("*"):
    if p.is_file() and ".git" not in str(p) and ".gradle" not in str(p) and "build/" not in str(p):
        files.append(str(p))

prompt = f"""
You are GOD MODE Android Build Fixer.

The Android project failed to build.

Fix the project by outputting ONLY corrected full files.

STRICT OUTPUT FORMAT:

FILE: path/to/file
```kotlin
full corrected file content
```

Rules:
- Output complete files only.
- Do not output explanations.
- Do not use placeholders.
- Fix Gradle, Kotlin, Compose, imports, manifest, package names, and dependency issues.
- The project must compile with ./gradlew assembleDebug.

FILE TREE:
{chr(10).join(files)}

BUILD LOG:
{(build_log + chr(10) + build_error)[-16000:]}
"""
Path(".godmode/fix_prompt.txt").write_text(prompt)
