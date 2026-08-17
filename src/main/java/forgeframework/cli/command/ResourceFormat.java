package forgeframework.cli.command;

import java.util.List;

/**
 * 자원 벡터를 콘솔 표에 맞춰 문자열로 렌더링하는 표현 계층 전용 유틸리티.
 *
 * <p>{@code res_info} / {@code res_req} / {@code detect} / {@code recover}가
 * 모두 같은 폭의 벡터를 찍어야 표가 세로로 정렬되므로, 포맷 규칙을 한 곳에
 * 모아 중복을 없앴다. 커널이 아니라 {@code command} 패키지에 두는 이유는
 * 명확하다 — 이건 전적으로 CLI의 표현 문제이며, GUI 클라이언트는 같은 DTO로
 * 전혀 다른 그림을 그릴 것이기 때문이다.</p>
 */
final class ResourceFormat {

    /** 벡터 원소 하나가 차지하는 칸 너비. */
    private static final int CELL_WIDTH = 3;

    private ResourceFormat() {
        // 인스턴스화 방지
    }

    /**
     * 정수 벡터를 {@code "[ 10   5   7]"} 형태로 렌더링한다.
     *
     * @param vector 렌더링할 벡터
     * @return 고정 폭으로 정렬된 문자열
     */
    static String vector(List<Integer> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int value : vector) {
            sb.append(String.format("%" + CELL_WIDTH + "d", value));
        }
        return sb.append(']').toString();
    }

    /**
     * 자원 이름 목록을 벡터와 같은 폭의 헤더로 렌더링한다.
     *
     * @param labels 자원 이름 목록
     * @return {@code "[ R1 R2 R3]"} 형태의 헤더
     */
    static String header(List<String> labels) {
        StringBuilder sb = new StringBuilder("[");
        for (String label : labels) {
            sb.append(String.format("%" + CELL_WIDTH + "s", label));
        }
        return sb.append(']').toString();
    }
}
