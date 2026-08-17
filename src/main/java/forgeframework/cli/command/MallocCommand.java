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
