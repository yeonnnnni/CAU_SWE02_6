import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import controller.Board;
import controller.GameManager;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class CoreClassesTest {

    Board board;
    Team team1, team2;
    Horse horse1, horse2;
    Node node1, node2, nodeGoal;
    GameManager gameManager;

    @BeforeEach
    public void setup() {
        team1 = new Team(0, "팀A", Color.RED, 2, "square");
        team2 = new Team(1, "팀B", Color.BLUE, 2, "square");

        board = new Board();

        node1 = new Node("N1");
        node2 = new Node("N2");
        nodeGoal = new Node("A2");
        nodeGoal.setGoal(true);

        node1.addNextNode(node2);
        node2.addNextNode(nodeGoal);

        List<Node> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(nodeGoal);

        board.setNodes(nodes);

        board.getAllHorses().addAll(team1.getHorses());
        board.getAllHorses().addAll(team2.getHorses());

        // 말 객체는 팀에서 꺼내 사용
        horse1 = team1.getHorses().get(0);
        horse2 = team1.getHorses().get(0);

        board.registerTeam(team1);
        board.registerTeam(team2);

        board.resetAll();

        gameManager = new GameManager(null, board, null, List.of(team1, team2), "square");
    }


    @Test
    public void testTeamInitialization() {
        assertEquals(2, team1.getHorses().size());
        assertEquals("팀A", team1.getName());
        assertEquals(Color.RED, team1.getColor());
    }

    @Test
    public void testHorseMoveAndFinish() {
        // 말이 처음에 위치 없으면 start node인 N1에 배치
        assertNull(horse1.getPosition());
        horse1.setPosition(node1);
        assertEquals("N1", horse1.getPosition().getId());

        // 말 1칸 이동 -> N2
        boolean captured = horse1.move(1, board.getNodes(), "square");
        assertEquals("N2", horse1.getPosition().getId());
        assertFalse(captured);

        // 말 1칸 이동 -> A2 (완주 노드)
        horse1.move(1, board.getNodes(), "square");
        assertTrue(horse1.isFinished());
    }

    @Test
    public void testBoardCanMove() {
        // 처음 상태 말은 이동 가능
        assertTrue(board.canMove(horse1, 1));
        // 말 상태 FINISHED 시 이동 불가
        horse1.setState(HorseState.FINISHED);
        assertFalse(board.canMove(horse1, 1));
    }

    @Test
    public void testNodeAddRemoveHorse() {
        node1.addHorse(horse1);
        assertTrue(node1.getHorsesOnNode().contains(horse1));

        node1.removeHorse(horse1);
        assertFalse(node1.getHorsesOnNode().contains(horse1));
    }

    @Test
    public void testTeamWinCondition() {
        // 모든 말 FINISHED -> true
        for (Horse h : team1.getHorses()) {
            h.setState(HorseState.FINISHED);
        }
        assertTrue(team1.isWin());

        // 하나라도 FINISHED가 아니면 false
        team1.getHorses().get(0).setState(HorseState.MOVING);
        assertFalse(team1.isWin());
    }


    @Test
    public void testGameManagerGetMovableHorses() {
        horse1.setPosition(node1);
        horse2.setPosition(node1);
        horse1.setState(HorseState.MOVING);
        horse2.setState(HorseState.MOVING);

        List<Horse> movable = gameManager.getMovableHorses(1);
        assertTrue(movable.contains(horse1));
        assertTrue(movable.contains(horse2));

        horse1.setState(HorseState.FINISHED);
        movable = gameManager.getMovableHorses(1);
        assertFalse(movable.contains(horse1));
    }


    @Test
    public void testHorseGrouping() {
        horse1 = team1.getHorses().get(0);
        horse2 = team1.getHorses().get(1);

        horse1.setPosition(node1);
        horse2.setPosition(node1);

        horse1.groupWith(horse2);

        System.out.println("horse1 grouped horses: " + horse1.getGroupedHorses());
        System.out.println("horse2 grouped horses: " + horse2.getGroupedHorses());
        System.out.println("horse1 position: " + horse1.getPosition().getId());
        System.out.println("horse2 position: " + horse2.getPosition().getId());

        assertTrue(horse1.getGroupedHorses().contains(horse2));
        assertEquals(horse1.getPosition(), horse2.getPosition());
    }


}
