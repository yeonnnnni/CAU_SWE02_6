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
            connect(dir + "0", "OO");
            connect(dir + "0", dir + "1");
            connect(dir + "1", dir + "2");
        }

        // 외곽 연결 (A2 → N19 → ... → N0 → A2)
        connect("A2", "N19");
        for (int i = 19; i > 0; i--) {
            connect("N" + i, "N" + (i - 1));
        }
        connect("N0", "A2");

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
        double outerR = 6.0;  // 바깥 반지름
        double step = 1.0;    // 안쪽 말까지 거리 간격
        positions.put("OO", new Point(0, 0));

        // A2, B2, ..., E2가 꼭짓점이 되도록 각도 지정 (반시계 방향)
        int[] angles = {-162, -90, -18, 54, 126}; // A, B, C, D, E
        String[] dirs = {"A", "B", "C", "D", "E"};

        for (int i = 0; i < dirs.length; i++) {
            for (int j = 0; j <= 2; j++) {
                double r = outerR - (2 - j) * step;
                double angle = Math.toRadians(angles[i]);
                int x = (int) (Math.cos(angle) * r * 10);
                int y = (int) (Math.sin(angle) * r * 10);
                // Java 좌표계 y축 반전
                positions.put(dirs[i] + j, new Point(x, -y));
            }
        }

        // 외곽 N0~N19: 정오각형 꼭짓점을 기준으로 선형 분포
        Point[] vertices = new Point[5];
        int[] vertexAngles = {-162, -90, -18, 54, 126};

        // 꼭짓점 위치 저장 (A2 ~ E2와 동일)
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(vertexAngles[i]);
            int x = (int)(Math.cos(angle) * outerR * 10);
            int y = (int)(Math.sin(angle) * outerR * 10);
            vertices[i] = new Point(x, -y);  // Java 좌표계 반영
        }

        // 외곽 노드 20개를 지정된 순서로 배치 (A2 → N0, N1, N2, N3 → E2 → N4, N5, N6, N7 → D2 → N8, N9, N10, N11 → C2 → N12, N13, N14, N15 → B2 → N16, N17, N18, N19 → A2)
        int idx = 0;
        int[][] connectionOrder = {
            {0, 4}, // A2 → E2
            {4, 3}, // E2 → D2
            {3, 2}, // D2 → C2
            {2, 1}, // C2 → B2
            {1, 0}  // B2 → A2
        };

        for (int[] pair : connectionOrder) {
            Point start = vertices[pair[0]];
            Point end = vertices[pair[1]];
            for (int j = 1; j <= 4; j++) {
                double t = j / 5.0;
                int x = (int)((1 - t) * start.x + t * end.x);
                int y = (int)((1 - t) * start.y + t * end.y);
                positions.put("N" + idx++, new Point(x, y));
            }
        }
    }
}