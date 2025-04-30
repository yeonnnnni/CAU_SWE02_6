package view;

import model.Node;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private Node[] nodes;

    public MainFrame() {
        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬
        setLayout(new BorderLayout());

        // 1. 노드 생성 (23칸)
        nodes = new Node[23];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i);
        }

        // 2. 보드 패널 생성 및 초기화
        boardPanel = new BoardPanel();
        boardPanel.initialize("square", nodes);

        // 3. 패널 추가
        add(boardPanel, BorderLayout.CENTER);

        // 4. 향후 DicePanel 추가할 공간 확보 예시 (지금은 주석 처리)
        // DicePanel dicePanel = new DicePanel();
        // add(dicePanel, BorderLayout.NORTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    // Getter (GameManager 등 외부에서 사용 가능하도록)
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public Node[] getNodes() {
        return nodes;
    }
}
