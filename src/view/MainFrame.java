// MainFrame.java
package view;

import controller.GameManager;
import model.*;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame implements GameUI {
    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JLabel currentPlayerLabel;
    private GameManager gameManager;
    private List<Node> nodeList;
    private static MainFrame instance;
    private ScoreboardPanel scoreboardPanel;
    private int pieceCount = 2;
    private int playerCount = 2;

    public MainFrame(List<Node> nodeList, Map<String, Point> nodePositions, String boardType) {
        this.nodeList = nodeList;
        this.setLayout(new BorderLayout());
        instance = this;

        setTitle("윷놀이 게임");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI 컴포넌트 배치
        scoreboardPanel = new ScoreboardPanel();
        add(scoreboardPanel, BorderLayout.EAST);
        boardPanel = new BoardPanel();
        boardPanel.renderBoard(nodeList, nodePositions, boardType);
        add(boardPanel, BorderLayout.CENTER);

        dicePanel = new DicePanel();
        currentPlayerLabel = new JLabel("현재: ", SwingConstants.CENTER);
        add(dicePanel, BorderLayout.NORTH);
        add(currentPlayerLabel, BorderLayout.SOUTH);

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

    @Override
    public boolean isRandomMode() {
        return dicePanel.isRandomMode();
    }

    @Override
    public String getManualInput() {
        return dicePanel.getManualInputText();
    }

    @Override
    public void showDiceResult(List<YutResult> results) {
        dicePanel.showResult(results);
    }

    @Override
    public void setRollListener(Runnable listener) {
        dicePanel.addRollListener(e -> listener.run());
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public boolean confirmShortcut(String direction) {
        int response = JOptionPane.showConfirmDialog(
                this,
                "지름길 (" + direction + " 방향)로 진입하시겠습니까?",
                "지름길 선택",
                JOptionPane.YES_NO_OPTION
        );
        return response == JOptionPane.YES_OPTION;
    }

    @Override
    public Horse selectHorse(List<Horse> candidates, int steps) {
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

    @Override
    public YutResult chooseYutResult(List<YutResult> options) {
        Object[] choices = options.toArray();
        return (YutResult) JOptionPane.showInputDialog(
                this,
                "사용할 윷 결과를 선택하세요:",
                "결과 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]
        );
    }

    @Override
    public boolean promptRestart(String winnerName) {
        int choice = JOptionPane.showOptionDialog(
                this,
                winnerName + " 팀이 승리했습니다! 게임을 다시 시작할까요?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"다시 시작", "종료"},
                "다시 시작"
        );
        return choice == JOptionPane.YES_OPTION;
    }

    @Override
    public void updatePiece(Node from, Node to) {
        boardPanel.updatePiecePosition(from, to);
    }

    @Override
    public void setDiceRollEnabled(boolean enabled) {
        dicePanel.setEnabled(enabled);
    }


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
}