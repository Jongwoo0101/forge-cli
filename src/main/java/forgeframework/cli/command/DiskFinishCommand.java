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
