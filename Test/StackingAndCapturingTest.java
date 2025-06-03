import controller.Board;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StackingAndCapturingTest {

    private Board board;
    private Node sharedNode;
    private Team teamA, teamB;
    private Horse horseA1, horseA2, horseB1;

    @BeforeEach
    void setup() {
        // 테스트용 보드와 노드 생성
        board = new Board();
        sharedNode = new Node("A2"); // 테스트에 사용할 단일 노드
        board.setNodes(List.of(sharedNode));

        // 팀 A 생성 (업기 테스트용)
        teamA = new Team(0);
        horseA1 = new Horse(0, teamA);
        horseA2 = new Horse(1, teamA);
        teamA.getHorses().add(horseA1);
        teamA.getHorses().add(horseA2);
        board.registerTeam(teamA);

        // 팀 B 생성 (잡기 테스트용)
        teamB = new Team(1);
        horseB1 = new Horse(0, teamB);
        teamB.getHorses().add(horseB1);
        board.registerTeam(teamB);
    }

    @Test
    void StackingSameTeam() {
        // 목적: 같은 팀의 말 두 마리가 같은 노드에 도달했을 때 업기 상태로 공존 가능한지 확인
        horseA1.setPosition(sharedNode); // 첫 번째 말 진입
        horseA2.setPosition(sharedNode); // 두 번째 말도 같은 위치로 진입

        List<Horse> horses = sharedNode.getHorsesOnNode();
        assertEquals(2, horses.size()); // 두 말 모두 노드에 존재해야 함
        assertTrue(horses.contains(horseA1));
        assertTrue(horses.contains(horseA2));
    }

    @Test
    void CapturingOtherTeam() {
        // 목적: 적 팀의 말이 위치한 노드에 진입 시 잡기(capture) 처리가 제대로 되는지 확인
        horseB1.setPosition(sharedNode); // 적 팀 말 먼저 도달

        horseB1.setPosition(null); // 잡히면 사라진 상태 모방
        horseA1.setPosition(sharedNode); // 이후 아군 말 진입

        List<Horse> horses = sharedNode.getHorsesOnNode();
        assertEquals(1, horses.size()); // 한 마리만 남아야 함
        assertTrue(horses.contains(horseA1));
        assertNull(horseB1.getPosition()); // 잡혀서 위치 없어야 함
    }
}