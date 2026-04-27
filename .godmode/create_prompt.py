import os
from pathlib import Path

app_prompt = os.environ["APP_PROMPT"]
package_name = os.environ["PACKAGE_NAME"]
app_name = os.environ["APP_NAME"]

package_path = package_name.replace(".", "/")

prompt = f"""
You are GOD MODE Android Builder.

Build a complete, production-level Android application.

APP NAME:
{app_name}

PACKAGE NAME:
{package_name}

PACKAGE PATH:
app/src/main/java/{package_path}

USER PROMPT:
{app_prompt}

HARD REQUIREMENTS:
- Kotlin only.
- Jetpack Compose only.
- Material 3.
- MVVM architecture.
- Android target SDK 36.
- Minimum SDK 26.
- Java 17.
- Gradle Kotlin DSL.
- Complete buildable Android project.
- No TODO placeholders.
- No fake dependencies.
- No missing files.
- No explanations outside file blocks.
- Must compile with ./gradlew assembleDebug.
- Include dark and light theme.
- Include polished premium UI.
- Include proper package name: {package_name}.
- Use simple local in-memory or local persistence when needed.
- Do not require paid services.
- Do not require API keys.
- Do not use external backend unless user specifically requested it.

ABSOLUTE OUTPUT RULE:
Output files only using this exact format:

FILE: settings.gradle.kts
```kotlin
content
```

FILE: build.gradle.kts
```kotlin
content
```

FILE: app/build.gradle.kts
```kotlin
content
```

FILE: app/src/main/AndroidManifest.xml
```xml
content
```

FILE: app/src/main/java/{package_path}/MainActivity.kt
```kotlin
content
```

Include every required Kotlin file.
"""
Path(".godmode/main_prompt.txt").write_text(prompt)
