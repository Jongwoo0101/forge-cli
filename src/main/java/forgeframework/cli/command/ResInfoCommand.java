package forgeframework.cli.command;

import forgeframework.deadlock.ResourceRowDto;
import forgeframework.deadlock.ResourceSnapshotDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 시스템 자원 현황과 프로세스별 Allocation / Max / Need 행렬을 출력하는 명령어.
 *
 * <p>Kernel/DeadlockManager는 {@link ResourceSnapshotDto}라는 순수 데이터만
 * 반환하고, 은행원 알고리즘 교재에 나오는 형태의 표로 꾸미는 건 이 클래스의
 * 책임이다 — MeminfoCommand/FrameTableCommand와 동일한 원칙이다.</p>
 */
public final class ResInfoCommand implements Command {

    @Override
    public String name() {
        return "res_info";
    }

    @Override
    public String description() {
        return "자원 총량/가용량과 프로세스별 Allocation·Max·Need 행렬을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.RES_INFO));
        if (!result.isSuccess()) {
            return result;
        }

        ResourceSnapshotDto snapshot = result.dataAs(ResourceSnapshotDto.class);
        String header = ResourceFormat.header(snapshot.labels());

        StringBuilder sb = new StringBuilder();
        sb.append("[System Resources]\n");
        sb.append(String.format("%-12s %s%n", "", header));
        sb.append(String.format("%-12s %s%n", "Total", ResourceFormat.vector(snapshot.total())));
        sb.append(String.format("%-12s %s%n", "Allocated", ResourceFormat.vector(snapshot.allocated())));
        sb.append(String.format("%-12s %s%n", "Available", ResourceFormat.vector(snapshot.available())));
        sb.append(String.format("%-12s %s / 희생자 정책: %s%n",
                "Banker's", snapshot.bankerEnabled() ? "ON (교착 회피)" : "OFF (교착 유발 가능)",
                snapshot.victimPolicy()));

        sb.append("\n[Process Matrix]\n");
        if (snapshot.rows().isEmpty()) {
            sb.append("자원 장부에 등록된 프로세스가 없습니다.");
            return SystemCallResult.success(sb.toString());
        }

        sb.append(String.format("%-5s | %-10s | %-12s | %-12s | %-12s | %s%n",
                "PID", "NAME", "ALLOCATION", "MAX", "NEED", "REQUEST"));
        sb.append("-".repeat(80)).append('\n');

        for (ResourceRowDto row : snapshot.rows()) {
            sb.append(String.format("%-5d | %-10s | %-12s | %-12s | %-12s | %s%s%n",
                    row.pid(),
                    truncate(row.name()),
                    ResourceFormat.vector(row.allocation()),
                    ResourceFormat.vector(row.max()),
                    ResourceFormat.vector(row.need()),
                    ResourceFormat.vector(row.pendingRequest()),
                    row.blocked() ? "  <-- WAITING" : ""));
        }

        return SystemCallResult.success(sb.toString().stripTrailing());
    }

    private String truncate(String name) {
        if (name == null) {
            return "-";
        }
        return (name.length() <= 10) ? name : name.substring(0, 9) + "…";
    }
}
