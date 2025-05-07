package view;

import model.Node;
import javax.swing.*;

import controller.Board;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * BoardPanel: 사각형, 오각형, 육각형 보드 지원
 * 노드 라벨링: 센터=00, 지름길=방향+레벨, 외각=N+idx, 꼭짓점=방향+1
 */
public class BoardPanel extends JPanel {
    private final Map<Node, JButton> nodeToButton = new HashMap<>();
    private final Map<JButton, Node> buttonToNode = new HashMap<>();
    private final int buttonSize = 50;

    private Board board; 
    
    public BoardPanel() {
        setPreferredSize(new Dimension(700, 700));
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    /**
     * @param boardType "square", "pentagon", "hexagon"
     * @param nodes Node 배열
     */
    public void initialize(String boardType, Node[] nodes) {
        removeAll();
        nodeToButton.clear();
        buttonToNode.clear();

        if ("square".equalsIgnoreCase(boardType)) {
            setLayout(new GridLayout(5, 5, 10, 10));
            initSquare(nodes);
        } else {
            setLayout(null);
            if ("pentagon".equalsIgnoreCase(boardType)) {
                initPolygon(nodes, 5);
            } else if ("hexagon".equalsIgnoreCase(boardType)) {
                initPolygon(nodes, 6);
            } else {
                throw new IllegalArgumentException("Unsupported board type: " + boardType);
            }
        }

        revalidate();
        repaint();
    }

    // 사각형 보드 초기화 (GridLayout)
    private void initSquare(Node[] nodes) {
        int[] idxMap = {
            0, 1, 2, 3, 4,
            5, -1, 6, -1, 7,
            8, 9, 10, 11, 12,
            13, -1,14, -1,15,
            16,17,18,19,20
        };
        for (int i = 0; i < idxMap.length; i++) {
            int idx = idxMap[i];
            if (idx < 0 || idx >= nodes.length) {
                add(new JLabel());
            } else {
                JButton b = createButton(idx);
                add(b);
                nodeToButton.put(nodes[idx], b);
                buttonToNode.put(b, nodes[idx]);
            }
        }
    }

    // 다각형 보드 초기화 (절대 좌표)
    private void initPolygon(Node[] nodes, int sides) {
        int w = getWidth() > 0 ? getWidth() : getPreferredSize().width;
        int h = getHeight() > 0 ? getHeight() : getPreferredSize().height;
        int cx = w/2, cy = h/2;
        int r = Math.min(w, h)/2 - buttonSize;
        Point[] verteces = new Point[sides];
        for (int i = 0; i < sides; i++) {
            double ang = -Math.PI/2 + 2*Math.PI*i/sides;
            verteces[i] = new Point(
                cx + (int)(r*Math.cos(ang)),
                cy + (int)(r*Math.sin(ang))
            );
        }
        int total = nodes.length;
        int base = total / sides;
        int rem = total % sides;
        int k = 0;
        for (int i = 0; i < sides; i++) {
            Point v1 = verteces[i], v2 = verteces[(i+1)%sides];
            int cnt = base + (i < rem ? 1 : 0);
            for (int j = 0; j < cnt && k < total; j++) {
                double t = (double)j/cnt;
                int x = (int)(v1.x*(1-t) + v2.x*t);
                int y = (int)(v1.y*(1-t) + v2.y*t);
                JButton b = createButton(k);
                b.setBounds(x-buttonSize/2, y-buttonSize/2, buttonSize, buttonSize);
                add(b);
                nodeToButton.put(nodes[k], b);
                buttonToNode.put(b, nodes[k]);
                k++;
            }
        }
    }

    // 버튼 생성
    private JButton createButton(int idx) {
        JButton b = new JButton(getNodeLabel(idx));
        b.setMargin(new Insets(0,0,0,0));
        b.setFont(new Font("Arial", Font.PLAIN, 11));
        b.setBackground(Color.WHITE);
        b.setForeground(Color.BLACK);
        return b;
    }

    // 노드 라벨 결정
    private String getNodeLabel(int idx) {
        if (idx == 0) return "00";                    // 센터
        if (isVertex(idx)) return direction(idx) + "1"; // 꼭짓점
        if (isShortcut(idx)) return direction(idx) + getShortcutLevel(idx); // 지름길
        return "N" + idx;                              // 외각
    }

    private boolean isVertex(int idx) {
        // TODO: 특정 보드 타입별 꼭짓점 인덱스 체크
        return false;
    }
    private boolean isShortcut(int idx) {
        // TODO: 특정 보드 타입별 지름길 노드 체크
        return false;
    }
    private int getShortcutLevel(int idx) {
        // TODO: 지름길 단계 계산
        return 1;
    }
    private String direction(int idx) {
        // TODO: idx 기반 방향(N,E,S,W,NE,SE,SW,NW) 반환
        return "";
    }

    public Map<Node, JButton> getNodeToButtonMap() { return nodeToButton; }
    public Map<JButton, Node> getButtonToNodeMap() { return buttonToNode; }
    public void resetBoardUI() { nodeToButton.values().forEach(b -> { b.setText(" "); b.setForeground(Color.BLACK); }); }
}
