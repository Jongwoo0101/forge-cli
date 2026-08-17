package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.kernel.UptimeDto;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 커널의 가동 시간을 조회하는 명령어.
 *
 * <p><b>[Phase 6 리팩토링]</b> {@code HH:mm:ss} 포맷팅은 CLI의 표현 방식일 뿐
 * 커널의 관심사가 아니므로, 커널에 있던 {@code formatDuration()}을 통째로
 * 이 클래스로 옮겼다.</p>
 */
public final class UptimeCommand implements Command {

    @Override
    public String name() {
        return "uptime";
    }

    @Override
    public String description() {
        return "커널 가동 시간을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.UPTIME));
        if (!result.isSuccess()) {
            return result;
        }

        UptimeDto uptime = result.dataAs(UptimeDto.class);
        return SystemCallResult.success("가동 시간: " + format(uptime.uptimeSeconds()));
    }

    private String format(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
