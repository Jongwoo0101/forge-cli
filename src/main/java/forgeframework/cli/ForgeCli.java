package forgeframework.cli;

import forgeframework.api.ForgeConfig;
import forgeframework.api.ForgeFramework;
import forgeframework.cli.shell.ForgeShell;
import forgeframework.kernel.Kernel;
import forgeframework.logger.ConsoleLogListener;
import forgeframework.logger.EventLogger;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * ForgeCLI 애플리케이션의 진입점.
 *
 * <p>ForgeFramework 1.0에서 CLI는 <b>커널을 쓰는 여러 클라이언트 중 하나</b>로
 * 분리되었다. 이 모듈({@code forgeframework.cli})은 커널 모듈
 * ({@code forgeframework})에 의존할 뿐이며, 반대 방향 의존은 존재하지
 * 않는다 — 커널 jar에는 셸도 명령어도 들어 있지 않다.</p>
 *
 * <p>실행 순서: 콘솔 준비 → 배너 출력 → {@link ForgeFramework#boot} → 셸 실행.</p>
 *
 * <p><b>[1.0]</b> 배너 출력은 원래 {@code BootManager}에 있었다. GUI 클라이언트가
 * 임베딩할 때 표준 출력이 더럽혀지는 문제가 있어, 콘솔에 무언가를 찍는 책임을
 * 전부 이쪽(표현 계층)으로 옮겼다.</p>
 */
public final class ForgeCli {

    private ForgeCli() {
    }

    /**
     * CLI를 시작한다.
     *
     * @param args 사용하지 않는다
     */
    public static void main(String[] args) {
        forceUtf8Console();
        printBanner();

        // 리스너를 먼저 붙여야 부팅 과정 로그까지 콘솔에 나온다.
        EventLogger logger = new EventLogger();
        logger.addListener(new ConsoleLogListener());

        // CLI는 부팅 과정을 눈으로 보여주는 편이 낫기 때문에 기본 지연을 유지한다.
        Kernel kernel = ForgeFramework.boot(ForgeConfig.defaults(), logger);

        try {
            new ForgeShell(kernel).run();
        } finally {
            // 셸이 어떤 이유로 끝나든 백그라운드 스레드는 반드시 정리한다.
            kernel.close();
        }
    }

    private static void printBanner() {
        System.out.println("=================================================");
        System.out.println(" " + ForgeFramework.name() + " v" + ForgeFramework.version());
        System.out.println(" Operating System Kernel Architecture Engine");
        System.out.println("=================================================");
    }

    /**
     * 실행 환경의 로케일 설정과 무관하게 한글 등이 깨지지 않도록
     * 표준 출력/에러 스트림을 UTF-8로 강제한다.
     */
    private static void forceUtf8Console() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }
}
