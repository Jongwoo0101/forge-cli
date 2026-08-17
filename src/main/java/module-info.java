/**
 * ForgeCLI — ForgeFramework 커널의 명령줄 클라이언트.
 *
 * <p>커널 모듈에 <b>단방향으로만</b> 의존한다. 이 모듈이 사라져도 커널은 아무
 * 영향을 받지 않으며, 실제로 릴리즈되는 {@code forgeframework} jar에는
 * 셸도 명령어도 포함되지 않는다.</p>
 *
 * <p>이 모듈은 라이브러리가 아니라 실행 가능한 데모 애플리케이션이므로
 * 아무 패키지도 export하지 않는다.</p>
 */
module forgeframework.cli {
    requires forgeframework;
}
