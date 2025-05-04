package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import model.Node;

import controller.Board;
import model.Horse;

public class BoardPanel extends JPanel {
    private Board board;
    private final Map<Integer, Point> nodePositions = new HashMap<>();
    private static final int GAP = 80;
    private static final int BASE_X = 60;
    private static final int BASE_Y = 60;

    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
        setupYutBoardFixed();
    }

    private void setupYutBoardFixed() {
        nodePositions.clear();
        int centerX = BASE_X + 2 * GAP;
        int centerY = BASE_Y + 2 * GAP;
        // 주변 20개 노드
        for (int i = 0; i <= 19; i++) {
            int x, y;
            switch (i) {
                case 0: x = BASE_X + 5*GAP; y = BASE_Y + 5*GAP; break;
                case 1: x = BASE_X + 5*GAP; y = BASE_Y + 4*GAP; break;
                case 2: x = BASE_X + 5*GAP; y = BASE_Y + 3*GAP; break;
                case 3: x = BASE_X + 5*GAP; y = BASE_Y + 2*GAP; break;
                case 4: x = BASE_X + 5*GAP; y = BASE_Y + GAP;   break;
                case 5: x = BASE_X + 5*GAP; y = BASE_Y;        break;
                case 6: x = BASE_X + 4*GAP; y = BASE_Y;        break;
                case 7: x = BASE_X + 3*GAP; y = BASE_Y;        break;
                case 8: x = BASE_X + 2*GAP; y = BASE_Y;        break;
                case 9: x = BASE_X + GAP;     y = BASE_Y;        break;
                case 10: x = BASE_X;          y = BASE_Y;        break;
                case 11: x = BASE_X;          y = BASE_Y + GAP;   break;
                case 12: x = BASE_X;          y = BASE_Y + 2*GAP; break;
                case 13: x = BASE_X;          y = BASE_Y + 3*GAP; break;
                case 14: x = BASE_X;          y = BASE_Y + 4*GAP; break;
                case 15: x = BASE_X;          y = BASE_Y + 5*GAP; break;
                case 16: x = BASE_X + GAP;    y = BASE_Y + 5*GAP; break;
                case 17: x = BASE_X + 2*GAP;  y = BASE_Y + 5*GAP; break;
                case 18: x = BASE_X + 3*GAP;  y = BASE_Y + 5*GAP; break;
                case 19: x = BASE_X + 4*GAP;  y = BASE_Y + 5*GAP; break;
                default: x = centerX; y = centerY;             break;
            }
            nodePositions.put(i, new Point(x, y));
        }
        // 대각선 지름길
        nodePositions.put(20, new Point(BASE_X + GAP,     BASE_Y + GAP));
        nodePositions.put(21, new Point(BASE_X + 2*GAP,   BASE_Y + 2*GAP));
        nodePositions.put(22, new Point(BASE_X + 3*GAP,   BASE_Y + 3*GAP));
        nodePositions.put(23, new Point(BASE_X + 4*GAP,   BASE_Y + 4*GAP));
        // 역지름길
        nodePositions.put(24, new Point(BASE_X + 4*GAP,   BASE_Y + GAP));
        nodePositions.put(25, new Point(BASE_X + 3*GAP,   BASE_Y + 2*GAP));
        nodePositions.put(26, new Point(BASE_X + 2*GAP,   BASE_Y + 3*GAP));
        nodePositions.put(27, new Point(BASE_X + GAP,     BASE_Y + 4*GAP));
        // 결승점 중앙
        nodePositions.put(28, new Point(centerX + GAP/2,   centerY + GAP/2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 기본 궤적
        for (int i = 0; i < 19; i++) drawLine(g2, i, i+1);
        drawLine(g2, 19, 0);
        // 대각선
        drawPath(g2, new int[]{0,20,21,22,23,10});
        drawPath(g2, new int[]{5,24,25,26,27,15});
        drawLine(g2,21,28);
        drawLine(g2,25,28);
        // 노드 그리기
        Set<Integer> doubleCircle = Set.of(0,5,10,15,28);
        for (var e : nodePositions.entrySet()) {
            int id = e.getKey(); Point p = e.getValue();
            if (doubleCircle.contains(id)) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(p.x-12,p.y-12,24,24);
                g2.drawOval(p.x-6,p.y-6,12,12);
                g2.setStroke(new BasicStroke(1));
            } else {
                g2.setColor(Color.BLACK);
                g2.fillOval(p.x-5,p.y-5,10,10);
            }
            if (id==0) {
                g2.setColor(Color.GREEN.darker());
                g2.drawString("START", p.x-18, p.y-12);
            }
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(id), p.x+6, p.y);
        }
        // 말 그리기
        for (Horse h : board.getHorses()) {
            Node n = h.getCurrentNode();
            Point p = nodePositions.get(n.getId());
            if (p!=null) {
                g2.setColor(h.getOwner().equals("Player1")?Color.BLUE:Color.RED);
                g2.fillOval(p.x-10,p.y-10,20,20);
                if (h.isGrouped()) {
                    g2.setColor(Color.YELLOW);
                    g2.drawOval(p.x-12,p.y-12,24,24);
                }
            }
        }
    }

    private void drawLine(Graphics2D g2, int from, int to) {
        Point a = nodePositions.get(from), b = nodePositions.get(to);
        if (a!=null && b!=null) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(a.x,a.y,b.x,b.y);
        }
    }

    private void drawPath(Graphics2D g2, int[] ids) {
        for(int i=0;i<ids.length-1;i++) drawLine(g2, ids[i], ids[i+1]);
    }

    /** 외부 갱신 */
    public void refresh() { repaint(); }
    /** 보드 교체 */
    public void setBoard(Board board) { this.board=board; repaint(); }
}

