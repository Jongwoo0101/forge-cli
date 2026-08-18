package forgeframework.cli.shell;

import forgeframework.cli.command.CommandRegistry;
import forgeframework.cli.command.StandardCommands;
import forgeframework.common.ForgeOSConstants;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

import java.util.Scanner;

/**
 * 표준 입출력에 붙는 대화형 셸.
 *
 * <p>명령어 목록과 토큰 분리 규칙은 이 클래스가 소유하지 않는다.
 * 각각 {@link StandardCommands}와 {@link CommandRegistry#dispatch}에 있으며,
 * 이 클래스는 "System.in에서 한 줄 읽어 System.out에 결과를 찍는다"는
 * CLI 고유의 책임만 남긴다. ForgeOS의 Terminal 앱은 같은 레지스트리를 쓰되
 * 입출력만 JavaFX 컨트롤로 바꿔 끼운다.</p>
 */
public class ForgeShell {

    private final Kernel kernel;
    private final CommandRegistry registry;
    private final ShellPrompt prompt;
    private final Scanner input;

    /**
     * 주어진 커널에 붙는 셸을 만든다.
     *
     * @param kernel 명령을 위임할 커널
     */
    public ForgeShell(Kernel kernel) {
        ShellContext context = new ShellContext();

        this.kernel = kernel;
        this.registry = StandardCommands.createRegistry(context);
        this.prompt = new ShellPrompt(context);
        this.input = new Scanner(System.in);
    }

    /** 커널이 종료되거나 표준 입력이 닫힐 때까지 REPL을 돈다. */
    public void run() {
        System.out.println(ForgeOSConstants.OS_NAME + " Shell에 오신 것을 환영합니다. 'help'를 입력해보세요.");

        while (kernel.isRunning()) {
            System.out.print(prompt.render());

            if (!input.hasNextLine()) {
                break;
            }

            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            SystemCallResult result = registry.dispatch(kernel, line);
            System.out.println(result.getMessage());
        }

        input.close();
    }
}
