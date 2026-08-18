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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public IoRequestCommand() {
    }
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
