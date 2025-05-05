package builder;

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
}
