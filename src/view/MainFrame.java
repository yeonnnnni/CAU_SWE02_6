package view;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameManager;
import model.DiceManager;
import model.Node;
import model.Team;
import model.Horse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JLabel currentPlayerLabel;
    private GameManager gameManager;
    private List<Node> nodeList;
    private static MainFrame instance;

    public MainFrame() {
        instance = this;
        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 보드 유형 선택
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

        // 보드 생성
        BoardBuilder builder = BoardFactory.create(boardType);
        this.nodeList = builder.buildBoard();

        // 보드 패널
        boardPanel = new BoardPanel();
        boardPanel.renderBoard(nodeList, builder.getNodePositions());
        add(boardPanel, BorderLayout.CENTER);

        // 윷 패널
        dicePanel = new DicePanel();
        add(dicePanel, BorderLayout.NORTH);

        // 현재 플레이어 라벨
        currentPlayerLabel = new JLabel("현재: ", SwingConstants.CENTER);
        add(currentPlayerLabel, BorderLayout.SOUTH);

        // 팀 구성
        List<Team> teams = List.of(
                new Team(0, "A", Color.BLUE),
                new Team(1, "B", Color.RED)
        );

        // 게임 매니저 연결
        Board board = new Board();
        teams.forEach(t -> board.getHorsesForTeam(t));  // 초기화용 호출
        gameManager = new GameManager(this, board, new DiceManager(), teams);
        gameManager.startGame();

        // 주사위 버튼 이벤트
        dicePanel.addRollListener(e -> gameManager.handleDiceRoll());

        setVisible(true);
    }

    public void setCurrentPlayer(String name) {
        currentPlayerLabel.setText("현재: " + name);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public DicePanel getDicePanel() {
        return dicePanel;
    }

    public List<Node> getNodes() {
        return nodeList;
    }

    public static MainFrame getInstance() {
        return instance;
    }

    public boolean promptShortcutChoice(String direction) {
        int choice = JOptionPane.showOptionDialog(
                this,
                direction + " 방향에서 지름길을 사용하시겠습니까?",
                "경로 선택",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"예", "아니오"},
                "예"
        );
        return choice == JOptionPane.YES_OPTION;
    }

    public Horse promptHorseSelection(List<Horse> candidates, int steps) {
        Object[] options = candidates.toArray();
        Horse selected = (Horse) JOptionPane.showInputDialog(
                this,
                steps + "칸 이동할 말을 선택하세요:",
                "말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        return selected;
    }

    public static void main(String[] args) {
        // Swing UI는 이벤트 디스패치 스레드에서 실행되어야 함
        SwingUtilities.invokeLater(() -> {
            new MainFrame();  // 메인 프레임 생성 및 실행
        });
    }
}
