package builder;

import model.Node;

import java.util.List;

public class BoardFactory {

    public static BoardBuilder create(String type) {
        switch (type.toLowerCase()) {
            case "square":
                return new SquareBoardBuilder();
            case "pentagon":
                return new PentagonBoardBuilder();
            case "hexagon":
                return new HexagonBoardBuilder();
            default:
                throw new IllegalArgumentException("Unsupported board type: " + type);
        }
    }

    public static Node getStartNode(List<Node> board, String boardType) {
        String startId;

        switch (boardType.toLowerCase()) {
            case "square":
            case "pentagon":
            case "hexagon":
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