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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameManager {
    // 메인 프레임 및 주요 컴포넌트 참조
    private final MainFrame mainFrame;
    private final Board board;
    private final DiceManager diceManager;

    // 팀 목록 및 현재 턴 인덱스
    private final List<Team> teams;
    private int currentPlayerIndex;

    // 생성자: 필요한 참조 객체들을 주입받아 초기화
    public GameManager(MainFrame mainFrame, Board board, DiceManager diceManager, List<Team> teams) {
        this.mainFrame = mainFrame;
        this.board = board;
        this.diceManager = diceManager;
        this.teams = teams;
        this.currentPlayerIndex = 0;
    }

    // 턴을 시작할 때 호출되는 메서드 (윷을 던지고 큐로 변환)
    public void startTurn() {
        Queue<YutResult> resultQueue = diceManager.rollRandomQueue();
        mainFrame.getDicePanel().showResult(List.copyOf(resultQueue)); // 결과 표시
        handleMoveQueue(resultQueue); // 결과 처리
    }

    // 게임을 처음 시작할 때 호출: 현재 플레이어 초기화, 보드와 UI 초기화
    public void startGame() {
        currentPlayerIndex = 0;
        updateCurrentPlayerLabel();  // 상단 라벨 표시
        board.resetAll();            // 말 초기화
        mainFrame.getBoardPanel().resetBoardUI();  // UI에서 말 위치 제거
        mainFrame.getDicePanel().showResult(new ArrayList<>());  // 윷 결과 초기화
    }

    // 다음 턴으로 넘어갈 때 호출
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % teams.size();  // 순환 구조
        updateCurrentPlayerLabel();  // 현재 팀 라벨 갱신
        mainFrame.getDicePanel().showResult(new ArrayList<>());  // 이전 윷 결과 지움
    }

    // 현재 팀이 승리 조건을 만족했는지 확인
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
                startGame();  // 다시 시작
            } else {
                System.exit(0);  // 프로그램 종료
            }
        }
    }

    // 윷 결과 큐를 받아서 순차적으로 처리하는 메서드
    public void handleMoveQueue(Queue<YutResult> resultQueue) {
        if (resultQueue == null || resultQueue.isEmpty()) {
            resultQueue = diceManager.rollRandomQueue();  // 결과가 없으면 새로 던짐
        }

        mainFrame.getDicePanel().setEnabled(false);  // 윷판 입력 일시 중지

        while (!resultQueue.isEmpty()) {
            YutResult result = resultQueue.poll();  // 하나씩 꺼내서 처리
            int steps = diceManager.convertToSteps(result);  // 이동할 칸 수 계산

            List<Horse> movable = getMovableHorses(steps);  // 이동 가능한 말 필터링
            if (movable.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "이동 가능한 말이 없습니다. 턴을 건너뜁니다.");
                continue;
            }

            // 사용자에게 이동할 말 선택하도록 요청
            Horse selected = mainFrame.promptHorseSelection(movable, steps);
            if (selected == null) {
                mainFrame.getDicePanel().setEnabled(true);  // 선택 취소 시 입력 복원
                return;
            }

            // 말 이동 수행
            selected.move(steps, board.getNodes());

            // 도착한 위치에서 잡기/그룹핑 처리
            for (Horse other : board.getAllHorses()) {
                if (selected.isGroupable(other)) {
                    selected.groupWith(other);  // 같은 팀이면 그룹핑
                } else if (selected.isCaptured(other)) {
                    other.reset();  // 다른 팀 말이면 잡고 리셋
                }
            }

            // UI 갱신
            mainFrame.getBoardPanel().updatePiecePosition(null, selected.getPosition(), selected.getId(), selected.getTeamColor());
        }

        checkWin();      // 승리 여부 확인
        nextTurn();      // 턴 넘기기
        mainFrame.getDicePanel().setEnabled(true);  // 윷 입력 다시 활성화
    }

    // 윷 결과 1개만 처리 (외부에서 직접 호출 가능)
    public void processRollResult(YutResult result) {
        int steps = diceManager.convertToSteps(result);
        List<Horse> movable = getMovableHorses(steps);

        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "이동 가능한 말이 없습니다. 턴을 건너뜁니다.");
            nextTurn();
            return;
        }

        Horse selected = mainFrame.promptHorseSelection(movable, steps);
        if (selected == null) return;

        selected.move(steps, board.getNodes());

        // 잡기 및 그룹핑 처리
        for (Horse other : board.getAllHorses()) {
            if (selected.isGroupable(other)) {
                selected.groupWith(other);
            } else if (selected.isCaptured(other)) {
                other.reset();
            }
        }

        mainFrame.getBoardPanel().updatePiecePosition(
                null, selected.getPosition(), selected.getId(), selected.getTeamColor()
        );

        checkWin();
        nextTurn();
    }

    // 게임 재시작: 보드와 UI 상태 초기화
    public void restartGame() {
        board.resetAll();
        currentPlayerIndex = 0;
        updateCurrentPlayerLabel();
        mainFrame.getBoardPanel().resetBoardUI();
        mainFrame.getDicePanel().showResult(new ArrayList<>());
    }

    // 현재 차례의 팀 객체 반환
    public Team getCurrentTeam() {
        return teams.get(currentPlayerIndex);
    }

    // 현재 팀의 말들 중에서 steps만큼 이동 가능한 말 리스트 반환
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

    // 윷 버튼을 눌렀을 때 호출되는 메서드: 수동/랜덤 입력 분기
    public void handleDiceRoll() {
        if (mainFrame.getDicePanel().isRandomMode()) {
            // 랜덤 모드: 전체 턴 처리 (윷/모 보너스 포함 가능)
            startTurn();
        } else {
            // ▶ 수동 모드: 입력값 한 번만 처리
            try {
                int input = Integer.parseInt(mainFrame.getDicePanel().getManualInputText().trim());
                YutResult result = diceManager.rollManual(input);
                mainFrame.getDicePanel().showResult(List.of(result)); // 결과 표시
                processRollResult(result); // 단일 처리
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(mainFrame, "입력이 잘못되었습니다 (-1 ~ 5 입력 가능)", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 현재 플레이어의 이름을 메인 프레임에 표시
    private void updateCurrentPlayerLabel() {
        mainFrame.setCurrentPlayer(getCurrentTeam().getName());
    }

}
