package view;

import model.Horse;
import model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {

    //노드와 버튼을 매핑하는 맵
    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();
    private final int buttonSize = 50; //각 노드 버튼의 크기

    private BufferedImage backgroundImage;  // 배경 이미지
    private String boardType = "square";    // 현재 보드 타입 저장

    //생성자:레이아웃을 절대위치로 설정하고 패널 크기를 고정함
    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 800));
    }

    /**
     * 보드 타입을 설정하고 그에 맞는 배경 이미지를 로딩함
     * @param boardType 선택한 보드 타입 문자열
     */
    public void setBoardType(String boardType) {
        this.boardType = boardType;
        loadBackgroundImage();
    }

    /**
     * boardType에 따라 리소스 경로에서 배경 이미지를 불러옵니다
     */
    private void loadBackgroundImage() {
        try {
            switch (boardType.toLowerCase()) {
                case "square":
                    backgroundImage = ImageIO.read(getClass().getResource("/square_board).png"));
                    break;
                case "pentagon":
                    backgroundImage = ImageIO.read(getClass().getResource("/pentagon_board.png"));
                    break;
                case "hexagon":
                    backgroundImage = ImageIO.read(getClass().getResource("/hexagon_board.png"));
                    break;
                default:
                    backgroundImage = null;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("⚠️ 배경 이미지 로딩 실패: " + e.getMessage());
            backgroundImage = null;
        }
    }

    /**
     * 배경 이미지를 직접 그리고 버튼 위에 표시함
     * 이미지 크기: 350x350
     * 위치: 중심 기준 -100px 위쪽
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 노드 중심 기준 (7x7, 50px spacing → 350x350)
            int imageSize = 350;
            int drawX = getWidth() / 2 - imageSize / 2;
            int drawY = getHeight() / 2 - imageSize / 2 - 100;

            g.drawImage(backgroundImage, drawX, drawY, imageSize, imageSize, this);
        }
    }

    /**
     * 보드 노드(Button)들을 생성하고 배치
     * @param nodes         생성된 노드 리스트
     * @param nodePositions 각 노드 ID에 대한 좌표 정보
     * @param boardType     현재 선택된 보드 타입
     */
    public void renderBoard(List<Node> nodes, Map<String, Point> nodePositions, String boardType) {
        setBoardType(boardType); // 🆕 이미지와 타입 설정
        removeAll();
        revalidate();
        nodeToButton.clear();
        buttonToNode.clear();

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
            sb.append(to.getId()).append("<br>");

            boolean hasVisibleHorse = false;

            for (Horse h : horses) {
                if (h.isFinished()) continue;
                hasVisibleHorse = true;
                sb.append(h.toString2()).append("<br>");
            }

            if (sb.toString().endsWith("<br>")) {
                sb.setLength(sb.length() - 4);
            }

            btn.setText("<html><center>" + sb + "</center></html>");

            if (hasVisibleHorse) {
                btn.setForeground(color);
            } else {
                btn.setForeground(Color.BLACK);
            }
        }
    }

    //모든 버튼을 초기 텍스트 및 색상으로 되돌림
    public void resetButtons() {
        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
            Node node = entry.getKey();
            JButton btn = entry.getValue();
            btn.setText(node.getId());
            btn.setForeground(Color.BLACK);
        }
    }

    //getter
    public Map<Node, JButton> getNodeToButtonMap() {
        return nodeToButton;
    }

    public Map<JButton, Node> getButtonToNodeMap() {
        return buttonToNode;
    }

    /**
     * 보드 리셋 시 모든 말 표시 제거 (텍스트 원상복귀)
     */
    public void resetBoardUI() {
        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
            Node node = entry.getKey();
            JButton btn = entry.getValue();
            btn.setText(node.getId());
            btn.setForeground(Color.BLACK);
        }
    }
}
