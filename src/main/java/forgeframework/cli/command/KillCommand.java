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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public KillCommand() {
    }
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
