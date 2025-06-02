import builder.BoardFactory;
import controller.Board;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquareScenarioTest {

    Board board;
    Team team;
    Horse horse;
    List<Node> nodes;

    @BeforeEach
    void setup() {
        // 1. 사각형 보드 초기화
        board = new Board();
        List<Node> squareNodes = BoardFactory.create("square").buildBoard();
        board.setNodes(squareNodes);

        // 2. 팀 초기화
        team = new Team(0, "TeamA", Color.RED, 1, "square");
        board.registerTeam(team);

        // 3. 첫 번째 말 가져오기
        horse = board.getHorsesForPlayer("0").get(0);

        // 4. 말의 시작 위치를 A2로 설정
        Node start = squareNodes.stream()
                .filter(n -> n.getId().equals("A2"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("시작 노드 A2를 찾을 수 없습니다."));

        horse.setPosition(start);
        horse.setState(HorseState.MOVING); // 이동 가능한 상태로 설정
        nodes = squareNodes;
    }

    private static final ShortcutDecisionProvider NO_SHORTCUT = d -> false;
    private static final ShortcutDecisionProvider YES_SHORTCUT = d -> true;

    @Test
    void NormalMove() {
        // 목적: 말이 직진 경로로 정상적으로 이동하는지 확인
        horse.move(2, nodes, "square", NO_SHORTCUT);
        assertEquals("N1", horse.getPosition().getId());
    }

    @Test
    void BackDoMove() {
        // 목적: 백도 처리 시 말이 역방향으로 이동하는지 확인
        horse.move(1, nodes, "square", NO_SHORTCUT); // 한 칸 이동
        String firstNode = horse.getPosition().getId();
        horse.move(-1, nodes, "square", NO_SHORTCUT); // 백도
        String secondNode = horse.getPosition().getId();
        assertNotEquals(firstNode, secondNode);
    }

    @Test
    void EnterShortcut() {
        // 목적: D2에서 지름길 진입 여부 확인
        horse.move(5, nodes, "square", YES_SHORTCUT);
        horse.move(1, nodes, "square", YES_SHORTCUT);
        assertTrue(horse.getPosition().getId().startsWith("D1"));
    }

    @Test
    void BestPathEntry() {
        // 목적: 중심 노드(00)에서 최적 경로 A0로 진입하는지 확인
        horse.move(5, nodes, "square", YES_SHORTCUT);
        horse.move(3, nodes, "square", YES_SHORTCUT);
        assertTrue(horse.getPosition().getId().startsWith("A") ||
                horse.getPosition().getId().equals("00"));
    }

    @Test
    void SkipThroughShortcut() {
        // 목적: N2에서 걸이 나올 경우 D2를 경유해 N4로 이동하는지 확인
        horse.move(3, nodes, "square", YES_SHORTCUT);
        horse.move(3, nodes, "square", YES_SHORTCUT);
        assertEquals("N4", horse.getPosition().getId());
    }

    @Test
    void ReachGoal() {
        // 목적: A2에 도착하면 말이 도착 처리되는지 확인
        horse.setPosition(nodes.stream().filter(n -> n.getId().equals("A1")).findFirst().get());
        horse.move(1, nodes, "square", NO_SHORTCUT);
        assertEquals("A2", horse.getPosition().getId());
    }

    @Test
    void TeamVictory() {
        // 목적: 팀의 모든 말이 도착 시 승리 조건이 만족되는지 확인
        for (Horse h : team.getHorses()) {
            h.setPosition(nodes.stream().filter(n -> n.getId().equals("A1")).findFirst().get());
            h.move(1, nodes, "square", NO_SHORTCUT);
        }
        assertTrue(team.isWin());
    }
}