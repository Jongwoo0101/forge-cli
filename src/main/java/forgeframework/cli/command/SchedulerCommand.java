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


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public SchedulerCommand() {
    }
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
