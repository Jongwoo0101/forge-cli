package forgeframework.cli.command;

import forgeframework.deadlock.ResourceResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스가 점유 중인 자원을 반납하는 명령어. 사용법: {@code res_free <pid> <R1> [R2] [R3]}
 *
 * <p>반납 즉시 그 자원을 기다리던 프로세스의 요청이 재평가되며, 승인 가능해진
 * 프로세스는 자동으로 READY 상태로 깨어난다.</p>
 */
public final class ResFreeCommand implements Command {

    @Override
    public String name() {
        return "res_free";
    }

    @Override
    public String description() {
        return "프로세스가 점유한 자원을 반납합니다. (res_free <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_free <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_FREE, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceResultDto dto = result.dataAs(ResourceResultDto.class);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("자원 반납: PID %d %s%n", dto.pid(), ResourceFormat.vector(dto.vector())));
        sb.append(String.format("  잔여 보유량: %s / 가용량: %s",
                ResourceFormat.vector(dto.allocation()),
                ResourceFormat.vector(dto.available())));

        if (!dto.wokenPids().isEmpty()) {
            sb.append(String.format("%n  대기가 풀려 깨어난 프로세스: %s", dto.wokenPids()));
        }
        return SystemCallResult.success(sb.toString());
    }
}
