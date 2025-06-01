package controller;
import model.*;
import view.GameUI;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final GameUI gameUI;
    private final Board board;
    private final DiceManager diceManager;
    private final List<Team> teams;
    private int currentPlayerIndex;
    private final String boardType;
    private final ShortcutDecisionProvider shortcutDecisionProvider;


    private List<YutResult> remainingResults = new ArrayList<>();
    private boolean capturedThisTurn = false;
    private boolean bonusTurnRequested = false;

    public GameManager(GameUI gameUI, Board board, DiceManager diceManager, List<Team> teams, String boardType, ShortcutDecisionProvider shortcutDecisionProvider) {
        this.gameUI = gameUI;
        this.board = board;
        this.diceManager = diceManager;
        this.teams = teams;
        this.boardType = boardType;
        this.shortcutDecisionProvider = shortcutDecisionProvider;
        this.currentPlayerIndex = 0;
    }

    /**
    * 게임을 새로 시작할 때 호출되는 메서드
    * - 플레이어 인덱스를 0으로 초기화
    * - UI 및 보드 상태 초기화
     */

    public void startGame() {
        currentPlayerIndex = 0;                         // 첫 번째 플레이어로 설정
        updateCurrentPlayerLabel();                     // 상단에 현재 플레이어 표시
        board.resetAll();                               // 모든 말과 노드 상태 초기화
        gameUI.getBoardPanel().resetBoardUI();       // 보드 UI 초기화 (말, 색상 등 리셋)
        gameUI.getDicePanel().showResult(new ArrayList<>()); // 주사위 패널 초기화
        updateScoreboard();                             // 점수판 초기화
    }

    /**
     * 현재 턴을 다음 플레이어에게 넘기는 메서드
     * - 플레이어 인덱스를 하나 증가시켜 순환
     * - UI 업데이트
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % teams.size(); // 다음 플레이어 순환
        updateCurrentPlayerLabel();                     // 현재 플레이어 UI 갱신
        gameUI.getDicePanel().showResult(new ArrayList<>()); // 주사위 결과 초기화
        updateScoreboard();                             // 점수판 갱신
    }

    /**
     * 현재 플레이어가 게임에서 승리했는지 확인하는 메서드
     * - 승리 시 메시지 표시 및 사용자에게 다음 행동 선택 요청
     */
    public void checkWin() {
        Team team = getCurrentTeam();                   // 현재 턴의 팀 확인
        if (team.isWin()) {                             // 승리 조건 충족 여부 확인
            if (gameUI.promptRestart(team.getName())) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }

    // 윷 던지기 처리 (랜덤 or 수동) 및 다음 이동 준비
    public void handleDiceRoll() {
        System.out.println("handleDiceRoll() 시작");

        // 새로운 턴 시작이므로 플래그 초기화
        capturedThisTurn = false;
        bonusTurnRequested = false;

        List<YutResult> results;

        if (gameUI.isRandomMode()) {
            results = diceManager.rollRandomSequence();
        } else {
            try {
                int val = Integer.parseInt(gameUI.getManualInput());
                results = List.of(diceManager.rollManual(val));
            } catch (Exception e) {
                gameUI.showMessage("유효한 숫자를 입력해주세요.");
                return;
            }
        }

        remainingResults.clear();
        remainingResults.addAll(results);
        gameUI.showDiceResult(results);

        promptNextMove();
    }

    // 윷 결과를 기반으로 말 이동 처리 및 후속 이동 반복
    private void promptNextMove() {
        // 모든 윷 결과를 소진한 경우
        if (remainingResults.isEmpty()) {
            checkWin(); // 승리 조건 확인

            // 말 잡은 경우 -> 보너스 턴
            if (capturedThisTurn) {
                System.out.println("보너스 턴 실행 중");
                gameUI.showMessage("말을 잡았습니다! 한 번 더 던집니다.");
                capturedThisTurn = false; // 보너스 턴 플래기 초기화
                handleDiceRoll(); // 보너스 턴
            } else {
                System.out.println("보너스 조건 없음, 턴 종료");
                nextTurn(); // 다음 플레이어로 턴 넘김
            }
            gameUI.setDiceRollEnabled(true);
            return;
        }

        // 사용자에게 사용할 윷 결과 선택 요청
        YutResult selected;
        if (remainingResults.size() == 1) {
            selected = remainingResults.get(0);
        } else {
            selected = gameUI.chooseYutResult(remainingResults);
            if (selected == null) {
                gameUI.setDiceRollEnabled(true);
                return;
            }
        }

        // 윷 결과를 이동 칸 수로 변환
        int steps = diceManager.convertToSteps(selected);

        // 이동 가능한 말 목록을 계산
        List<Horse> movable = getMovableHorses(steps);
        if (movable.isEmpty()) {
            gameUI.showMessage("이동 가능한 말이 없습니다.");
            remainingResults.remove(selected);
            promptNextMove(); // 다음 윷 결과로 이동
            return;
        }

        // 사용자에게 말 선택하도록 요청
        Horse horse = gameUI.selectHorse(movable, steps);
        if (horse == null) {
            gameUI.setDiceRollEnabled(true);
            return;
        }

        Node from = horse.getPosition();
        ShortcutDecisionProvider provider = direction -> gameUI.confirmShortcut(direction);

        boolean captured = horse.move(steps, board.getNodes(), boardType, provider);
        Node to = horse.getPosition();

        //말 잡았을 때 보너스 턴 플래그 설정
        if (captured) {
            capturedThisTurn = true;
            System.out.println(horse.getId() + " 이(가) 상대 말을 잡았습니다. 추가 턴이 부여됩니다.");
            gameUI.refreshBoard();
        }

        gameUI.updatePiece(from, to);
        updateScoreboard();
        remainingResults.remove(selected);
        promptNextMove();
    }

    // 게임 전체 초기화 후 새 게임 준비
    public void restartGame() {
        board.resetAll();
        currentPlayerIndex = 0;
        remainingResults.clear();
        capturedThisTurn = false;
        bonusTurnRequested = false;

        for (Team team : teams) {
            team.resetTeam(); // 각 팀의 말 상태 초기화
        }

        updateCurrentPlayerLabel();
        gameUI.getBoardPanel().resetBoardUI();
        gameUI.getDicePanel().showResult(new ArrayList<>());
        updateScoreboard();

        // UI 상 말 아이콘 완전 제거를 위해 노드들 강제 업데이트
        for (Node node : board.getNodes()) {
            gameUI.updatePiece(node, null);
        }
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
        gameUI.setCurrentPlayer(getCurrentTeam().getName());
    }
    private void updateScoreboard() {
        gameUI.getScoreboardPanel().updateScoreboard(teams);
    }
}
