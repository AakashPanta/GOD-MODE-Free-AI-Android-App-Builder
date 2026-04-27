import os
import re
import json
import subprocess
from pathlib import Path

MODEL = os.environ["AI_MODEL"]
APP_PROMPT = os.environ["APP_PROMPT"]
PACKAGE_NAME = os.environ["PACKAGE_NAME"]
APP_NAME = os.environ["APP_NAME"]
PACKAGE_PATH = PACKAGE_NAME.replace(".", "/")
ROOT = Path(".")
ULTRA = Path(".ultra")
ULTRA.mkdir(exist_ok=True)

def run_ollama(prompt: str, out_file: str) -> str:
    path = ULTRA / out_file
    print(f"Running model -> {out_file}")
    result = subprocess.run(
        ["ollama", "run", MODEL],
        input=prompt,
        text=True,
        capture_output=True,
        timeout=900
    )
    output = (result.stdout or "") + "\n" + (result.stderr or "")
    path.write_text(output, errors="ignore")
    return output

def extract_code(text: str) -> str:
    fence = re.search(r"```(?:kotlin|kt|xml|gradle|properties|toml|txt|text)?\n(.*?)```", text, re.DOTALL)
    if fence:
        return fence.group(1).strip() + "\n"
    return text.strip() + "\n"

def safe_write(path: str, content: str):
    path = path.strip().replace("\\", "/")
    if path.startswith("/") or ".." in Path(path).parts:
        print(f"Unsafe path skipped: {path}")
        return
    p = ROOT / path
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content, errors="ignore")
    print(f"Wrote {path}")

def fallback_project():
    print("Creating fallback production Android project...")
    safe_write("settings.gradle.kts", f'''
pluginManagement {{
    repositories {{
        google()
        mavenCentral()
        gradlePluginPortal()
    }}
}}

dependencyResolutionManagement {{
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {{
        google()
        mavenCentral()
    }}
}}

rootProject.name = "{APP_NAME.replace('"', '')}"
include(":app")
''')

    safe_write("build.gradle.kts", '''
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
''')

    safe_write("app/build.gradle.kts", f'''
plugins {{
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}}

android {{
    namespace = "{PACKAGE_NAME}"
    compileSdk = 36

    defaultConfig {{
        applicationId = "{PACKAGE_NAME}"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }}

    buildFeatures {{
        compose = true
    }}

    composeOptions {{
        kotlinCompilerExtensionVersion = "1.5.14"
    }}

    kotlinOptions {{
        jvmTarget = "17"
    }}
}}

dependencies {{
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
}}
''')

    safe_write("app/src/main/AndroidManifest.xml", f'''
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:theme="@style/AppTheme"
        android:label="{APP_NAME}"
        android:allowBackup="true"
        android:supportsRtl="true">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
''')

    safe_write("app/src/main/res/values/styles.xml", '''
<resources>
    <style name="AppTheme" parent="android:style/Theme.Material.Light.NoActionBar" />
</resources>
''')

    safe_write(f"app/src/main/java/{PACKAGE_PATH}/MainActivity.kt", f'''
package {PACKAGE_NAME}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {{
    override fun onCreate(savedInstanceState: Bundle?) {{
        super.onCreate(savedInstanceState)
        setContent {{
            MaterialTheme {{
                Surface(modifier = Modifier.fillMaxSize()) {{
                    UltraApp()
                }}
            }}
        }}
    }}
}}

@Composable
fun UltraApp() {{
    var text by remember {{ mutableStateOf("") }}
    var items by remember {{
        mutableStateOf(
            listOf(
                "Production-ready Compose shell",
                "Material 3 interface",
                "Generated by ULTRA GOD MODE",
                "Prompt: {APP_PROMPT[:90].replace('"', "'")}"
            )
        )
    }}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {{
        Text(
            text = "{APP_NAME}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "AI-generated Android foundation with a polished Compose UI.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = text,
            onValueChange = {{ text = it }},
            modifier = Modifier.fillMaxWidth(),
            label = {{ Text("Add item") }}
        )

        Button(
            onClick = {{
                if (text.isNotBlank()) {{
                    items = listOf(text.trim()) + items
                    text = ""
                }}
            }},
            modifier = Modifier.fillMaxWidth()
        ) {{
            Text("Add")
        }}

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {{
            items(items) {{ item ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {{
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {{
                        Text(item, style = MaterialTheme.typography.bodyLarge)
                    }}
                }}
            }}
        }}
    }}
}}
''')

def create_manifest_list():
    required = [
        "settings.gradle.kts",
        "build.gradle.kts",
        "app/build.gradle.kts",
        "app/src/main/AndroidManifest.xml",
        f"app/src/main/java/{PACKAGE_PATH}/MainActivity.kt",
        f"app/src/main/java/{PACKAGE_PATH}/ui/theme/Theme.kt",
        f"app/src/main/java/{PACKAGE_PATH}/ui/home/HomeScreen.kt",
        f"app/src/main/java/{PACKAGE_PATH}/ui/home/HomeViewModel.kt",
    ]

    prompt = f"""
You are an Android project architect.

Create a JSON array of file paths needed for a complete Kotlin Jetpack Compose Android app.

App name: {APP_NAME}
Package name: {PACKAGE_NAME}
Package path: app/src/main/java/{PACKAGE_PATH}

User app request:
{APP_PROMPT}

Rules:
- Return ONLY JSON array.
- Include Gradle files.
- Include Manifest.
- Include MainActivity.
- Include theme files.
- Include screens, ViewModels, models, repositories if useful.
- Keep total file count between 8 and 18.
- Use only Kotlin, XML, Gradle Kotlin DSL.

Mandatory starting files:
{json.dumps(required, indent=2)}
"""
    out = run_ollama(prompt, "01_manifest_raw.txt")
    try:
        arr_match = re.search(r"\[.*\]", out, re.DOTALL)
        files = json.loads(arr_match.group(0)) if arr_match else required
    except Exception:
        files = required

    cleaned = []
    for f in files:
        if isinstance(f, str):
            f = f.strip().replace("\\", "/")
            if f and not f.startswith("/") and ".." not in Path(f).parts:
                cleaned.append(f)

    for f in required:
        if f not in cleaned:
            cleaned.append(f)

    cleaned = cleaned[:22]
    (ULTRA / "manifest.json").write_text(json.dumps(cleaned, indent=2))
    print("Manifest files:")
    print(json.dumps(cleaned, indent=2))
    return cleaned

def generate_file(file_path: str, all_files: list[str]):
    ext = Path(file_path).suffix.lower()
    lang = "kotlin"
    if ext == ".xml":
        lang = "xml"
    elif ext == ".properties":
        lang = "properties"
    elif ext == ".toml":
        lang = "toml"

    prompt = f"""
You are a senior Android engineer.

Generate ONE complete file for a production Android app.

App name: {APP_NAME}
Package name: {PACKAGE_NAME}
User request: {APP_PROMPT}

Full project file list:
{json.dumps(all_files, indent=2)}

Current file to generate:
{file_path}

Requirements:
- Output ONLY this file's code.
- No markdown explanation outside code.
- Kotlin + Jetpack Compose + Material 3.
- MVVM where applicable.
- Compile SDK 36.
- Min SDK 26.
- Java 17.
- Gradle Kotlin DSL.
- Use package {PACKAGE_NAME} for Kotlin files.
- No fake dependencies.
- No TODO placeholders.
- Keep implementation realistic and buildable.
"""
    out_name = "file_" + re.sub(r"[^a-zA-Z0-9]+", "_", file_path) + ".txt"
    out = run_ollama(prompt, out_name)
    content = extract_code(out)

    if file_path.endswith(".kt") and f"package {PACKAGE_NAME}" not in content:
        if "app/src/main/java/" in file_path:
            content = f"package {PACKAGE_NAME}\n\n" + re.sub(r"^\s*package\s+[\w.]+\s*", "", content)

    safe_write(file_path, content)

def validate_minimum():
    required = [
        "settings.gradle.kts",
        "build.gradle.kts",
        "app/build.gradle.kts",
        "app/src/main/AndroidManifest.xml",
        f"app/src/main/java/{PACKAGE_PATH}/MainActivity.kt",
    ]
    missing = [f for f in required if not Path(f).exists()]
    return missing

def generate_project():
    files = create_manifest_list()
    for f in files:
        generate_file(f, files)

    missing = validate_minimum()
    if missing:
        print("Missing critical files. Activating fallback.")
        print(missing)
        fallback_project()

if __name__ == "__main__":
    generate_project()
