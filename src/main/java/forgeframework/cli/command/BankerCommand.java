package forgeframework.cli.command;

import forgeframework.deadlock.BankerConfigDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * Banker's Algorithm(교착 회피)을 런타임에 켜고 끄는 명령어.
 * 사용법: {@code banker [on|off]}, {@code banker policy <정책>}
 *
 * <p>끄면 불안전 상태로 이어질 위험한 요청도 무조건 승인되므로, 교착 상태를
 * 고의로 만들어 {@code detect} / {@code recover}의 동작을 관찰할 수 있다.</p>
 */
public final class BankerCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public BankerCommand() {
    }
    @Override
    public String name() {
        return "banker";
    }

    @Override
    public String description() {
        return "교착 회피(Banker's Algorithm)를 켜거나 끕니다. (banker [on|off] | banker policy <정책>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.BANKER, args));
        if (!result.isSuccess()) {
            return result;
        }

        BankerConfigDto dto = result.dataAs(BankerConfigDto.class);
        String state = dto.enabled() ? "ON" : "OFF";
        String note = dto.enabled()
                ? "불안전 상태로 이어질 요청은 보류됩니다."
                : "위험한 요청도 무조건 승인됩니다 — 교착 상태가 발생할 수 있습니다.";

        String headline;
        if (dto.policyChanged()) {
            headline = "희생자 선정 정책이 " + dto.victimPolicy() + " (으)로 변경되었습니다."
                    + " (Banker's Algorithm: " + state + ")";
        } else if (dto.toggled()) {
            headline = "Banker's Algorithm이 " + state + " 되었습니다."
                    + " (희생자 정책: " + dto.victimPolicy() + ")";
        } else {
            headline = "Banker's Algorithm: " + state + " / 희생자 정책: " + dto.victimPolicy();
        }

        return SystemCallResult.success(headline + "\n  " + note);
    }
}
