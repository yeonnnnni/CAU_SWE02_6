import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import view.JavaFX.TeamScoreFX;

// TeamScoreFX 클래스의 단위 테스트 클래스
public class TeamScoreFXTest {

    // 테스트 대상 객체 선언
    TeamScoreFX teamScore;

    // 각 테스트 실행 전에 매번 새로운 TeamScoreFX 인스턴스를 생성함
    @BeforeEach
    void setup() {
        // "Team A"라는 이름과 초기 점수: 대기 3, 이동 2, 도착 1로 초기화
        teamScore = new TeamScoreFX("Team A", 3, 2, 1);
    }

    // 초기 점수가 제대로 설정되었는지 확인하는 테스트
    @Test
    void testInitialScoreValues() {
        assertEquals(3, teamScore.waitingProperty().get());  // 대기 중 말 수 확인
        assertEquals(2, teamScore.movingProperty().get());   // 이동 중 말 수 확인
        assertEquals(1, teamScore.finishedProperty().get()); // 도착한 말 수 확인
    }

    // 도착한 말 수를 업데이트했을 때 정확히 반영되는지 확인
    @Test
    void testUpdateFinishedScore() {
        teamScore.finishedProperty().set(5);                 // 도착 수를 5로 변경
        assertEquals(5, teamScore.finishedProperty().get()); // 변경된 값이 반영되었는지 확인
    }
}
