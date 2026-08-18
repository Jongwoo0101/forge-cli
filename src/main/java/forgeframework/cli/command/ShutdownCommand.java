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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public ShutdownCommand() {
    }
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
