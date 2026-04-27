import re
from pathlib import Path

text = Path(".godmode/ai_output.txt").read_text(errors="ignore")

pattern = re.compile(
    r"FILE:\s*(.*?)\n```(?:kotlin|kt|xml|gradle|properties|toml|txt|text)?\n(.*?)```",
    re.DOTALL
)

matches = pattern.findall(text)

if not matches:
    print("No valid FILE blocks found.")
    print("AI output saved at .godmode/ai_output.txt")
    raise SystemExit(1)

count = 0

for raw_path, content in matches:
    path = raw_path.strip().replace("\\", "/")

    if path.startswith("/") or ".." in Path(path).parts:
        print(f"Skipping unsafe path: {path}")
        continue

    file_path = Path(path)
    file_path.parent.mkdir(parents=True, exist_ok=True)
    file_path.write_text(content.strip() + "\n")
    print(f"Wrote {file_path}")
    count += 1

print(f"Total files written: {count}")
