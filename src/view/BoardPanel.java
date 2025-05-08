package view;

import model.Horse;
import model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BoardPanel 클래스는 게임판을 시각적으로 구성하며,
 * 각 Node에 해당하는 JButton을 위치 좌표와 함께 표시합니다.
 * 말의 이동에 따라 버튼 텍스트와 색상을 동적으로 업데이트합니다.
 */
public class BoardPanel extends JPanel {

    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();
    private final int buttonSize = 50;

    /**
     * 생성자: 절대 위치 배치 및 패널 기본 크기 설정
     */
    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 800));
    }

    /**
     * 노드와 좌표 정보를 받아 버튼 생성 및 배치
     * @param nodes 노드 리스트
     * @param nodePositions 노드 ID별 위치 정보 (Point)
     */
    public void renderBoard(List<Node> nodes, Map<String, Point> nodePositions) {
        removeAll();
        revalidate();
        nodeToButton.clear();
        buttonToNode.clear();

        int gridSize = buttonSize + 20;

        // Calculate bounding box and center offset
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (Point pt : nodePositions.values()) {
            if (pt.x < minX) minX = pt.x;
            if (pt.x > maxX) maxX = pt.x;
            if (pt.y < minY) minY = pt.y;
            if (pt.y > maxY) maxY = pt.y;
        }

        int offsetX = (minX + maxX) / 2;
        int offsetY = (minY + maxY) / 2;

        Dimension size = getPreferredSize();
        int panelWidth = size.width;
        int panelHeight = size.height;

        for (Node node : nodes) {
            Point pt = nodePositions.get(node.getId());
            if (pt == null) {
                System.err.println("[BoardPanel] 경고: 위치 정보 없음 - " + node.getId());
                continue;
            }

            JButton btn = new JButton();
            btn.setBounds(pt.x - offsetX + panelWidth / 2 - buttonSize / 2,
                    pt.y - offsetY + panelHeight / 2 - buttonSize / 2 - 100,
                    buttonSize, buttonSize);
            btn.setFont(new Font("Arial", Font.BOLD, 8));
            btn.setText(node.getId());
            btn.setBackground(Color.WHITE);

            add(btn);
            nodeToButton.put(node, btn);
            buttonToNode.put(btn, node);
        }

        revalidate();
        repaint();
        System.out.println("[BoardPanel] 총 노드 수: " + nodes.size());
    }

    /**
     * 특정 말의 이동에 따른 버튼 텍스트 및 색상 업데이트
     * @param from 이전 위치 노드
     * @param to 새로운 위치 노드
     * @param pieceText 표시할 말 텍스트
     * @param color 말의 색상
     */
    public void updatePiecePosition(Node from, Node to, String pieceText, Color color) {
        if (from != null && nodeToButton.containsKey(from)) {
            JButton btn = nodeToButton.get(from);
            btn.setText(from.getId());
            btn.setForeground(Color.BLACK);
        }

        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            List<Horse> horses = to.getHorsesOnNode();

            StringBuilder sb = new StringBuilder();
            sb.append(to.getId()).append("\n");
            for (Horse h : horses) {
                sb.append(h.getId()).append("<br>");
            }
            if (!horses.isEmpty()) sb.setLength(sb.length() - 1);  // 마지막 + 제거

            btn.setText("<html><center>"+sb.toString()+"</center></html>");
            //btn.setText("<html><center>" + sb.toString().replace("\n", "<br>") + "</html>");
            btn.setForeground(color);
        }
    }

    /**
     * 보드 초기화 시 모든 버튼을 기본 상태로 되돌림
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

    // 말 위치 초기화 (버튼 텍스트 초기화)
    public void resetBoardUI() {
        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
            Node node = entry.getKey();
            JButton btn = entry.getValue();
            btn.setText(node.getId()); // 또는 공백으로 초기화하려면: btn.setText(" ");
            btn.setForeground(Color.BLACK);
        }
    }

}