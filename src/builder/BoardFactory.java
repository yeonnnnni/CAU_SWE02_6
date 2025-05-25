package builder;

import model.Node;

import java.util.List;

/**
 * BoardFactory 클래스는 다양한 형태의 보드(Square, Pentagon, Hexagon)에 대해
 * 적절한 BoardBuilder 인스턴스를 생성하고, 보드에서 시작 노드를 찾는 기능을 제공합니다.
 */
public class BoardFactory {

    /**
     * 주어진 문자열 타입에 따라 적절한 BoardBuilder 구현체를 생성합니다.
     * @param type 보드 형태 문자열 ("square", "pentagon", "hexagon")
     * @return 해당 보드 타입에 맞는 BoardBuilder 인스턴스
     */
    public static BoardBuilder create(String type) {
        switch (type.toLowerCase()) {
            case "square":
                // 사각형 보드 빌더 생성
                return new SquareBoardBuilder();
            case "pentagon":
                // 오각형 보드 빌더 생성
                return new PentagonBoardBuilder();
            case "hexagon":
                // 육각형 보드 빌더 생성
                return new HexagonBoardBuilder();
            default:
                throw new IllegalArgumentException("Unsupported board type: " + type);
        }
    }

    /**
     * 보드에서 게임의 시작 노드를 찾아 반환합니다.
     * @param board 노드 리스트
     * @param boardType 보드 형태 문자열
     * @return 시작 노드 객체
     * @throws IllegalStateException 시작 노드를 찾을 수 없는 경우
     */
    public static Node getStartNode(List<Node> board, String boardType) {
        String startId;

        switch (boardType.toLowerCase()) {
            case "square":
            case "pentagon":
            case "hexagon":
                // 모든 보드 타입에서 시작 노드 ID는 "A2"로 설정
                startId = "A2";  // 향후 필요 시 다르게 설정 가능
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 보드 타입입니다: " + boardType);
        }

        return board.stream()
                .filter(n -> startId.equals(n.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(startId + " 노드를 찾을 수 없습니다."));
    }
}
