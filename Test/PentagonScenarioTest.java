import builder.BoardFactory;
import controller.Board;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PentagonScenarioTest {

    Board board;
    Team team;
    Horse horse;
    List<Node> nodes;

    @BeforeEach
    void setup() {
        // 오각형 보드 및 팀, 말 초기화
        board = new Board();
        nodes = BoardFactory.create("pentagon").buildBoard();
        board.setNodes(nodes);

        team = new Team(0, "TeamA", Color.BLUE, 1, "pentagon");
        board.registerTeam(team);

        horse = board.getHorsesForPlayer("0").get(0);

        // 시작 위치: A2 노드
        Node start = nodes.stream()
                .filter(n -> n.getId().equals("A2"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("A2 시작 노드 없음"));
        horse.setPosition(start);
        horse.setState(HorseState.MOVING);
    }

    private static final ShortcutDecisionProvider NO_SHORTCUT = d -> false;
    private static final ShortcutDecisionProvider YES_SHORTCUT = d -> true;

    @Test
    void NormalMove() {
        // 일반 경로 직진 이동 (2칸 → N1 도착)
        horse.move(2, nodes, "pentagon", NO_SHORTCUT);
        assertEquals("N1", horse.getPosition().getId());
    }

    @Test
    void BackDoMove() {
        // 백도 이동 확인 (1칸 전진 후 -1칸 후진 → 위치 변화 확인)
        horse.move(1, nodes, "pentagon", NO_SHORTCUT);
        String before = horse.getPosition().getId();
        horse.move(-1, nodes, "pentagon", NO_SHORTCUT);
        String after = horse.getPosition().getId();
        assertNotEquals(before, after);
    }

    @Test
    void EnterShortcutFromD2() {
        // D2 지름길 진입 확인 (D2 → D1)
        horse.setPosition(find("D2"));
        horse.move(1, nodes, "pentagon", YES_SHORTCUT);
        assertEquals("D1", horse.getPosition().getId());
    }

    @Test
    void EnterShortcutFromC2() {
        // C2 지름길 진입 확인 (C2 → C1)
        horse.setPosition(find("C2"));
        horse.move(1, nodes, "pentagon", YES_SHORTCUT);
        assertEquals("C1", horse.getPosition().getId());
    }

    @Test
    void SkipViaShortcut() {
        // 지름길 경유로 노드 건너뜀 확인 (N3 → D2 경유 → N4)
        horse.setPosition(find("N3"));
        horse.move(2, nodes, "pentagon", YES_SHORTCUT);
        assertEquals("N4", horse.getPosition().getId());
    }

    @Test
    void OptimalPathToA0() {
        // 중심 진입 후 A0로 진입 확인 (C0 → 00 → A0)
        horse.setPosition(find("C0"));
        horse.move(1, nodes, "pentagon", YES_SHORTCUT); // 00
        horse.move(1, nodes, "pentagon", YES_SHORTCUT); // → A0 or B0
        assertTrue(horse.getPosition().getId().startsWith("A"));
    }

    @Test
    void AlternateCenterPathToB0() {
        // 중심 경유 후 B0 경로 선택 확인 (D0 → 00 → B0)
        horse.setPosition(find("D0"));
        horse.move(2, nodes, "pentagon", NO_SHORTCUT);
        assertEquals("B0", horse.getPosition().getId());
    }

    @Test
    void ReachGoalA2() {
        // 도착 노드 A2 도달 및 FINISHED 상태 확인
        horse.setPosition(find("A1"));
        horse.move(1, nodes, "pentagon", NO_SHORTCUT);
        assertEquals("A2", horse.getPosition().getId());
        assertTrue(horse.isFinished());
    }

    @Test
    void TeamVictory() {
        // 팀의 모든 말이 도착했을 때 승리 조건 만족 확인
        for (Horse h : team.getHorses()) {
            h.setPosition(find("A1"));
            h.move(1, nodes, "pentagon", NO_SHORTCUT);
        }
        assertTrue(team.isWin());
    }

    // id로 노드 탐색 (존재하지 않으면 예외)
    private Node find(String id) {
        return nodes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노드 " + id + " 없음"));
    }
}