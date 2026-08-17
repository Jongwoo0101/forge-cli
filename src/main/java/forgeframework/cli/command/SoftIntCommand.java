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
