package view;

import controller.GameManager;
import model.Node;
import model.Horse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private Node[] nodes;
    private GameManager gameManager;

    public MainFrame() {
        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 노드 생성
        nodes = new Node[23];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i);
        }

        // 2. 보드 초기화
        boardPanel = new BoardPanel();
        boardPanel.initialize("square", nodes);
        add(boardPanel, BorderLayout.CENTER);

        // GameManager 연결
        gameManager = new GameManager(this);

        // 나중에 DicePanel 추가
        // dicePanel = new DicePanel();
        // add(dicePanel, BorderLayout.NORTH);

        setVisible(true);
    }

    // === 💡 말 선택 유도 메서드 추가 ===
    public void promptHorseSelection(List<Horse> horses, int steps) {
        if (horses.isEmpty()) return;

        Horse selected = (Horse) JOptionPane.showInputDialog(
            this,
            "이동할 말을 선택하세요 (" + steps + "칸 이동)",
            "말 선택",
            JOptionPane.PLAIN_MESSAGE,
            null,
            horses.toArray(),
            horses.get(0)
        );

        if (selected != null) {
            selected.move(steps);         // 선택된 말 이동
            boardPanel.repaint();         // 보드 화면 갱신
        }
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public DicePanel getDicePanel(){
        return dicePanel;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

