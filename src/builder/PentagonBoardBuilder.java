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

/*
 * package builder;

import model.Node;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class HexagonBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        createPositions(); // 🧠 위치 설정 반드시 호출!
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    private void createNodes() {
        node("OO").setCenter(true);  // 중심

        // 외곽 N0 ~ N23
        for (int i = 0; i <= 23; i++) {
            node("N" + i);
        }

        // A~F 방향 노드 (0~3)
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            for (int i = 0; i <= 3; i++) {
                node(""+dir + i);
            }
        }

        // 골지점 지정
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            node(dir + "3").setGoal(true);
        }
    }

    private void connectNodes() {
        // 외곽 연결
        for (int i = 0; i < 23; i++) {
            node("N" + i).addNextNode(node("N" + (i + 1)));
        }
        node("N23").addNextNode(node("N0")); // 순환

        // 방향별 지름길
        connectPath("A", "N0");
        connectPath("B", "N4");
        connectPath("C", "N8");
        connectPath("D", "N12");
        connectPath("E", "N16");
        connectPath("F", "N20");

        // 중심 ↔ 방향별 지름길 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
            node("OO").addNextNode(node(dir + "1"));
            node(dir + "2").addNextNode(node("OO"));
        }
    }

    private void connectPath(String dir, String startId) {
        node(startId).addNextNode(node(dir + "0"));
        node(dir + "0").addNextNode(node(dir + "1"));
        node(dir + "1").addNextNode(node(dir + "2"));
        node(dir + "2").addNextNode(node(dir + "3"));
    }

    // 반드시 buildBoard()에서 호출해야 적용됨
    private void createPositions() {
        // 외곽 24개 노드 (시계 기준 위치)
        double radius = 6.0;
        for (int i = 0; i < 24; i++) {
            double angle = 2 * Math.PI * i / 24;
            int x = (int) (radius * Math.cos(angle) * 10 + 12);
            int y = (int) (radius * Math.sin(angle) * 10 + 12);
            positions.put("N" + i, new Point(y, x));
        }

        positions.put("OO", new Point(12, 12)); // 센터

        // 방향별 지름길 (적당한 좌표 배치)
        positions.put("A0", new Point(13, 12));
        positions.put("A1", new Point(14, 12));
        positions.put("A2", new Point(15, 12));
        positions.put("A3", new Point(16, 12));

        positions.put("B0", new Point(14, 10));
        positions.put("B1", new Point(15, 9));
        positions.put("B2", new Point(16, 8));
        positions.put("B3", new Point(17, 7));

        positions.put("C0", new Point(13, 8));
        positions.put("C1", new Point(14, 7));
        positions.put("C2", new Point(15, 6));
        positions.put("C3", new Point(16, 5));

        positions.put("D0", new Point(11, 7));
        positions.put("D1", new Point(10, 6));
        positions.put("D2", new Point(9, 5));
        positions.put("D3", new Point(8, 4));

        positions.put("E0", new Point(9, 10));
        positions.put("E1", new Point(8, 9));
        positions.put("E2", new Point(7, 8));
        positions.put("E3", new Point(6, 7));

        positions.put("F0", new Point(10, 13));
        positions.put("F1", new Point(9, 14));
        positions.put("F2", new Point(8, 15));
        positions.put("F3", new Point(7, 16));
    }
}
 */
package builder;

import builder.BoardBuilder;
import model.Node;

import java.awt.*;
import java.util.*;

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

        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            node(dir + "2").setGoal(true);
        }
    }

    private void connectNodes() {
        // 지름길 연결
        for (char dir : new char[]{'A', 'B', 'C', 'D', 'E'}) {
            node(dir + "2").addNextNode(node(dir + "1"));
            node(dir + "1").addNextNode(node(dir + "0"));
            node(dir + "0").addNextNode(node("OO"));
            node("OO").addNextNode(node(dir + "1"));
        }

        // 외곽 연결 (A2 → N0 → ... → N19 → A2)
        node("A2").addNextNode(node("N0"));
        for (int i = 0; i < 19; i++) {
            node("N" + i).addNextNode(node("N" + (i + 1)));
        }
        node("N19").addNextNode(node("A2"));

        // 분기 연결
        node("E2").addNextNode(node("N3"));
        node("E2").addNextNode(node("N4"));
        node("D2").addNextNode(node("N7"));
        node("D2").addNextNode(node("N8"));
        node("C2").addNextNode(node("N11"));
        node("C2").addNextNode(node("N12"));
        node("B2").addNextNode(node("N15"));
        node("B2").addNextNode(node("N16"));
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
