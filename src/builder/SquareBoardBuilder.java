package builder;

import model.Node;

import java.awt.Point;
import java.util.*;

public class SquareBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    private Node add(String id, int row, int col) {
        Node n = node(id);
        // Reflect across y = x by swapping row and col, and use positive orientation
        positions.put(id, new Point(row * 50, col * 50));
        return n;
    }

    private void createNodes() {
        add("OO", 3, 3).setCenter(true);

        add("A0", 4, 4); add("A1", 5, 5); add("A2", 6, 6);
        add("D0", 4, 2); add("D1", 5, 1); add("D2", 6, 0);
        add("C0", 2, 2); add("C1", 1, 1); add("C2", 0, 0);
        add("B0", 2, 4); add("B1", 1, 5); add("B2", 0, 6);

        String[] outerIds = {
                "N0", "N1", "N2", "N3", "N4", "N5", "N6", "N7",
                "N8", "N9", "N10", "N11", "N12", "N13", "N14", "N15"
        };
        int[][] outerCoords = {
                {6, 5}, {6, 4}, {6, 3}, {6, 2},
                {5, 0}, {4, 0}, {3, 0}, {2, 0},
                {0, 1}, {0, 2}, {0, 3}, {0, 4},
                {1, 6}, {2, 6}, {3, 6}, {4, 6}
        };
        for (int i = 0; i < outerIds.length; i++) {
            add(outerIds[i], outerCoords[i][0], outerCoords[i][1]);
        }

        node("A2").setGoal(true);

    }

    private void connectNodes() {
        connect("A2", "N0"); connect("N0", "N1"); connect("N1", "N2"); connect("N2", "N3");
        connect("N3", "N4"); connect("N4", "N5"); connect("N5", "N6"); connect("N6", "N7");
        connect("N7", "N8"); connect("N8", "N9"); connect("N9", "N10"); connect("N10", "N11");
        connect("N11", "N12"); connect("N12", "N13"); connect("N13", "N14"); connect("N14", "N15");
        connect("N15", "A2");

        connect("N3", "D2"); connect("D2", "D1"); connect("D1", "D0"); connect("D0", "OO");
        connect("N7", "C2"); connect("C2", "C1"); connect("C1", "C0"); connect("C0", "OO");
        connect("OO", "A0"); connect("A0", "A1"); connect("A1", "A2");

        connect("OO", "B0"); connect("B0", "B1"); connect("B1", "B2"); connect("B2", "N12");

        connect("D2", "N4");
        connect("C2", "N8");
        connect("B2", "N11");
    }

    private void connect(String fromId, String toId) {
        Node from = nodeMap.get(fromId);
        Node to = nodeMap.get(toId);
        if (from != null && to != null) {
            from.addNextNode(to);
            to.addNextNode(from); // 양방향 연결 추가
        }
    }
}