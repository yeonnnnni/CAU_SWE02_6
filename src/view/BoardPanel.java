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

        switch (boardType.toLowerCase()) {
            case "square":
                initializeSquareBoard(nodes);
                break;
            case "pentagon":
                initializePentagonBoard(nodes);
                break;
            case "hexagon":
                initializeHexagonBoard(nodes);
                break;
            default:
                System.err.println("알 수 없는 판 유형: " + boardType);
        }

        revalidate();
        repaint();
    }

    private void initializeSquareBoard(Node[] nodes) {
        Point[] positions = new Point[]{
                // 외곽 (시계방향)
                p(0, 0), p(0, 1), p(0, 2), p(0, 3), p(0, 4),
                p(1, 4), p(2, 4), p(3, 4), p(4, 4),
                p(4, 3), p(4, 2), p(4, 1), p(4, 0),
                p(3, 0), p(2, 0), p(1, 0),
                // ↘ 대각선
                p(1, 1), p(2, 2), p(3, 3),
                // ↙ 대각선
                p(1, 3), p(3, 1),
                // 중심
                p(2, 2)
        };

        int gridSize = buttonSize + 20;

        for (int i = 0; i < positions.length && i < nodes.length; i++) {
            Point pt = positions[i];
            JButton btn = new JButton(" ");
            btn.setBounds(pt.y * gridSize + 100, pt.x * gridSize + 100, buttonSize, buttonSize);

            // ✅ 폰트 크기 작게 설정
            btn.setFont(new Font("Arial", Font.PLAIN, 11));

            // 텍스트 설정
            if (i == 0) {
                btn.setText("출발");
            } else if (positions[i].equals(p(2, 2))) {
                btn.setText("중앙");
            }

            add(btn);
            nodeToButton.put(nodes[i], btn);
            buttonToNode.put(btn, nodes[i]);
        }
    }


    // 오각형/육각형용 메서드는 추후 정의
    private void initializePentagonBoard(Node[] nodes) {
        // TODO: 좌표 정의
        System.err.println("오각형 판은 아직 구현되지 않았습니다.");
    }

    private void initializeHexagonBoard(Node[] nodes) {
        // TODO: 좌표 정의
        System.err.println("육각형 판은 아직 구현되지 않았습니다.");
    }

    private Point p(int x, int y) {
        return new Point(x, y);
    }

    public void updatePiecePosition(Node from, Node to, String pieceText, Color color) {
        if (from != null && nodeToButton.containsKey(from)) {
            nodeToButton.get(from).setText(" ");
            nodeToButton.get(from).setForeground(Color.BLACK);
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
