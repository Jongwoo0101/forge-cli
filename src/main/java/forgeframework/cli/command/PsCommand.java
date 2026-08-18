package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.process.ProcessDto;
import forgeframework.process.ProcessState;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 프로세스 목록과 상태를 표로 출력하는 명령어.
 *
 * <p><b>[Phase 6 리팩토링]</b> Phase 2 시절 이 명령어는 커널이 통째로 만들어준
 * 문자열을 그대로 출력하기만 했다. 이제 커널은 {@link ProcessDto} 목록만
 * 반환하고, 표의 컬럼 폭과 RUNNING 표시(*)를 정하는 일은 표현 계층인 이
 * 클래스의 책임이다 — MeminfoCommand/FrameTableCommand가 이미 따르고 있던
 * 원칙을 {@code ps}에도 뒤늦게 적용한 것이다.</p>
 */
public final class PsCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public PsCommand() {
    }
    @Override
    public String name() {
        return "ps";
    }

    @Override
    public String description() {
        return "프로세스 상태 목록을 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.PS));
        if (!result.isSuccess()) {
            return result;
        }

        List<ProcessDto> processes = result.dataAsList(ProcessDto.class);
        if (processes.isEmpty()) {
            return SystemCallResult.success("실행 중인 프로세스가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-10s | %-8s | %-10s | %s%n",
                "PID", "STATE", "CPU_TIME", "BURST", "NAME"));
        sb.append("-".repeat(55));

        for (ProcessDto process : processes) {
            String indicator = (process.state() == ProcessState.RUNNING || process.running()) ? "*" : "";
            sb.append(String.format("%n%-5d | %-10s | %-8d | %-10d | %s%s",
                    process.pid(), process.state(), process.cpuTimeUsed(),
                    process.burstTime(), process.name(), indicator));
        }
        return SystemCallResult.success(sb.toString());
    }
}
