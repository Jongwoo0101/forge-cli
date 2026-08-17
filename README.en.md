# ForgeCLI

**Command-line client for the ForgeFramework kernel**

> 🇺🇸 English (한국어: [README.md](README.md))

ForgeCLI is a shell that drives the
[ForgeFramework](https://github.com/Jongwoo0101/forge-framework) kernel from a terminal.
Create processes, allocate memory, write files, and deliberately construct a deadlock so you
can watch it be detected and recovered — **one command at a time**.

This repository contains no kernel code. The kernel arrives as a Maven artifact, and the
dependency is **one-way**: if ForgeCLI disappeared, the kernel would not notice.

```text
                       ForgeFramework          ← the kernel (separate repository)
                              ▲
                              │  requires
                ┌─────────────┼─────────────┐
                │             │             │
           ForgeOS      ★ ForgeCLI ★    ForgeStudio
```

---

## Contents

- [Quick start](#quick-start)
- [Using the shell](#using-the-shell)
- [Command reference](#command-reference)
  - [Kernel](#kernel)
  - [Processes and scheduling](#processes-and-scheduling)
  - [Memory and paging](#memory-and-paging)
  - [File system](#file-system)
  - [Devices and interrupts](#devices-and-interrupts)
  - [Resources and deadlock](#resources-and-deadlock)
- [Learn by scenario](#learn-by-scenario)
- [Project layout](#project-layout)
- [License](#license)

---

## Quick start

### Requirements

- **JDK 21** or newer
- ForgeFramework kernel `1.0` installed in your local Maven repository (`~/.m2`)

### 1. Install the kernel first

```bash
git clone https://github.com/Jongwoo0101/forge-framework.git
cd forge-framework
./gradlew publishToMavenLocal      # or ./scripts/publish.sh
```

This installs `io.github.jongwoo0101:forgeframework:1.0` into `~/.m2/repository`.

### 2. Build and run ForgeCLI

```bash
git clone https://github.com/Jongwoo0101/forge-cli.git
cd forge-cli

./scripts/build.sh                 # or ./gradlew build
./scripts/run.sh                   # or java -jar build/libs/forgecli-1.0-all.jar
```

While developing, launching straight from Gradle is quicker:

```bash
./gradlew run --console=plain
```

### Artifacts

| File | Description |
|---|---|
| `build/libs/forgecli-1.0-all.jar` | **Single runnable jar with the kernel bundled in.** Download and `java -jar` it. |
| `build/libs/forgecli-1.0.jar` | Thin jar. Requires the kernel jar on the classpath to run. |

> If dependency resolution fails because the kernel cannot be found, you almost certainly
> skipped the `publishToMavenLocal` in step 1.

---

## Using the shell

Once boot finishes, a prompt appears. It shows the **current working directory**.

```text
=================================================
 ForgeFramework v1.0
 Operating System Kernel Architecture Engine
=================================================
[10:32:11.153] [INFO] Hardware check...
[10:32:11.306] [INFO] Initialising event logger...
[10:32:11.457] [INFO] Initialising kernel...
[10:32:11.607] [INFO] Initialising subsystems...
...
forgeframework:/>
```

- Commands and arguments are separated by **whitespace**.
- An unregistered name simply reports "unknown command"; the shell stays alive.
- `shutdown` exits, cleaning up the timer and printer daemon threads.
- Lines starting with `[INFO]` are the **kernel's live event log**. Events raised by other
  threads (the timer, for instance) are interleaved with your command output.

---

## Command reference

Notation: `<required>` · `[optional]` · `a|b` means pick one.

### Kernel

| Command | Arguments | Description |
|---|---|---|
| `help` | — | Print all registered commands with their descriptions. |
| `uptime` | — | Print kernel uptime. |
| `shutdown` | — | Shut the kernel down and leave the shell. |

```text
forgeframework:/> uptime
forgeframework:/> shutdown
```

---

### Processes and scheduling

| Command | Arguments | Description |
|---|---|---|
| `ps` | — | Print processes with state and CPU usage as a table. |
| `exec` | `<name> [burstTime]` | Create a process. Omitting `burstTime` uses the default of 5 ticks. |
| `kill` | `<PID>` | Force-terminate a process; its memory and resources are reclaimed. |
| `scheduler` | `[fcfs\|rr]` | With no argument, query the scheduler; with one, **swap it at runtime**. |

**`exec`** — a new process becomes `READY` immediately and joins the ready queue.

```text
forgeframework:/> exec worker 20
```

**`ps`**

```text
forgeframework:/> ps
PID   | STATE      | CPU_TIME | BURST      | NAME
-------------------------------------------------------
1     | RUNNING    | 3        | 20         | worker
2     | READY      | 0        | 10         | logger
```

**`scheduler`** — `rr` also accepts `roundrobin` and `round-robin`, case-insensitively.
Under the preemptive scheduler (RR) a context switch happens every time quantum
(3 ticks by default); under FCFS a process keeps the CPU until it finishes.

```text
forgeframework:/> scheduler
forgeframework:/> scheduler fcfs
```

> Time advances via a **timer interrupt once per second**. Run `ps` twice a few seconds
> apart and you will see `CPU_TIME` climb and states change.

---

### Memory and paging

| Command | Arguments | Description |
|---|---|---|
| `malloc` | `<PID> <size>` | Allocate on that process's heap and return a **virtual address**. |
| `free` | `<PID> <address>` | Release an address returned by `malloc`. |
| `meminfo` | — | Print physical memory, per-process heaps and TLB statistics. |
| `translate` | `<PID> <virtualAddress>` | Translate a virtual address and report whether it was a **TLB hit**. |
| `frametable` | — | Print the whole physical frame table (owner and page per frame). |

Default physical memory is **16 frames × 4 bytes**; the TLB holds **4 entries**.

```text
forgeframework:/> exec app 10
forgeframework:/> malloc 1 8
forgeframework:/> translate 1 0
forgeframework:/> translate 1 0      ← the second call is a TLB HIT
forgeframework:/> meminfo
forgeframework:/> frametable
```

> Call `translate` twice on the same address: the first is a miss, the second a hit.
> Because the TLB holds only 4 entries, sweeping five or more distinct pages starts
> producing misses again — a neat way to watch TLB replacement happen.

---

### File system

| Command | Arguments | Description |
|---|---|---|
| `pwd` | — | Print the current working directory. |
| `cd` | `<path>` | Change the working directory. Absolute and relative paths both work. |
| `ls` | `[path]` | List directory contents; defaults to the current directory. |
| `mkdir` | `<name>` | Create a directory. Names may not contain spaces. |
| `touch` | `<name>` | Create an empty file. |
| `rm` | `<name>` | Remove a file or an **empty** directory. |
| `write` | `<name> <text...>` | **Overwrite** a file's contents. Multiple words are joined with spaces. |
| `cat` | `<name>` | Print a file's contents. |
| `tree` | `[path]` | Print the directory structure as a tree. |

The virtual disk is **16 blocks × 16 bytes** with **16 inodes** (root included). Nothing
touches a real disk, so contents vanish when the shell exits.

```text
forgeframework:/> mkdir docs
forgeframework:/> cd docs
forgeframework:/docs> touch notes.txt
forgeframework:/docs> write notes.txt hello forge
forgeframework:/docs> cat notes.txt
hello forge
forgeframework:/docs> cd /
forgeframework:/> tree
```

> `cd` state is **owned by the shell**. The kernel does not remember "where you are"; the
> shell passes the current path along with every system call. That is why several shells can
> attach to one kernel and each keep its own working directory.

---

### Devices and interrupts

| Command | Arguments | Description |
|---|---|---|
| `devinfo` | — | Print device (keyboard · disk · printer · timer) status and queue depth. |
| `io_request` | `<PID> <device>` | Issue an I/O request; the process transitions to `WAITING`. |
| `type` | `<text...>` | **(Virtual hardware)** Raise a keyboard input, triggering a Keyboard interrupt. |
| `disk_finish` | — | **(Virtual hardware)** Signal disk I/O completion, waking the blocked process. |
| `soft_int` | `[payload...]` | Raise a software interrupt. Without a payload, `manual` is used. |

`device` is one of `keyboard`, `disk`, `printer`, `timer`.

`type` and `disk_finish` are less "commands" than **switches by which a human drives the
virtual hardware**. There is no real keyboard or disk here, so somebody has to produce the
signal.

```text
forgeframework:/> exec reader 15
forgeframework:/> io_request 1 disk     ← PID 1 transitions to WAITING
forgeframework:/> ps                    ← confirm STATE = WAITING
forgeframework:/> disk_finish           ← completion signal → PID 1 returns to READY
forgeframework:/> ps
```

```text
forgeframework:/> type hello
forgeframework:/> soft_int checkpoint
forgeframework:/> devinfo
```

---

### Resources and deadlock

There are **three resource types — `R1`, `R2`, `R3`** — with a default total of `[10, 5, 7]`.
Vector arguments follow that order; trailing entries may be omitted (treated as 0).

| Command | Arguments | Description |
|---|---|---|
| `res_info` | — | Print totals and availability plus the per-process **Allocation · Max · Need** matrix. |
| `res_max` | `<PID> <R1> [R2] [R3]` | Declare the process's maximum claim (Max), the input to Banker's. |
| `res_req` | `<PID> <R1> [R2] [R3]` | Request resources. |
| `res_free` | `<PID> <R1> [R2] [R3]` | Release held resources; blocked processes may wake up. |
| `banker` | `[on\|off]` · `policy <policy>` | Toggle deadlock **avoidance** (Banker's Algorithm) and change the victim policy. |
| `detect` | — | **Detect** whether a deadlock (circular wait) exists. |
| `recover` | — | **Recover** by selecting a victim and force-terminating it. |

**The three possible endings of `res_req`**

| Outcome | Meaning |
|---|---|
| `GRANTED` | Allocated. With Banker's on, a safe sequence is printed alongside. |
| `BLOCKED_INSUFFICIENT` | Not physically enough available; the process goes to `WAITING`. |
| `BLOCKED_UNSAFE` | The resources exist, but **granting them would leave an unsafe state**, so Banker's refused. |

`BLOCKED_UNSAFE` is avoidance actually earning its keep. With `banker off` it can never
appear — and real deadlocks become possible instead.

**The three forms of `banker`**

```text
forgeframework:/> banker                    ← query the current setting
forgeframework:/> banker off                ← on/off, enable/disable, true/false all accepted
forgeframework:/> banker policy HIGHEST_ALLOCATION
```

| Victim policy | Meaning |
|---|---|
| `LOWEST_ALLOCATION` | Sacrifice the process holding the fewest resources (**default**) |
| `HIGHEST_ALLOCATION` | Sacrifice the largest holder — reclaims the most at once |
| `YOUNGEST_FIRST` | Sacrifice the most recently created process |

**Sample `res_info` output**

```text
forgeframework:/> res_info
[System Resources]
             [ R1 R2 R3]
Total        [ 10  5  7]
Allocated    [  0  0  0]
Available    [ 10  5  7]
Banker's     ON / victim policy: LOWEST_ALLOCATION

[Process Matrix]
PID   | NAME       | ALLOCATION   | MAX          | NEED         | REQUEST
--------------------------------------------------------------------------------
1     | demo       | [  0  0  0]  | [ 10  5  7]  | [ 10  5  7]  | [  0  0  0]
```

---

## Learn by scenario

### Scenario 1 — the moment avoidance blocks a request

```text
exec p1 30
exec p2 30
res_max 1 7 5 3
res_max 2 3 2 2
res_req 1 7 4 3        ← GRANTED
res_req 2 3 2 2        ← BLOCKED_UNSAFE: resources remain, but granting them is unsafe
res_info
```

Turn avoidance off with `banker off` and repeat the same request: it is granted, and a real
deadlock becomes possible afterwards. That contrast is where avoidance shows its value.

### Scenario 2 — build a deadlock, then detect and recover

```text
banker off             ← avoidance must be off for a deadlock to form
exec p1 30
exec p2 30
res_req 1 6 0 0
res_req 2 4 4 0
res_req 1 4 0 0        ← blocks
res_req 2 0 2 0        ← blocks → circular wait complete
detect                 ← hasDeadlock = true, prints the wait-for edges
recover                ← victim terminated → resources reclaimed → waiters wake
detect                 ← no deadlock now
```

### Scenario 3 — I/O blocking and interrupts

```text
exec reader 15
io_request 1 disk      ← WAITING
ps
disk_finish            ← disk completion interrupt → back to READY
ps
```

### Scenario 4 — watching the TLB hit ratio

```text
exec app 20
malloc 1 8
translate 1 0          ← MISS
translate 1 0          ← HIT
meminfo                ← tlbHits / tlbMisses / hitRatio
```

---

## Project layout

```text
forge-cli/
├── build.gradle.kts                 # depends on the kernel as a Maven artifact
├── settings.gradle.kts
├── gradle.properties                # forgeFrameworkVersion=1.0
├── scripts/
│   ├── build.sh
│   └── run.sh
└── src/main/java/
    ├── module-info.java             # requires forgeframework;
    └── forgeframework/cli/
        ├── ForgeCli.java            # entry point — banner · logger · boot · shell
        ├── shell/
        │   ├── ForgeShell.java      # the REPL loop and command registration
        │   ├── ShellContext.java    # current working directory (the shell's only state)
        │   └── ShellPrompt.java     # prompt rendering
        └── command/
            ├── Command.java         # name() · description() · execute()
            ├── CommandRegistry.java # insertion-ordered lookup table
            ├── UnknownCommand.java  # Null Object
            └── ...                  # 33 commands
```

### Design principles

- **The shell never touches a kernel subsystem directly.** Every action goes through
  `SystemCallRequest` → `kernel.handleSystemCall(...)`.
- **The kernel composes no sentences.** It returns immutable record DTOs; every table,
  alignment and colour you see above is produced by the Command classes in this repository.
  Render the same DTOs into a GUI and you get ForgeOS.
- **Adding a command is one file plus one line.** Implement `Command` and register it in
  `ForgeShell.registerDefaultCommands()`; it shows up in `help` automatically.
- **The shell owns exactly one piece of state: the current working directory.** Everything
  else lives in the kernel.

---

## Related repositories

| Repository | Description |
|---|---|
| [forge-framework](https://github.com/Jongwoo0101/forge-framework) | The kernel engine this project depends on · [API docs](https://github.com/Jongwoo0101/forge-framework/blob/master/docs/api/README.en.md) |
| ForgeOS | JavaFX GUI operating-system simulator (planned) |
| ForgeStudio | Operating-system teaching and visualisation platform (planned) |

---

## License

MIT License
