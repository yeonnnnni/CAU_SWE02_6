//package builder;
//
//import builder.BoardBuilder;
//import model.Node;
//
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class PentagonBoardBuilder implements BoardBuilder {
//    private final Node[] nodes = new Node[30];
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public Node[] buildBoard() {
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = new Node("P" + i);
//        }
//        // 연결 로직 생략 (구현 예정)
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        for (int i = 0; i < 30; i++) {
//            positions.put("P" + i, new Point(i % 5, i / 5));
//        }
//        return positions;
//    }
//}



//package builder;
//
//import model.Node;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//public class PentagonBoardBuilder implements BoardBuilder {
//    private final List<Node> nodes = new ArrayList<>();
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public List<Node> buildBoard() {
//        nodes.clear();
//        for (int i = 0; i < 30; i++) {
//            nodes.add(new Node("P" + i));
//        }
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        for (int i = 0; i < 30; i++) {
//            positions.put("P" + i, new Point(i % 5, i / 5));
//        }
//        return positions;
//    }
//}


package builder;

import model.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PentagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        createPositions(); // ⬅️ 필수 호출
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    private void createNodes() {
        // 외곽 노드 N0 ~ N19
        for (int i = 0; i <= 19; i++) {
            node("N" + i);
        }

        // 중앙
        node("OO").setCenter(true);

        // 지름길 및 코너 노드
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            for (int i = 0; i <= 3; i++) {
                node(""+dir + i);
            }
        }

        // 골지점
        node("A3").setGoal(true);
        node("B3").setGoal(true);
        node("C3").setGoal(true);
        node("D3").setGoal(true);
        node("E3").setGoal(true);
    }

    private void connectNodes() {
        // 외곽 시계반대 방향 순환
        for (int i = 0; i < 19; i++) {
            node("N" + i).addNextNode(node("N" + (i + 1)));
        }
        node("N19").addNextNode(node("N0"));  // 순환

        // 지름길 방향 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            node("N" + switch (dir) {
                case 'A' -> "0";
                case 'B' -> "4";
                case 'C' -> "8";
                case 'D' -> "12";
                case 'E' -> "16";
                default -> throw new IllegalStateException("Unexpected dir: " + dir);
            }).addNextNode(node(dir + "0"));

            node(dir + "0").addNextNode(node(dir + "1"));
            node(dir + "1").addNextNode(node(dir + "2"));
            node(dir + "2").addNextNode(node(dir + "3"));
        }

        // 중심 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            node(dir + "2").addNextNode(node("OO"));  // 중간에서 중심으로
            node("OO").addNextNode(node(dir + "1"));  // 중심에서 지름길로
        }
    }

    private void createPositions() {
        // 원형 배치 기준 외곽 노드 위치
        double radius = 5.0;
        for (int i = 0; i < 20; i++) {
            double angle = 2 * Math.PI * i / 20;
            int x = (int) (radius * Math.cos(angle) * 10 + 10);
            int y = (int) (radius * Math.sin(angle) * 10 + 10);
            positions.put("N" + i, new Point(y, x));
        }

        // 센터
        positions.put("OO", new Point(10, 10));

        // 방향별 지름길 노드 위치 (대략적 좌표)
        positions.put("A0", new Point(13, 7));
        positions.put("A1", new Point(12, 8));
        positions.put("A2", new Point(11, 9));
        positions.put("A3", new Point(10, 11));

        positions.put("B0", new Point(7, 6));
        positions.put("B1", new Point(8, 7));
        positions.put("B2", new Point(9, 8));
        positions.put("B3", new Point(11, 8));

        positions.put("C0", new Point(6, 10));
        positions.put("C1", new Point(7, 10));
        positions.put("C2", new Point(8, 10));
        positions.put("C3", new Point(9, 10));

        positions.put("D0", new Point(7, 13));
        positions.put("D1", new Point(8, 12));
        positions.put("D2", new Point(9, 11));
        positions.put("D3", new Point(11, 11));

        positions.put("E0", new Point(13, 13));
        positions.put("E1", new Point(12, 12));
        positions.put("E2", new Point(11, 11));
        positions.put("E3", new Point(10, 10));
    }
}
