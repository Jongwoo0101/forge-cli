package forgeframework.cli.shell;

import forgeframework.cli.command.BankerCommand;
import forgeframework.cli.command.CatCommand;
import forgeframework.cli.command.CdCommand;
import forgeframework.cli.command.Command;
import forgeframework.cli.command.CommandRegistry;
import forgeframework.cli.command.DetectCommand;
import forgeframework.cli.command.DevinfoCommand;
import forgeframework.cli.command.DiskFinishCommand;
import forgeframework.cli.command.ExecCommand;
import forgeframework.cli.command.FrameTableCommand;
import forgeframework.cli.command.FreeCommand;
import forgeframework.cli.command.HelpCommand;
import forgeframework.cli.command.IoRequestCommand;
import forgeframework.cli.command.KillCommand;
import forgeframework.cli.command.LsCommand;
import forgeframework.cli.command.MallocCommand;
import forgeframework.cli.command.MeminfoCommand;
import forgeframework.cli.command.MkdirCommand;
import forgeframework.cli.command.PsCommand;
import forgeframework.cli.command.PwdCommand;
import forgeframework.cli.command.RecoverCommand;
import forgeframework.cli.command.ResFreeCommand;
import forgeframework.cli.command.ResInfoCommand;
import forgeframework.cli.command.ResMaxCommand;
import forgeframework.cli.command.ResReqCommand;
import forgeframework.cli.command.RmCommand;
import forgeframework.cli.command.SchedulerCommand;
import forgeframework.cli.command.ShutdownCommand;
import forgeframework.cli.command.SoftIntCommand;
import forgeframework.cli.command.TouchCommand;
import forgeframework.cli.command.TranslateCommand;
import forgeframework.cli.command.TreeCommand;
import forgeframework.cli.command.TypeCommand;
import forgeframework.cli.command.UptimeCommand;
import forgeframework.cli.command.WriteCommand;
import forgeframework.common.ForgeOSConstants;
import forgeframework.kernel.Kernel;
import forgeframework.syscall.SystemCallResult;

import java.util.Scanner;

public class ForgeShell {

    private final Kernel kernel;
    private final CommandRegistry registry;
    private final ShellContext context;
    private final ShellPrompt prompt;
    private final Scanner input;

    public ForgeShell(Kernel kernel) {
        this.kernel = kernel;
        this.registry = new CommandRegistry();
        this.context = new ShellContext();
        this.prompt = new ShellPrompt(context);
        this.input = new Scanner(System.in);
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registry.register(new HelpCommand(registry));
        registry.register(new ShutdownCommand());
        registry.register(new UptimeCommand());

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
    }

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

            handleLine(line);
        }

        input.close();
    }

    private void handleLine(String line) {
        String[] tokens = line.split(ForgeOSConstants.COMMAND_DELIMITER);
        String commandName = tokens[0];
        String[] args = (tokens.length > 1)
                ? java.util.Arrays.copyOfRange(tokens, 1, tokens.length)
                : new String[0];

        Command command = registry.resolve(commandName);
        SystemCallResult result = command.execute(kernel, args);

        System.out.println(result.getMessage());
    }
}
