# 커밋 플랜 — ForgeCLI 1.0.1

이번 릴리즈의 내용은 하나로 요약된다. **명령어 계층을 라이브러리로 개방했다.**
[ForgeOS](https://github.com/Jongwoo0101/forge-os)의 터미널 앱이 같은 명령어 세트를
쓰기 위한 변경이며, CLI 자체의 동작은 한 군데도 바뀌지 않는다.

파일명을 ASCII 로 둔 이유: macOS NFD 파일명이 Linux 쪽 git 에서 NFC 로 트래킹된 항목과
중복 등록되는 문제가 있었다(`docs/Phase4_테스트시나리오.md` 등).

> 총 대상: 수정 32개 · 신규 4개.
> 아래 순서대로 커밋하면 각 커밋이 하나의 이유만 담는다.

---

## 1. `refactor(shell): 명령어 등록과 토큰 분리를 한 곳으로 모은다`

```
새 파일  src/main/java/forgeframework/cli/command/StandardCommands.java
수정     src/main/java/forgeframework/cli/command/CommandRegistry.java
수정     src/main/java/forgeframework/cli/shell/ForgeShell.java
```

명령어 목록이 `ForgeShell`의 private 메서드 안에만 있으면, CLI 가 아닌 클라이언트는
같은 목록을 손으로 다시 적는 수밖에 없다. 그렇게 두 벌이 되는 순간 한쪽에만 명령이
추가되는 사고가 반드시 일어난다.

- `StandardCommands.createRegistry(ShellContext)` — 33종의 유일한 등록 지점
- `CommandRegistry.dispatch(Kernel, String)` — 공백 기준 토큰 분리 규칙의 유일한 소유자
- `ForgeShell` — import 34줄이 사라지고 "System.in 에서 읽어 System.out 에 쓴다"만 남음

`CommandRegistry`의 명시적 생성자도 이 커밋에 함께 들어간다(2번의 이유와 같지만
파일이 이미 이 커밋에서 바뀌므로 쪼개지 않는다).

---

## 2. `chore(lint): 개방할 패키지에 명시적 생성자를 추가한다`

```
수정  src/main/java/forgeframework/cli/command/*.java   (23개)
      Banker · Detect · Devinfo · DiskFinish · Exec · FrameTable · Free · IoRequest ·
      Kill · Malloc · Meminfo · Ps · Recover · ResFree · ResInfo · ResMax · ResReq ·
      Scheduler · Shutdown · SoftInt · Translate · Type · Uptime
수정  src/main/java/forgeframework/cli/shell/ShellContext.java
```

패키지를 export 하면 `-Xlint:all` 의 `missing-explicit-ctor` 가 켜진다. 경고 0건이
이 프로젝트의 릴리즈 기준선이므로, 경고를 끄는 대신 생성자를 넣었다. 커널의
`EventLogger` 도 같은 이유로 명시적 생성자를 갖고 있다.

> 3번보다 **먼저** 커밋해야 한다. 순서를 바꾸면 3번 커밋 시점에 빌드가 깨진다.

---

## 3. `feat(api): command · shell 패키지를 공개하고 라이브러리로 배포한다`

```
수정  src/main/java/module-info.java
수정  build.gradle.kts
```

- `requires transitive forgeframework` — `Command.execute` 시그니처가 커널의 `Kernel`과
  `SystemCallResult` 를 그대로 노출하므로 전이 의존이어야 한다
- `exports forgeframework.cli.command` · `exports forgeframework.cli.shell`
- `maven-publish` 플러그인 + `forgecli` artifactId 로 publishing 블록

배포되는 것은 fatJar 가 아니라 일반 jar 다. 클라이언트가 커널을 이미 직접 의존하므로
커널이 두 번 들어가면 안 된다.

표준 입출력에 묶인 `ForgeShell` 은 개방하지 않는다 — CLI 고유의 책임이다.

---

## 4. `chore(release): 1.0.1`

```
수정  gradle.properties        version=1.0 → 1.0.1
수정  scripts/run.sh           jar 이름에서 버전을 뺀다
수정  .gitignore               _to_delete/ · hs_err_pid*.log 추가
```

`run.sh` 가 `forgecli-1.0-all.jar` 를 하드코딩하고 있었다. 버전을 올릴 때마다 이 줄이
썩으므로 `ls build/libs/forgecli-*-all.jar` 로 찾아 쓰도록 바꿨다.

---

## 5. `docs: 라이브러리 사용법과 ForgeOS 연결을 반영한다`

```
수정     README.md
수정     README.en.md
새 파일  assets/forge-os-logo.svg
새 파일  docs/COMMIT_PLAN_1.0.1.md          (이 문서)
새 파일  docs/RELEASE_NOTES_1.0.1.md
```

README 변경 내역:

| 위치 | 내용 |
|---|---|
| 목차 · 새 절 | **라이브러리로 쓰기** — 좌표, 예제 코드, `ShellContext` 주의사항 |
| 산출물 표 | `forgecli-1.0-all.jar` → `forgecli-1.0.1-all.jar` |
| 프로젝트 구조 | `StandardCommands.java` 추가, `module-info` 설명을 개방 상태로 |
| 설계 원칙 | 등록 지점이 `ForgeShell.registerDefaultCommands()` → `StandardCommands.createRegistry(...)` |
| 관련 저장소 | ForgeOS "(예정)" → forge-os 저장소 링크 |

기존 `docs/COMMIT_PLAN_library_opening.md` 는 이 문서로 대체된다. 이미 커밋된 적이
없으므로 파일을 지우고 이 문서만 남기면 된다.

---

## 커밋 후 — 릴리즈

```bash
./gradlew clean build              # 경고 0건 확인
./gradlew publishToMavenLocal      # io.github.jongwoo0101:forgecli:1.0.1

git push origin main
git tag -a v1.0.1 -m "ForgeCLI 1.0.1"
git push origin v1.0.1
```

기존 `v1.0` 태그는 그대로 두고 **새 태그 `v1.0.1`** 을 만든다. 이미 공개된 릴리즈의
태그를 재사용하면 그 시점의 소스를 받아 간 사람이 다른 코드를 보게 된다.

GitHub → Releases → **Draft a new release**

| 항목 | 값 |
|---|---|
| Tag | `v1.0.1` |
| Target | `main` |
| Title | `ForgeCLI 1.0.1` |
| 본문 | `docs/RELEASE_NOTES_1.0.1.md` 전체 붙여넣기 |
| 첨부 | `build/libs/forgecli-1.0.1-all.jar` (필요하면 `-sources.jar` 도) |

`Set as the latest release` 체크. Pre-release 아님.

## 검증 (이번 세션에서 확인한 것)

- `./gradlew build` 경고 0건 (`-Xlint:all -Werror`)
- fat jar 실행 후 `help` · `exec` · `ps` · `res_req` · `detect` · `shutdown` 정상
- `forgecli:1.0.1` 좌표로 ForgeOS 가 컴파일됨
