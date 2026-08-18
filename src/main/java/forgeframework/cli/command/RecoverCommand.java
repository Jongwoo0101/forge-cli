package forgeframework.cli.command;

import forgeframework.deadlock.DeadlockRecoverDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 교착 상태를 복구하는 명령어. 사용법: {@code recover}
 *
 * <p>희생자(Victim) 프로세스를 정책에 따라 선정해 강제 종료시키고, 그가 붙잡고
 * 있던 자원을 회수해 교착 상태를 해소한다. 회수된 자원으로 대기가 풀린
 * 프로세스는 자동으로 READY 상태가 된다.</p>
 */
public final class RecoverCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public RecoverCommand() {
    }
    @Override
    public String name() {
        return "recover";
    }

    @Override
    public String description() {
        return "희생자를 선정해 강제 종료하고 교착 상태를 해소합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RECOVER));
        if (!result.isSuccess()) {
            return result;
        }

        DeadlockRecoverDto dto = result.dataAs(DeadlockRecoverDto.class);
        if (dto.victims().isEmpty()) {
            return SystemCallResult.success("복구할 교착 상태가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("교착 상태 복구 (정책: %s)%n", dto.policy()));
        sb.append(String.format("  강제 종료된 희생자: %s%n", dto.victims()));
        sb.append(String.format("  회수한 자원: %s%n", ResourceFormat.vector(dto.reclaimed())));

        if (!dto.wokenPids().isEmpty()) {
            sb.append(String.format("  대기가 풀려 깨어난 프로세스: %s%n", dto.wokenPids()));
        }

        if (dto.recovered()) {
            sb.append("  ✔ 교착 상태가 완전히 해소되었습니다.");
        } else {
            sb.append(String.format("  ✖ 복구 후에도 교착 상태가 남아 있습니다: %s", dto.stillDeadlocked()));
        }
        return SystemCallResult.success(sb.toString());
    }
}
