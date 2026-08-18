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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public ExecCommand() {
    }
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
