package controller;

import model.YutResult;
import model.DiceManager;
import model.Horse;
import model.Team;
import model.Node;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final MainFrame mainFrame;
    private final Board board;
    private final DiceManager diceManager;
    private final List<Team> teams;
    private int currentPlayerIndex;
    private final String boardType;

    // 현재 남은 윷 결과 리스트
    private List<YutResult> remainingResults = new ArrayList<>();

    public GameManager(MainFrame mainFrame, Board board, DiceManager diceManager, List<Team> teams, String boardType) {
        this.mainFrame = mainFrame;
        this.board = board;
        this.diceManager = diceManager;
        this.teams = teams;
        this.boardType = boardType;
        this.currentPlayerIndex = 0;
    }

    public void startGame() {
        currentPlayerIndex = 0;
        updateCurrentPlayerLabel();
        board.resetAll();
        mainFrame.getBoardPanel().resetBoardUI();
        mainFrame.getDicePanel().showResult(new ArrayList<>());
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % teams.size();
        updateCurrentPlayerLabel();
        mainFrame.getDicePanel().showResult(new ArrayList<>());
    }

    public void checkWin() {
        Team team = getCurrentTeam();
        if (team.isWin()) {
            int choice = JOptionPane.showOptionDialog(
                    mainFrame,
                    team.getName() + " 팀이 승리했습니다!",
                    "게임 종료",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"다시 시작", "종료"},
                    "다시 시작"
            );
            if (choice == JOptionPane.YES_OPTION) {
                startGame();
            } else {
                System.exit(0);
            }
        }
    }

    public void handleDiceRoll() {
        List<YutResult> results;

        if (mainFrame.getDicePanel().isRandomMode()) {
            results = diceManager.rollRandomSequence();
        } else {
            try {
                int val = Integer.parseInt(mainFrame.getDicePanel().getManualInputText());
                results = List.of(diceManager.rollManual(val));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "유효한 숫자를 입력해주세요.");
                return;
            }
        }

        remainingResults.clear();
        remainingResults.addAll(results);
        mainFrame.getDicePanel().showResult(results);

        promptNextMove();
    }

    private void promptNextMove() {
        if (remainingResults.isEmpty()) {
            checkWin();
            nextTurn();
            mainFrame.getDicePanel().setEnabled(true);
            return;
        }

        YutResult selected;

        // 🎯 윷 결과 1개일 경우 자동 선택
        if (remainingResults.size() == 1) {
            selected = remainingResults.get(0);
        } else {
            selected = promptYutSingleChoice(remainingResults);
            if (selected == null) {
                mainFrame.getDicePanel().setEnabled(true);
                return;
            }
        }

        int steps = diceManager.convertToSteps(selected);
        List<Horse> movable = getMovableHorses(steps);

        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "이동 가능한 말이 없습니다.");
            remainingResults.remove(selected);
            promptNextMove();
            return;
        }

        Horse horse = mainFrame.promptHorseSelection(movable, steps);
        if (horse == null) {
            mainFrame.getDicePanel().setEnabled(true);
            return;
        }

        Node from = horse.getPosition();
        horse.move(steps, board.getNodes(), boardType);
        Node to = horse.getPosition();

        for (Horse other : board.getAllHorses()) {
            if (horse.isGroupable(other)) horse.groupWith(other);
            else if (horse.isCaptured(other)) other.reset();
        }

        mainFrame.getBoardPanel().updatePiecePosition(from, to, horse.getId(), horse.getTeamColor());
        remainingResults.remove(selected);

        promptNextMove();  // 재귀적으로 다음 결과 처리
    }

    private YutResult promptYutSingleChoice(List<YutResult> options) {
        Object[] choices = options.toArray();
        return (YutResult) JOptionPane.showInputDialog(
                mainFrame,
                "사용할 윷 결과를 선택하세요:",
                "결과 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]
        );
    }

    public void restartGame() {
        board.resetAll();
        currentPlayerIndex = 0;
        updateCurrentPlayerLabel();
        mainFrame.getBoardPanel().resetBoardUI();
        mainFrame.getDicePanel().showResult(new ArrayList<>());
    }

    public Team getCurrentTeam() {
        return teams.get(currentPlayerIndex);
    }

    public List<Horse> getMovableHorses(int steps) {
        List<Horse> horses = board.getHorsesForTeam(getCurrentTeam());
        List<Horse> movable = new ArrayList<>();
        for (Horse h : horses) {
            if (board.canMove(h, steps)) {
                movable.add(h);
            }
        }
        return movable;
    }

    private void updateCurrentPlayerLabel() {
        mainFrame.setCurrentPlayer(getCurrentTeam().getName());
    }
}
