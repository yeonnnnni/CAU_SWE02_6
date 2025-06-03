package builder;

import model.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * PentagonBoardBuilder는 오각형 형태의 윷놀이 보드를 구성하는 클래스입니다.
 * BoardBuilder 인터페이스를 구현하며, 노드 생성, 노드 간 연결, 좌표 배치를 담당합니다.
 */
public class PentagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    /**
     * 보드의 노드들을 생성하고 연결하며, 각 노드의 위치를 설정하여 보드를 구성합니다.
     * @return 생성된 노드들의 리스트
     */
    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        createPositions();
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 노드들의 좌표 정보를 반환합니다.
     * @return 노드 ID와 위치를 매핑한 맵
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    /**
     * 두 노드를 상호 연결합니다.
     * @param fromId 출발 노드 ID
     * @param toId 도착 노드 ID
     */
    private void connect(String fromId, String toId) {
        Node from = node(fromId);
        Node to = node(toId);
        from.addNextNode(to);
        to.addNextNode(from);
    }

    /**
     * 보드에 사용될 노드들을 생성하고 초기 설정을 합니다.
     */
    private void createNodes() {
        node("00").setCenter(true);

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

    /**
     * 생성된 노드들 간의 연결 관계를 설정합니다.
     */
    private void connectNodes() {
        // 지름길 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            connect(dir + "0", "00");
            connect(dir + "0", dir + "1");
            connect(dir + "1", dir + "2");
        }

        // 외곽 연결 (A2 → N19 → ... → N0 → A2)
        connect("A2", "N19");
        for (int i = 19; i > 0; i--) {
            if (i!=16 && i!=12 && i!=8 && i!=4) {
                connect("N" + i, "N" + (i - 1));
            }
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

    /**
     * 각 노드의 화면 내 위치를 계산하여 설정합니다.
     */
    private void createPositions() {
        double outerR = 6.0;  // 바깥 반지름
        double step = 1.0;    // 안쪽 말까지 거리 간격
        positions.put("00", new Point(0, 0));

        // A2, B2, ..., E2가 꼭짓점이 되도록 각도 지정 (반시계 방향)
        int[] angles = {-162, -90, -18, 54, 126}; // A, B, C, D, E
        String[] dirs = {"A", "B", "C", "D", "E"};

        for (int i = 0; i < dirs.length; i++) {
            double angle = Math.toRadians(angles[i]);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            double[] radii = {2, 4, 6.0};  // A0 ~ A2: progressively farther from center
            for (int j = 0; j <= 2; j++) {
                double r = radii[j];
                int x = (int)(dx * r * 40);
                int y = (int)(dy * r * 40);
                positions.put(dirs[i] + j, new Point(x, -y));
            }
        }

        // 외곽 N0~N19: 정오각형 꼭짓점을 기준으로 선형 분포
        Point[] vertices = new Point[5];
        int[] vertexAngles = {-162, -90, -18, 54, 126};

        // 꼭짓점 위치 저장 (A2 ~ E2와 동일)
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(vertexAngles[i]);
            int x = (int)(Math.cos(angle) * outerR * 40);
            int y = (int)(Math.sin(angle) * outerR * 40);
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

        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            positions.put(entry.getKey(), new Point(p.x, -p.y));
        }
    }
}
