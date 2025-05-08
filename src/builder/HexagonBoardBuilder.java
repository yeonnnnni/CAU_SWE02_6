package builder;

import model.Node;
import java.awt.Point;
import java.util.*;

public class HexagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        createPositions();
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    private void createNodes() {
        node("OO").setCenter(true);

        for (int i = 0; i < 24; i++) {
            node("N" + i);
        }

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            for (int i = 0; i <= 2; i++) {
                node("" + dir + i);
            }
        }

        node("A2").setGoal(true);  // ✅ A2만 goal
    }

    private void connectNodes() {
        for (int i = 0; i < 24; i++) {
            connect("N" + i, "N" + ((i + 1) % 24));
        }

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            connect(dir + "0", dir + "1");
            connect(dir + "1", dir + "2");
            connect(dir + "2", "OO");
            connect("OO", dir + "1");
        }

        Map<String, String> invalidLinks = Map.of(
            "F0", "N20", "E0", "N16", "C0", "N8", "B0", "N4"
        );

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            String end = dir + "0";
            int targetIdx = "ABCDEF".indexOf(dir) * 4;
            String target = "N" + targetIdx;
            if (!invalidLinks.getOrDefault(end, "").equals(target)) {
                connect(end, target);
            }
        }

        connect("F2", "N3"); connect("F2", "N4");
        connect("E2", "N7"); connect("E2", "N8");
        connect("D2", "N11"); connect("D2", "N12");
        connect("C2", "N15"); connect("C2", "N16");
        connect("B2", "N19"); connect("B2", "N20");
        connect("A2", "N23"); connect("A2", "N0");
    }

    private void createPositions() {
        double radius = 6.0;
        for (int i = 0; i < 24; i++) {
            double angle = Math.toRadians(180 - i * (360.0 / 24));
            int x = (int) (Math.cos(angle) * radius * 10);
            int y = (int) (Math.sin(angle) * radius * 10);
            positions.put("N" + i, new Point(x, y));
        }

        positions.put("OO", new Point(0, 0));

        Map<String, Integer> baseAngles = Map.of(
            "A", 180, "B", 120, "C", 60, "D", 0, "E", -60, "F", -120
        );

        for (Map.Entry<String, Integer> entry : baseAngles.entrySet()) {
            String dir = entry.getKey();
            int angleDeg = entry.getValue();
            for (int j = 0; j <= 2; j++) {
                double dist = 1.5 + j;
                double angleRad = Math.toRadians(angleDeg);
                int x = (int) (Math.cos(angleRad) * dist * 10);
                int y = (int) (Math.sin(angleRad) * dist * 10);
                positions.put(dir + j, new Point(x, y));
            }
        }
    }

    // ✅ 양방향 연결 메서드
    private void connect(String fromId, String toId) {
        Node from = nodeMap.get(fromId);
        Node to = nodeMap.get(toId);
        if (from != null && to != null) {
            from.addNextNode(to);
            to.addNextNode(from);
        }
    }
}