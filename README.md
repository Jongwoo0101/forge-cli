<div align="center">

<img src="assets/forge-cli-banner.svg" alt="ForgeCLI — Interactive Terminal Client for ForgeFramework Kernel" width="860">

**🇰🇷 한국어 문서** · [English](README.en.md)

</div>

---

ForgeCLI는 [ForgeFramework](https://github.com/Jongwoo0101/forge-framework) 커널을
터미널에서 조작하는 셸입니다. 프로세스를 만들고, 메모리를 할당하고, 파일을 쓰고,
교착 상태를 일부러 만들어 탐지·복구하는 과정을 **명령어 한 줄씩** 눈으로 볼 수 있습니다.

<img src="assets/forge-logo.svg" alt="Forge" width="76" align="right">

이 저장소에는 커널 코드가 없습니다. 커널은 Maven 아티팩트로 가져오며,
**의존은 단방향**입니다 — ForgeCLI가 사라져도 커널은 아무 영향을 받지 않습니다.

```text
                       ForgeFramework          ← 커널 (별도 저장소)
                              ▲
                              │  requires
                ┌─────────────┼─────────────┐
                │             │             │
           ForgeOS      ★ ForgeCLI ★    ForgeStudio
```

---

## 목차

- [빠른 시작](#빠른-시작)
- [셸 사용법](#셸-사용법)
- [명령어 레퍼런스](#명령어-레퍼런스)
  - [커널](#커널)
  - [프로세스 · 스케줄러](#프로세스--스케줄러)
  - [메모리 · 페이징](#메모리--페이징)
  - [파일 시스템](#파일-시스템)
  - [장치 · 인터럽트](#장치--인터럽트)
  - [자원 · 교착 상태](#자원--교착-상태)
- [시나리오로 배우기](#시나리오로-배우기)
- [프로젝트 구조](#프로젝트-구조)
- [License](#license)

---

## 빠른 시작

### 요구 사항

- **JDK 21** 이상
- ForgeFramework 커널 `1.0`이 로컬 Maven 저장소(`~/.m2`)에 설치되어 있을 것

### 1. 커널을 먼저 설치합니다

```bash
git clone https://github.com/Jongwoo0101/forge-framework.git
cd forge-framework
./gradlew publishToMavenLocal      # 또는 ./scripts/publish.sh
```

`io.github.jongwoo0101:forgeframework:1.0`이 `~/.m2/repository`에 설치됩니다.

### 2. ForgeCLI를 빌드하고 실행합니다

```bash
git clone https://github.com/Jongwoo0101/forge-cli.git
cd forge-cli

./scripts/build.sh                 # 또는 ./gradlew build
./scripts/run.sh                   # 또는 java -jar build/libs/forgecli-1.0-all.jar
```

개발 중이라면 Gradle에서 바로 띄우는 쪽이 빠릅니다.

```bash
./gradlew run --console=plain
```

### 산출물

| 파일 | 설명 |
|---|---|
| `build/libs/forgecli-1.0-all.jar` | **커널이 포함된 단일 실행 jar.** 받아서 `java -jar`로 바로 실행됩니다. |
| `build/libs/forgecli-1.0.jar` | 얇은 jar. 실행하려면 커널 jar가 클래스패스에 필요합니다. |

> 커널을 찾지 못해 의존성 해석에서 실패한다면, 1번 단계의 `publishToMavenLocal`을
> 실행하지 않았을 가능성이 큽니다.

---

## 셸 사용법

부팅이 끝나면 프롬프트가 뜹니다. 프롬프트에는 **현재 작업 디렉터리**가 표시됩니다.

```text
=================================================
 ForgeFramework v1.0
 Operating System Kernel Architecture Engine
=================================================
[10:32:11.153] [INFO] 하드웨어 점검 중...
[10:32:11.306] [INFO] 이벤트 로거 초기화 중...
[10:32:11.457] [INFO] 커널 초기화 중...
[10:32:11.607] [INFO] 서브시스템 초기화 중...
...
ForgeFramework Shell에 오신 것을 환영합니다. 'help'를 입력해보세요.
forgeframework:/>
```

- 명령어와 인자는 **공백**으로 구분합니다.
- 등록되지 않은 이름을 입력하면 알 수 없는 명령어라고만 답하고 셸은 계속 살아 있습니다.
- 종료는 `shutdown`입니다. 종료 시 타이머·프린터 데몬까지 정리됩니다.
- `[INFO]` 로 시작하는 줄은 **커널이 실시간으로 흘리는 이벤트 로그**입니다. 명령어의
  응답과 별개로, 다른 스레드(타이머 등)가 일으킨 사건도 그대로 섞여 나옵니다.

---

## 명령어 레퍼런스

표기 규칙: `<필수>` · `[선택]` · `a|b` 중 택일

### 커널

| 명령어 | 인자 | 설명 |
|---|---|---|
| `help` | — | 등록된 명령어 목록과 설명을 출력합니다. |
| `uptime` | — | 커널 가동 시간을 출력합니다. |
| `shutdown` | — | ForgeFramework를 종료하고 셸을 빠져나옵니다. |

```text
forgeframework:/> uptime
forgeframework:/> shutdown
ForgeFramework v1.0를 종료합니다. (총 가동 시간: 42초)
```

---

### 프로세스 · 스케줄러

| 명령어 | 인자 | 설명 |
|---|---|---|
| `ps` | — | 프로세스 목록을 상태·CPU 사용량과 함께 표로 출력합니다. |
| `exec` | `<이름> [burstTime]` | 새 프로세스를 생성합니다. `burstTime`을 생략하면 기본값 5 tick. |
| `kill` | `<PID>` | 프로세스를 강제 종료합니다. 보유한 메모리와 자원이 회수됩니다. |
| `scheduler` | `[fcfs\|rr]` | 인자가 없으면 현재 스케줄러를 조회하고, 있으면 **런타임에 교체**합니다. |

**`exec`** — 생성된 프로세스는 곧바로 `READY`가 되어 ready queue에 올라갑니다.

```text
forgeframework:/> exec worker 20
프로세스가 생성되었습니다. (PID: 1, name: worker, burstTime: 20, state: READY)
```

**`ps`**

```text
forgeframework:/> ps
PID   | STATE      | CPU_TIME | BURST      | NAME
-------------------------------------------------------
1     | RUNNING    | 3        | 20         | worker
2     | READY      | 0        | 10         | logger
```

**`scheduler`** — `rr`은 `roundrobin` · `round-robin`으로도 받으며 대소문자를 가리지 않습니다.
선점형(RR)에서는 타임 퀀텀(기본 3 tick)마다 Context Switch가 일어나고,
비선점형(FCFS)에서는 프로세스가 끝날 때까지 CPU를 놓지 않습니다.

```text
forgeframework:/> scheduler
forgeframework:/> scheduler fcfs
```

> 시간은 **타이머 인터럽트가 1초에 한 번** 흐르게 합니다. `ps`를 몇 초 간격으로 두 번
> 실행하면 `CPU_TIME`이 올라가고 상태가 바뀌는 것을 볼 수 있습니다.

---

### 메모리 · 페이징

| 명령어 | 인자 | 설명 |
|---|---|---|
| `malloc` | `<PID> <size>` | 해당 프로세스의 힙에 메모리를 할당하고 **가상 주소**를 돌려줍니다. |
| `free` | `<PID> <address>` | `malloc`이 돌려준 주소를 해제합니다. |
| `meminfo` | — | 물리 메모리·프로세스별 힙·TLB 통계를 출력합니다. |
| `translate` | `<PID> <가상주소>` | 가상 주소를 물리 주소로 변환하고 **TLB 적중 여부**를 함께 보여줍니다. |
| `frametable` | — | 물리 프레임 테이블 전체(프레임별 소유자·페이지 매핑)를 출력합니다. |

기본 물리 메모리는 **16 프레임 × 4 byte**, TLB는 **4 엔트리**입니다.

```text
forgeframework:/> exec app 10
forgeframework:/> malloc 1 8
forgeframework:/> translate 1 0
forgeframework:/> translate 1 0      ← 두 번째 호출은 TLB HIT
forgeframework:/> meminfo
forgeframework:/> frametable
```

> `translate`를 같은 주소로 두 번 부르면 첫 번째는 MISS, 두 번째는 HIT입니다.
> TLB 용량이 4이므로 서로 다른 페이지를 5개 이상 훑으면 다시 MISS가 나기 시작합니다 —
> TLB 교체를 눈으로 확인하기 좋은 실험입니다.

---

### 파일 시스템

| 명령어 | 인자 | 설명 |
|---|---|---|
| `pwd` | — | 현재 작업 디렉터리를 출력합니다. |
| `cd` | `<path>` | 작업 디렉터리를 변경합니다. 절대·상대 경로 모두 가능합니다. |
| `ls` | `[path]` | 디렉터리 내용을 나열합니다. 생략하면 현재 디렉터리. |
| `mkdir` | `<name>` | 새 디렉터리를 만듭니다. 이름에 공백은 쓸 수 없습니다. |
| `touch` | `<name>` | 빈 파일을 만듭니다. |
| `rm` | `<name>` | 파일 또는 **빈** 디렉터리를 지웁니다. |
| `write` | `<name> <text...>` | 파일 내용을 **덮어씁니다**. 여러 단어는 공백으로 이어집니다. |
| `cat` | `<name>` | 파일 내용을 출력합니다. |
| `tree` | `[path]` | 디렉터리 구조를 트리 형태로 출력합니다. |

가상 디스크는 **16 블록 × 16 byte**, inode는 **16개**(루트 포함)입니다. 실제 디스크에
쓰지 않으므로 셸을 종료하면 내용은 사라집니다.

```text
forgeframework:/> mkdir docs
forgeframework:/> cd docs
forgeframework:/docs> touch notes.txt
forgeframework:/docs> write notes.txt hello forge
forgeframework:/docs> cat notes.txt
hello forge
forgeframework:/docs> cd /
forgeframework:/> tree
```

> `cd`는 **셸이 상태를 소유**합니다. 커널은 "지금 어느 디렉터리에 있는가"를 기억하지
> 않고, 셸이 매 시스템 콜마다 현재 경로를 함께 넘깁니다. 그래서 같은 커널에 셸을
> 여러 개 붙여도 각자의 작업 디렉터리를 가질 수 있습니다.

---

### 장치 · 인터럽트

| 명령어 | 인자 | 설명 |
|---|---|---|
| `devinfo` | — | 등록된 장치(keyboard · disk · printer · timer)의 상태와 대기 큐를 출력합니다. |
| `io_request` | `<PID> <device>` | 프로세스가 장치에 I/O를 요청합니다. 프로세스는 `WAITING`이 됩니다. |
| `type` | `<text...>` | **(가상 하드웨어)** 키보드 입력을 발생시켜 Keyboard 인터럽트를 일으킵니다. |
| `disk_finish` | — | **(가상 하드웨어)** 디스크 I/O 완료를 통보해 대기 중인 프로세스를 깨웁니다. |
| `soft_int` | `[payload...]` | Software Interrupt를 강제로 발생시킵니다. 생략 시 payload는 `manual`. |

`device`에 올 수 있는 값은 `keyboard` · `disk` · `printer` · `timer` 입니다.

`type`과 `disk_finish`는 명령어라기보다 **사람이 가상 하드웨어를 대신 밀어주는 스위치**에
가깝습니다. 실제 키보드나 디스크가 없으므로, 인터럽트를 일으키려면 누군가 대신
신호를 만들어야 합니다.

```text
forgeframework:/> exec reader 15
forgeframework:/> io_request 1 disk     ← PID 1 이 WAITING 으로 전이
forgeframework:/> ps                    ← STATE = WAITING 확인
forgeframework:/> disk_finish           ← 디스크 완료 통보 → PID 1 이 READY 로 복귀
forgeframework:/> ps
```

```text
forgeframework:/> type hello
forgeframework:/> soft_int checkpoint
forgeframework:/> devinfo
```

---

### 자원 · 교착 상태

자원은 **`R1` · `R2` · `R3` 세 종류**이고, 기본 총량은 `[10, 5, 7]`입니다.
벡터 인자는 앞에서부터 R1, R2, R3 순서이며 뒤쪽은 생략할 수 있습니다(생략분은 0).

| 명령어 | 인자 | 설명 |
|---|---|---|
| `res_info` | — | 자원 총량·가용량과 프로세스별 **Allocation · Max · Need** 행렬을 출력합니다. |
| `res_max` | `<PID> <R1> [R2] [R3]` | 프로세스의 최대 요구량(Max)을 선언합니다. Banker's의 입력이 됩니다. |
| `res_req` | `<PID> <R1> [R2] [R3]` | 자원을 요청합니다. |
| `res_free` | `<PID> <R1> [R2] [R3]` | 점유한 자원을 반납합니다. 대기 중이던 프로세스가 깨어날 수 있습니다. |
| `banker` | `[on\|off]` · `policy <정책>` | 교착 **회피**(Banker's Algorithm)를 켜거나 끄고, 희생자 선정 정책을 바꿉니다. |
| `detect` | — | 교착 상태(순환 대기)가 발생했는지 **탐지**합니다. |
| `recover` | — | 희생자를 선정해 강제 종료하고 교착을 **복구**합니다. |

**`res_req`의 세 가지 결말**

| 결과 | 의미 |
|---|---|
| `GRANTED` | 할당되었습니다. Banker's가 켜져 있으면 안전 순서(Safe Sequence)도 함께 출력됩니다. |
| `BLOCKED_INSUFFICIENT` | 가용 자원이 물리적으로 부족합니다. 프로세스는 `WAITING`이 됩니다. |
| `BLOCKED_UNSAFE` | 자원은 있지만 **주면 불안전 상태가 되므로** Banker's가 막았습니다. |

`BLOCKED_UNSAFE`는 교착 회피가 실제로 일하는 순간입니다. `banker off`로 두면 절대
나타나지 않으며, 대신 교착이 실제로 발생할 수 있게 됩니다.

**`banker`의 세 가지 형태**

```text
forgeframework:/> banker                    ← 현재 설정 조회
forgeframework:/> banker off                ← 교착 회피 끄기 (on/off, enable/disable, true/false)
forgeframework:/> banker policy HIGHEST_ALLOCATION
```

| 희생자 정책 | 의미 |
|---|---|
| `LOWEST_ALLOCATION` | 자원을 가장 적게 쥔 프로세스를 희생 (**기본값**) |
| `HIGHEST_ALLOCATION` | 자원을 가장 많이 쥔 프로세스를 희생 — 한 번에 많이 회수 |
| `YOUNGEST_FIRST` | 가장 나중에 생성된 프로세스를 희생 |

**`res_info` 출력 예**

```text
forgeframework:/> res_info
[System Resources]
             [ R1 R2 R3]
Total        [ 10  5  7]
Allocated    [  0  0  0]
Available    [ 10  5  7]
Banker's     ON (교착 회피) / 희생자 정책: LOWEST_ALLOCATION

[Process Matrix]
PID   | NAME       | ALLOCATION   | MAX          | NEED         | REQUEST
--------------------------------------------------------------------------------
1     | demo       | [  0  0  0]  | [ 10  5  7]  | [ 10  5  7]  | [  0  0  0]
```

---

## 시나리오로 배우기

### 시나리오 1 — 교착 회피(Avoidance)가 요청을 막는 순간

```text
exec p1 30
exec p2 30
res_max 1 7 5 3
res_max 2 3 2 2
res_req 1 7 4 3        ← GRANTED
res_req 2 3 2 2        ← BLOCKED_UNSAFE : 자원은 남아 있지만 주면 불안전해진다
res_info
```

`banker off`로 끄고 같은 요청을 다시 해 보면 `GRANTED`가 되고, 그 뒤 교착이 실제로
발생할 수 있습니다. 회피의 값어치가 가장 잘 드러나는 비교입니다.

### 시나리오 2 — 교착을 만들고 탐지·복구하기

```text
banker off             ← 회피를 꺼야 교착이 발생할 수 있다
exec p1 30
exec p2 30
res_req 1 6 0 0
res_req 2 4 4 0
res_req 1 4 0 0        ← 대기
res_req 2 0 2 0        ← 대기 → 순환 대기 성립
detect                 ← hasDeadlock = true, Wait-For 간선 목록 출력
recover                ← 희생자 강제 종료 → 자원 회수 → 대기 프로세스 깨어남
detect                 ← 이제 교착 없음
```

### 시나리오 3 — I/O 블로킹과 인터럽트

```text
exec reader 15
io_request 1 disk      ← WAITING
ps
disk_finish            ← 디스크 완료 인터럽트 → READY 복귀
ps
```

### 시나리오 4 — TLB 히트율 관찰

```text
exec app 20
malloc 1 8
translate 1 0          ← MISS
translate 1 0          ← HIT
meminfo                ← tlbHits / tlbMisses / hitRatio
```

---

## 프로젝트 구조

```text
forge-cli/
├── assets/                          # 로고·배너 SVG (README 및 릴리즈용)
├── build.gradle.kts                 # 커널을 Maven 아티팩트로 의존
├── settings.gradle.kts
├── gradle.properties                # forgeFrameworkVersion=1.0
├── scripts/
│   ├── build.sh
│   └── run.sh
└── src/main/java/
    ├── module-info.java             # requires forgeframework;
    └── forgeframework/cli/
        ├── ForgeCli.java            # 진입점 — 배너 · 로거 · 부팅 · 셸
        ├── shell/
        │   ├── ForgeShell.java      # REPL 루프와 명령어 등록
        │   ├── ShellContext.java    # 현재 작업 디렉터리 (셸이 소유하는 유일한 상태)
        │   └── ShellPrompt.java     # 프롬프트 렌더링
        └── command/
            ├── Command.java         # name() · description() · execute()
            ├── CommandRegistry.java # 등록 순서를 유지하는 조회 테이블
            ├── UnknownCommand.java  # Null Object
            └── ...                  # 명령어 33종
```

### 설계 원칙

- **Shell은 커널의 서브시스템에 절대 직접 접근하지 않습니다.** 모든 동작은
  `SystemCallRequest` → `kernel.handleSystemCall(...)`을 통과합니다.
- **커널은 문장을 만들지 않습니다.** 커널은 불변 record DTO만 돌려주고, 위에 보이는
  표·정렬·색은 전부 이 저장소의 Command 클래스가 만듭니다. 같은 DTO로 GUI를 그리면
  그것이 ForgeOS입니다.
- **명령어 추가는 파일 하나 + 등록 한 줄**입니다. `Command`를 구현하고
  `ForgeShell.registerDefaultCommands()`에 등록하면 `help`에도 자동으로 나타납니다.
- **셸이 소유하는 상태는 현재 작업 디렉터리 하나뿐**입니다. 나머지는 전부 커널에 있습니다.

---

## 관련 저장소

<table>
  <tr>
    <td width="64" align="center"><img src="assets/forge-framework-logo.svg" alt="" width="48"></td>
    <td>
      <b><a href="https://github.com/Jongwoo0101/forge-framework">forge-framework</a></b><br>
      <sub>커널 엔진 — 본 프로젝트가 의존합니다 ·
      <a href="https://github.com/Jongwoo0101/forge-framework/blob/master/docs/api/README.md">API 문서</a></sub>
    </td>
  </tr>
  <tr>
    <td width="64" align="center"><img src="assets/forge-cli-logo.svg" alt="" width="48"></td>
    <td><b>forge-cli</b><br><sub>본 저장소 — 커널의 명령줄 클라이언트</sub></td>
  </tr>
  <tr>
    <td width="64" align="center">🖥️</td>
    <td><b>ForgeOS</b><br><sub>JavaFX 기반 GUI 운영체제 시뮬레이터 (예정)</sub></td>
  </tr>
  <tr>
    <td width="64" align="center">📚</td>
    <td><b>ForgeStudio</b><br><sub>운영체제 교육 · 시각화 플랫폼 (예정)</sub></td>
  </tr>
</table>

---

## License

MIT License
