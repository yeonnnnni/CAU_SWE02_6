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
            // 1. 보드 타입별 이미지 크기 지정
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

            // 2. 보드 타입별 위치 보정 오프셋 설정
            Map<String, Point> offsetMap = new HashMap<>();
            offsetMap.put("square", new Point(+125, -75));
            offsetMap.put("pentagon", new Point(+100, -80));
            offsetMap.put("hexagon", new Point(+70, -50));
            Point offsetAdjust = offsetMap.getOrDefault(boardType.toLowerCase(), new Point(0, 0));
//            int drawX = getWidth() / 2 - drawWidth / 2 - 15;
//            int drawY = getHeight() / 2 - drawHeight / 2+50;
            // 3. 이미지 그리기 좌표 계산 (버튼 기준과 정확히 일치)
            int panelWidth = getPreferredSize().width;
            int panelHeight = getPreferredSize().height;

            int drawX = panelWidth / 2 - drawWidth / 2 + offsetAdjust.x;
            int drawY = panelHeight / 2 - drawHeight / 2 + offsetAdjust.y;

            // 4. 배경 이미지 그리기
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

        // 1. 오프셋 테이블 정의 (보드 타입별 버튼 위치 조정값)
        Map<String, Point> offsetMap = new HashMap<>();
        offsetMap.put("square", new Point(+125, -75));
        offsetMap.put("pentagon", new Point(+100, -80));
        offsetMap.put("hexagon", new Point(+70, -50));
        Point offsetAdjust = offsetMap.getOrDefault(boardType.toLowerCase(), new Point(0, 0));

        // 2. 중심 정렬용 좌표 계산
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

        // 3. 각 노드에 버튼 배치
        for (Node node : nodes) {
            Point pt = nodePositions.get(node.getId());
            if (pt == null) {
                System.err.println("[BoardPanel] 경고: 위치 정보 없음 - " + node.getId());
                continue;
            }

            JButton btn = new JButton();
            int drawX = pt.x - offsetX + panelWidth / 2 - buttonSize / 2 + offsetAdjust.x;
            int drawY = pt.y - offsetY + panelHeight / 2 - buttonSize / 2 + offsetAdjust.y;
            btn.setBounds(drawX, drawY, buttonSize, buttonSize);
//            btn.setBounds(pt.x - offsetX + panelWidth / 2 - buttonSize / 2,
//                    pt.y - offsetY + panelHeight / 2 - buttonSize / 2 - 100,
//                    buttonSize, buttonSize);
            btn.setFont(new Font("Arial", Font.BOLD, 8));
            //btn.setText(node.getId());
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

    public void updatePiecePosition(Node from, Node to) {

        //출발 위치 처리
        if (from != null && nodeToButton.containsKey(from)) {
            JButton btn = nodeToButton.get(from);
            btn.removeAll();         // 기존 아이콘 제거
            btn.setText("");         // 텍스트도 초기화
            btn.revalidate();        // UI 갱신
            btn.repaint();           // 다시 그림
        }

        //도착 위치 처리
        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            // 버튼 위 내용 싹 지움
            btn.removeAll();
            // 해당 위치에 있는 말들
            List<Horse> horses = to.getHorsesOnNode();
            JPanel panel = new JPanel(new GridLayout(3, 2, 0, 0)); // 최대 6개 정렬
            panel.setOpaque(false);     // 배경 투명
            panel.setBounds(0, 0, buttonSize, buttonSize);

            for (Horse h : horses) {
                //완주한 말은 건너뜀
                //말이 FINISHED 상태이면 continue로 건너뜀 → 말 표시 안 함
                if (h.isFinished()) continue;

                //말마다 팀 색과 번호로 아이콘을 생성하고, 그걸 버튼 위에 표시
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

            //버튼에 패널 붙이고 갱신
            btn.add(panel);
            btn.revalidate();
            btn.repaint();
        }
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
