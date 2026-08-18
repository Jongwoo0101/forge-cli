package forgeframework.cli.command;

import forgeframework.device.DeviceInfoDto;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

import java.util.List;

/**
 * 현재 커널에 등록된 장치(Keyboard, Disk, Printer, Timer)의 목록과 상태를 출력하는 명령어.
 *
 * <p>Kernel/DeviceManager는 {@link DeviceInfoDto} 리스트라는 순수 데이터만 반환하고,
 * 표 형태로 꾸미는 건 이 클래스(Shell 계층)의 책임이다 — FrameTableCommand와
 * 동일한 원칙을 따른다.</p>
 */
public final class DevinfoCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public DevinfoCommand() {
    }
    @Override
    public String name() {
        return "devinfo";
    }

    @Override
    public String description() {
        return "등록된 장치(Keyboard/Disk/Printer/Timer)의 목록과 상태를 출력합니다.";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.DEVINFO));
        if (!result.isSuccess()) {
            return result;
        }

        List<DeviceInfoDto> devices = result.dataAsList(DeviceInfoDto.class);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-8s | %s%n", "NAME", "TYPE", "STATUS"));
        for (DeviceInfoDto device : devices) {
            sb.append(String.format("%-10s | %-8s | %s%n", device.name(), device.type(), device.status()));
        }

        return SystemCallResult.success(sb.toString().stripTrailing());
    }
}
