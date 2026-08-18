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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public FreeCommand() {
    }
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
