//package model;
//
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class SquareBoardBuilder implements BoardBuilder {
//    private final Node[] nodes = new Node[24];
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public Node[] buildBoard() {
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = new Node("N" + i);
//        }
//
//        for (int i = 0; i < 16; i++) {
//            nodes[i].addNextNode(nodes[i + 1]);
//        }
//
//        nodes[16].addNextNode(nodes[17]);
//        nodes[17].setCenter(true);
//
//        nodes[17].addNextNode(nodes[18]);
//        nodes[17].addNextNode(nodes[19]);
//        nodes[18].addNextNode(nodes[20]);
//        nodes[19].addNextNode(nodes[21]);
//        nodes[20].addNextNode(nodes[22]);
//        nodes[21].addNextNode(nodes[23]);
//
//        nodes[23].setGoal(true);
//
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        positions.put("N0", new Point(4, 4));
//        positions.put("N1", new Point(3, 4));
//        positions.put("N2", new Point(2, 4));
//        positions.put("N3", new Point(1, 4));
//        positions.put("N4", new Point(0, 4));
//        positions.put("N5", new Point(0, 3));
//        positions.put("N6", new Point(0, 2));
//        positions.put("N7", new Point(0, 1));
//        positions.put("N8", new Point(0, 0));
//        positions.put("N9", new Point(1, 0));
//        positions.put("N10", new Point(2, 0));
//        positions.put("N11", new Point(3, 0));
//        positions.put("N12", new Point(4, 0));
//        positions.put("N13", new Point(4, 1));
//        positions.put("N14", new Point(4, 2));
//        positions.put("N15", new Point(4, 3));
//        positions.put("N16", new Point(4, 4));
//        positions.put("N17", new Point(2, 2));
//        positions.put("N18", new Point(1, 3));
//        positions.put("N19", new Point(3, 1));
//        positions.put("N20", new Point(0, 0));
//        positions.put("N21", new Point(4, 0));
//        positions.put("N22", new Point(0, 4));
//        positions.put("N23", new Point(4, 4));
//        return positions;
//    }
//}





//package builder;
//
//import builder.BoardBuilder;
//import model.Node;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//public class SquareBoardBuilder implements BoardBuilder {
//
//    private final List<Node> nodes = new ArrayList<>();
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public List<Node> buildBoard() {
//        nodes.clear();
//        positions.clear();
//
//        // Center node
//        Node center = new Node("OO");
//        center.setCenter(true);
//        nodes.add(center);
//
//        // Outer nodes (N0 ~ N16) counter-clockwise from A3 top
//        String[] outerIds = {
//                "N0", "N1", "N2", "N3", "N4",
//                "N5", "N6", "N7", "N8", "N9",
//                "N10", "N11", "N12", "N13", "N14", "N15", "N16"
//        };
//        for (String id : outerIds) {
//            nodes.add(new Node(id));
//        }
//
//        // Diagonal paths A0~A2, B1~B3, C1~C3, D1~D3
//        for (int i = 0; i < 3; i++) nodes.add(new Node("A" + i)); // Bottom-right
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("B" + i)); // Bottom-left
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("C" + i)); // Top-left
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("D" + i)); // Top-right
//
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        // Coordinates in 7x7 grid
//        positions.put("OO", p(3, 3));
//
//        // Outer loop - counterclockwise from bottom-right upward
//        String[] outerIds = {
//                "N0", "N1", "N2", "N3", "N4", "N5", "N6", "N7", "N8",
//                "N9", "N10", "N11", "N12", "N13", "N14", "N15", "N16"
//        };
//        Point[] outerPts = {
//                p(6, 6), p(5, 6), p(4, 6), p(3, 6), p(2, 6),
//                p(1, 6), p(0, 6), p(0, 5), p(0, 4), p(0, 3),
//                p(0, 2), p(0, 1), p(0, 0), p(1, 0), p(2, 0),
//                p(3, 0), p(4, 0)
//        };
//        for (int i = 0; i < outerIds.length && i < outerPts.length; i++) {
//            positions.put(outerIds[i], outerPts[i]);
//        }
//
//        // Diagonal paths - example layout (adjusted manually)
//        positions.put("A0", p(4, 4));
//        positions.put("A1", p(5, 5));
//        positions.put("A2", p(6, 6));
//
//        positions.put("B1", p(5, 1));
//        positions.put("B2", p(6, 2));
//        positions.put("B3", p(6, 3));
//
//        positions.put("C1", p(1, 1));
//        positions.put("C2", p(0, 2));
//        positions.put("C3", p(0, 3));
//
//        positions.put("D1", p(1, 5));
//        positions.put("D2", p(0, 4));
//        positions.put("D3", p(0, 3));
//
//        return positions;
//    }
//
//    private Point p(int x, int y) {
//        return new Point(x * 50 + 100, y * 50 + 100);
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
//public class SquareBoardBuilder implements BoardBuilder {
//
//    private final List<Node> nodes = new ArrayList<>();
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public List<Node> buildBoard() {
//        nodes.clear();
//        positions.clear();
//
//        Node center = new Node("OO");
//        center.setCenter(true);
//        nodes.add(center);
//
//        String[] outerIds = {
//                "N0", "N1", "N2", "N3", "N4", "N5", "N6", "N7", "N8",
//                "N9", "N10", "N11", "N12", "N13", "N14", "N15", "N16"
//        };
//        for (String id : outerIds) {
//            nodes.add(new Node(id));
//        }
//
//        for (int i = 0; i < 3; i++) nodes.add(new Node("A" + i));
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("B" + i));
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("C" + i));
//        for (int i = 1; i <= 3; i++) nodes.add(new Node("D" + i));
//
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        positions.put("OO", p(3, 3));
//        String[] outerIds = {
//                "N0", "N1", "N2", "N3", "N4", "N5", "N6", "N7", "N8",
//                "N9", "N10", "N11", "N12", "N13", "N14", "N15", "N16"
//        };
//        Point[] outerPts = {
//                p(6, 6), p(5, 6), p(4, 6), p(3, 6), p(2, 6),
//                p(1, 6), p(0, 6), p(0, 5), p(0, 4), p(0, 3),
//                p(0, 2), p(0, 1), p(0, 0), p(1, 0), p(2, 0),
//                p(3, 0), p(4, 0)
//        };
//        for (int i = 0; i < outerIds.length && i < outerPts.length; i++) {
//            positions.put(outerIds[i], outerPts[i]);
//        }
//        positions.put("A0", p(4, 4)); positions.put("A1", p(5, 5)); positions.put("A2", p(6, 6));
//        positions.put("B1", p(5, 1)); positions.put("B2", p(6, 2)); positions.put("B3", p(6, 3));
//        positions.put("C1", p(1, 1)); positions.put("C2", p(0, 2)); positions.put("C3", p(0, 3));
//        positions.put("D1", p(1, 5)); positions.put("D2", p(0, 4)); positions.put("D3", p(0, 3));
//
//        return positions;
//    }
//
//    private Point p(int x, int y) {
//        return new Point(x * 50 + 100, y * 50 + 100);
//    }
//}


package builder;

import model.Node;

import java.awt.Point;
import java.util.*;

public class SquareBoardBuilder implements BoardBuilder {

    private final Map<String, Node> nodeMap = new LinkedHashMap<>();
    private final Map<String, Point> positions = new HashMap<>();

    @Override
    public List<Node> buildBoard() {
        createNodes();
        connectNodes();
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public Map<String, Point> getNodePositions() {
        return positions;
    }

    // ✅ nodeMap에 중복 없이 저장
    private Node node(String id) {
        return nodeMap.computeIfAbsent(id, Node::new);
    }

    // ✅ 좌표와 함께 Node 등록
    private Node add(String id, int row, int col) {
        Node n = node(id); // 중복 방지
        positions.put(id, new Point(col, row));
        return n;
    }

    private void createNodes() {
        // Center
        add("OO", 3, 3).setCenter(true);

        // Diagonal (pink shortcut) nodes
        add("A0", 4, 4); add("A1", 5, 5); add("A2", 6, 6); add("A3", 7, 7);
        add("B1", 5, 1); add("B2", 6, 0); add("B3", 7, 0);
        add("C1", 1, 1); add("C2", 0, 0); add("C3", 0, 0); // 위치는 나중에 수정해도 됨
        add("D1", 1, 5); add("D2", 0, 6); add("D3", 0, 7);

        // Outer nodes (N0 ~ N16)
        String[] outerOrder = {
                "N0", "N1", "N2", "N3", "N4",
                "N5", "N6", "N7", "N8", "N9",
                "N10", "N11", "N12", "N13", "N14", "N15", "N16"
        };
        int[][] outerPos = {
                {6,7}, {5,7}, {4,7}, {3,7}, {2,7},
                {1,7}, {0,6}, {0,5}, {0,4}, {0,3},
                {0,2}, {0,1}, {0,0}, {1,0}, {2,0},
                {3,0}, {4,0}
        };
        for (int i = 0; i < outerOrder.length; i++) {
            add(outerOrder[i], outerPos[i][0], outerPos[i][1]);
        }

        // Goal points
        node("A3").setGoal(true);
        node("B3").setGoal(true);
        node("C3").setGoal(true);
        node("D3").setGoal(true);
    }

    private void connectNodes() {
        // Outer path (counter-clockwise)
        connect("N0", "N1");
        connect("N1", "N2");
        connect("N2", "N3");
        connect("N3", "N4");
        connect("N4", "N5");
        connect("N5", "N6");
        connect("N6", "N7");
        connect("N7", "N8");
        connect("N8", "N9");
        connect("N9", "N10");
        connect("N10", "N11");
        connect("N11", "N12");
        connect("N12", "N13");
        connect("N13", "N14");
        connect("N14", "N15");
        connect("N15", "N16");
        connect("N16", "N0");

        // Shortcuts
        connect("A0", "A1");
        connect("A1", "A2");
        connect("A2", "A3");

        connect("B1", "B2");
        connect("B2", "B3");

        connect("C1", "C2");
        connect("C2", "C3");

        connect("D1", "D2");
        connect("D2", "D3");

        // Entry into shortcuts
        connect("N0", "A0");
        connect("N4", "D1");
        connect("N8", "C1");
        connect("N12", "B1");

        // Center cross
        connect("B3", "OO");
        connect("C3", "OO");
        connect("D3", "OO");
        connect("A3", "OO");

        // From center to 4 directions
        connect("OO", "A0");
        connect("OO", "B1");
        connect("OO", "C1");
        connect("OO", "D1");
    }

    private void connect(String fromId, String toId) {
        Node from = nodeMap.get(fromId);
        Node to = nodeMap.get(toId);
        if (from != null && to != null) {
            from.addNextNode(to);
        }
    }
}
