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
        node("00").setCenter(true);

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
            if (i!=3 && i!=7 && i!=11 && i!=15 && i!=19 && i!=23) {
                connect("N" + i, "N" + (i + 1));
            }
        }

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            connect(dir + "0", dir + "1");
            connect(dir + "1", dir + "2");
            connect("00", dir + "0");
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

        positions.put("00", new Point(0, 0));

        Map<String, Integer> baseAngles = Map.of(
            "A", 180, "B", 120, "C", 60, "D", 0, "E", -60, "F", -120
        );

        for (Map.Entry<String, Integer> entry : baseAngles.entrySet()) {
            String dir = entry.getKey();
            int angleDeg = entry.getValue();
            for (int j = 0; j <= 2; j++) {
                double dist = 1.5 + j;
                double angleRad = Math.toRadians(angleDeg);
                int x = (int) (Math.cos(angleRad) * dist * 80);
                int y = (int) (Math.sin(angleRad) * dist * 80);
                positions.put(dir + j, new Point(x, y));
            }
        }

        // Define hexagon corners (A2, B2, ..., F2) counter-clockwise
        String[] hexCorners = {"A2", "F2", "E2", "D2", "C2", "B2"};
        List<Point> cornerPoints = new ArrayList<>();
        for (String dir : hexCorners) {
            cornerPoints.add(positions.get(dir));
        }

        // Interpolate 4 nodes between each corner (6 corners)
        int nIdx = 0;
        for (int i = 0; i < 6; i++) {
            Point start = cornerPoints.get(i);
            Point end = cornerPoints.get((i + 1) % 6);
            for (int j = 1; j <= 4; j++) {
                double t = j / 5.0;
                int x = (int)((1 - t) * start.x + t * end.x);
                int y = (int)((1 - t) * start.y + t * end.y);
                positions.put("N" + nIdx++, new Point(x, y));
            }
        }

        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            entry.setValue(new Point(p.x, -(p.y + 100)));
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