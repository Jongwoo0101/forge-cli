# ForgeCLI Source Dump

총 Java 파일 수 : **42개**

- 모듈 : `forgecli` (커널)

---

## Files

- `src/main/java/forgeframework/cli/ForgeCli.java`
- `src/main/java/forgeframework/cli/command/BankerCommand.java`
- `src/main/java/forgeframework/cli/command/CatCommand.java`
- `src/main/java/forgeframework/cli/command/CdCommand.java`
- `src/main/java/forgeframework/cli/command/Command.java`
- `src/main/java/forgeframework/cli/command/CommandRegistry.java`
- `src/main/java/forgeframework/cli/command/DetectCommand.java`
- `src/main/java/forgeframework/cli/command/DevinfoCommand.java`
- `src/main/java/forgeframework/cli/command/DiskFinishCommand.java`
- `src/main/java/forgeframework/cli/command/ExecCommand.java`
- `src/main/java/forgeframework/cli/command/FrameTableCommand.java`
- `src/main/java/forgeframework/cli/command/FreeCommand.java`
- `src/main/java/forgeframework/cli/command/HelpCommand.java`
- `src/main/java/forgeframework/cli/command/IoRequestCommand.java`
- `src/main/java/forgeframework/cli/command/KillCommand.java`
- `src/main/java/forgeframework/cli/command/LsCommand.java`
- `src/main/java/forgeframework/cli/command/MallocCommand.java`
- `src/main/java/forgeframework/cli/command/MeminfoCommand.java`
- `src/main/java/forgeframework/cli/command/MkdirCommand.java`
- `src/main/java/forgeframework/cli/command/PsCommand.java`
- `src/main/java/forgeframework/cli/command/PwdCommand.java`
- `src/main/java/forgeframework/cli/command/RecoverCommand.java`
- `src/main/java/forgeframework/cli/command/ResFreeCommand.java`
- `src/main/java/forgeframework/cli/command/ResInfoCommand.java`
- `src/main/java/forgeframework/cli/command/ResMaxCommand.java`
- `src/main/java/forgeframework/cli/command/ResReqCommand.java`
- `src/main/java/forgeframework/cli/command/ResourceFormat.java`
- `src/main/java/forgeframework/cli/command/RmCommand.java`
- `src/main/java/forgeframework/cli/command/SchedulerCommand.java`
- `src/main/java/forgeframework/cli/command/ShutdownCommand.java`
- `src/main/java/forgeframework/cli/command/SoftIntCommand.java`
- `src/main/java/forgeframework/cli/command/TouchCommand.java`
- `src/main/java/forgeframework/cli/command/TranslateCommand.java`
- `src/main/java/forgeframework/cli/command/TreeCommand.java`
- `src/main/java/forgeframework/cli/command/TypeCommand.java`
- `src/main/java/forgeframework/cli/command/UnknownCommand.java`
- `src/main/java/forgeframework/cli/command/UptimeCommand.java`
- `src/main/java/forgeframework/cli/command/WriteCommand.java`
- `src/main/java/forgeframework/cli/shell/ForgeShell.java`
- `src/main/java/forgeframework/cli/shell/ShellContext.java`
- `src/main/java/forgeframework/cli/shell/ShellPrompt.java`
- `src/main/java/module-info.java`

---

# 1. ForgeCli.java

**Path**
`src/main/java/forgeframework/cli/ForgeCli.java`

```java
package forgeframework.cli;

import forgeframework.api.ForgeConfig;
import forgeframework.api.ForgeFramework;
import forgeframework.cli.shell.ForgeShell;
import forgeframework.kernel.Kernel;
import forgeframework.logger.ConsoleLogListener;
import forgeframework.logger.EventLogger;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * ForgeCLI 애플리케이션의 진입점.
 *
 * <p>ForgeFramework 1.0에서 CLI는 <b>커널을 쓰는 여러 클라이언트 중 하나</b>로
 * 분리되었다. 이 모듈({@code forgeframework.cli})은 커널 모듈
 * ({@code forgeframework})에 의존할 뿐이며, 반대 방향 의존은 존재하지
 * 않는다 — 커널 jar에는 셸도 명령어도 들어 있지 않다.</p>
 *
 * <p>실행 순서: 콘솔 준비 → 배너 출력 → {@link ForgeFramework#boot} → 셸 실행.</p>
 *
 * <p><b>[1.0]</b> 배너 출력은 원래 {@code BootManager}에 있었다. GUI 클라이언트가
 * 임베딩할 때 표준 출력이 더럽혀지는 문제가 있어, 콘솔에 무언가를 찍는 책임을
 * 전부 이쪽(표현 계층)으로 옮겼다.</p>
 */
public final class ForgeCli {

    private ForgeCli() {
    }

    /**
     * CLI를 시작한다.
     *
     * @param args 사용하지 않는다
     */
    public static void main(String[] args) {
        forceUtf8Console();
        printBanner();

        // 리스너를 먼저 붙여야 부팅 과정 로그까지 콘솔에 나온다.
        EventLogger logger = new EventLogger();
        logger.addListener(new ConsoleLogListener());

        // CLI는 부팅 과정을 눈으로 보여주는 편이 낫기 때문에 기본 지연을 유지한다.
        Kernel kernel = ForgeFramework.boot(ForgeConfig.defaults(), logger);

        try {
            new ForgeShell(kernel).run();
        } finally {
            // 셸이 어떤 이유로 끝나든 백그라운드 스레드는 반드시 정리한다.
            kernel.close();
        }
    }

    private static void printBanner() {
        System.out.println("=================================================");
        System.out.println(" " + ForgeFramework.name() + " v" + ForgeFramework.version());
        System.out.println(" Operating System Kernel Architecture Engine");
        System.out.println("=================================================");
    }

    /**
     * 실행 환경의 로케일 설정과 무관하게 한글 등이 깨지지 않도록
     * 표준 출력/에러 스트림을 UTF-8로 강제한다.
     */
    private static void forceUtf8Console() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }
}
```

---

# 2. BankerCommand.java

**Path**
`src/main/java/forgeframework/cli/command/BankerCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.BankerConfigDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * Banker's Algorithm(교착 회피)을 런타임에 켜고 끄는 명령어.
 * 사용법: {@code banker [on|off]}, {@code banker policy <정책>}
 *
 * <p>끄면 불안전 상태로 이어질 위험한 요청도 무조건 승인되므로, 교착 상태를
 * 고의로 만들어 {@code detect} / {@code recover}의 동작을 관찰할 수 있다.</p>
 */
public final class BankerCommand implements Command {

    @Override
    public String name() {
        return "banker";
    }

    @Override
    public String description() {
        return "교착 회피(Banker's Algorithm)를 켜거나 끕니다. (banker [on|off] | banker policy <정책>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.BANKER, args));
        if (!result.isSuccess()) {
            return result;
        }

        BankerConfigDto dto = result.dataAs(BankerConfigDto.class);
        String state = dto.enabled() ? "ON" : "OFF";
        String note = dto.enabled()
                ? "불안전 상태로 이어질 요청은 보류됩니다."
                : "위험한 요청도 무조건 승인됩니다 — 교착 상태가 발생할 수 있습니다.";

        String headline;
        if (dto.policyChanged()) {
            headline = "희생자 선정 정책이 " + dto.victimPolicy() + " (으)로 변경되었습니다."
                    + " (Banker's Algorithm: " + state + ")";
        } else if (dto.toggled()) {
            headline = "Banker's Algorithm이 " + state + " 되었습니다."
                    + " (희생자 정책: " + dto.victimPolicy() + ")";
        } else {
            headline = "Banker's Algorithm: " + state + " / 희생자 정책: " + dto.victimPolicy();
        }

        return SystemCallResult.success(headline + "\n  " + note);
    }
}
```

---

# 3. CatCommand.java

**Path**
`src/main/java/forgeframework/cli/command/CatCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.FileContentDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 파일 내용을 출력하는 명령어. 사용법: cat &lt;name&gt;
 */
public final class CatCommand implements Command {

    private final ShellContext context;

    public CatCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "cat";
    }

    @Override
    public String description() {
        return "파일 내용을 출력합니다. (cat <name>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 1) {
            return SystemCallResult.failure("사용법: cat <name>");
        }
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.CAT, new String[]{context.getCwd(), args[0]}
        ));
        if (!result.isSuccess()) {
            return result;
        }
        FileContentDto dto = result.dataAs(FileContentDto.class);
        return SystemCallResult.success(dto.content());
    }
}
```

---

# 4. CdCommand.java

**Path**
`src/main/java/forgeframework/cli/command/CdCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.PathDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 작업 디렉터리를 변경하는 명령어. 사용법: cd &lt;path&gt;
 *
 * <p>Kernel이 대상 경로가 유효한 디렉터리인지 검증하고 절대경로로 해석해서
 * 돌려주면, 성공한 경우에만 이 명령어가 {@link ShellContext}의 CWD를 갱신한다
 * (Kernel 자신은 상태를 갖지 않는다).</p>
 */
public final class CdCommand implements Command {

    private final ShellContext context;

    public CdCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "cd";
    }

    @Override
    public String description() {
        return "작업 디렉터리를 변경합니다. (cd <path>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        String target = (args.length > 0) ? args[0] : "/";
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.CD, new String[]{context.getCwd(), target}
        ));
        if (!result.isSuccess()) {
            return result;
        }

        PathDto dto = result.dataAs(PathDto.class);
        context.setCwd(dto.resolvedPath());
        return SystemCallResult.success(dto.resolvedPath());
    }
}
```

---

# 5. Command.java

**Path**
`src/main/java/forgeframework/cli/command/Command.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

/**
 * Shell에서 실행 가능한 명령어를 표현하는 인터페이스 (Command 패턴).
 *
 * <p>모든 구현체는 반드시 {@link Kernel#handleSystemCall}을 통해서만
 * 실제 기능을 수행해야 하며, 커널 서브시스템에 직접 접근해서는 안 된다.</p>
 */
public interface Command {

    /**
     * 명령어 이름 (Shell에 입력하는 문자열).
     *
     * @return 명령어 이름
     */
    String name();

    /**
     * help 명령에서 보여줄 한 줄 설명.
     *
     * @return 명령어 설명
     */
    String description();

    /**
     * 명령어를 실행한다.
     *
     * @param kernel 시스템 콜을 전달할 Kernel
     * @param args   명령줄 인자 (명령어 이름 제외)
     * @return 실행 결과
     */
    SystemCallResult execute(Kernel kernel, String[] args);
}
```

---

# 6. CommandRegistry.java

**Path**
`src/main/java/forgeframework/cli/command/CommandRegistry.java`

```java
package forgeframework.cli.command;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 사용 가능한 {@link Command}들을 이름으로 조회할 수 있도록 관리하는 레지스트리.
 *
 * <p>등록 순서를 유지하기 위해 {@link LinkedHashMap}을 사용하며,
 * 등록되지 않은 이름으로 조회 시 {@link UnknownCommand}(Null Object)를 반환한다.</p>
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    /**
     * 명령어를 레지스트리에 등록한다.
     *
     * @param command 등록할 명령어
     */
    public void register(Command command) {
        commands.put(command.name(), command);
    }

    /**
     * 이름으로 명령어를 조회한다.
     *
     * @param name 조회할 명령어 이름
     * @return 등록된 명령어, 없으면 {@link UnknownCommand}
     */
    public Command resolve(String name) {
        return commands.getOrDefault(name, new UnknownCommand(name));
    }

    /**
     * 등록된 모든 명령어를 반환한다. help 명령에서 사용된다.
     *
     * @return 등록된 명령어 컬렉션 (등록 순서 유지)
     */
    public Collection<Command> getAll() {
        return commands.values();
    }
}
```

---

# 7. DetectCommand.java

**Path**
`src/main/java/forgeframework/cli/command/DetectCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.DeadlockDetectDto;
import forgeframework.deadlock.WaitForEdgeDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 교착 상태 탐지 알고리즘을 실행하는 명령어. 사용법: {@code detect}
 *
 * <p>순환 대기에 갇힌 프로세스 목록과, 누가 누구를 기다리고 있는지를 보여주는
 * 대기 그래프(Wait-For Graph) 간선을 함께 출력한다.</p>
 */
public final class DetectCommand implements Command {

    @Override
    public String name() {
        return "detect";
    }

    @Override
    public String description() {
        return "교착 상태(순환 대기)가 발생했는지 탐지합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.DETECT));
        if (!result.isSuccess()) {
            return result;
        }

        DeadlockDetectDto dto = result.dataAs(DeadlockDetectDto.class);
        if (!dto.hasDeadlock()) {
            return SystemCallResult.success(String.format(
                    "교착 상태가 감지되지 않았습니다. (검사 대상 %d개 프로세스, 가용 자원 %s)",
                    dto.inspected(), ResourceFormat.vector(dto.available())));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("⚠ 교착 상태가 감지되었습니다.\n");
        sb.append("  교착 프로세스: ").append(dto.deadlockedPids()).append('\n');
        sb.append("  가용 자원: ").append(ResourceFormat.vector(dto.available())).append('\n');
        sb.append("\n[Wait-For Graph]\n");
        for (WaitForEdgeDto edge : dto.waitForEdges()) {
            sb.append(String.format("  P%d ──(%s ×%d 부족)──> P%d%n",
                    edge.waiterPid(), edge.resource(), edge.shortage(), edge.holderPid()));
        }
        sb.append("\n'recover' 명령으로 희생자를 선정해 교착 상태를 해소할 수 있습니다.");

        return SystemCallResult.success(sb.toString());
    }
}
```

---

# 8. DevinfoCommand.java

**Path**
`src/main/java/forgeframework/cli/command/DevinfoCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.device.DeviceInfoDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 현재 커널에 등록된 장치(Keyboard, Disk, Printer, Timer)의 목록과 상태를 출력하는 명령어.
 *
 * <p>Kernel/DeviceManager는 {@link DeviceInfoDto} 리스트라는 순수 데이터만 반환하고,
 * 표 형태로 꾸미는 건 이 클래스(Shell 계층)의 책임이다 — FrameTableCommand와
 * 동일한 원칙을 따른다.</p>
 */
public final class DevinfoCommand implements Command {

    @Override
    public String name() {
        return "devinfo";
    }

    @Override
    public String description() {
        return "등록된 장치(Keyboard/Disk/Printer/Timer)의 목록과 상태를 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.DEVINFO));
        if (!result.isSuccess()) {
            return result;
        }

        List<DeviceInfoDto> devices = result.dataAsList(DeviceInfoDto.class);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-8s | %s%n", "NAME", "TYPE", "STATUS"));
        for (DeviceInfoDto device : devices) {
            sb.append(String.format("%-10s | %-8s | %s%n", device.name(), device.type(), device.status()));
        }

        return SystemCallResult.success(sb.toString().stripTrailing());
    }
}
```

---

# 9. DiskFinishCommand.java

**Path**
`src/main/java/forgeframework/cli/command/DiskFinishCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.device.HardwareEventDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * (가상 하드웨어 조작) 디스크 하드웨어가 작업을 마쳤음을 커널에 알리는 명령어.
 */
public final class DiskFinishCommand implements Command {

    @Override
    public String name() {
        return "disk_finish";
    }

    @Override
    public String description() {
        return "(가상 하드웨어) 디스크 I/O 작업 완료를 알립니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.HW_DISK));
        if (!result.isSuccess()) {
            return result;
        }

        HardwareEventDto dto = result.dataAs(HardwareEventDto.class);
        return SystemCallResult.success(
                "디스크 작업이 완료되어 대기 중이던 프로세스가 깨어났습니다. (남은 대기열 "
                        + dto.queueDepth() + "건)");
    }
}
```

---

# 10. ExecCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ExecCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.process.ExecResultDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 새 프로세스를 생성하는 명령어.
 *
 * <p>사용법: {@code exec <이름> [burstTime]}. burstTime을 생략하면
 * 커널의 기본 burst time이 적용된다.</p>
 */
public final class ExecCommand implements Command {

    @Override
    public String name() {
        return "exec";
    }

    @Override
    public String description() {
        return "새 프로세스를 생성합니다. (exec <이름> [burstTime])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.EXEC, args));
        if (!result.isSuccess()) {
            return result;
        }

        ExecResultDto dto = result.dataAs(ExecResultDto.class);
        return SystemCallResult.success(String.format(
                "프로세스가 생성되었습니다. (PID: %d, name: %s, burstTime: %d, state: %s)",
                dto.pid(), dto.name(), dto.burstTime(), dto.state()));
    }
}
```

---

# 11. FrameTableCommand.java

**Path**
`src/main/java/forgeframework/cli/command/FrameTableCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.FrameInfo;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 물리 프레임 전체의 상태(할당 여부, 소유 pid, 매핑된 페이지 번호)를 표로 출력하는 명령어.
 *
 * <p>Kernel/MemoryManager는 {@link FrameInfo} 리스트라는 순수 데이터만 반환하고,
 * 표 형태로 꾸미는 건 이 클래스(Shell 계층)의 책임이다 — MeminfoCommand/PsCommand와
 * 동일한 원칙을 따른다.</p>
 */
public final class FrameTableCommand implements Command {

    @Override
    public String name() {
        return "frametable";
    }

    @Override
    public String description() {
        return "물리 프레임 테이블(프레임별 소유자/페이지 매핑)을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.FRAMETABLE));
        if (!result.isSuccess()) {
            return result;
        }

        List<FrameInfo> frames = result.dataAsList(FrameInfo.class);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s | %-6s | %-5s | %s%n", "FRAME", "STATUS", "PID", "PAGE"));
        for (FrameInfo frame : frames) {
            sb.append(String.format(
                    "%-6d | %-6s | %-5s | %s%n",
                    frame.frameNumber(),
                    frame.allocated() ? "USED" : "FREE",
                    frame.allocated() ? String.valueOf(frame.ownerPid()) : "-",
                    frame.allocated() ? String.valueOf(frame.pageNumber()) : "-"
            ));
        }

        return SystemCallResult.success(sb.toString().stripTrailing());
    }
}
```

---

# 12. FreeCommand.java

**Path**
`src/main/java/forgeframework/cli/command/FreeCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.FreeResultDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 할당된 메모리를 해제하는 명령어. 사용법: free &lt;PID&gt; &lt;address&gt;
 */
public final class FreeCommand implements Command {

    @Override
    public String name() {
        return "free";
    }

    @Override
    public String description() {
        return "할당된 메모리를 해제합니다. (free <PID> <address>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.FREE, args));
        if (!result.isSuccess()) {
            return result;
        }

        FreeResultDto dto = result.dataAs(FreeResultDto.class);
        return SystemCallResult.success("PID " + dto.pid() + ": 주소 " + dto.address() + " 해제됨");
    }
}
```

---

# 13. HelpCommand.java

**Path**
`src/main/java/forgeframework/cli/command/HelpCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 등록된 모든 명령어의 목록과 설명을 출력하는 명령어.
 *
 * <p>명령어 목록 자체는 Shell 계층의 관심사({@link CommandRegistry})이므로
 * 여기서 직접 조합하되, 이벤트 기록을 위해 {@link Kernel#handleSystemCall}은
 * 반드시 거친다 (Shell → Kernel 직접 접근 금지 원칙 준수).</p>
 */
public final class HelpCommand implements Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "사용 가능한 명령어 목록을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        kernel.handleSystemCall(new SystemCallRequest(SystemCallType.HELP));

        StringBuilder builder = new StringBuilder("사용 가능한 명령어:\n");
        for (Command command : registry.getAll()) {
            builder.append(String.format("  %-10s %s%n", command.name(), command.description()));
        }
        return SystemCallResult.success(builder.toString().stripTrailing());
    }
}
```

---

# 14. IoRequestCommand.java

**Path**
`src/main/java/forgeframework/cli/command/IoRequestCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.device.IoRequestDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스가 장치에 I/O를 요청하는 명령어. 사용법: {@code io_request <pid> <device>}
 *
 * <p>요청한 프로세스는 즉시 WAITING 상태가 되며, 이후 {@code type}이나
 * {@code disk_finish}로 해당 장치의 하드웨어 이벤트가 발생해야 다시 깨어난다.</p>
 */
public final class IoRequestCommand implements Command {

    @Override
    public String name() {
        return "io_request";
    }

    @Override
    public String description() {
        return "프로세스가 장치에 I/O를 요청합니다. 사용법: io_request <pid> <device>";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length != 2) {
            return SystemCallResult.failure("사용법: io_request <pid> <device>");
        }
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.IO_REQ, args));
        if (!result.isSuccess()) {
            return result;
        }

        IoRequestDto dto = result.dataAs(IoRequestDto.class);
        return SystemCallResult.success(String.format(
                "PID %d 가 %s 장치에 I/O를 요청하여 WAITING 상태로 전이되었습니다. (대기열 %d건)",
                dto.pid(), dto.device(), dto.queueDepth()));
    }
}
```

---

# 15. KillCommand.java

**Path**
`src/main/java/forgeframework/cli/command/KillCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.process.KillResultDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스를 강제 종료하는 명령어. 사용법: {@code kill <PID>}
 */
public final class KillCommand implements Command {

    @Override
    public String name() {
        return "kill";
    }

    @Override
    public String description() {
        return "프로세스를 강제 종료합니다. (kill <PID>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.KILL, args));
        if (!result.isSuccess()) {
            return result;
        }

        KillResultDto dto = result.dataAs(KillResultDto.class);
        String name = (dto.name() == null) ? "" : " (" + dto.name() + ")";
        return SystemCallResult.success("프로세스가 종료되었습니다: PID " + dto.pid() + name);
    }
}
```

---

# 16. LsCommand.java

**Path**
`src/main/java/forgeframework/cli/command/LsCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.DirectoryEntryDto;
import forgeframework.filesystem.FileListDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 디렉터리 내용을 나열하는 명령어. 사용법: ls [path]
 *
 * <p>Kernel/FileSystemManager는 {@link FileListDto}라는 순수 데이터만 반환하고,
 * 표 형태로 꾸미는 건 이 클래스(Shell 계층)의 책임이다 — PsCommand/MeminfoCommand와
 * 동일한 원칙을 따른다.</p>
 */
public final class LsCommand implements Command {

    private final ShellContext context;

    public LsCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "ls";
    }

    @Override
    public String description() {
        return "디렉터리 내용을 나열합니다. (ls [path])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        String target = (args.length > 0) ? args[0] : ".";
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.LS, new String[]{context.getCwd(), target}
        ));
        if (!result.isSuccess()) {
            return result;
        }

        FileListDto dto = result.dataAs(FileListDto.class);
        StringBuilder sb = new StringBuilder();
        sb.append(dto.currentPath()).append('\n');
        sb.append(String.format("%-20s | %-10s | %s%n", "NAME", "TYPE", "SIZE"));
        for (DirectoryEntryDto entry : dto.entries()) {
            sb.append(String.format("%-20s | %-10s | %d%n", entry.name(), entry.type(), entry.size()));
        }
        return SystemCallResult.success(sb.toString().stripTrailing());
    }
}
```

---

# 17. MallocCommand.java

**Path**
`src/main/java/forgeframework/cli/command/MallocCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.MallocResultDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스의 힙에 메모리를 할당하는 명령어. 사용법: malloc &lt;PID&gt; &lt;size&gt;
 */
public final class MallocCommand implements Command {

    @Override
    public String name() {
        return "malloc";
    }

    @Override
    public String description() {
        return "힙에 메모리를 할당합니다. (malloc <PID> <size>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.MALLOC, args));
        if (!result.isSuccess()) {
            return result;
        }

        MallocResultDto dto = result.dataAs(MallocResultDto.class);
        return SystemCallResult.success(String.format(
                "PID %d: %d바이트 할당됨 (가상 주소: %d)", dto.pid(), dto.size(), dto.address()));
    }
}
```

---

# 18. MeminfoCommand.java

**Path**
`src/main/java/forgeframework/cli/command/MeminfoCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.HeapSnapshot;
import forgeframework.memory.MemorySnapshot;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 물리 메모리 / 프로세스별 힙 / TLB 사용 현황을 출력하는 명령어.
 *
 * <p>Kernel/MemoryManager는 {@link MemorySnapshot}이라는 순수 데이터만 반환하고,
 * 표 형태로 꾸미는 건 이 클래스(Shell 계층)의 책임이다 — PsCommand와 동일한
 * 원칙을 따른다.</p>
 */
public final class MeminfoCommand implements Command {

    @Override
    public String name() {
        return "meminfo";
    }

    @Override
    public String description() {
        return "물리 메모리/힙/TLB 사용 현황을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.MEMINFO));
        if (!result.isSuccess()) {
            return result;
        }

        MemorySnapshot snapshot = result.dataAs(MemorySnapshot.class);
        StringBuilder sb = new StringBuilder();

        sb.append("[Physical Memory]\n");
        sb.append(String.format(
                "Frame Size: %d, Total Frames: %d (Total: %d bytes)%n",
                snapshot.frameSize(), snapshot.totalFrames(),
                (long) snapshot.frameSize() * snapshot.totalFrames()
        ));
        sb.append(String.format(
                "Used Frames: %d, Free Frames: %d%n",
                snapshot.usedFrames(), snapshot.freeFrames()
        ));

        sb.append("\n[Process Heap]\n");
        if (snapshot.heapByPid().isEmpty()) {
            sb.append("등록된 프로세스가 없습니다.\n");
        } else {
            sb.append(String.format("%-5s | %-10s | %-10s | %s%n", "PID", "CAPACITY", "USED", "FREE"));
            for (HeapSnapshot heap : snapshot.heapByPid().values()) {
                sb.append(String.format(
                        "%-5d | %-10d | %-10d | %d%n",
                        heap.pid(), heap.capacity(), heap.used(), heap.free()
                ));
            }
        }

        sb.append("\n[TLB]\n");
        sb.append(String.format(
                "Hits: %d, Misses: %d, Hit Ratio: %.1f%%",
                snapshot.tlbHits(), snapshot.tlbMisses(), snapshot.tlbHitRatio() * 100
        ));

        return SystemCallResult.success(sb.toString());
    }
}
```

---

# 19. MkdirCommand.java

**Path**
`src/main/java/forgeframework/cli/command/MkdirCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.DirectoryEntryDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 새 디렉터리를 생성하는 명령어. 사용법: mkdir &lt;name&gt;
 */
public final class MkdirCommand implements Command {

    private final ShellContext context;

    public MkdirCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "mkdir";
    }

    @Override
    public String description() {
        return "새 디렉터리를 생성합니다. (mkdir <name>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
//        아래 조건식을 사용하게 된다면 인자를 하나씩 떨어뜨려서 명령어를 전달 시
//        ex) mkdir d 9
//        첫번째 인자인 "d"만 전달되고 나머지 9는 아무런 경고없이 무시되는 문제가 있음
//        TouchCommand.java, RMCommand.java도 동일한 문제를 가지고 있어 모두 수정한다.
//        if (args.length < 1) {
//            return SystemCallResult.failure("사용법: mkdir <name>");
//        }

        // 해결 버전
        if ( args.length != 1 ) {
            return SystemCallResult.failure("사용법: mkdir <name> (공백 없는 이름 하나만 입력)");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.MKDIR, new String[]{context.getCwd(), args[0]}
        ));
        if (!result.isSuccess()) {
            return result;
        }
        DirectoryEntryDto dto = result.dataAs(DirectoryEntryDto.class);
        return SystemCallResult.success("디렉터리가 생성되었습니다: " + dto.name());
    }
}
```

---

# 20. PsCommand.java

**Path**
`src/main/java/forgeframework/cli/command/PsCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.process.ProcessDto;
import forgeframework.process.ProcessState;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 프로세스 목록과 상태를 표로 출력하는 명령어.
 *
 * <p><b>[Phase 6 리팩토링]</b> Phase 2 시절 이 명령어는 커널이 통째로 만들어준
 * 문자열을 그대로 출력하기만 했다. 이제 커널은 {@link ProcessDto} 목록만
 * 반환하고, 표의 컬럼 폭과 RUNNING 표시(*)를 정하는 일은 표현 계층인 이
 * 클래스의 책임이다 — MeminfoCommand/FrameTableCommand가 이미 따르고 있던
 * 원칙을 {@code ps}에도 뒤늦게 적용한 것이다.</p>
 */
public final class PsCommand implements Command {

    @Override
    public String name() {
        return "ps";
    }

    @Override
    public String description() {
        return "프로세스 상태 목록을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.PS));
        if (!result.isSuccess()) {
            return result;
        }

        List<ProcessDto> processes = result.dataAsList(ProcessDto.class);
        if (processes.isEmpty()) {
            return SystemCallResult.success("실행 중인 프로세스가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-10s | %-8s | %-10s | %s%n",
                "PID", "STATE", "CPU_TIME", "BURST", "NAME"));
        sb.append("-".repeat(55));

        for (ProcessDto process : processes) {
            String indicator = (process.state() == ProcessState.RUNNING || process.running()) ? "*" : "";
            sb.append(String.format("%n%-5d | %-10s | %-8d | %-10d | %s%s",
                    process.pid(), process.state(), process.cpuTimeUsed(),
                    process.burstTime(), process.name(), indicator));
        }
        return SystemCallResult.success(sb.toString());
    }
}
```

---

# 21. PwdCommand.java

**Path**
`src/main/java/forgeframework/cli/command/PwdCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallResult;

/**
 * 현재 작업 디렉터리를 출력하는 명령어.
 *
 * <p>CWD는 Kernel이 아니라 Shell({@link ShellContext})이 들고 있는 상태이므로,
 * 이 명령어는 시스템 콜을 전혀 보내지 않는다 (Kernel의 무상태성을 지키기 위한
 * 의도적인 예외).</p>
 */
public final class PwdCommand implements Command {

    private final ShellContext context;

    public PwdCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "pwd";
    }

    @Override
    public String description() {
        return "현재 작업 디렉터리를 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        return SystemCallResult.success(context.getCwd());
    }
}
```

---

# 22. RecoverCommand.java

**Path**
`src/main/java/forgeframework/cli/command/RecoverCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.DeadlockRecoverDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 교착 상태를 복구하는 명령어. 사용법: {@code recover}
 *
 * <p>희생자(Victim) 프로세스를 정책에 따라 선정해 강제 종료시키고, 그가 붙잡고
 * 있던 자원을 회수해 교착 상태를 해소한다. 회수된 자원으로 대기가 풀린
 * 프로세스는 자동으로 READY 상태가 된다.</p>
 */
public final class RecoverCommand implements Command {

    @Override
    public String name() {
        return "recover";
    }

    @Override
    public String description() {
        return "희생자를 선정해 강제 종료하고 교착 상태를 해소합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RECOVER));
        if (!result.isSuccess()) {
            return result;
        }

        DeadlockRecoverDto dto = result.dataAs(DeadlockRecoverDto.class);
        if (dto.victims().isEmpty()) {
            return SystemCallResult.success("복구할 교착 상태가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("교착 상태 복구 (정책: %s)%n", dto.policy()));
        sb.append(String.format("  강제 종료된 희생자: %s%n", dto.victims()));
        sb.append(String.format("  회수한 자원: %s%n", ResourceFormat.vector(dto.reclaimed())));

        if (!dto.wokenPids().isEmpty()) {
            sb.append(String.format("  대기가 풀려 깨어난 프로세스: %s%n", dto.wokenPids()));
        }

        if (dto.recovered()) {
            sb.append("  ✔ 교착 상태가 완전히 해소되었습니다.");
        } else {
            sb.append(String.format("  ✖ 복구 후에도 교착 상태가 남아 있습니다: %s", dto.stillDeadlocked()));
        }
        return SystemCallResult.success(sb.toString());
    }
}
```

---

# 23. ResFreeCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ResFreeCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.ResourceResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스가 점유 중인 자원을 반납하는 명령어. 사용법: {@code res_free <pid> <R1> [R2] [R3]}
 *
 * <p>반납 즉시 그 자원을 기다리던 프로세스의 요청이 재평가되며, 승인 가능해진
 * 프로세스는 자동으로 READY 상태로 깨어난다.</p>
 */
public final class ResFreeCommand implements Command {

    @Override
    public String name() {
        return "res_free";
    }

    @Override
    public String description() {
        return "프로세스가 점유한 자원을 반납합니다. (res_free <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_free <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_FREE, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceResultDto dto = result.dataAs(ResourceResultDto.class);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("자원 반납: PID %d %s%n", dto.pid(), ResourceFormat.vector(dto.vector())));
        sb.append(String.format("  잔여 보유량: %s / 가용량: %s",
                ResourceFormat.vector(dto.allocation()),
                ResourceFormat.vector(dto.available())));

        if (!dto.wokenPids().isEmpty()) {
            sb.append(String.format("%n  대기가 풀려 깨어난 프로세스: %s", dto.wokenPids()));
        }
        return SystemCallResult.success(sb.toString());
    }
}
```

---

# 24. ResInfoCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ResInfoCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.ResourceRowDto;
import forgeframework.deadlock.ResourceSnapshotDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 시스템 자원 현황과 프로세스별 Allocation / Max / Need 행렬을 출력하는 명령어.
 *
 * <p>Kernel/DeadlockManager는 {@link ResourceSnapshotDto}라는 순수 데이터만
 * 반환하고, 은행원 알고리즘 교재에 나오는 형태의 표로 꾸미는 건 이 클래스의
 * 책임이다 — MeminfoCommand/FrameTableCommand와 동일한 원칙이다.</p>
 */
public final class ResInfoCommand implements Command {

    @Override
    public String name() {
        return "res_info";
    }

    @Override
    public String description() {
        return "자원 총량/가용량과 프로세스별 Allocation·Max·Need 행렬을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_INFO));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceSnapshotDto snapshot = result.dataAs(ResourceSnapshotDto.class);
        String header = ResourceFormat.header(snapshot.labels());

        StringBuilder sb = new StringBuilder();
        sb.append("[System Resources]\n");
        sb.append(String.format("%-12s %s%n", "", header));
        sb.append(String.format("%-12s %s%n", "Total", ResourceFormat.vector(snapshot.total())));
        sb.append(String.format("%-12s %s%n", "Allocated", ResourceFormat.vector(snapshot.allocated())));
        sb.append(String.format("%-12s %s%n", "Available", ResourceFormat.vector(snapshot.available())));
        sb.append(String.format("%-12s %s / 희생자 정책: %s%n",
                "Banker's", snapshot.bankerEnabled() ? "ON (교착 회피)" : "OFF (교착 유발 가능)",
                snapshot.victimPolicy()));

        sb.append("\n[Process Matrix]\n");
        if (snapshot.rows().isEmpty()) {
            sb.append("자원 장부에 등록된 프로세스가 없습니다.");
            return SystemCallResult.success(sb.toString());
        }

        sb.append(String.format("%-5s | %-10s | %-12s | %-12s | %-12s | %s%n",
                "PID", "NAME", "ALLOCATION", "MAX", "NEED", "REQUEST"));
        sb.append("-".repeat(80)).append('\n');

        for (ResourceRowDto row : snapshot.rows()) {
            sb.append(String.format("%-5d | %-10s | %-12s | %-12s | %-12s | %s%s%n",
                    row.pid(),
                    truncate(row.name()),
                    ResourceFormat.vector(row.allocation()),
                    ResourceFormat.vector(row.max()),
                    ResourceFormat.vector(row.need()),
                    ResourceFormat.vector(row.pendingRequest()),
                    row.blocked() ? "  <-- WAITING" : ""));
        }

        return SystemCallResult.success(sb.toString().stripTrailing());
    }

    private String truncate(String name) {
        if (name == null) {
            return "-";
        }
        return (name.length() <= 10) ? name : name.substring(0, 9) + "…";
    }
}
```

---

# 25. ResMaxCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ResMaxCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.ResourceRowDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스의 최대 자원 요구량(Max)을 선언하는 명령어.
 * 사용법: {@code res_max <pid> <R1> [R2] [R3]}
 *
 * <p>Banker's Algorithm은 정의상 "각 프로세스가 최대 얼마까지 요구할 수 있는가"를
 * 미리 알아야 안전 상태를 계산할 수 있다. 이 선언이 없으면 모든 프로세스의 Max가
 * 시스템 총량으로 잡혀(가장 보수적인 기본값) 회피 알고리즘이 사실상 무력해지므로,
 * 교재 예제를 재현하려면 이 명령으로 Max를 먼저 설정해야 한다.</p>
 */
public final class ResMaxCommand implements Command {

    @Override
    public String name() {
        return "res_max";
    }

    @Override
    public String description() {
        return "프로세스의 최대 자원 요구량(Max)을 선언합니다. (res_max <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_max <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_MAX, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceRowDto row = result.dataAs(ResourceRowDto.class);
        return SystemCallResult.success(String.format(
                "PID %d의 최대 요구량이 선언되었습니다. Max: %s / Allocation: %s / Need: %s",
                row.pid(),
                ResourceFormat.vector(row.max()),
                ResourceFormat.vector(row.allocation()),
                ResourceFormat.vector(row.need())));
    }
}
```

---

# 26. ResReqCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ResReqCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.deadlock.ResourceResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스가 자원을 요청하는 명령어. 사용법: {@code res_req <pid> <R1> [R2] [R3]}
 *
 * <p>Banker's Algorithm이 켜져 있으면 요청 승인 시 시스템이 안전 상태를
 * 유지하는지 검사한다. 승인되지 못한 요청은 대기열에 등록되고 프로세스는
 * WAITING으로 전이된다 — 나중에 다른 프로세스가 자원을 반납하거나 종료되면
 * 자동으로 승인되어 깨어난다.</p>
 */
public final class ResReqCommand implements Command {

    @Override
    public String name() {
        return "res_req";
    }

    @Override
    public String description() {
        return "프로세스가 자원을 요청합니다. (res_req <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_req <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_REQ, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceResultDto dto = result.dataAs(ResourceResultDto.class);
        StringBuilder sb = new StringBuilder();

        switch (dto.outcome()) {
            case GRANTED -> {
                sb.append(String.format("자원 요청 승인: PID %d %s%n", dto.pid(),
                        ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  보유량: %s / 가용량: %s",
                        ResourceFormat.vector(dto.allocation()),
                        ResourceFormat.vector(dto.available())));
                if (!dto.safeSequence().isEmpty()) {
                    sb.append(String.format("%n  안전 순서(Safe Sequence): %s", formatSequence(dto.safeSequence())));
                }
            }
            case BLOCKED_INSUFFICIENT -> {
                sb.append(String.format("자원 부족으로 대기: PID %d %s → WAITING%n", dto.pid(),
                        ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  현재 가용량: %s", ResourceFormat.vector(dto.available())));
            }
            case BLOCKED_UNSAFE -> {
                sb.append(String.format(
                        "Banker's Algorithm이 요청을 보류했습니다 (불안전 상태): PID %d %s → WAITING%n",
                        dto.pid(), ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  가용량은 충분하지만(%s) 승인 시 안전 순서가 사라집니다.",
                        ResourceFormat.vector(dto.available())));
            }
        }

        return SystemCallResult.success(sb.toString());
    }

    private String formatSequence(java.util.List<Integer> sequence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.size(); i++) {
            if (i > 0) {
                sb.append(" → ");
            }
            sb.append('P').append(sequence.get(i));
        }
        return sb.toString();
    }
}
```

---

# 27. ResourceFormat.java

**Path**
`src/main/java/forgeframework/cli/command/ResourceFormat.java`

```java
package forgeframework.cli.command;

import java.util.List;

/**
 * 자원 벡터를 콘솔 표에 맞춰 문자열로 렌더링하는 표현 계층 전용 유틸리티.
 *
 * <p>{@code res_info} / {@code res_req} / {@code detect} / {@code recover}가
 * 모두 같은 폭의 벡터를 찍어야 표가 세로로 정렬되므로, 포맷 규칙을 한 곳에
 * 모아 중복을 없앴다. 커널이 아니라 {@code command} 패키지에 두는 이유는
 * 명확하다 — 이건 전적으로 CLI의 표현 문제이며, GUI 클라이언트는 같은 DTO로
 * 전혀 다른 그림을 그릴 것이기 때문이다.</p>
 */
final class ResourceFormat {

    /** 벡터 원소 하나가 차지하는 칸 너비. */
    private static final int CELL_WIDTH = 3;

    private ResourceFormat() {
        // 인스턴스화 방지
    }

    /**
     * 정수 벡터를 {@code "[ 10   5   7]"} 형태로 렌더링한다.
     *
     * @param vector 렌더링할 벡터
     * @return 고정 폭으로 정렬된 문자열
     */
    static String vector(List<Integer> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int value : vector) {
            sb.append(String.format("%" + CELL_WIDTH + "d", value));
        }
        return sb.append(']').toString();
    }

    /**
     * 자원 이름 목록을 벡터와 같은 폭의 헤더로 렌더링한다.
     *
     * @param labels 자원 이름 목록
     * @return {@code "[ R1 R2 R3]"} 형태의 헤더
     */
    static String header(List<String> labels) {
        StringBuilder sb = new StringBuilder("[");
        for (String label : labels) {
            sb.append(String.format("%" + CELL_WIDTH + "s", label));
        }
        return sb.append(']').toString();
    }
}
```

---

# 28. RmCommand.java

**Path**
`src/main/java/forgeframework/cli/command/RmCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.RemoveResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 파일 또는 비어있는 디렉터리를 삭제하는 명령어. 사용법: rm &lt;name&gt;
 */
public final class RmCommand implements Command {

    private final ShellContext context;

    public RmCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "rm";
    }

    @Override
    public String description() {
        return "파일 또는 빈 디렉터리를 삭제합니다. (rm <name>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        // MkdirCommand.java 33 ~ 36 line 참고
        /* if (args.length < 1) {
            return SystemCallResult.failure("사용법: rm <name>");
        } */

        if (args.length != 1) {
            return SystemCallResult.failure("사용법: rm <name> (공백 없는 이름 하나만 입력)");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.RM, new String[]{context.getCwd(), args[0]}
        ));
        if (!result.isSuccess()) {
            return result;
        }

        RemoveResultDto dto = result.dataAs(RemoveResultDto.class);
        return SystemCallResult.success("삭제되었습니다: " + dto.name());
    }
}
```

---

# 29. SchedulerCommand.java

**Path**
`src/main/java/forgeframework/cli/command/SchedulerCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.process.SchedulerDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 현재 스케줄러를 조회하거나 런타임에 교체하는 명령어.
 * 사용법: scheduler [fcfs|rr]
 */
public final class SchedulerCommand implements Command {

    @Override
    public String name() {
        return "scheduler";
    }

    @Override
    public String description() {
        return "스케줄러를 조회하거나 변경합니다. (scheduler [fcfs|rr])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.SCHEDULER, args));
        if (!result.isSuccess()) {
            return result;
        }

        SchedulerDto dto = result.dataAs(SchedulerDto.class);
        String detail = dto.preemptive()
                ? "선점형, time quantum " + dto.timeQuantum() + " tick"
                : "비선점형";
        String prefix = dto.changed() ? "스케줄러가 변경되었습니다: " : "현재 스케줄러: ";
        return SystemCallResult.success(prefix + dto.name() + " (" + detail + ")");
    }
}
```

---

# 30. ShutdownCommand.java

**Path**
`src/main/java/forgeframework/cli/command/ShutdownCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.kernel.ShutdownDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 시스템을 종료하는 명령어.
 */
public final class ShutdownCommand implements Command {

    @Override
    public String name() {
        return "shutdown";
    }

    @Override
    public String description() {
        return "ForgeOS를 종료합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.SHUTDOWN));
        if (!result.isSuccess()) {
            return result;
        }

        ShutdownDto dto = result.dataAs(ShutdownDto.class);
        if (dto.alreadyDown()) {
            return SystemCallResult.success("이미 종료 절차가 진행 중입니다.");
        }
        return SystemCallResult.success(String.format(
                "%s v%s를 종료합니다. (총 가동 시간: %d초)",
                dto.osName(), dto.version(), dto.uptimeSeconds()));
    }
}
```

---

# 31. SoftIntCommand.java

**Path**
`src/main/java/forgeframework/cli/command/SoftIntCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.interrupt.SoftwareInterruptDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 사용자가 강제로 Software Interrupt를 발생시키는 명령어. 사용법: {@code soft_int [payload...]}
 */
public final class SoftIntCommand implements Command {

    @Override
    public String name() {
        return "soft_int";
    }

    @Override
    public String description() {
        return "Software Interrupt를 강제로 발생시킵니다. 사용법: soft_int [payload]";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.SW_INT, args));
        if (!result.isSuccess()) {
            return result;
        }

        SoftwareInterruptDto dto = result.dataAs(SoftwareInterruptDto.class);
        return SystemCallResult.success(String.format(
                "Software Interrupt가 발생했습니다. (payload: %s, 핸들러 %d개 호출)",
                dto.payload(), dto.handlerCount()));
    }
}
```

---

# 32. TouchCommand.java

**Path**
`src/main/java/forgeframework/cli/command/TouchCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.DirectoryEntryDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 빈 파일을 생성하는 명령어. 사용법: touch &lt;name&gt;
 */
public final class TouchCommand implements Command {

    private final ShellContext context;

    public TouchCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "touch";
    }

    @Override
    public String description() {
        return "빈 파일을 생성합니다. (touch <name>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        // MkdirCommand.java 33 ~ 36 line 참고
        /* if (args.length < 1) {
            return SystemCallResult.failure("사용법: touch <name>");
        } */

        if ( args.length != 1 ) {
            return SystemCallResult.failure("사용법: touch <name> (공백 없는 이름 하나만 입력)");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.TOUCH, new String[]{context.getCwd(), args[0]}
        ));
        if (!result.isSuccess()) {
            return result;
        }
        DirectoryEntryDto dto = result.dataAs(DirectoryEntryDto.class);
        return SystemCallResult.success("파일이 생성되었습니다: " + dto.name());
    }
}
```

---

# 33. TranslateCommand.java

**Path**
`src/main/java/forgeframework/cli/command/TranslateCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.TranslationResult;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 가상 주소를 물리 주소로 변환해 보여주는 명령어 (Paging + TLB 동작 확인용).
 * 사용법: translate &lt;PID&gt; &lt;virtualAddress&gt;
 */
public final class TranslateCommand implements Command {

    @Override
    public String name() {
        return "translate";
    }

    @Override
    public String description() {
        return "가상 주소를 물리 주소로 변환합니다. (translate <PID> <vaddr>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.TRANSLATE, args));
        if (!result.isSuccess()) {
            return result;
        }

        TranslationResult dto = result.dataAs(TranslationResult.class);
        return SystemCallResult.success(String.format(
                "가상주소 %d -> 물리주소 %d (페이지 #%d -> 프레임 #%d, TLB %s)",
                dto.virtualAddress(), dto.physicalAddress(),
                dto.pageNumber(), dto.frameNumber(),
                dto.tlbHit() ? "HIT" : "MISS"));
    }
}
```

---

# 34. TreeCommand.java

**Path**
`src/main/java/forgeframework/cli/command/TreeCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.TreeNodeDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 디렉터리 구조를 트리 형태로 출력하는 명령어. 사용법: tree [path]
 *
 * <p>Kernel/FileSystemManager는 재귀적 DTO({@link TreeNodeDto})만 반환하고,
 * ├──/└── 같은 ASCII 트리 렌더링은 전적으로 이 클래스의 책임이다.</p>
 */
public final class TreeCommand implements Command {

    private final ShellContext context;

    public TreeCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "tree";
    }

    @Override
    public String description() {
        return "디렉터리 구조를 트리 형태로 출력합니다. (tree [path])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        String target = (args.length > 0) ? args[0] : ".";
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.TREE, new String[]{context.getCwd(), target}
        ));
        if (!result.isSuccess()) {
            return result;
        }

        TreeNodeDto root = result.dataAs(TreeNodeDto.class);
        StringBuilder sb = new StringBuilder();
        render(sb, root, "", true, true);
        return SystemCallResult.success(sb.toString().stripTrailing());
    }

    private void render(StringBuilder sb, TreeNodeDto node, String prefix, boolean isLast, boolean isRoot) {
        String suffix = (node.isDirectory() && !node.name().endsWith("/")) ? "/" : "";
        if (isRoot) {
            sb.append(node.name()).append(suffix).append('\n');
        } else {
            sb.append(prefix).append(isLast ? "└── " : "├── ").append(node.name()).append(suffix).append('\n');
        }

        String childPrefix = isRoot ? "" : prefix + (isLast ? "    " : "│   ");
        List<TreeNodeDto> children = node.children();
        for (int i = 0; i < children.size(); i++) {
            render(sb, children.get(i), childPrefix, i == children.size() - 1, false);
        }
    }
}
```

---

# 35. TypeCommand.java

**Path**
`src/main/java/forgeframework/cli/command/TypeCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.device.HardwareEventDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * (가상 하드웨어 조작) 키보드에 텍스트를 입력하는 명령어. 사용법: {@code type <text...>}
 *
 * <p>write 명령처럼 공백이 섞인 여러 단어를 하나의 문자열로 합쳐서 Kernel에 전달한다.</p>
 */
public final class TypeCommand implements Command {

    @Override
    public String name() {
        return "type";
    }

    @Override
    public String description() {
        return "(가상 하드웨어) 키보드에 텍스트를 입력합니다. 사용법: type <text>";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 1) {
            return SystemCallResult.failure("사용법: type <text>");
        }
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.HW_INPUT, args));
        if (!result.isSuccess()) {
            return result;
        }

        HardwareEventDto dto = result.dataAs(HardwareEventDto.class);
        return SystemCallResult.success("키보드 입력 처리됨: \"" + dto.payload() + "\"");
    }
}
```

---

# 36. UnknownCommand.java

**Path**
`src/main/java/forgeframework/cli/command/UnknownCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

/**
 * 등록되지 않은 명령어가 입력되었을 때 반환되는 Null Object.
 *
 * <p>{@link CommandRegistry#resolve(String)}에서 null 대신 이 객체를 반환함으로써
 * 호출부의 null 체크 분기를 제거한다.</p>
 */
public final class UnknownCommand implements Command {

    private final String inputName;

    public UnknownCommand(String inputName) {
        this.inputName = inputName;
    }

    @Override
    public String name() {
        return inputName;
    }

    @Override
    public String description() {
        return "알 수 없는 명령어";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        return SystemCallResult.failure(
                "'" + inputName + "': 알 수 없는 명령어입니다. 'help'를 입력해 사용 가능한 명령어를 확인하세요."
        );
    }
}
```

---

# 37. UptimeCommand.java

**Path**
`src/main/java/forgeframework/cli/command/UptimeCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.kernel.UptimeDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 커널의 가동 시간을 조회하는 명령어.
 *
 * <p><b>[Phase 6 리팩토링]</b> {@code HH:mm:ss} 포맷팅은 CLI의 표현 방식일 뿐
 * 커널의 관심사가 아니므로, 커널에 있던 {@code formatDuration()}을 통째로
 * 이 클래스로 옮겼다.</p>
 */
public final class UptimeCommand implements Command {

    @Override
    public String name() {
        return "uptime";
    }

    @Override
    public String description() {
        return "커널 가동 시간을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.UPTIME));
        if (!result.isSuccess()) {
            return result;
        }

        UptimeDto uptime = result.dataAs(UptimeDto.class);
        return SystemCallResult.success("가동 시간: " + format(uptime.uptimeSeconds()));
    }

    private String format(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
```

---

# 38. WriteCommand.java

**Path**
`src/main/java/forgeframework/cli/command/WriteCommand.java`

```java
package forgeframework.cli.command;

import forgeframework.filesystem.WriteResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.cli.shell.ShellContext;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.Arrays;

/**
 * 파일 내용을 덮어쓰는 명령어. 사용법: write &lt;name&gt; &lt;text...&gt;
 *
 * <p>ForgeShell이 입력을 공백 기준으로 토큰화하기 때문에, text에 공백이
 * 여러 단어로 들어오면 args[1] 이후를 전부 공백으로 다시 합쳐서 원래
 * 문자열을 복원한다.</p>
 */
public final class WriteCommand implements Command {

    private final ShellContext context;

    public WriteCommand(ShellContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return "write";
    }

    @Override
    public String description() {
        return "파일 내용을 덮어씁니다. (write <name> <text>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: write <name> <text>");
        }
        String content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(
                SystemCallType.WRITE, new String[]{context.getCwd(), args[0], content}
        ));
        if (!result.isSuccess()) {
            return result;
        }

        WriteResultDto dto = result.dataAs(WriteResultDto.class);
        return SystemCallResult.success(dto.bytesWritten() + "바이트 기록됨: " + dto.name());
    }
}
```

---

# 39. ForgeShell.java

**Path**
`src/main/java/forgeframework/cli/shell/ForgeShell.java`

```java
package forgeframework.cli.shell;

import forgeframework.cli.command.BankerCommand;
import forgeframework.cli.command.CatCommand;
import forgeframework.cli.command.CdCommand;
import forgeframework.cli.command.Command;
import forgeframework.cli.command.CommandRegistry;
import forgeframework.cli.command.DetectCommand;
import forgeframework.cli.command.DevinfoCommand;
import forgeframework.cli.command.DiskFinishCommand;
import forgeframework.cli.command.ExecCommand;
import forgeframework.cli.command.FrameTableCommand;
import forgeframework.cli.command.FreeCommand;
import forgeframework.cli.command.HelpCommand;
import forgeframework.cli.command.IoRequestCommand;
import forgeframework.cli.command.KillCommand;
import forgeframework.cli.command.LsCommand;
import forgeframework.cli.command.MallocCommand;
import forgeframework.cli.command.MeminfoCommand;
import forgeframework.cli.command.MkdirCommand;
import forgeframework.cli.command.PsCommand;
import forgeframework.cli.command.PwdCommand;
import forgeframework.cli.command.RecoverCommand;
import forgeframework.cli.command.ResFreeCommand;
import forgeframework.cli.command.ResInfoCommand;
import forgeframework.cli.command.ResMaxCommand;
import forgeframework.cli.command.ResReqCommand;
import forgeframework.cli.command.RmCommand;
import forgeframework.cli.command.SchedulerCommand;
import forgeframework.cli.command.ShutdownCommand;
import forgeframework.cli.command.SoftIntCommand;
import forgeframework.cli.command.TouchCommand;
import forgeframework.cli.command.TranslateCommand;
import forgeframework.cli.command.TreeCommand;
import forgeframework.cli.command.TypeCommand;
import forgeframework.cli.command.UptimeCommand;
import forgeframework.cli.command.WriteCommand;
import forgeframework.common.ForgeOSConstants;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

import java.util.Scanner;

public class ForgeShell {

    private final Kernel kernel;
    private final CommandRegistry registry;
    private final ShellContext context;
    private final ShellPrompt prompt;
    private final Scanner input;

    public ForgeShell(Kernel kernel) {
        this.kernel = kernel;
        this.registry = new CommandRegistry();
        this.context = new ShellContext();
        this.prompt = new ShellPrompt(context);
        this.input = new Scanner(System.in);
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registry.register(new HelpCommand(registry));
        registry.register(new ShutdownCommand());
        registry.register(new UptimeCommand());

        registry.register(new PsCommand());
        registry.register(new ExecCommand());
        registry.register(new KillCommand());
        registry.register(new SchedulerCommand());
        registry.register(new MallocCommand());
        registry.register(new FreeCommand());
        registry.register(new MeminfoCommand());
        registry.register(new TranslateCommand());
        registry.register(new FrameTableCommand());

        // Phase 4 — File System
        registry.register(new PwdCommand(context));
        registry.register(new CdCommand(context));
        registry.register(new LsCommand(context));
        registry.register(new MkdirCommand(context));
        registry.register(new TouchCommand(context));
        registry.register(new RmCommand(context));
        registry.register(new WriteCommand(context));
        registry.register(new CatCommand(context));
        registry.register(new TreeCommand(context));

        // Phase 5 — Device & Interrupt
        registry.register(new DevinfoCommand());
        registry.register(new IoRequestCommand());
        registry.register(new TypeCommand());
        registry.register(new DiskFinishCommand());
        registry.register(new SoftIntCommand());

        // Phase 6 — Deadlock (자원 할당 및 교착 상태)
        registry.register(new ResInfoCommand());
        registry.register(new ResMaxCommand());
        registry.register(new ResReqCommand());
        registry.register(new ResFreeCommand());
        registry.register(new BankerCommand());
        registry.register(new DetectCommand());
        registry.register(new RecoverCommand());
    }

    public void run() {
        System.out.println(ForgeOSConstants.OS_NAME + " Shell에 오신 것을 환영합니다. 'help'를 입력해보세요.");

        while (kernel.isRunning()) {
            System.out.print(prompt.render());

            if (!input.hasNextLine()) {
                break;
            }

            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            handleLine(line);
        }

        input.close();
    }

    private void handleLine(String line) {
        String[] tokens = line.split(ForgeOSConstants.COMMAND_DELIMITER);
        String commandName = tokens[0];
        String[] args = (tokens.length > 1)
                ? java.util.Arrays.copyOfRange(tokens, 1, tokens.length)
                : new String[0];

        Command command = registry.resolve(commandName);
        SystemCallResult result = command.execute(kernel, args);

        System.out.println(result.getMessage());
    }
}
```

---

# 40. ShellContext.java

**Path**
`src/main/java/forgeframework/cli/shell/ShellContext.java`

```java
package forgeframework.cli.shell;

/**
 * Shell이 소유하는 상태를 담는 컨테이너.
 *
 * <p>Kernel은 무상태(stateless)를 유지해야 API 서버(Web/GUI)로 재사용하기
 * 쉬워진다. 그래서 현재 작업 디렉터리(CWD) 같은 "세션 상태"는 Kernel이나
 * FileSystemManager가 아니라 여기, Shell 계층에 둔다. CD/PWD/파일시스템
 * 명령어들은 생성자로 이 객체를 주입받아 CWD를 읽고(쓰기는 CdCommand만) 사용한다
 * — {@link forgeframework.cli.command.HelpCommand}가
 * {@link forgeframework.cli.command.CommandRegistry}를 주입받는 것과 동일한 패턴이다.</p>
 *
 * <p><b>[Phase 6]</b> 기존에는 두 링크가 단순 클래스명이라 {@code shell} 패키지에서
 * 해석되지 않아 Javadoc 생성이 에러로 실패했다. 1.0 산출물에 API 문서가 포함되므로
 * 완전 수식명으로 고쳤다.</p>
 */
public final class ShellContext {

    private String currentWorkingDirectory = "/";

    public String getCwd() {
        return currentWorkingDirectory;
    }

    public void setCwd(String cwd) {
        this.currentWorkingDirectory = cwd;
    }
}
```

---

# 41. ShellPrompt.java

**Path**
`src/main/java/forgeframework/cli/shell/ShellPrompt.java`

```java
package forgeframework.cli.shell;

import forgeframework.common.ForgeOSConstants;

/**
 * ForgeShell의 프롬프트 문자열을 관리하는 클래스.
 *
 * <p>Phase 4부터는 현재 작업 디렉터리(CWD)를 반영해
 * {@code forgeframework:/usr/local> } 형태로 동적으로 렌더링한다.
 * CWD 상태 자체는 {@link ShellContext}가 들고 있으므로, 이 클래스는
 * 매 호출마다 그 값을 읽어 포맷팅만 담당한다.</p>
 */
public class ShellPrompt {

    private final ShellContext context;

    public ShellPrompt(ShellContext context) {
        this.context = context;
    }

    /**
     * 현재 프롬프트 문자열을 반환한다. (예: {@code forgeframework:/usr/local> })
     *
     * @return 프롬프트 문자열
     */
    public String render() {
        return ForgeOSConstants.SHELL_PROMPT_PREFIX + ":" + context.getCwd() + ForgeOSConstants.SHELL_PROMPT_SUFFIX;
    }
}
```

---

# 42. module-info.java

**Path**
`src/main/java/module-info.java`

```java
/**
 * ForgeCLI — ForgeFramework 커널의 명령줄 클라이언트.
 *
 * <p>커널 모듈에 <b>단방향으로만</b> 의존한다. 이 모듈이 사라져도 커널은 아무
 * 영향을 받지 않으며, 실제로 릴리즈되는 {@code forgeframework} jar에는
 * 셸도 명령어도 포함되지 않는다.</p>
 *
 * <p>이 모듈은 라이브러리가 아니라 실행 가능한 데모 애플리케이션이므로
 * 아무 패키지도 export하지 않는다.</p>
 */
module forgeframework.cli {
    requires forgeframework;
}
```

---

