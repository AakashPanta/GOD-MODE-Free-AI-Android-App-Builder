import os
import re
import subprocess
from pathlib import Path

MODEL = os.environ["AI_MODEL"]
PACKAGE_NAME = os.environ["PACKAGE_NAME"]
APP_NAME = os.environ["APP_NAME"]

def run_ollama(prompt: str) -> str:
    result = subprocess.run(
        ["ollama", "run", MODEL],
        input=prompt,
        text=True,
        capture_output=True,
        timeout=900
    )
    return (result.stdout or "") + "\n" + (result.stderr or "")

def write_files_from_blocks(text: str) -> int:
    pattern = re.compile(
        r"FILE:\s*(.*?)\n```(?:kotlin|kt|xml|gradle|properties|toml|txt|text)?\n(.*?)```",
        re.DOTALL
    )
    matches = pattern.findall(text)
    count = 0
    for raw_path, content in matches:
        path = raw_path.strip().replace("\\", "/")
        if path.startswith("/") or ".." in Path(path).parts:
            continue
        p = Path(path)
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(content.strip() + "\n", errors="ignore")
        print(f"Fixed {path}")
        count += 1
    return count

files = []
for p in Path(".").rglob("*"):
    s = str(p)
    if p.is_file() and ".git" not in s and ".gradle" not in s and "/build/" not in s and ".ultra" not in s:
        files.append(s)

build_log = ""
for name in [".ultra/build.log", ".ultra/build_error.log"]:
    if Path(name).exists():
        build_log += Path(name).read_text(errors="ignore") + "\n"

prompt = f"""
You are ULTRA GOD MODE Android build fixer.

The Android project failed to build.

App name: {APP_NAME}
Package name: {PACKAGE_NAME}

Return ONLY changed files using:

FILE: path/to/file
```kotlin
full corrected file content
```

Rules:
- Full files only.
- No explanations.
- Fix Gradle/Kotlin/Compose/Manifest/import/version errors.
- Keep project buildable with ./gradlew assembleDebug.
- Do not invent unavailable dependencies.
- Prefer simple stable Compose code.

Existing file tree:
{chr(10).join(files)}

Build error:
{build_log[-18000:]}
"""

output = run_ollama(prompt)
Path(".ultra/fix_output.txt").write_text(output, errors="ignore")
count = write_files_from_blocks(output)

if count == 0:
    print("No valid fix blocks returned.")
    raise SystemExit(1)
