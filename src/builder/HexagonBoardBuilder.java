package builder;

import model.Node;

import java.awt.Point;
import java.util.*;

/**
 * HexagonBoardBuilder는 육각형 형태의 윷놀이 보드를 구성하는 클래스입니다.
 * BoardBuilder 인터페이스를 구현하여, 노드 생성, 연결, 좌표 배치를 수행합니다.
 */
public class HexagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    /**
     * 보드를 구성하는 노드들을 생성하고 연결하며, 노드들의 위치를 설정합니다.
     * 최종적으로 생성된 노드 리스트를 반환합니다.
     *
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
     * 노드들의 좌표 정보를 포함하는 맵을 반환합니다.
     *
     * @return 노드 ID와 위치를 매핑한 맵
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    /**
     * 보드에 필요한 노드들을 생성하고, 특정 노드에 중심 및 목표 설정을 합니다.
     */
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

        node("A2").setGoal(true);  // A2만 goal
    }

    /**
     * 생성된 노드들을 서로 연결하여 보드의 경로를 구성합니다.
     */
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

    /**
     * 노드들의 실제 위치 좌표를 계산하여 positions 맵에 저장합니다.
     * 육각형 모양의 보드 구조에 맞게 좌표를 배치합니다.
     */
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

    /**
     * 두 노드를 양방향으로 연결합니다.
     *
     * @param fromId 연결 시작 노드 ID
     * @param toId 연결 대상 노드 ID
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
