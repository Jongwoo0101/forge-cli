package forgeframework.cli.command;

import forgeframework.deadlock.DeadlockDetectDto;
import forgeframework.deadlock.WaitForEdgeDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 교착 상태 탐지 알고리즘을 실행하는 명령어. 사용법: {@code detect}
 *
 * <p>순환 대기에 갇힌 프로세스 목록과, 누가 누구를 기다리고 있는지를 보여주는
 * 대기 그래프(Wait-For Graph) 간선을 함께 출력한다.</p>
 */
public final class DetectCommand implements Command {

    @Override
    public String name() {
        return "detect";
    }

    @Override
    public String description() {
        return "교착 상태(순환 대기)가 발생했는지 탐지합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.DETECT));
        if (!result.isSuccess()) {
            return result;
        }

        DeadlockDetectDto dto = result.dataAs(DeadlockDetectDto.class);
        if (!dto.hasDeadlock()) {
            return SystemCallResult.success(String.format(
                    "교착 상태가 감지되지 않았습니다. (검사 대상 %d개 프로세스, 가용 자원 %s)",
                    dto.inspected(), ResourceFormat.vector(dto.available())));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("⚠ 교착 상태가 감지되었습니다.\n");
        sb.append("  교착 프로세스: ").append(dto.deadlockedPids()).append('\n');
        sb.append("  가용 자원: ").append(ResourceFormat.vector(dto.available())).append('\n');
        sb.append("\n[Wait-For Graph]\n");
        for (WaitForEdgeDto edge : dto.waitForEdges()) {
            sb.append(String.format("  P%d ──(%s ×%d 부족)──> P%d%n",
                    edge.waiterPid(), edge.resource(), edge.shortage(), edge.holderPid()));
        }
        sb.append("\n'recover' 명령으로 희생자를 선정해 교착 상태를 해소할 수 있습니다.");

        return SystemCallResult.success(sb.toString());
    }
}
