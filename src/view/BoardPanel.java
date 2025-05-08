/*
package view;

import model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardPanel extends JPanel {

    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();
    private final int buttonSize = 50;

    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(700, 700));
    }

    public void initialize(@org.jetbrains.annotations.NotNull String boardType, Node[] nodes) {
        removeAll();
        nodeToButton.clear();
        buttonToNode.clear();

        if (boardType.equalsIgnoreCase("square")) {
            initializeSquareBoard(nodes);
        }

        revalidate();
        repaint();
    }

    private void initializeSquareBoard(Node[] nodes) {
        Point[] positions = new Point[]{
                p(0, 0), p(0, 1), p(0, 2), p(0, 3), p(0, 4), // 상단
                p(1, 4), p(2, 4), p(3, 4), p(4, 4),         // 우측
                p(4, 3), p(4, 2), p(4, 1), p(4, 0),         // 하단
                p(3, 0), p(2, 0), p(1, 0),                 // 좌측
                p(1, 1), p(2, 2), p(3, 3),                 // ↘ 대각선
                p(1, 3), p(3, 1),                         // ↙ 대각선
                p(3, 2), p(1, 2)                          // 중앙 좌우 교차점
        };

        int gridSize = buttonSize + 20;

        for (int i = 0; i < positions.length && i < nodes.length; i++) {
            Point pt = positions[i];
            JButton btn = new JButton(" ");
            btn.setBounds(pt.y * gridSize + 100, pt.x * gridSize + 100, buttonSize, buttonSize);
            btn.setMargin(new Insets(0,0,0,0));
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);

            if (i == 0) btn.setText("start!");
            if (positions[i].equals(p(2, 2))) btn.setText("center");

            add(btn);
            nodeToButton.put(nodes[i], btn);
            buttonToNode.put(btn, nodes[i]);
        }
    }

    private Point p(int x, int y) {
        return new Point(x, y);
    }

    public void updatePiecePosition(Node from, Node to, String pieceText, Color color) {
        if (from != null && nodeToButton.containsKey(from)) {
            JButton fromButton = nodeToButton.get(from);
            fromButton.setText(" ");
            fromButton.setForeground(Color.BLACK);
        }

        if (to != null && nodeToButton.containsKey(to)) {
            JButton toButton = nodeToButton.get(to);
            String existingText = toButton.getText();

            if (existingText.equals(" ") || existingText.isEmpty()) {
                toButton.setText(pieceText);
            } else {
                toButton.setText(existingText + "+" + pieceText);
            }

            toButton.setForeground(color);
        }
    }

    public void resetBoardUI() {
        for (JButton btn : nodeToButton.values()) {
            btn.setText(" ");
            btn.setForeground(Color.BLACK);
        }
    }

    public Map<Node, JButton> getNodeToButtonMap() {
        return nodeToButton;
    }

    public Map<JButton, Node> getButtonToNodeMap() {
        return buttonToNode;
    }
}
*/

//package view;
//
//import model.Node;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class BoardPanel extends JPanel {
//
//    private final Map<Node, JButton> nodeToButton = new HashMap<>();
//    private final Map<JButton, Node> buttonToNode = new HashMap<>();
//    private final int buttonSize = 50;
//
//    public BoardPanel() {
//        setLayout(new GridLayout(5, 5, 10, 10));
//        setPreferredSize(new Dimension(700, 700));
//    }
//
//    public void initialize(String boardType, Node[] nodes) {
//        removeAll();
//        nodeToButton.clear();
//        buttonToNode.clear();
//
//        if (boardType.equalsIgnoreCase("square")) {
//            initializeSquareBoard(nodes);
//        }
//
//        revalidate();
//        repaint();
//    }
//
//    private void initializeSquareBoard(Node[] nodes) {
//        int[] nodeIndices = {
//                0, 1, 2, 3, 4,
//                5, -1, 6, -1, 7,
//                8, 9, 10, 11, 12,
//                13, -1, 14, -1, 15,
//                16, 17, 18, 19, 20
//        };
//
//        for (int i = 0; i < nodeIndices.length; i++) {
//            int nodeIndex = nodeIndices[i];
//
//            if (nodeIndex == -1) {
//                add(new JLabel());
//            } else {
//                JButton btn = new JButton(" ");
//                btn.setPreferredSize(new Dimension(buttonSize, buttonSize));
//                btn.setMargin(new Insets(0,0,0,0));
//                btn.setFont(new Font("Arial", Font.PLAIN, 11));
//                btn.setBackground(Color.WHITE);
//                btn.setForeground(Color.BLACK);
//
//                if (nodeIndex == 0) btn.setText("start!");
//                if (nodeIndex == 10) btn.setText("center");
//
//                add(btn);
//                nodeToButton.put(nodes[nodeIndex], btn);
//                buttonToNode.put(btn, nodes[nodeIndex]);
//            }
//        }
//    }
//
//    public void updatePiecePosition(Node from, Node to, String pieceText, Color color) {
//        if (from != null && nodeToButton.containsKey(from)) {
//            JButton btn = nodeToButton.get(from);
//            btn.setText(" ");
//            btn.setForeground(Color.BLACK);
//        }
//
//        if (to != null && nodeToButton.containsKey(to)) {
//            JButton btn = nodeToButton.get(to);
//            String current = btn.getText();
//
//            if (current.equals(" ") || current.isEmpty()) {
//                btn.setText(pieceText);
//            } else {
//                btn.setText(current + "+" + pieceText);
//            }
//
//            btn.setForeground(color);
//        }
//    }
//
//    public void resetBoardUI() {
//        for (JButton btn : nodeToButton.values()) {
//            btn.setText(" ");
//            btn.setForeground(Color.BLACK);
//        }
//    }
//
//    public Map<Node, JButton> getNodeToButtonMap() {
//        return nodeToButton;
//    }
//
//    public Map<JButton, Node> getButtonToNodeMap() {
//        return buttonToNode;
//    }
//}

package view;

import model.Horse;
import model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {

    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();
    private final int buttonSize = 50;

    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 800));
    }

    /**
     * 전체 노드와 위치 좌표를 받아 렌더링
     */
    public void renderBoard(List<Node> nodes, Map<String, Point> nodePositions) {
        removeAll();
        nodeToButton.clear();
        buttonToNode.clear();

        int gridSize = buttonSize + 20;

        for (Node node : nodes) {
            Point pt = nodePositions.get(node.getId());
            if (pt == null) {
                System.err.println("경고! 위치 정보 누락:"+node.getId());
                continue;
            }

            JButton btn = new JButton();
            btn.setBounds(pt.y * gridSize + 100, pt.x * gridSize + 100, buttonSize, buttonSize);
            btn.setFont(new Font("Arial", Font.BOLD, 9));
            btn.setText(node.getId()); // 디버깅용 텍스트
            btn.setBackground(Color.WHITE);

            add(btn);
            nodeToButton.put(node, btn);
            buttonToNode.put(btn, node);
        }

        revalidate();
        repaint();
        System.out.println("[BoardPanel] 노드 수: " + nodes.size());
    }

    /**
     * 말 이동 시 버튼 UI 업데이트
     */
    public void updatePiecePosition(Node from, Node to, String pieceText, Color color) {
        if (from != null && nodeToButton.containsKey(from)) {
            JButton btn = nodeToButton.get(from);
            btn.setText(from.getId()); // 초기화
            btn.setForeground(Color.BLACK);
        }

        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            List<Horse> horses = to.getHorsesOnNode();  // 말 여러마리 추적

            StringBuilder sb = new StringBuilder();
            sb.append(to.getId()).append("\n");

            for (Horse h : horses) {
                sb.append(h.getId()).append("+");
            }
            if (!horses.isEmpty()) sb.setLength(sb.length() - 1); // 마지막 + 제거

            btn.setText("<html>" + sb.toString().replace("\n", "<br>") + "</html>");
            btn.setForeground(color);  // 말 색상 반영
        }
    }

    /**
     * 모든 버튼 초기화 (새 게임 등)
     */
    public void resetButtons() {
        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
            Node node = entry.getKey();
            JButton btn = entry.getValue();
            btn.setText(node.getId());
            btn.setForeground(Color.BLACK);
        }
    }

    public Map<Node, JButton> getNodeToButtonMap() {
        return nodeToButton;
    }

    public Map<JButton, Node> getButtonToNodeMap() {
        return buttonToNode;
    }
}
