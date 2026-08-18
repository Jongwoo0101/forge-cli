# ForgeCLI 1.0.1

**명령어 계층을 라이브러리로 개방했습니다.**

`1.0` 의 ForgeCLI 는 실행 파일이었습니다. `1.0.1` 부터는 실행 파일이면서 동시에
**명령어 계층 라이브러리**입니다. 오늘 함께 공개하는
[ForgeOS](https://github.com/Jongwoo0101/forge-os) 의 터미널 앱이 이 계층을 그대로
가져다 씁니다 — GUI 쪽에 파서를 한 벌 더 만들지 않았습니다.

> **셸을 쓰던 방식은 아무것도 바뀌지 않았습니다.** 명령어도, 출력 형식도, 동작도
> `1.0` 과 동일합니다. 이번 변경은 전부 더하기입니다.

---

## 왜 개방했나

명령어 목록이 `ForgeShell` 의 private 메서드 안에만 있으면, CLI 가 아닌 클라이언트는
같은 목록을 손으로 다시 적는 수밖에 없습니다. 그렇게 두 벌이 되는 순간 **한쪽에만
명령이 추가되는 사고**가 반드시 일어납니다. "CLI 에서는 되는데 GUI 에서는 안 되는"
명령이 생기는 것이죠.

등록 지점을 한 곳으로 모으고 그 한 곳을 공개했습니다. 이제 명령어를 하나 추가하면
CLI 의 `help` 에도, ForgeOS 터미널에도 자동으로 나타납니다.

## 라이브러리로 쓰기

```bash
./gradlew publishToMavenLocal      # io.github.jongwoo0101:forgecli:1.0.1
```

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

`dispatch` 는 공백 기준 토큰 분리까지 포함합니다. 같은 문자열을 넣으면 셸과 정확히
같은 결과가 나옵니다.

> `ShellContext` 는 현재 작업 디렉터리를 들고 있는 **가변** 객체입니다. 창(탭)마다
> cwd 가 독립적이어야 하므로 레지스트리도 창마다 하나씩 만드십시오. 전역으로 공유하면
> 한 창에서 `cd` 한 것이 다른 창에도 반영됩니다.

---

## 변경 사항

### 추가

- **`StandardCommands.createRegistry(ShellContext)`** — 표준 명령어 33종의 유일한 등록 지점
- **`CommandRegistry.dispatch(Kernel, String)`** — 입력 한 줄을 토큰으로 나눠 실행.
  토큰 분리 규칙이 CLI 와 GUI 에 흩어지지 않도록 여기 하나에만 둡니다
- **JPMS 개방** — `exports forgeframework.cli.command` · `exports forgeframework.cli.shell`,
  그리고 `requires transitive forgeframework`
  (`Command.execute` 가 커널의 `Kernel` · `SystemCallResult` 를 그대로 노출하므로)
- **Maven 배포** — `maven-publish` 로 `io.github.jongwoo0101:forgecli:1.0.1` 발행

### 변경

- `ForgeShell` 이 명령어 목록과 토큰 분리 책임을 내려놓고 **표준 입출력 REPL** 만 담당합니다
  (import 34줄 제거)
- `scripts/run.sh` 가 jar 이름에서 버전을 찾지 않습니다. 버전을 올릴 때마다 썩던 줄을 고쳤습니다
- 명령어 클래스 23개와 `ShellContext` 에 명시적 생성자를 추가했습니다. 패키지를 export
  하면 `-Xlint:all` 의 `missing-explicit-ctor` 가 켜지는데, **경고 0건 기준선**을 지키려고
  경고를 끄는 대신 생성자를 넣었습니다

### 개방하지 않은 것

`ForgeShell` 자체는 export 하지 않았습니다. 표준 입출력에 묶여 있는 것은 CLI 고유의
책임이고, 공개 API 로 약속하지 않은 것은 처음부터 내보내지 않는 편이 이후 변경을
자유롭게 합니다.

---

## 호환성

| 항목 | 내용 |
|---|---|
| 셸 사용자 | **영향 없음.** 명령어·출력·동작이 `1.0` 과 동일합니다 |
| 필요한 커널 | ForgeFramework `1.0` (변경 없음) |
| JDK | 21 이상 |
| 산출물 이름 | `forgecli-1.0-all.jar` → **`forgecli-1.0.1-all.jar`** |

## 설치

```bash
# 커널을 먼저 설치합니다
git clone https://github.com/Jongwoo0101/forge-framework.git
cd forge-framework && ./gradlew publishToMavenLocal

# ForgeCLI
git clone https://github.com/Jongwoo0101/forge-cli.git
cd forge-cli
./scripts/build.sh
./scripts/run.sh
```

또는 아래 jar 를 받아 바로 실행합니다.

```bash
java -jar forgecli-1.0.1-all.jar
```

## 산출물

| 파일 | 설명 |
|---|---|
| `forgecli-1.0.1-all.jar` | 커널이 포함된 단일 실행 jar. 받아서 `java -jar` 로 바로 실행됩니다 |

---

**Full Changelog:** https://github.com/Jongwoo0101/forge-cli/compare/v1.0...v1.0.1
