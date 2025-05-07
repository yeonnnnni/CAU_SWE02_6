package controller;

import model.YutResult;
import model.DiceManager;
import model.Horse;
import model.Team;
import view.MainFrame;
import view.DicePanel;
import view.BoardPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GameManager {
    private final MainFrame mainFrame;
    private final Board board;
    private final DiceManager diceManager;

    private final List<Team> teams;
    private int currentPlayerIndex;

    public GameManager(MainFrame mainFrame, Board board, DiceManager diceManager, List<Team> teams) {
        this.mainFrame = mainFrame;
        this.board = board;
        this.diceManager = diceManager;
        this.teams = teams;
        this.currentPlayerIndex = 0;
    }

    /**
     * 게임 시작 시 호출: 현재 플레이어 초기화 및 UI 갱신
     */
    public void startGame() {
        currentPlayerIndex = 0;
        mainFrame.setCurrentPlayer(getCurrentTeam().getName());
        mainFrame.getBoardPanel().repaint();
        // 초기화된 윷 결과 목록 표시
        mainFrame.getDicePanel().showResult(new ArrayList<YutResult>());
    }

    /**
     * 턴 종료 후 다음 플레이어로 전환 및 UI 갱신
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % teams.size();
        mainFrame.setCurrentPlayer(getCurrentTeam().getName());
        // 초기화된 윷 결과 목록 표시
        mainFrame.getDicePanel().showResult(new ArrayList<YutResult>());
    }

    /**
     * 현재 플레이어의 승리 조건을 검사
     */
    public void checkWin() {
        Team team = getCurrentTeam();
        boolean allFinished = true;
        for (Horse h : board.getHorsesForTeam(team)) {
            if (!h.isFinished()) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            Object[] options = {"다시 시작", "종료"};
            int choice = JOptionPane.showOptionDialog(
                mainFrame,
                team.getName() + " 팀이 승리했습니다!",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (choice == JOptionPane.YES_OPTION) {
                // 다시 시작
                startGame();
            } else {
                // 프로그램 종료
                System.exit(0);
            }
    }

    /**
     * 현재 차례의 팀 반환
     */
    public Team getCurrentTeam() {
        return teams.get(currentPlayerIndex);
    }

    /**
         * 이동 가능한 말이 없을 경우 자동으로 턴을 넘깁니다.
         */
        private void autoSkipTurn() {
            JOptionPane.showMessageDialog(
                mainFrame,
                "이동 가능한 말이 없어 턴을 건너뜁니다.",
                "턴 스킵",
                JOptionPane.INFORMATION_MESSAGE
            );
            nextTurn();
        }

    /** 윷 결과 큐를 처리하여 말 이동 */
    public void handleMoveQueue(Queue<YutResult> resultQueue) {
        boolean moved = false;
        Queue<YutResult> queue = (resultQueue == null || resultQueue.isEmpty())
            ? diceManager.rollRandomQueue()
            : resultQueue;

        mainFrame.getDicePanel().setEnabled(false);

        while (!resultQueue.isEmpty()) {
            YutResult result = resultQueue.poll();
            int steps = diceManager.convertToSteps(result);

            List<Horse> movable = board.getHorsesForTeam(getCurrentTeam());
            movable.removeIf(h -> !board.canMove(h, steps));
            if (movable.isEmpty()) continue;

            // 사용자 선택 및 이동
            Horse selected = mainFrame.promptHorseSelection(movable, steps);
            if (selected == null) return; // 취소 시 중단

            // 잡기/그룹핑 처리
            for (Horse other : board.getAllHorses()) {
                if (selected.isGroupable(other)) {
                    selected.groupWith(other);
                } else if (selected.isCaptured(other)) {
                    other.setPosition(board.getStartNode());
                }
            }
            mainFrame.getBoardPanel().repaint();
        }

        if (!moved) {
            autoSkipTurn();
            mainFrame.getDicePanel().setEnabled(true);
            return;
        }
    
        checkWin();
        nextTurn();
        mainFrame.getDicePanel().setEnabled(true);
        //false 로 설정하면 그 패널(DicePanel)과 그 안에 들어 있는 모든 버튼·입력창이 클릭이나 입력을 받지 않게됨
    }

    /** 게임을 완전히 초기화하고 새로 시작 */
    public void restartGame() {
        // 말 위치 초기화, 턴 초기화 등
    }

    /** 현재 플레이어 이름 반환 */
    public Team getCurrentTeam() {
        return Team.get(currentPlayerIndex);
    }

    /** 윷 결과를 받아 처리 (GameController에서 호출) */
    public void processRollResult(YutResult result) {
        // TODO: 한 결과에 대해 칸 수 계산 → 이동 처리 (예: moveHorse)
    }


    //이동 가능 말 필터 메서드
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


}

//GameManager.handleDiceRoll() 또는 startTurn() 같은 함수 안에서
//List<YutResult> results = diceManager.rollRandomSequence();
//이런 식으로 처리할 수 있도록 만들어야 함.
//YutResult 리스트는 반드시 순서대로 처리해야 함. (윷/모 보너스 포함된 상태니까!)

/*
(예시본)
List<YutResult> results = diceManager.rollRandomSequence();

for (YutResult result : results) {
    int steps = diceManager.convertToSteps(result);
    // → 여기서 이동 처리
    game.moveCurrentHorse(steps); // 예시 함수
}

(예시본)
public void startTurn(DiceManager diceManager) {
    List<YutResult> results = diceManager.rollRandomSequence();

    for (YutResult result : results) {
        int steps = diceManager.convertToSteps(result);
        System.out.println("말 이동 거리: " + steps);
        // TODO: 이동 처리 → game.moveCurrentHorse(steps);
    }
}

메서드 역할
startGame() 전체 초기화 및 첫 턴 준비
startTurn() 윷 던지고 큐 준비
handleMoveQueue()   YutResult 큐를 순서대로 처리
processRollResult() 단일 결과 처리용 (직접 호출 시)
checkWin()  말 4개 도착 등 조건 검사
nextTurn()  다음 플레이어로 전환
restartGame()   판 전체 리셋
getCurrentPlayer()  UI에서 현재 사용자 표시용
*/

