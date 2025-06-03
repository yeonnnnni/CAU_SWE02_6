package view.Swing;

import model.Horse;
import model.Node;

import javax.swing.*; //swing 컴포넌트(JPanel, JButton 등)

import java.awt.*;  //Graphics, Color, Point 등 그래픽 도구
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;  //이미지 로딩용
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * BoardPanle 클래스는 윷놀이 보드 UI를 그리는 JPanel이다.
 * -말의 위치 갱신
 * -버튼 및 배경 이미지 배치
 * -말 이미지 아이콘 표시 기능
 */
public class BoardPanel extends JPanel {

    //노드와 해당 버튼 간의 양방향 매핑을 위한 맵
    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();

    private final int buttonSize = 50;  //각 노드를 표현하는 버튼의 고정 크기를 50x50으로 설정
    private BufferedImage backgroundImage;  //배경 보드 이미지(정사각형, 오각형, 육각형)
    private String boardType = "square";  //현재 선택된 보드 타입("square","pentagon","hexagon")
    private final Map<String, ImageIcon> horseIcons = new HashMap<>(); //말 이미지 아이콘 저장용

    //생성자 : 레이아웃 null로 설정(절대좌표 사용) + 패널 크기 고정 + 말 아이콘 사전 로드
    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 800));
        loadHorseIcons(); // 말 아이콘 미리 메모리에 로딩
    }

    // 각 팀 말의 아이콘을 미리 로딩해서 캐시에 저장
    private void loadHorseIcons() {
        String[] colors = {"blue", "green", "red", "yellow", "pink"};
        for (String color : colors) {
            for (int i = 0; i <= 4; i++) {  //각 팀은 0~4번 말까지 있음
                String key = color + "_h" + i;
                try {
                    ImageIcon raw = new ImageIcon(getClass().getResource("/horses/" + key + ".png"));
                    //아이콘의 크기를 30x30으로 리사이징
                    Image scaled = raw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // 크기 조정
                    horseIcons.put(key, new ImageIcon(scaled));
                } catch (Exception e) {
                    System.err.println("아이콘 로딩 실패: " + key);
                }
            }
        }
    }

    /**
     * 외부에서 보드 타입을 설정하면 그에 맞는 배경 이미지를 로딩
     */
    public void setBoardType(String boardType) {
        this.boardType = boardType;
        loadBackgroundImage(); //보드 타입에 맞는 이미지 불러오기
    }

    /**
     * boardType에 따라 리소스 경로에서 배경 이미지를 읽어옴
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
                    backgroundImage = null; //타입이 잘못된 경우 null
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("배경 이미지 로딩 실패: " + e.getMessage());
            backgroundImage = null;
        }
    }

    /**
     * JPanel의 paintComponent 오버라이드: 배경 이미지를 직접 그림
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 보드 타입별로 배경 이미지의 크기를 설정
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
                    drawHeight = 600;
                    break;
                default:
                    drawWidth = drawHeight = 350;
            }

            // 각 보드별로  이미지 위치를 약간 보정(말 위치와 정렬 위함)
            Map<String, Point> offsetMap = new HashMap<>();
            offsetMap.put("square", new Point(+125, -75));
            offsetMap.put("pentagon", new Point(+100, -80));
            offsetMap.put("hexagon", new Point(+70, -50));
            Point offsetAdjust = offsetMap.getOrDefault(boardType.toLowerCase(), new Point(0, 0));

            int panelWidth = getPreferredSize().width;
            int panelHeight = getPreferredSize().height;

            //이미지가 중앙 정렬되도록 x,y 좌표 계산
            int drawX = panelWidth / 2 - drawWidth / 2 + offsetAdjust.x;
            int drawY = panelHeight / 2 - drawHeight / 2 + offsetAdjust.y;

            // 배경 이미지 그리기
            g.drawImage(backgroundImage, drawX, drawY, drawWidth, drawHeight, this);
        }
    }

    /**
     * 보드에 있는 노드(Button)들을 생성하고 배치함
     * @param nodes         Node 객체 리스트
     * @param nodePositions Node ID에 대응되는 좌표 정보
     * @param boardType     현재 선택된 보드 타입
     */
    public void renderBoard(List<Node> nodes, Map<String, Point> nodePositions, String boardType) {
        setBoardType(boardType); // 배경 이미지도 함께 설정됨
        revalidate();  // 레이아웃 갱신
        nodeToButton.clear();  // 기존 맵 초기화
        buttonToNode.clear();

        // 1. 보드 타입별로 버튼 위치를 미세하게 보정하는 오프셋 설정
        Map<String, Point> offsetMap = new HashMap<>();
        offsetMap.put("square", new Point(+125, -75));
        offsetMap.put("pentagon", new Point(+100, -80));
        offsetMap.put("hexagon", new Point(+70, -50));
        Point offsetAdjust = offsetMap.getOrDefault(boardType.toLowerCase(), new Point(0, 0));

        // 2. 모든 노드 좌표의 최소/최대값을 기준으로 중심 좌표 계산
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

        // 3. 각 노드를 버튼으로 변환하고 위치 계산 후 add()
        for (Node node : nodes) {
            Point pt = nodePositions.get(node.getId());
            if (pt == null) {
                System.err.println("[BoardPanel] 경고: 위치 정보 없음 - " + node.getId());
                continue;
            }

            // 버튼 생성
            JButton btn = new JButton();

            // 중앙 정렬 + 오프셋 보정 적용
            int drawX = pt.x - offsetX + panelWidth / 2 - buttonSize / 2 + offsetAdjust.x;
            int drawY = pt.y - offsetY + panelHeight / 2 - buttonSize / 2 + offsetAdjust.y;
            btn.setBounds(drawX, drawY, buttonSize, buttonSize);
            btn.setFont(new Font("Arial", Font.BOLD, 8));
            btn.setText("");  // 텍스트 초기화
            btn.setContentAreaFilled(false);  // 배경 제거
            btn.setBorderPainted(false);  //테두리 제거
            btn.setOpaque(false);
            btn.setLayout(null); // 내부 아이콘 수동 배치 가능하게 설정

            add(btn);  // 패널에 버튼 추가
            nodeToButton.put(node, btn);
            buttonToNode.put(btn, node);
        }

        revalidate();  // 전체 레이아웃 재계산
        repaint();  // 화면 다시 그림
        System.out.println("[BoardPanel] 총 노드 수: " + nodes.size());
    }

    /**
     * 말의 위치가 바뀌었을 때 이전 위치에서 말 제거하고 새로운 위치에 다시 표시
     */
    public void updatePiecePosition(Node from, Node to) {

        // 1. 출발 위치 처리 : 버튼에서 모든 아이콘 제거
        if (from != null && nodeToButton.containsKey(from)) {
            JButton btn = nodeToButton.get(from);
            btn.removeAll();         // 기존 아이콘 제거
            btn.setText("");         // 텍스트 제거
            btn.revalidate();        // UI 갱신
            btn.repaint();           // 다시 그림
        }

        // 2. 도착 지점 처리 : 새로 아이콘 추가
        if (to != null && nodeToButton.containsKey(to)) {
            JButton btn = nodeToButton.get(to);
            btn.removeAll();  // 기존 UI 삭제
            // 해당 위치에 있는 말들
            List<Horse> horses = to.getHorsesOnNode();  // 말 리스트 가져옴

            // 최대 6개까지 아이콘 정렬(3행 2열)
            JPanel panel = new JPanel(new GridLayout(3, 2, 0, 0)); // 최대 6개 정렬
            panel.setOpaque(false);     // 배경 투명 처리
            panel.setBounds(0, 0, buttonSize, buttonSize);

            for (Horse h : horses) {
                if (h.isFinished()) continue;  // 완주한 말은 표시 안 함

                int horseIdx = Integer.parseInt(h.getId().split("-H")[1]);  // 말 번호 추출
                String colorKey = getColorKey(h.getTeamColor());   //색상 키 추출
                String iconKey = colorKey + "_h" + horseIdx;    // 이미지 키 생성
                ImageIcon icon = horseIcons.get(iconKey);    // 아이콘 가져옴

                if (icon != null) {
                    Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    JLabel label = new JLabel(new ImageIcon(scaled));  // JLabel로 아이콘 표현
                    panel.add(label);
                }
            }

            btn.add(panel);  // 버튼 위에 패널 덧붙임
            btn.revalidate();
            btn.repaint();
        }
    }

    /**
     * 색상 객체(Color)를 문자열 키로 변환(아이콘 찾기 위함)
     */
    private String getColorKey(Color color) {
        if (Color.BLUE.equals(color)) return "blue";
        if (Color.RED.equals(color)) return "red";
        if (Color.GREEN.equals(color)) return "green";
        if (Color.YELLOW.equals(color)) return "yellow";
        if (Color.PINK.equals(color)) return "pink";
        return "unknown";  // 예외 처리용
    }

    //모든 버튼의 텍스트 및 아이콘 초기화
    public void resetButtons() {
        for (JButton btn : nodeToButton.values()) {
            btn.setText("");
            btn.removeAll();
        }
    }

    // 전체 보드 UI 초기화(텍스트, 아이콘 제거)
    public void resetBoardUI() {
        for (JButton btn : nodeToButton.values()) {
            btn.setText("");
            btn.removeAll();
        }
    }

    //Getter: 외부에서 노드->버튼 매핑 참조 가능
    public Map<Node, JButton> getNodeToButtonMap() {
        return nodeToButton;
    }

    //Getter: 외부에서 버튼->노드 매핑 참조 가능
    public Map<JButton, Node> getButtonToNodeMap() {
        return buttonToNode;
    }
}
