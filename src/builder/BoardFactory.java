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

    public static Node getStartNode(List<Node> board) {
        return board.stream()
                    .filter(node-> "A2".equals(node.getId())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("No start node with 'A2' found on the board."));
    }
}
