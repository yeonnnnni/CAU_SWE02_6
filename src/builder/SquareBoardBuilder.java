 package builder;

 import model.Node;
 
 import java.awt.Point;
 import java.util.*;
 
/**
 * SquareBoardBuilder는 사각형 형태의 윷놀이 보드를 생성하는 클래스입니다.
 * 노드를 생성하고, 노드 간 연결을 설정하며, 각 노드의 좌표를 정의합니다.
 */
public class SquareBoardBuilder implements BoardBuilder {
 
     private final Map<String, Node> nodeMap = new LinkedHashMap<>();
     private final Map<String, Point> positions = new HashMap<>();
 
    /**
     * 보드를 구성하는 모든 노드를 생성하고 연결하여 완성된 노드 리스트를 반환합니다.
     * @return 윷놀이 보드의 모든 노드 리스트
     */
    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        return new ArrayList<>(nodeMap.values());
    }
 
    /**
     * 각 노드의 좌표 정보를 반환합니다.
     * @return 노드 ID와 그에 대응하는 좌표의 맵
     */
    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }
 
    /**
     * 주어진 ID에 해당하는 노드를 반환합니다. 존재하지 않으면 새로 생성합니다.
     * @param id 노드의 고유 식별자
     * @return 해당 ID의 노드 객체
     */
    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }
 
    /**
     * 주어진 ID와 좌표 정보를 바탕으로 노드를 생성(또는 반환)하고, 좌표 맵에 추가합니다.
     * @param id 노드의 고유 식별자
     * @param row 행 좌표
     * @param col 열 좌표
     * @return 생성된 또는 기존 노드 객체
     */
    private Node add(String id, int row, int col) {
        Node n = node(id);
        // Reflect across y = x by swapping row and col, and use positive orientation
        positions.put(id, new Point(row * 50, col * 50));
        return n;
    }

    /**
     * 윷놀이 보드에 필요한 모든 노드를 생성하고 위치를 지정합니다.
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
     * 생성된 노드들 간의 연결 관계를 설정합니다.
     */
    private void connectNodes() {
         connect("A2", "N0"); connect("N0", "N1"); connect("N1", "N2"); connect("N2", "N3");
         connect("N4", "N5"); connect("N5", "N6"); connect("N6", "N7");
         connect("N8", "N9"); connect("N9", "N10"); connect("N10", "N11");
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
     * 두 노드 ID를 받아 해당 노드들을 서로 연결합니다(양방향).
     * @param fromId 시작 노드의 ID
     * @param toId 연결할 대상 노드의 ID
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