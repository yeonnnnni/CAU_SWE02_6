// MainFrame.java
package view;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameManager;
import model.DiceManager;
import model.Node;
import model.Team;
import model.YutResult;
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
    private ScoreboardPanel scoreboardPanel;
    private int pieceCount = 2;

    public MainFrame() {
        instance = this;
        setTitle("윷놀이 게임");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 추가
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        scoreboardPanel = new ScoreboardPanel();
        add(scoreboardPanel, BorderLayout.EAST);

        initCount();



        // 보드 생성
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

        BoardBuilder builder = BoardFactory.create(boardType);
        this.nodeList = builder.buildBoard();

        // 보드 패널
        boardPanel = new BoardPanel();
        boardPanel.renderBoard(nodeList, builder.getNodePositions());
        add(boardPanel, BorderLayout.CENTER);

        // 윷 패널
        dicePanel = new DicePanel();
        add(dicePanel, BorderLayout.NORTH);

        // 플레이어 라벨
        currentPlayerLabel = new JLabel("현재: ", SwingConstants.CENTER);
        add(currentPlayerLabel, BorderLayout.SOUTH);

        // 팀 구성 및 등록
        // MainFrame에서 팀 만들 때
        List<Team> teams = List.of(
            new Team(0, "A", Color.BLUE, pieceCount, boardType),
            new Team(1, "B", Color.RED, pieceCount, boardType)
        );


        Board board = new Board();
        board.setNodes(nodeList);
        //for (Team t : teams) board.registerTeam(t);

        // 게임 매니저 연결
        gameManager = new GameManager(this, board, new DiceManager(), teams, boardType);
        gameManager.startGame();

        // 윷 이벤트
        dicePanel.addRollListener(e -> gameManager.handleDiceRoll());

        setVisible(true);
    }

    public List<YutResult> promptYutOrder(List<YutResult> results) {
        DefaultListModel<YutResult> listModel = new DefaultListModel<>();
        for (YutResult r : results) listModel.addElement(r);

        JList<YutResult> resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(resultList),
                "사용할 윷 결과의 순서를 선택하세요 (위→아래 순서)",
                JOptionPane.PLAIN_MESSAGE
        );

        List<YutResult> selected = resultList.getSelectedValuesList();
        if (selected.size() != results.size()) {
            JOptionPane.showMessageDialog(this, "모든 결과를 선택해야 합니다. 기본 순서로 진행합니다.");
            return results;
        }

        return selected;
    }

    // getter 추가
    public ScoreboardPanel getScoreboardPanel() {
        return scoreboardPanel;
    }

    public void setCurrentPlayer(String name) {
        currentPlayerLabel.setText("현재: " + name);
    }

    public BoardPanel getBoardPanel() { return boardPanel; }
    public DicePanel getDicePanel() { return dicePanel; }
    public List<Node> getNodes() { return nodeList; }
    public static MainFrame getInstance() { return instance; }

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
        return (Horse) JOptionPane.showInputDialog(
                this,
                steps + "칸 이동할 말을 선택하세요:",
                "말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }


    // 말 개수 설정: 2~5개 입력, 그 외는 2로 설정
    private void initCount() {
        try {
            String pieceInput = JOptionPane.showInputDialog(null, "말 개수 (2~5):", "설정", JOptionPane.QUESTION_MESSAGE);
            int input = Integer.parseInt(pieceInput);
            if (input >= 2 && input <= 5) {
                pieceCount = input;
            } else {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "잘못된 입력입니다. 말 개수를 2개로 설정합니다.");
            pieceCount = 2;
        }
    }
}