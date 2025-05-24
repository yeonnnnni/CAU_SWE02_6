package builder;

import builder.BoardBuilder;
import model.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * PentagonBoardBuilder는 오각형 형태의 윷놀이 보드를 구성하는 클래스입니다.
 * BoardBuilder 인터페이스를 구현하며, 노드 생성, 연결, 좌표 배치를 담당합니다.
 */
public class PentagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    // 노드 ID를 기준으로 중복 없이 생성하거나 기존 노드 반환
    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    /**
     * 보드를 구성하는 전체 노드를 생성하고 연결한 후 리스트로 반환합니다.
     */
    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        createPositions();
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 각 노드의 화면상 위치 정보를 반환합니다.
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    /**
     * 두 노드를 양방향으로 연결합니다.
     */
    private void connect(String fromId, String toId) {
        Node from = node(fromId);
        Node to = node(toId);
        from.addNextNode(to);
        to.addNextNode(from);
    }

    /**
     * 중심, 지선, 외곽 노드를 생성합니다.
     */
    private void createNodes() {
        node("00").setCenter(true); // 중심 노드

        // A0~E2 방향 노드 생성 (지선)
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            for (int i = 0; i <= 2; i++) {
                node("" + dir + i);
            }
        }

        // 외곽 노드 N0 ~ N19 생성
        for (int i = 0; i < 20; i++) {
            node("N" + i);
        }

        node("A2").setGoal(true); // 도착 노드
    }

    /**
     * 노드 간 연결 관계를 설정합니다.
     * 지름길, 외곽 루프, 분기 경로 포함.
     */
    private void connectNodes() {
        // 중심과 지선 노드 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            connect(dir + "0", "00");
            connect(dir + "0", dir + "1");
            connect(dir + "1", dir + "2");
        }

        // 외곽 순환 연결 (A2 → N19 → ... → N0 → A2)
        connect("A2", "N19");
        for (int i = 19; i > 0; i--) {
            if (i!=16 && i!=12 && i!=8 && i!=4) {
                connect("N" + i, "N" + (i - 1));
            }
        }
        connect("N0", "A2");

        // 각 지선 끝 노드에서 외곽으로 분기 연결
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
     * 노드의 화면 좌표를 설정합니다.
     * 오각형 꼭짓점을 기준으로 위치를 선형 보간하여 배치합니다.
     */
    private void createPositions() {
        double outerR = 6.0;
        positions.put("00", new Point(0, 0));

        // 오각형 꼭짓점 방향 설정 (반시계 방향)
        int[] angles = {-162, -90, -18, 54, 126}; // A~E
        String[] dirs = {"A", "B", "C", "D", "E"};

        // 중심에서 방향별 지선 좌표 계산
        for (int i = 0; i < dirs.length; i++) {
            double angle = Math.toRadians(angles[i]);
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            double[] radii = {2, 4, 6.0};  // 거리 증가
            for (int j = 0; j <= 2; j++) {
                double r = radii[j];
                int x = (int)(dx * r * 40);
                int y = (int)(dy * r * 40);
                positions.put(dirs[i] + j, new Point(x, -y));
            }
        }

        // 오각형 꼭짓점 위치 저장
        Point[] vertices = new Point[5];
        int[] vertexAngles = {-162, -90, -18, 54, 126};
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(vertexAngles[i]);
            int x = (int)(Math.cos(angle) * outerR * 40);
            int y = (int)(Math.sin(angle) * outerR * 40);
            vertices[i] = new Point(x, -y); // Java 좌표계 기준
        }

        // 꼭짓점 사이 4개씩 외곽 노드 배치 (총 20개)
        int idx = 0;
        int[][] connectionOrder = {
            {0, 4}, {4, 3}, {3, 2}, {2, 1}, {1, 0}
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

        // Y좌표 반전 보정
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            positions.put(entry.getKey(), new Point(p.x, -p.y));
        }
    }
}