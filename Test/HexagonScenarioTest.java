import builder.BoardFactory;
import controller.Board;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HexagonScenarioTest {

    Board board;
    Team team;
    Horse horse;
    List<Node> nodes;

    @BeforeEach
    void setup() {
        // 육각형 보드 및 팀, 말 초기화
        board = new Board();
        nodes = BoardFactory.create("hexagon").buildBoard();
        board.setNodes(nodes);

        team = new Team(0, "TeamA", Color.GREEN, 1, "hexagon");
        board.registerTeam(team);

        horse = board.getHorsesForPlayer("0").get(0);

        Node start = find("A2"); // 시작 노드 설정
        horse.setPosition(start);
        horse.setState(HorseState.MOVING);
    }

    private static final ShortcutDecisionProvider NO_SHORTCUT = d -> false;
    private static final ShortcutDecisionProvider YES_SHORTCUT = d -> true;

    @Test
    void NormalMove() {
        // 일반 경로로 2칸 직진 이동 → N1 도달
        horse.move(2, nodes, "hexagon", NO_SHORTCUT);
        assertEquals("N1", horse.getPosition().getId());
    }

    @Test
    void BackDoMove() {
        // 백도 상황 확인: 한 칸 이동 후 -1칸 후진 → 위치 변경 확인
        horse.move(1, nodes, "hexagon", NO_SHORTCUT);
        String first = horse.getPosition().getId();
        horse.move(-1, nodes, "hexagon", NO_SHORTCUT);
        String second = horse.getPosition().getId();
        assertNotEquals(first, second);
    }

    @Test
    void EnterShortcut_D2() {
        // D2에서 지름길 진입 → D1로 이동
        horse.setPosition(find("D2"));
        horse.move(1, nodes, "hexagon", YES_SHORTCUT);
        assertEquals("D1", horse.getPosition().getId());
    }

    @Test
    void EnterShortcut_C2() {
        // C2에서 지름길 진입 → C1로 이동
        horse.setPosition(find("C2"));
        horse.move(1, nodes, "hexagon", YES_SHORTCUT);
        assertEquals("C1", horse.getPosition().getId());
    }

    @Test
    void SkipThroughShortcut() {
        // N3에서 개 → D2 밟고 N4 도달 확인
        horse.setPosition(find("N3"));
        horse.move(2, nodes, "hexagon", YES_SHORTCUT);
        assertEquals("N4", horse.getPosition().getId());
    }

    @Test
    void OptimalToA0() {
        // 중심(00) 도달 후 A0 방향으로 진입 확인
        horse.setPosition(find("C0"));
        horse.move(1, nodes, "hexagon", YES_SHORTCUT); // → 00
        horse.move(1, nodes, "hexagon", YES_SHORTCUT); // → A0
        assertTrue(horse.getPosition().getId().startsWith("A"));
    }

    @Test
    void AlternateToB0() {
        // 00에서 B0 방향으로 진입하는 경로 확인
        horse.setPosition(find("D0"));
        horse.move(2, nodes, "hexagon", NO_SHORTCUT);
        assertEquals("B0", horse.getPosition().getId());
    }

    @Test
    void ReachGoal() {
        // A2에 도달하면 도착 상태로 처리되는지 확인
        horse.setPosition(find("A1"));
        horse.move(1, nodes, "hexagon", NO_SHORTCUT);
        assertEquals("A2", horse.getPosition().getId());
        assertTrue(horse.isFinished());
    }

    @Test
    void TeamVictory() {
        // 팀의 모든 말이 도착했을 때 승리 조건 확인
        for (Horse h : team.getHorses()) {
            h.setPosition(find("A1"));
            h.move(1, nodes, "hexagon", NO_SHORTCUT);
        }
        assertTrue(team.isWin());
    }

    // 노드 ID로 탐색하는 유틸리티 메서드
    private Node find(String id) {
        return nodes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노드 " + id + " 없음"));
    }
}