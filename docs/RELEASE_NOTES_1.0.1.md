# ForgeCLI 1.0.1

**명령어 계층을 라이브러리로 개방했습니다**

`1.0`의 ForgeCLI는 실행 파일이었습니다. `1.0.1`부터는 실행 파일이면서 동시에
**명령어 계층 라이브러리**입니다. 함께 공개되는
[ForgeOS](https://github.com/Jongwoo0101/forge-os)의 터미널 앱이 이 계층을 그대로
가져다 씁니다 — GUI 쪽에 파서를 한 벌 더 만들지 않았습니다.

> **셸을 쓰던 방식은 아무것도 바뀌지 않았습니다.** 명령어도, 출력 형식도, 동작도
> `1.0`과 동일합니다. 이번 변경은 전부 더하기입니다.

---

## 받아서 바로 실행하기

`forgecli-1.0.1-all.jar` 하나만 받으면 됩니다. **커널이 포함되어 있어 별도 설치가 필요 없습니다.**

```bash
java -Dfile.encoding=UTF-8 -jar forgecli-1.0.1-all.jar
```

**요구 사항: JDK 21 이상**

> `-Dfile.encoding=UTF-8` 은 Windows 등에서 한글이 깨지지 않게 하기 위한 것입니다.
> 실행 파일이 콘솔 스트림을 UTF-8로 강제하지만, 셸 쪽 인코딩까지 맞춰두는 편이 안전합니다.

명령어는 **33종 그대로**입니다. 인자와 사용 예제는 [README](README.md)에 전부 정리되어 있습니다.

---

## 왜 개방했나

명령어 목록이 `ForgeShell`의 private 메서드 안에만 있으면, CLI가 아닌 클라이언트는
같은 목록을 손으로 다시 적는 수밖에 없습니다. 그렇게 두 벌이 되는 순간 **한쪽에만
명령이 추가되는 사고**가 반드시 일어납니다 — "CLI에서는 되는데 GUI에서는 안 되는"
명령이 생기는 것이죠.

등록 지점을 한 곳으로 모으고, 그 한 곳을 공개했습니다. 이제 명령어를 하나 추가하면
CLI의 `help`에도, ForgeOS 터미널에도 자동으로 나타납니다.

---

## 라이브러리로 쓰기

```kotlin
// build.gradle.kts
repositories { mavenLocal(); mavenCentral() }

dependencies {
    implementation("io.github.jongwoo0101:forgeframework:1.0")
    implementation("io.github.jongwoo0101:forgecli:1.0.1")
}
```

```java
ShellContext context = new ShellContext();                // 창마다 하나 — cwd 보관소
CommandRegistry registry = StandardCommands.createRegistry(context);

SystemCallResult result = registry.dispatch(kernel, "exec worker 20");
System.out.println(result.getMessage());
```

`dispatch`는 공백 기준 토큰 분리까지 포함합니다. 같은 문자열을 넣으면 셸과 정확히
같은 결과가 나옵니다.

> `ShellContext`는 현재 작업 디렉터리를 들고 있는 **가변** 객체입니다. 창(탭)마다
> cwd가 독립적이어야 하므로 레지스트리도 창마다 하나씩 만드십시오. 전역으로 공유하면
> 한 창에서 `cd`한 것이 다른 창에도 반영됩니다.

로컬 개발에서는 다음 한 줄로 설치합니다.

```bash
./gradlew publishToMavenLocal      # io.github.jongwoo0101:forgecli:1.0.1
```

---

## 1.0.1의 내용

### 추가

| 항목 | 설명 |
|---|---|
| `StandardCommands.createRegistry(ShellContext)` | 표준 명령어 33종의 **유일한** 등록 지점 |
| `CommandRegistry.dispatch(Kernel, String)` | 입력 한 줄을 토큰으로 나눠 실행. 토큰 분리 규칙이 CLI와 GUI에 흩어지지 않습니다 |
| JPMS 개방 | `exports forgeframework.cli.command` · `exports forgeframework.cli.shell` |
| Maven 배포 | `maven-publish` 로 `io.github.jongwoo0101:forgecli:1.0.1` 발행 |

`requires forgeframework` 가 `requires transitive` 로 바뀌었습니다.
`Command.execute` 시그니처가 커널의 `Kernel` · `SystemCallResult` 를 그대로 노출하므로
전이 의존이어야 합니다.

### 변경

- `ForgeShell` 이 명령어 목록과 토큰 분리 책임을 내려놓고 **표준 입출력 REPL** 만
  담당합니다 (import 34줄 제거)
- 명령어 클래스 23개와 `ShellContext` 에 명시적 생성자를 추가했습니다. 패키지를 export
  하면 `-Xlint:all` 의 `missing-explicit-ctor` 가 켜지는데, **경고 0건 기준선**을 지키려고
  경고를 끄는 대신 생성자를 넣었습니다
- `scripts/run.sh` 가 jar 이름에서 버전을 찾지 않습니다. 버전을 올릴 때마다 썩던 줄입니다

### 개방하지 않은 것

`ForgeShell` 자체는 export하지 않았습니다. 표준 입출력에 묶여 있는 것은 CLI 고유의
책임이고, 공개 API로 약속하지 않은 것은 처음부터 내보내지 않는 편이 이후 변경을
자유롭게 합니다.

---

## 호환성

| 항목 | 내용 |
|---|---|
| 셸 사용자 | **영향 없음.** 명령어·출력·동작이 `1.0` 과 동일합니다 |
| 필요한 커널 | ForgeFramework `1.0` (변경 없음) |
| JDK | 21 이상 |
| 산출물 이름 | `forgecli-1.0-all.jar` → **`forgecli-1.0.1-all.jar`** |

---

## 파일

| 파일 | 용도 |
|---|---|
| `forgecli-1.0.1-all.jar` | **커널이 포함된 단일 실행 jar.** 대부분 이것만 받으면 됩니다 |
| `forgecli-1.0.1.jar` | 얇은 jar. 실행하려면 `forgeframework-1.0.jar` 가 클래스패스에 필요합니다 |
| `forgecli-1.0.1-sources.jar` | IDE 소스 탐색용 |

소스에서 직접 빌드하려면 커널을 먼저 로컬 Maven 저장소에 설치해야 합니다.

```bash
# 1) 커널 설치
git clone https://github.com/Jongwoo0101/forge-framework.git
cd forge-framework && ./gradlew publishToMavenLocal

# 2) CLI 빌드
cd ../forge-cli && ./gradlew build
./gradlew run --console=plain     # 개발 중에는 이쪽이 빠릅니다
```

---

## 관련 저장소

| 저장소 | 설명 |
|---|---|
| [forge-framework](https://github.com/Jongwoo0101/forge-framework) | 커널 엔진 · [API 문서](https://github.com/Jongwoo0101/forge-framework/blob/master/docs/api/README.md) |
| [forge-os](https://github.com/Jongwoo0101/forge-os) | JavaFX 기반 GUI 시뮬레이터 — 터미널 앱이 이 저장소의 명령어 계층을 재사용합니다 |
| ForgeStudio | 운영체제 교육 · 시각화 플랫폼 (예정) |

---

<details>
<summary><b>English</b></summary>

## ForgeCLI 1.0.1

**The command layer is now a library.**

ForgeCLI `1.0` was an executable. From `1.0.1` it is an executable *and* a **command-layer
library**. The Terminal app in [ForgeOS](https://github.com/Jongwoo0101/forge-os), released
alongside this, uses that layer directly — no second parser was written for the GUI.

> **Nothing about using the shell has changed.** Commands, output format and behaviour are
> identical to `1.0`. Everything in this release is additive.

### Run it

Download `forgecli-1.0.1-all.jar` — **the kernel is bundled, nothing else to install.**

```bash
java -Dfile.encoding=UTF-8 -jar forgecli-1.0.1-all.jar
```

**Requires JDK 21.** All 33 commands are unchanged; arguments and examples live in the
[README](README.en.md).

### Why open it up

When the command list lives only inside a private method of `ForgeShell`, any client that
is not the CLI has to retype that list. The moment there are two copies, a command
**inevitably gets added to only one of them** — and you get a command that works in the CLI
but not in the GUI.

There is now a single registration point, and it is public. Add a command once and it shows
up in `help` and in the ForgeOS Terminal.

### Using it as a library

```kotlin
dependencies {
    implementation("io.github.jongwoo0101:forgeframework:1.0")
    implementation("io.github.jongwoo0101:forgecli:1.0.1")
}
```

```java
ShellContext context = new ShellContext();                // one per window — holds the cwd
CommandRegistry registry = StandardCommands.createRegistry(context);

SystemCallResult result = registry.dispatch(kernel, "exec worker 20");
System.out.println(result.getMessage());
```

`dispatch` includes whitespace tokenisation, so the same string produces exactly the same
result as it would in the shell.

> `ShellContext` is a **mutable** holder for the current working directory. Each window (or
> tab) needs its own, so build one registry per window. Share it globally and a `cd` in one
> window silently moves the others.

### What's in 1.0.1

- **Added** — `StandardCommands.createRegistry(ShellContext)` (the single registration point
  for all 33 commands), `CommandRegistry.dispatch(Kernel, String)` (the single owner of the
  tokenisation rule), JPMS exports for `command` and `shell`, and Maven publication as
  `io.github.jongwoo0101:forgecli:1.0.1`.
- **Changed** — `ForgeShell` now owns only the stdin/stdout REPL. 23 command classes and
  `ShellContext` gained explicit constructors: exporting a package turns on
  `missing-explicit-ctor` under `-Xlint:all`, and this project keeps a **zero-warning**
  baseline, so the constructors were added rather than the warning suppressed.
- **Not exported** — `ForgeShell` itself. Being tied to stdin/stdout is the CLI's own concern.

### Compatibility

| Item | Detail |
|---|---|
| Shell users | **No impact.** Commands, output and behaviour match `1.0` |
| Kernel required | ForgeFramework `1.0` (unchanged) |
| JDK | 21 or newer |
| Artifact name | `forgecli-1.0-all.jar` → **`forgecli-1.0.1-all.jar`** |

### Files

| File | Purpose |
|---|---|
| `forgecli-1.0.1-all.jar` | **Single runnable jar with the kernel bundled in** — take this one |
| `forgecli-1.0.1.jar` | Thin jar; needs `forgeframework-1.0.jar` on the classpath |
| `forgecli-1.0.1-sources.jar` | Source browsing in the IDE |

</details>
