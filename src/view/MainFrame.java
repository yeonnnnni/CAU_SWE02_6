package view;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameManager;
import model.Node;
import model.Horse;
import model.DiceManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private Node[] nodes;
    private GameManager gameManager;
    private Board board;
    private DiceManager diceManager;
    private List<Node> nodeList;
    private static MainFrame instance;

    public MainFrame() {
        instance = this;

        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 보드 타입 선택 (현재는 square 고정)
        BoardBuilder builder = BoardFactory.create("square");
        this.nodeList = builder.buildBoard();

        // 2. 보드 UI 초기화
        boardPanel = new BoardPanel();
        boardPanel.renderBoard(nodeList, builder.getNodePositions());
        add(boardPanel, BorderLayout.CENTER);

        // 3. 주사위 패널 UI
        dicePanel = new DicePanel();
        add(dicePanel, BorderLayout.NORTH);

        // 4. 게임 로직 객체 생성 순서 (중요!)
        diceManager = new DiceManager();         // 먼저 생성
        board = new Board();
        List<String> players = List.of("0", "1");
        for (String player : players) {
            board.registerPlayer(player);
        }

        gameManager = new GameManager(this, board, diceManager, players);  // ✅ 그다음 안전하게 주입

        // 5. 게임 시작
        gameManager.startGame();
        // 6. 주사위 버튼 이벤트 연결
        dicePanel.addRollListener(e -> gameManager.handleDiceRoll());

        setVisible(true);
    }

    // 💡 사용자에게 말 선택 팝업
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
            Node from = selected.getPosition();            // 이동 전 위치
            selected.move(steps, nodeList);                // 말 이동
            Node to = selected.getPosition();              // 이동 후 위치
            Color teamColor = selected.getTeamColor();     // 말 색상

            boardPanel.updatePiecePosition(from, to, selected.getId(), teamColor);

            // 말 이동이 끝난 후 턴 전환 및 승리 체크
            gameManager.checkWin();
            gameManager.nextTurn();
        }
    }

    // 💬 사용자에게 분기점 선택 여부를 묻는 팝업 (지름길 진입 여부)
    public boolean promptShortcutChoice(String direction) {
        int option = JOptionPane.showConfirmDialog(
                this,
                direction + " 방향의 지름길로 진입하시겠습니까?",
                "지름길 선택",
                JOptionPane.YES_NO_OPTION
        );
        return option == JOptionPane.YES_OPTION;
    }

    public static MainFrame getInstance() {
        return instance;
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public DicePanel getDicePanel() {
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