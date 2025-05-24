package builder;

import model.Node;
import java.awt.Point;
import java.util.*;

/**
 * HexagonBoardBuilder는 육각형 형태의 윷놀이 보드를 생성하는 클래스입니다.
 * BoardBuilder 인터페이스를 구현하며, 노드 생성, 연결, 좌표 설정을 담당합니다.
 */
public class HexagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    // 노드 ID를 기반으로 Node 객체를 생성하거나 반환
    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    /**
     * 보드를 구성할 노드들을 생성하고 연결한 후 반환합니다.
     */
    @Override
    public List<Node> buildBoard() {
        createNodes();     // 노드 생성
        connectNodes();    // 노드 연결
        createPositions(); // 노드 좌표 설정
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 노드들의 화면 좌표 정보를 반환합니다.
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    /**
     * 보드에서 사용할 노드들을 생성합니다.
     * 중심 노드, 외곽 노드(N0~N23), 방향별 노드(A0~F2) 포함.
     */
    private void createNodes() {
        node("00").setCenter(true); // 중심 노드

        // 외곽 노드 N0 ~ N23 생성
        for (int i = 0; i < 24; i++) {
            node("N" + i);
        }

        // 각 방향 A~F의 지선 노드 0~2 생성
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            for (int i = 0; i <= 2; i++) {
                node("" + dir + i);
            }
        }

        node("A2").setGoal(true);  // A2를 도착 지점으로 설정
    }

    /**
     * 노드 간 연결 관계를 설정합니다.
     * 외곽 순환, 중심과 지선 연결, 지선에서 외곽으로 분기 연결 포함.
     */
    private void connectNodes() {
        // 외곽 노드 순환 연결 (일부 분기점 제외)
        for (int i = 0; i < 24; i++) {
            // 3,7,11,15,19,23 번 노드는 분기점이므로 순환 연결에서 제외
            if (i!=3 && i!=7 && i!=11 && i!=15 && i!=19 && i!=23) {
                connect("N" + i, "N" + (i + 1));
            }
        }

        // 중심(00)과 각 방향 지선 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            connect(dir + "0", dir + "1"); // 지선 0-1 연결
            connect(dir + "1", dir + "2"); // 지선 1-2 연결
            connect("00", dir + "0");      // 중심과 지선 0 연결
        }

        // 각 지선 끝 노드(A2~F2)와 외곽 노드 연결 (분기점)
        connect("F2", "N3"); connect("F2", "N4");
        connect("E2", "N7"); connect("E2", "N8");
        connect("D2", "N11"); connect("D2", "N12");
        connect("C2", "N15"); connect("C2", "N16");
        connect("B2", "N19"); connect("B2", "N20");
        connect("A2", "N23"); connect("A2", "N0");
    }

    /**
     * 각 노드의 좌표를 설정합니다.
     * 방향별 지선 노드, 외곽 노드는 육각형을 기준으로 균형 있게 배치.
     */
    private void createPositions() {
        double radius = 6.0;

        positions.put("00", new Point(0, 0)); // 중심 좌표

        // 각 방향 A~F의 기준 각도 설정 (시계 반대 방향)
        Map<String, Integer> baseAngles = Map.of(
            "A", 180, "B", 120, "C", 60, "D", 0, "E", -60, "F", -120
        );

        // 방향별 지선 노드 좌표 배치
        for (Map.Entry<String, Integer> entry : baseAngles.entrySet()) {
            String dir = entry.getKey();
            int angleDeg = entry.getValue();
            for (int j = 0; j <= 2; j++) {
                double dist = 1.5 + j;
                double angleRad = Math.toRadians(angleDeg);
                int x = (int) (Math.cos(angleRad) * dist * 80);
                int y = (int) (Math.sin(angleRad) * dist * 80);
                positions.put(dir + j, new Point(x, y)); // 각 지선 노드의 좌표 지정
            }
        }

        // 육각형 외곽의 꼭짓점 위치 (A2 ~ B2)
        String[] hexCorners = {"A2", "F2", "E2", "D2", "C2", "B2"};
        List<Point> cornerPoints = new ArrayList<>();
        for (String dir : hexCorners) {
            cornerPoints.add(positions.get(dir));
        }

        // 꼭짓점 사이에 외곽 노드(N0~N23)를 선형 보간하여 배치
        int nIdx = 0;
        for (int i = 0; i < 6; i++) {
            Point start = cornerPoints.get(i);
            Point end = cornerPoints.get((i + 1) % 6);
            for (int j = 1; j <= 4; j++) {
                double t = j / 5.0;
                int x = (int)((1 - t) * start.x + t * end.x);
                int y = (int)((1 - t) * start.y + t * end.y);
                positions.put("N" + nIdx++, new Point(x, y)); // 외곽 노드 좌표 배치
            }
        }

        // 전체 좌표를 아래로 이동 (뷰 기준 Y 축 반전)
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            entry.setValue(new Point(p.x, -(p.y + 100)));
        }
    }

    /**
     * 두 노드를 양방향으로 연결합니다.
     *
     * @param fromId 시작 노드 ID
     * @param toId 도착 노드 ID
     */
    private void connect(String fromId, String toId) {
        Node from = nodeMap.get(fromId);
        Node to = nodeMap.get(toId);
        if (from != null && to != null) {
            from.addNextNode(to);
            to.addNextNode(from);
        }
    }
}