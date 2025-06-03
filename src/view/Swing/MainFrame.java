// MainFrame.java
package view.Swing;

import model.*;

import javax.swing.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame implements GameUI {
    private view.Swing.BoardPanel boardPanel;
    private view.Swing.DicePanel dicePanel;
    private JLabel currentPlayerLabel;
    private List<Node> nodeList;
    private static MainFrame instance;
    private view.Swing.ScoreboardPanel scoreboardPanel;

    public MainFrame(List<Node> nodeList, Map<String, Point> nodePositions, String boardType) {
        this.nodeList = nodeList;
        this.setLayout(new BorderLayout());
        instance = this;

        setTitle("윷놀이 게임");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI 컴포넌트 배치
        scoreboardPanel = new view.Swing.ScoreboardPanel();
        add(scoreboardPanel, BorderLayout.EAST);
        boardPanel = new view.Swing.BoardPanel();
        boardPanel.renderBoard(nodeList, nodePositions, boardType);
        add(boardPanel, BorderLayout.CENTER);

        dicePanel = new view.Swing.DicePanel();
        currentPlayerLabel = new JLabel("현재: ", SwingConstants.CENTER);
        add(dicePanel, BorderLayout.NORTH);
        add(currentPlayerLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // getter 추가
    public view.Swing.ScoreboardPanel getScoreboardPanel() {
        return scoreboardPanel;
    }

    public void setCurrentPlayer(String name) {
        currentPlayerLabel.setText("현재: " + name);
    }

    public view.Swing.BoardPanel getBoardPanel() { return boardPanel; }
    public view.Swing.DicePanel getDicePanel() { return dicePanel; }
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

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
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
}