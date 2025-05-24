package builder;

import model.Node;

import java.util.List;

/**
 * BoardFactory 클래스는 다양한 보드 형태(Square, Pentagon, Hexagon)에 따라
 * 적절한 BoardBuilder 인스턴스를 생성하는 팩토리 클래스입니다.
 * 또한, 보드에서 시작 노드를 찾아 반환하는 기능도 제공합니다.
 */
public class BoardFactory {

    /**
     * 주어진 문자열 타입에 따라 알맞은 BoardBuilder 인스턴스를 생성합니다.
     *
     * @param type 보드 타입 (예: "square", "pentagon", "hexagon")
     * @return 해당 타입에 맞는 BoardBuilder 객체
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우 예외 발생
     */
    public static BoardBuilder create(String type) {
        switch (type.toLowerCase()) {
            case "square":
                return new SquareBoardBuilder();  // 사각형 보드 생성기 반환
            case "pentagon":
                return new PentagonBoardBuilder();  // 오각형 보드 생성기 반환
            case "hexagon":
                return new HexagonBoardBuilder();  // 육각형 보드 생성기 반환
            default:
                throw new IllegalArgumentException("Unsupported board type: " + type);
        }
    }

    /**
     * 보드에서 시작 노드를 찾아 반환합니다.
     * 현재는 보드 타입에 관계없이 "A2" 노드를 시작점으로 간주합니다.
     *
     * @param board 노드 리스트
     * @param boardType 보드 타입 문자열
     * @return 시작 노드 객체
     * @throws IllegalArgumentException 지원하지 않는 보드 타입인 경우
     * @throws IllegalStateException 시작 노드를 찾을 수 없는 경우
     */
    public static Node getStartNode(List<Node> board, String boardType) {
        String startId;

        switch (boardType.toLowerCase()) {
            case "square":
            case "pentagon":
            case "hexagon":
                startId = "A2";  // 모든 보드에서 공통적으로 사용하는 시작 노드 ID
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 보드 타입입니다: " + boardType);
        }

        return board.stream()
                .filter(n -> startId.equals(n.getId()))  // 노드 ID가 "A2"인 노드를 필터링
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(startId + " 노드를 찾을 수 없습니다."));
    }
}