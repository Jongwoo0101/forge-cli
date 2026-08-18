package forgeframework.cli.command;

import forgeframework.deadlock.ResourceRowDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 프로세스의 최대 자원 요구량(Max)을 선언하는 명령어.
 * 사용법: {@code res_max <pid> <R1> [R2] [R3]}
 *
 * <p>Banker's Algorithm은 정의상 "각 프로세스가 최대 얼마까지 요구할 수 있는가"를
 * 미리 알아야 안전 상태를 계산할 수 있다. 이 선언이 없으면 모든 프로세스의 Max가
 * 시스템 총량으로 잡혀(가장 보수적인 기본값) 회피 알고리즘이 사실상 무력해지므로,
 * 교재 예제를 재현하려면 이 명령으로 Max를 먼저 설정해야 한다.</p>
 */
public final class ResMaxCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public ResMaxCommand() {
    }
    @Override
    public String name() {
        return "res_max";
    }

    @Override
    public String description() {
        return "프로세스의 최대 자원 요구량(Max)을 선언합니다. (res_max <pid> <R1> [R2] [R3])";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        if (args.length < 2) {
            return SystemCallResult.failure("사용법: res_max <pid> <R1> [R2] [R3]");
        }

        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_MAX, args));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceRowDto row = result.dataAs(ResourceRowDto.class);
        return SystemCallResult.success(String.format(
                "PID %d의 최대 요구량이 선언되었습니다. Max: %s / Allocation: %s / Need: %s",
                row.pid(),
                ResourceFormat.vector(row.max()),
                ResourceFormat.vector(row.allocation()),
                ResourceFormat.vector(row.need())));
    }
}
