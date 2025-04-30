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

    public void initialize(String boardType, Node[] nodes) {
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
            JButton btn = nodeToButton.get(from);
            btn.setText(" ");
            btn.setForeground(Color.BLACK);
        }

        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            String current = btn.getText();

            if (current.equals(" ") || current.isEmpty()) {
                btn.setText(pieceText);
            } else {
                btn.setText(current + "+" + pieceText);
            }

            btn.setForeground(color);
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
