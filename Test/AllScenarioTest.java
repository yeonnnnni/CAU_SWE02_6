import builder.BoardFactory;
import builder.BoardBuilder;
import controller.Board;
import controller.GameManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GameUIBase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AllScenarioTest {

    DiceManager diceManager;

    @BeforeEach
    void setUp() {
        // 각 테스트 전 DiceManager 초기화
        diceManager = new DiceManager();
    }

    @Test
    void testBoardTypeSelection() {
        // 목적: BoardFactory가 각 보드 타입에 대해 builder를 생성하고, 보드를 반환하는지 확인
        BoardBuilder square = BoardFactory.create("square");
        BoardBuilder pentagon = BoardFactory.create("pentagon");
        BoardBuilder hexagon = BoardFactory.create("hexagon");

        // 각 보드 타입의 buildBoard 결과가 null이 아니어야 함
        assertNotNull(square.buildBoard());
        assertNotNull(pentagon.buildBoard());
        assertNotNull(hexagon.buildBoard());
    }

    @Test
    void testRandomDiceRoll() {
        // 목적: 랜덤 윷 결과가 반환되며, 모든 결과가 -1~5 사이인지 확인
        List<YutResult> results = diceManager.rollRandomSequence();

        // 결과가 비어 있지 않아야 하며
        assertFalse(results.isEmpty());

        // 각 YutResult의 steps가 -1 이상, 5 이하인지 확인
        for (YutResult result : results) {
            int steps = result.getSteps();
            assertTrue(steps >= -1 && steps <= 5);
        }
    }

    @Test
    void testManualDiceRollValidInputs() {
        // 목적: 수동 윷 입력이 정상적으로 처리되는지 확인
        int[] validInputs = {-1, 1, 2, 3, 4, 5};

        for (int i : validInputs) {
            YutResult result = diceManager.rollManual(i);
            assertEquals(i, result.getSteps());
        }
    }

    @Test
    void testManualDiceRollInvalidInputs() {
        // 목적: 유효하지 않은 수동 입력이 예외를 발생시키는지 확인

        // 6은 허용 범위 초과 → IllegalArgumentException 발생 예상
        assertThrows(IllegalArgumentException.class, () -> diceManager.rollManual(6));

        // 문자 입력은 UI 레벨에서 발생하는 NumberFormatException 시뮬레이션
        assertThrows(NumberFormatException.class, () -> Integer.parseInt("a"));
    }

    @Test
    void testGameRestart() {
        // 목적: GameManager의 restartGame이 말 상태를 초기화하는지 확인

        DummyUI ui = new DummyUI();
        Board board = new Board();
        Team team = new Team(0);

        // FINISHED 상태인 말 등록
        Horse horse = new Horse(0, team);
        horse.setState(HorseState.FINISHED);
        team.getHorses().add(horse);
        board.registerTeam(team);

        GameManager manager = new GameManager(ui, board, new DiceManager(), List.of(team), "square", d -> false);

        // 게임 재시작
        manager.restartGame();

        // 모든 말이 FINISHED 상태가 아니어야 함
        for (Horse h : team.getHorses()) {
            assertFalse(h.isFinished());
        }
    }

    @Test
    void testGameEndCondition() {
        // 목적: 모든 말이 FINISHED 상태일 경우 팀이 승리했다고 판단하는지 확인
        Team team = new Team(0);

        // 팀 내 모든 말을 FINISHED 상태로 설정
        team.getHorses().forEach(h -> h.setState(HorseState.FINISHED));

        // isWin() 호출 시 true 반환
        assertTrue(team.isWin());
    }

    // ✅ GameUIBase 최소 구현 더미 클래스
    static class DummyUI implements GameUIBase {

        @Override
        public boolean isRandomMode() {
            return true; // 랜덤 모드로 고정
        }

        @Override
        public String getManualInput() {
            return "1"; // 수동 입력 시 1 반환
        }

        @Override
        public void showDiceResult(List<YutResult> results) {
            // UI 생략
        }

        @Override
        public void setRollListener(Runnable listener) {
            // 무시
        }

        @Override
        public void showMessage(String message) {
            // 무시
        }

        @Override
        public boolean confirmShortcut(String direction) {
            return false; // 기본적으로 지름길 사용 안 함
        }

        @Override
        public Horse selectHorse(List<Horse> candidates, int steps) {
            return candidates.isEmpty() ? null : candidates.get(0);
        }

        @Override
        public YutResult chooseYutResult(List<YutResult> options) {
            return options.isEmpty() ? null : options.get(0);
        }

        @Override
        public boolean promptRestart(String winnerName) {
            return false; // 재시작 안 함
        }

        @Override
        public void updatePiece(Node from, Node to) {
            // 무시
        }

        @Override
        public void setDiceRollEnabled(boolean enabled) {
            // 무시
        }

        @Override
        public void setCurrentPlayer(String name) {
            // 무시
        }

        @Override
        public Object getBoardPanel() { return null; }

        @Override
        public Object getDicePanel() { return null; }

        @Override
        public Object getScoreboardPanel() { return null; }
    }
}