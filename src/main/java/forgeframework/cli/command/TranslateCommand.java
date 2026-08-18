package forgeframework.cli.command;

import forgeframework.kernel.Kernel;
import forgeframework.memory.TranslationResult;
import forgeframework.syscall.SystemCallRequest;
import forgeframework.syscall.SystemCallResult;
import forgeframework.syscall.SystemCallType;

/**
 * 가상 주소를 물리 주소로 변환해 보여주는 명령어 (Paging + TLB 동작 확인용).
 * 사용법: translate &lt;PID&gt; &lt;virtualAddress&gt;
 */
public final class TranslateCommand implements Command {


    /** 기본 생성자 — 상태가 없어 인자를 받지 않는다. */
    public TranslateCommand() {
    }
    @Override
    public String name() {
        return "translate";
    }

    @Override
    public String description() {
        return "가상 주소를 물리 주소로 변환합니다. (translate <PID> <vaddr>)";
    }

    @Override
    public SystemCallResult execute(Kernel kernel, String[] args) {
        SystemCallResult result = kernel.handleSystemCall(new SystemCallRequest(SystemCallType.TRANSLATE, args));
        if (!result.isSuccess()) {
            return result;
        }

        TranslationResult dto = result.dataAs(TranslationResult.class);
        return SystemCallResult.success(String.format(
                "가상주소 %d -> 물리주소 %d (페이지 #%d -> 프레임 #%d, TLB %s)",
                dto.virtualAddress(), dto.physicalAddress(),
                dto.pageNumber(), dto.frameNumber(),
                dto.tlbHit() ? "HIT" : "MISS"));
    }
}
