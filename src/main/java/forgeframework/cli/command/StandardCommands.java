package forgeframework.cli.command;

import forgeframework.cli.shell.ShellContext;

/**
 * ForgeFramework 표준 명령어 세트를 한 번에 등록해 주는 팩토리.
 *
 * <p>이 클래스가 존재하는 이유는 하나다. 명령어 목록이 {@code ForgeShell}의
 * private 메서드 안에만 있으면, CLI가 아닌 클라이언트(ForgeOS의 Terminal 앱 등)는
 * 같은 목록을 손으로 다시 적는 수밖에 없다. 그렇게 두 벌이 되는 순간 한쪽에만
 * 명령이 추가되는 사고가 반드시 일어난다. 등록 지점을 여기 하나로 모아 두면
 * 새 명령을 추가할 곳도 한 곳뿐이다.</p>
 *
 * <p>주의: {@link ShellContext}는 현재 작업 디렉터리를 들고 있는 <b>가변</b>
 * 객체다. 창(터미널 탭)마다 cwd가 독립적이어야 하므로 레지스트리도 창마다
 * 하나씩 만들어야 한다 — 전역 싱글턴으로 공유하면 안 된다.</p>
 */
public final class StandardCommands {

    private StandardCommands() {
    }

    /**
     * 표준 명령어가 모두 등록된 새 레지스트리를 만든다.
     *
     * @param context 이 레지스트리가 사용할 셸 컨텍스트(cwd 보관소)
     * @return 새로 만들어진 레지스트리
     */
    public static CommandRegistry createRegistry(ShellContext context) {
        CommandRegistry registry = new CommandRegistry();

        registry.register(new HelpCommand(registry));
        registry.register(new ShutdownCommand());
        registry.register(new UptimeCommand());

        // Phase 2~3 — Process & Memory
        registry.register(new PsCommand());
        registry.register(new ExecCommand());
        registry.register(new KillCommand());
        registry.register(new SchedulerCommand());
        registry.register(new MallocCommand());
        registry.register(new FreeCommand());
        registry.register(new MeminfoCommand());
        registry.register(new TranslateCommand());
        registry.register(new FrameTableCommand());

        // Phase 4 — File System
        registry.register(new PwdCommand(context));
        registry.register(new CdCommand(context));
        registry.register(new LsCommand(context));
        registry.register(new MkdirCommand(context));
        registry.register(new TouchCommand(context));
        registry.register(new RmCommand(context));
        registry.register(new WriteCommand(context));
        registry.register(new CatCommand(context));
        registry.register(new TreeCommand(context));

        // Phase 5 — Device & Interrupt
        registry.register(new DevinfoCommand());
        registry.register(new IoRequestCommand());
        registry.register(new TypeCommand());
        registry.register(new DiskFinishCommand());
        registry.register(new SoftIntCommand());

        // Phase 6 — Deadlock (자원 할당 및 교착 상태)
        registry.register(new ResInfoCommand());
        registry.register(new ResMaxCommand());
        registry.register(new ResReqCommand());
        registry.register(new ResFreeCommand());
        registry.register(new BankerCommand());
        registry.register(new DetectCommand());
        registry.register(new RecoverCommand());

        return registry;
    }
}
