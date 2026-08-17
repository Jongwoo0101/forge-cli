"""ForgeCLI 소스 전체를 하나의 Markdown 파일로 덤프한다.

"""

from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent

SOURCE_ROOT = PROJECT_ROOT / "src" / "main" / "java"

OUTPUT = PROJECT_ROOT / "forgecli_source_dump.md"


def collect():
    """(저장소 기준 상대경로, 파일 경로) 목록을 모은다."""
    if not SOURCE_ROOT.exists():
        return []
    return [
        (path.relative_to(PROJECT_ROOT), path)
        for path in sorted(SOURCE_ROOT.rglob("*.java"))
    ]


def main():
    files = collect()

    with OUTPUT.open("w", encoding="utf-8") as out:
        out.write("# ForgeCLI Source Dump\n\n")
        out.write(f"총 Java 파일 수 : **{len(files)}개**\n\n")
        out.write("- 모듈 : `forgecli` (커널)\n")

        out.write("\n---\n\n## Files\n\n")
        for rel, _ in files:
            out.write(f"- `{rel}`\n")

        out.write("\n---\n\n")

        for index, (rel, path) in enumerate(files, start=1):
            out.write(f"# {index}. {path.name}\n\n")
            out.write("**Path**\n")
            out.write(f"`{rel}`\n\n")
            out.write("```java\n")

            try:
                source = path.read_text(encoding="utf-8")
            except UnicodeDecodeError:
                source = path.read_text(encoding="utf-8", errors="replace")

            out.write(source)
            if not source.endswith("\n"):
                out.write("\n")

            out.write("```\n\n---\n\n")

    print()
    print("완료!")
    print(f"총 {len(files)}개의 Java 파일을 저장했습니다.")
    print(f"출력 파일 : {OUTPUT}")


if __name__ == "__main__":
    main()
