package forgeframework.cli.command;

import forgeframework.common.ForgeOSConstants;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 사용 가능한 {@link Command}들을 이름으로 조회할 수 있도록 관리하는 레지스트리.
 *
 * <p>등록 순서를 유지하기 위해 {@link LinkedHashMap}을 사용하며,
 * 등록되지 않은 이름으로 조회 시 {@link UnknownCommand}(Null Object)를 반환한다.</p>
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    /** 빈 레지스트리를 만든다. 표준 명령어 세트는 {@link StandardCommands}가 채운다. */
    public CommandRegistry() {
    }

    /**
     * 명령어를 레지스트리에 등록한다.
     *
     * @param command 등록할 명령어
     */
    public void register(Command command) {
        commands.put(command.name(), command);
    }

    /**
     * 이름으로 명령어를 조회한다.
     *
     * @param name 조회할 명령어 이름
     * @return 등록된 명령어, 없으면 {@link UnknownCommand}
     */
    public Command resolve(String name) {
        return commands.getOrDefault(name, new UnknownCommand(name));
    }

    /**
     * 등록된 모든 명령어를 반환한다. help 명령에서 사용된다.
     *
     * @return 등록된 명령어 컬렉션 (등록 순서 유지)
     */
    public Collection<Command> getAll() {
        return commands.values();
    }

    /**
     * 입력 한 줄을 토큰으로 나눠 해당 명령어를 실행한다.
     *
     * <p>토큰 분리 규칙(공백 기준)을 여기 한 곳에 두는 이유는, CLI 셸과 GUI
     * 터미널이 같은 문자열을 넣었을 때 반드시 같은 결과를 내야 하기 때문이다.
     * 양쪽이 각자 {@code split}을 부르면 언젠가 규칙이 어긋난다.</p>
     *
     * <p>빈 줄은 호출하는 쪽에서 걸러야 한다 — 여기서는 판단하지 않는다.</p>
     *
     * @param kernel 명령을 위임할 커널
     * @param line   사용자가 입력한 한 줄 (앞뒤 공백은 이 메서드가 제거한다)
     * @return 명령 실행 결과. 등록되지 않은 명령이면 {@link UnknownCommand}의 실패 결과
     */
    public SystemCallResult dispatch(Kernel kernel, String line) {
        String[] tokens = line.trim().split(ForgeOSConstants.COMMAND_DELIMITER);
        String commandName = tokens[0];
        String[] args = (tokens.length > 1)
                ? Arrays.copyOfRange(tokens, 1, tokens.length)
                : new String[0];

        return resolve(commandName).execute(kernel, args);
    }
}
