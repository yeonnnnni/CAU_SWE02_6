//package builder;
//
//import builder.BoardBuilder;
//import model.Node;
//
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class HexagonBoardBuilder implements BoardBuilder {
//    private final Node[] nodes = new Node[36];
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public Node[] buildBoard() {
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = new Node("H" + i);
//        }
//        // 연결 로직 생략 (구현 예정)
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        for (int i = 0; i < 36; i++) {
//            positions.put("H" + i, new Point(i % 6, i / 6));
//        }
//        return positions;
//    }
//}

//
//package builder;
//
//import model.Node;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//public class HexagonBoardBuilder implements BoardBuilder {
//    private final List<Node> nodes = new ArrayList<>();
//    private final Map<String, Point> positions = new HashMap<>();
//
//    @Override
//    public List<Node> buildBoard() {
//        nodes.clear();
//        for (int i = 0; i < 30; i++) {
//            nodes.add(new Node("H" + i));
//        }
//        return nodes;
//    }
//
//    @Override
//    public Map<String, Point> getNodePositions() {
//        for (int i = 0; i < 30; i++) {
//            positions.put("H" + i, new Point(i % 5, i / 5));
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

 import model.Node;
 
 import java.awt.Point;
 import java.util.*;
 
 public class HexagonBoardBuilder implements BoardBuilder {
 
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
         node("OO").setCenter(true); // 중심
 
         // 외곽 N0 ~ N23
         for (int i = 0; i < 24; i++) {
             node("N" + i);
         }
 
         // A~F 방향 노드 (0~2)
         for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
             for (int i = 0; i <= 2; i++) {
                 node(dir + "" + i);
             }
         }
 
         // 골지점 지정
         for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
             node(dir + "2").setGoal(true);
         }
     }
 
     private void connectNodes() {
         // 외곽 연결
         for (int i = 0; i < 24; i++) {
             node("N" + i).addNextNode(node("N" + ((i + 1) % 24)));
         }
 
         // 중심과 지름길 연결
         for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
             node(dir + "0").addNextNode(node(dir + "1"));
             node(dir + "1").addNextNode(node(dir + "2"));
             node(dir + "2").addNextNode(node("OO"));
             node("OO").addNextNode(node(dir + "1"));
         }
 
         // 제외된 외곽 연결
         Map<String, String> invalidLinks = Map.of(
                 "F0", "N20",
                 "E0", "N16",
                 "C0", "N8",
                 "B0", "N4"
         );
 
         for (char dir : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
             String end = dir + "0";
             int targetIdx = "ABCDEF".indexOf(dir) * 4;
             String target = "N" + targetIdx;
             if (!invalidLinks.getOrDefault(end, "").equals(target)) {
                 node(end).addNextNode(node(target));
             }
         }
 
         // 분기 연결
         node("F2").addNextNode(node("N3"));
         node("F2").addNextNode(node("N4"));
         node("E2").addNextNode(node("N7"));
         node("E2").addNextNode(node("N8"));
         node("D2").addNextNode(node("N11"));
         node("D2").addNextNode(node("N12"));
         node("C2").addNextNode(node("N15"));
         node("C2").addNextNode(node("N16"));
         node("B2").addNextNode(node("N19"));
         node("B2").addNextNode(node("N20"));
         node("A2").addNextNode(node("N23"));
         node("A2").addNextNode(node("N0"));
     }
 
     private void createPositions() {
         // 회전된 외곽 노드 배치
         double radius = 6.0;
         for (int i = 0; i < 24; i++) {
             double angle = Math.toRadians(90 + 90 - i * (360.0 / 24)); // 반시계 90도 회전
             int x = (int) (Math.cos(angle) * radius * 10);
             int y = (int) (Math.sin(angle) * radius * 10);
             positions.put("N" + i, new Point(x, y));
         }
 
         // 중심
         positions.put("OO", new Point(0, 0));
 
         // 회전 적용된 지름길 방향 설정
         Map<String, Integer> baseAngles = Map.of(
                 "A", 180,
                 "B", 120,
                 "C", 60,
                 "D", 0,
                 "E", -60,
                 "F", -120
         );
 
         for (Map.Entry<String, Integer> entry : baseAngles.entrySet()) {
             String dir = entry.getKey();
             int angleDeg = entry.getValue();
             for (int j = 0; j <= 2; j++) {
                 double dist = 1.5 + j;
                 double angleRad = Math.toRadians(angleDeg);
                 int x = (int) (Math.cos(angleRad) * dist * 10);
                 int y = (int) (Math.sin(angleRad) * dist * 10);
                 positions.put(dir + j, new Point(x, y));
             }
         }
     }
 }
 