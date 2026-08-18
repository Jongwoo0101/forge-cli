/**
 * ForgeCLI — ForgeFramework 커널의 명령줄 클라이언트이자 <b>공용 명령어 계층</b>.
 *
 * <p>커널 모듈에 <b>단방향으로만</b> 의존한다. 이 모듈이 사라져도 커널은 아무
 * 영향을 받지 않으며, 실제로 릴리즈되는 {@code forgeframework} jar에는
 * 셸도 명령어도 포함되지 않는다.</p>
 *
 * <h2>왜 패키지를 export하는가</h2>
 * <p>원래 이 모듈은 실행 파일이었을 뿐이라 아무것도 내보내지 않았다. 그런데
 * ForgeOS(JavaFX)의 Terminal 앱이 "CLI에서 되던 명령이 GUI에서도 똑같이 된다"를
 * 보장하려면 명령어 해석 계층이 한 곳에만 있어야 한다. 같은 파서를 두 벌
 * 유지하면 반드시 어긋나므로, 명령어 레지스트리를 라이브러리로 개방하고
 * 표준 입출력에 묶인 부분({@link forgeframework.cli.shell.ForgeShell})만
 * CLI 전용으로 남겼다.</p>
 *
 * <p>{@code forgeframework}는 {@code requires transitive}다 —
 * {@link forgeframework.cli.command.Command#execute} 시그니처가 커널의
 * {@code Kernel}과 {@code SystemCallResult}를 그대로 노출하기 때문이다.</p>
 */
module forgeframework.cli {
    requires transitive forgeframework;

    /** 명령어 인터페이스와 레지스트리 — GUI 클라이언트가 재사용한다. */
    exports forgeframework.cli.command;

    /** 셸 실행 컨텍스트(cwd)와 프롬프트 렌더러. */
    exports forgeframework.cli.shell;
}
