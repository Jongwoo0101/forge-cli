package forgeframework.cli.command;

import forgeframework.deadlock.ResourceResultDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스가 자원을 요청하는 명령어. 사용법: {@code res_req <pid> <R1> [R2] [R3]}
 *
 * <p>Banker's Algorithm이 켜져 있으면 요청 승인 시 시스템이 안전 상태를
 * 유지하는지 검사한다. 승인되지 못한 요청은 대기열에 등록되고 프로세스는
 * WAITING으로 전이된다 — 나중에 다른 프로세스가 자원을 반납하거나 종료되면
 * 자동으로 승인되어 깨어난다.</p>
 */
public final class ResReqCommand implements Command {

    @Override
    public String name() {
        return "res_req";
    }

    @Override
    public String description() {
        return "프로세스가 자원을 요청합니다. (res_req <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_req <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_REQ, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceResultDto dto = result.dataAs(ResourceResultDto.class);
        StringBuilder sb = new StringBuilder();

        switch (dto.outcome()) {
            case GRANTED -> {
                sb.append(String.format("자원 요청 승인: PID %d %s%n", dto.pid(),
                        ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  보유량: %s / 가용량: %s",
                        ResourceFormat.vector(dto.allocation()),
                        ResourceFormat.vector(dto.available())));
                if (!dto.safeSequence().isEmpty()) {
                    sb.append(String.format("%n  안전 순서(Safe Sequence): %s", formatSequence(dto.safeSequence())));
                }
            }
            case BLOCKED_INSUFFICIENT -> {
                sb.append(String.format("자원 부족으로 대기: PID %d %s → WAITING%n", dto.pid(),
                        ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  현재 가용량: %s", ResourceFormat.vector(dto.available())));
            }
            case BLOCKED_UNSAFE -> {
                sb.append(String.format(
                        "Banker's Algorithm이 요청을 보류했습니다 (불안전 상태): PID %d %s → WAITING%n",
                        dto.pid(), ResourceFormat.vector(dto.vector())));
                sb.append(String.format("  가용량은 충분하지만(%s) 승인 시 안전 순서가 사라집니다.",
                        ResourceFormat.vector(dto.available())));
            }
        }

        return SystemCallResult.success(sb.toString());
    }

    private String formatSequence(java.util.List<Integer> sequence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.size(); i++) {
            if (i > 0) {
                sb.append(" → ");
            }
            sb.append('P').append(sequence.get(i));
        }
        return sb.toString();
    }
}
