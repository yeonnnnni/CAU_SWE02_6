package view;

import controller.Board;
import controller.GameManager;
import model.Node;
import model.DiceManager;
import model.Horse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JLabel currentPlayerLabel;
    private Node[] nodes;
    private GameManager gameManager;

    public MainFrame() {
        String[] types = {"square", "pentagon", "hexagon"};
        String boardType = (String) JOptionPane.showInputDialog(
            null,
            "보드 유형을 선택하세요:",
            "판 설정",
            JOptionPane.PLAIN_MESSAGE,
            null,
            types,
            types[0]
        );
        if (boardType == null) boardType = "square";

        // ▶ 상단에 현재 플레이어 라벨 추가
        currentPlayerLabel = new JLabel("", SwingConstants.CENTER);
        add(currentPlayerLabel, BorderLayout.NORTH);

        // 1) 노드 생성
        nodes = new Node[23];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i);
        }

        // 2) 모델(Board) 초기화
        Board board = new Board();
        board.initialize(boardType, nodes);

        // 3) 보드 패널 초기화 및 연결
        setLayout(new BorderLayout());
        boardPanel = new BoardPanel();
        boardPanel.initialize(boardType, nodes);
        boardPanel.setBoard(board);
        add(boardPanel, BorderLayout.CENTER);

        // 4) DicePanel 생성 및 추가
        dicePanel = new DicePanel();
        add(dicePanel, BorderLayout.SOUTH);

        // 5) GameManager 생성 및 게임 시작
        List<Team> teams = List.of(
            new Team(1, "A", Color.BLUE),
            new Team(2, "B", Color.RED)
        );
        gameManager = new GameManager(this, board, new DiceManager(), teams);
        gameManager.startGame(); // ▶ 초기 라벨, 보드, DicePanel 리셋

        // ▶ 윷 던지기 버튼 클릭 시 GameManager에 전달
        dicePanel.addRollListener(e ->
            gameManager.handleMoveQueue(dicePanel.getResultQueue())
        );

        setTitle("윷놀이 게임 (" + boardType + ")");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ▶ GameManager에서 호출하여 현재 플레이어 라벨 업데이트
    public void setCurrentPlayer(String name) {
        currentPlayerLabel.setText("현재: " + name);
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


