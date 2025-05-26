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
    private final Map<String, ImageIcon> horseIcons = new HashMap<>(); // ✨ 말 이미지 아이콘 저장용

    //생성자:레이아웃을 절대위치로 설정하고 패널 크기를 고정함
    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 800));
        loadHorseIcons(); // ✨ 말 아이콘 미리 로드
    }

    private void loadHorseIcons() {
        String[] colors = {"blue", "green", "red", "yellow", "pink"};
        for (String color : colors) {
            for (int i = 0; i <= 4; i++) {
                String key = color + "_h" + i;
                try {
                    ImageIcon raw = new ImageIcon(getClass().getResource("/horses/" + key + ".png"));
                    Image scaled = raw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // ✅ 크기 조정
                    horseIcons.put(key, new ImageIcon(scaled));
                } catch (Exception e) {
                    System.err.println("❌ 아이콘 로딩 실패: " + key);
                }
            }
        }
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
                    backgroundImage = ImageIO.read(getClass().getResource("/square_good.png"));
                    break;
                case "pentagon":
                    backgroundImage = ImageIO.read(getClass().getResource("/pentagon.png"));
                    break;
                case "hexagon":
                    backgroundImage = ImageIO.read(getClass().getResource("/hexagon.png"));
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
     * 이미지 크기:
     * square:350x350, pentagon:480x480, hexagon:960x960
     * 위치: 중심 기준 -100px 위쪽
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int drawWidth, drawHeight;

            switch (boardType.toLowerCase()) {
                case "square":
                    drawWidth = drawHeight = 350;
                    break;
                case "pentagon":
                    drawWidth = 600;
                    drawHeight = 580;
                    break;
                case "hexagon":
                    drawWidth = 700;
                    drawHeight = 600; // ⬅ 위아래 줄이기
                    break;
                default:
                    drawWidth = drawHeight = 350;
            }
            int drawX = getWidth() / 2 - drawWidth / 2 - 15;
            int drawY = getHeight() / 2 - drawHeight / 2+50;

            g.drawImage(backgroundImage, drawX, drawY, drawWidth, drawHeight, this);
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
            btn.setText("");
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setLayout(null); // ✨ 아이콘 수동 배치용
            //btn.setBackground(Color.WHITE);

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
            btn.removeAll();
            btn.setText("");
            btn.revalidate();  // 🔥 추가
            btn.repaint();     // 🔥 추가
        }

        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            btn.removeAll();

            List<Horse> horses = to.getHorsesOnNode();
            JPanel panel = new JPanel(new GridLayout(3, 2, 0, 0)); // 최대 6개 정렬
            panel.setOpaque(false);
            panel.setBounds(0, 0, buttonSize, buttonSize);

            for (Horse h : horses) {
                if (h.isFinished()) continue;

                int horseIdx = Integer.parseInt(h.getId().split("-H")[1]);
                String colorKey = getColorKey(h.getTeamColor());
                String iconKey = colorKey + "_h" + horseIdx;
                ImageIcon icon = horseIcons.get(iconKey);

                if (icon != null) {
                    Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    JLabel label = new JLabel(new ImageIcon(scaled));
                    panel.add(label);
                }
            }

            btn.add(panel);
            btn.revalidate();
            btn.repaint();
        }

//        if (from != null && nodeToButton.containsKey(from)) {
//            JButton btn = nodeToButton.get(from);
//            //btn.setText(from.getId());
//            btn.removeAll();
//            btn.setText("");
//            //btn.setForeground(Color.BLACK);
//        }
//
//        if (to != null && nodeToButton.containsKey(to)) {
//            JButton btn = nodeToButton.get(to);
//            List<Horse> horses = to.getHorsesOnNode();
//
//            StringBuilder sb = new StringBuilder();
//            sb.append(to.getId()).append("<br>");
//
//            boolean hasVisibleHorse = false;
//
//            for (Horse h : horses) {
//                if (h.isFinished()) continue;
//                hasVisibleHorse = true;
//                sb.append(h.toString2()).append("<br>");
//            }
//
//            if (sb.toString().endsWith("<br>")) {
//                sb.setLength(sb.length() - 4);
//            }
//
//            btn.setText("<html><center>" + sb + "</center></html>");
//
//            if (hasVisibleHorse) {
//                btn.setForeground(color);
//            } else {
//                btn.setForeground(Color.BLACK);
//            }
//        }
    }

    private String getColorKey(Color color) {
        if (Color.BLUE.equals(color)) return "blue";
        if (Color.RED.equals(color)) return "red";
        if (Color.GREEN.equals(color)) return "green";
        if (Color.YELLOW.equals(color)) return "yellow";
        if (Color.PINK.equals(color)) return "pink";
        return "unknown";
    }

    //모든 버튼을 초기 텍스트 및 색상으로 되돌림
    public void resetButtons() {
        for (JButton btn : nodeToButton.values()) {
            btn.setText("");
            btn.removeAll();
        }
    }
//    public void resetButtons() {
//        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
//            //Node node = entry.getKey();
//            JButton btn = entry.getValue();
//            //btn.setText(node.getId());
//            btn.setText("");
//            btn.removeAll();
//            //btn.setForeground(Color.BLACK);
//        }
//    }

    /**
     * 보드 리셋 시 모든 말 표시 제거 (텍스트 원상복귀)
     */
    public void resetBoardUI() {
        for (JButton btn : nodeToButton.values()) {
            btn.setText("");
            btn.removeAll();
        }
    }

//    public void resetBoardUI() {
//        for (Map.Entry<Node, JButton> entry : nodeToButton.entrySet()) {
//            Node node = entry.getKey();
//            JButton btn = entry.getValue();
//            //btn.setText(node.getId());
//            btn.setText("");
//            btn.setForeground(Color.BLACK);
//        }
//    }

    //getter
    public Map<Node, JButton> getNodeToButtonMap() {
        return nodeToButton;
    }

    public Map<JButton, Node> getButtonToNodeMap() {
        return buttonToNode;
    }
}
