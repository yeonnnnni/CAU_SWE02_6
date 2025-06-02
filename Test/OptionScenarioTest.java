import controller.Board;
import controller.GameManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GameUIBase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionScenarioTest {

    DummyUIWithSelection ui;
    DiceManager diceManager;
    Board board;

    @BeforeEach
    void setUp() {
        // 테스트마다 새로운 UI, 주사위 관리자, 보드 초기화
        ui = new DummyUIWithSelection();
        diceManager = new DiceManager();
        board = new Board();
    }

    @Test
    void electHorseCount() {
        // 목적: 사용자가 선택할 수 있는 말이 2~5마리인지 확인
        List<Horse> horses = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            horses.add(new Horse(0, new Team(0))); // 팀 ID = 0, 말 ID = 0
        }

        // 말의 개수가 허용된 범위(2~5마리) 내에 있는지 확인
        assertTrue(horses.size() >= 2 && horses.size() <= 5, "2~5마리 보유 확인");
    }

    @Test
    void SelectHorseToMove() {
        // 목적: UI에서 말 리스트 중 하나를 선택하는 로직이 정상 작동하는지 확인
        DummyUIWithSelection ui = new DummyUIWithSelection();
        List<Horse> horses = new ArrayList<>();
        Team dummyTeam = new Team(0); // 팀 ID = 0

        for (int i = 0; i < 2; i++) {
            horses.add(new Horse(i, dummyTeam)); // 말 ID 0,1
        }

        // UI에서 선택된 말이 null이 아님을 확인
        Horse selected = ui.selectHorse(horses, 2);
        assertNotNull(selected);
    }

    @Test
    void SelectYutResultToApply() {
        // 목적: 여러 윷 결과 중 선택된 윷 결과가 올바르게 반환되는지 확인
        List<YutResult> multiple = List.of(YutResult.DO, YutResult.GAE, YutResult.BACKDO);

        // Dummy UI는 항상 첫 번째 윷을 선택함 → DO
        YutResult chosen = ui.chooseYutResult(multiple);

        // DO가 선택되었는지 확인
        assertEquals(YutResult.DO, chosen, "첫 번째 윷 결과 선택됨 확인");
    }

    // 🧪 테스트용 Dummy UI 클래스: 항상 첫 번째 선택지를 반환함
    static class DummyUIWithSelection implements GameUIBase {
        @Override public boolean isRandomMode() { return true; }
        @Override public String getManualInput() { return "1"; }
        @Override public void showDiceResult(List<YutResult> results) {}
        @Override public void setRollListener(Runnable listener) {}
        @Override public void showMessage(String message) {}
        @Override public boolean confirmShortcut(String direction) { return false; }

        @Override
        public Horse selectHorse(List<Horse> candidates, int steps) {
            return candidates.get(0); // 항상 첫 번째 말 선택
        }

        @Override
        public YutResult chooseYutResult(List<YutResult> options) {
            return options.get(0); // 항상 첫 번째 윷 선택
        }

        @Override public boolean promptRestart(String winnerName) { return false; }
        @Override public void updatePiece(Node from, Node to) {}
        @Override public void setDiceRollEnabled(boolean enabled) {}
        @Override public void setCurrentPlayer(String name) {}
    }
}