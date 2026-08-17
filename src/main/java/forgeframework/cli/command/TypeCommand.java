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
