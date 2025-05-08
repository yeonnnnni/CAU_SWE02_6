package builder;

import builder.BoardBuilder;
import model.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PentagonBoardBuilder implements BoardBuilder {

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

    private void connect(String fromId, String toId) {
        Node from = node(fromId);
        Node to = node(toId);
        from.addNextNode(to);
        to.addNextNode(from);
    }

    private void createNodes() {
        node("OO").setCenter(true);

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            for (int i = 0; i <= 2; i++) {
                node("" + dir + i);
            }
        }

        for (int i = 0; i < 20; i++) {
            node("N" + i);
        }

        node("A2").setGoal(true);
        
    }

    private void connectNodes() {
        // 지름길 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            connect(dir + "2", dir + "1");
            connect(dir + "1", dir + "0");
            connect(dir + "0", "OO");
            connect("OO", dir + "1");
        }

        // 외곽 연결 (A2 → N0 → ... → N19 → A2)
        connect("A2", "N0");
        for (int i = 0; i < 19; i++) {
            connect("N" + i, "N" + (i + 1));
        }
        connect("N19", "A2");

        // 분기 연결
        connect("E2", "N3");
        connect("E2", "N4");
        connect("D2", "N7");
        connect("D2", "N8");
        connect("C2", "N11");
        connect("C2", "N12");
        connect("B2", "N15");
        connect("B2", "N16");
    }

    private void createPositions() {
        double radius = 6.0;
        double centerX = 0.0;
        double centerY = 0.0;

        // 외곽 노드 배치 (반시계)
        for (int i = 0; i < 20; i++) {
            double angle = Math.toRadians(90 - i * (360.0 / 20) + 72); // rotate 72 degrees
            int x = (int) (Math.cos(angle) * radius * 10);
            int y = (int) (Math.sin(angle) * radius * 10);
            positions.put("N" + i, new Point(x, y));
        }

        // 중심
        positions.put("OO", new Point(0, 0));

        // 시계방향으로 방향 설정
        int[] angles = {90, 18, -54, -126, -198};
        String[] dirs = {"A", "B", "C", "D", "E"};
        for (int i = 0; i < dirs.length; i++) {
            double base = radius - 0.5;
            for (int j = 2; j >= 0; j--) {
                double angle = Math.toRadians(angles[i] + 72);
                double r = base - j;
                int x = (int) (Math.cos(angle) * r * 10);
                int y = (int) (Math.sin(angle) * r * 10);
                positions.put(dirs[i] + j, new Point(x, y));
            }
        }
    }
}
