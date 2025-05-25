package builder;

import model.Node;

import java.awt.Point;
import java.util.*;

/**
 * 사각형 윷놀이 판을 생성하는 빌더 클래스입니다.
 * 노드 생성, 연결, 위치 설정을 담당합니다.
 */
public class SquareBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    /**
     * 보드 노드를 생성하고 연결하여 리스트로 반환합니다.
     * @return 보드를 나타내는 노드의 리스트
     */
    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 노드의 위치(Point)를 맵 형태로 반환합니다.
     * @return 노드 ID와 Point 위치의 맵
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    /**
     * 주어진 ID의 노드를 반환하며, 없으면 생성합니다.
     * @param id 노드의 ID
     * @return 해당 ID에 해당하는 노드
     */
    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    /**
     * 노드를 생성하고 위치를 설정하여 반환합니다.
     * @param id 노드의 ID
     * @param row 행 좌표
     * @param col 열 좌표
     * @return 생성되거나 기존에 존재하는 노드
     */
    private Node add(String id, int row, int col) {
        Node n = node(id);
        // Reflect across y = x by swapping row and col, and use positive orientation
        positions.put(id, new Point(row * 50, col * 50));
        return n;
    }

    /**
     * 보드 위에 필요한 노드들을 생성합니다.
     */
    private void createNodes() {
        add("00", 3, 3).setCenter(true);

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

    /**
     * 노드들을 서로 연결합니다.
     */
    private void connectNodes() {
        connect("A2", "N0"); connect("N0", "N1"); connect("N1", "N2"); connect("N2", "N3");
        //connect("N3", "N4");
        connect("N4", "N5"); connect("N5", "N6"); connect("N6", "N7");
        //connect("N7", "N8");
        connect("N8", "N9"); connect("N9", "N10"); connect("N10", "N11");
        //connect("N11", "N12");
        connect("N12", "N13"); connect("N13", "N14"); connect("N14", "N15");
        connect("N15", "A2");

        connect("N3", "D2"); connect("D2", "D1"); connect("D1", "D0"); connect("D0", "00");
        connect("N7", "C2"); connect("C2", "C1"); connect("C1", "C0"); connect("C0", "00");
        connect("00", "A0"); connect("A0", "A1"); connect("A1", "A2");

        connect("00", "B0"); connect("B0", "B1"); connect("B1", "B2"); connect("B2", "N12");

        connect("D2", "N4");
        connect("C2", "N8");
        connect("B2", "N11");
    }

    /**
     * 두 노드를 양방향으로 연결합니다.
     * @param fromId 시작 노드의 ID
     * @param toId 끝 노드의 ID
     */
    private void connect(String fromId, String toId) {
        Node from = nodeMap.get(fromId);
        Node to = nodeMap.get(toId);
        if (from != null && to != null) {
            from.addNextNode(to);
            to.addNextNode(from); // 양방향 연결 추가
        }
    }
}
