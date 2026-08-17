package forgeframework.cli.shell;

/**
 * Shell이 소유하는 상태를 담는 컨테이너.
 *
 * <p>Kernel은 무상태(stateless)를 유지해야 API 서버(Web/GUI)로 재사용하기
 * 쉬워진다. 그래서 현재 작업 디렉터리(CWD) 같은 "세션 상태"는 Kernel이나
 * FileSystemManager가 아니라 여기, Shell 계층에 둔다. CD/PWD/파일시스템
 * 명령어들은 생성자로 이 객체를 주입받아 CWD를 읽고(쓰기는 CdCommand만) 사용한다
 * — {@link forgeframework.cli.command.HelpCommand}가
 * {@link forgeframework.cli.command.CommandRegistry}를 주입받는 것과 동일한 패턴이다.</p>
 *
 * <p><b>[Phase 6]</b> 기존에는 두 링크가 단순 클래스명이라 {@code shell} 패키지에서
 * 해석되지 않아 Javadoc 생성이 에러로 실패했다. 1.0 산출물에 API 문서가 포함되므로
 * 완전 수식명으로 고쳤다.</p>
 */
public final class ShellContext {

    private String currentWorkingDirectory = "/";

    public String getCwd() {
        return currentWorkingDirectory;
    }

    public void setCwd(String cwd) {
        this.currentWorkingDirectory = cwd;
    }
}
