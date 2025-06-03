package model;

import java.util.*;

// 윷놀이에서 윷을 던지고 결과를 반환하는 기능을 담당하는 클래스
public class DiceManager {
    // 난수 생성을 위한 Random 인스턴스 생성 (랜덤 윷 던지기용)
    private final Random random = new Random();

    /**
     * 랜덤 윷 던지기 시퀀스를 생성하는 메서드
     * - 윷(YUT) 또는 모(MO)가 나오면 한 번 더 던짐 (추가 턴 규칙)
     * - 백도/도/개/걸 중 하나가 나올 때까지 반복
     */
    public List<YutResult> rollRandomSequence() {
        List<YutResult> results = new ArrayList<>(); // 윷 결과 리스트
        while (true) {
            YutResult result = rollOnce();          // 한 번 던진 결과
            results.add(result);                    // 결과 저장
            if (result != YutResult.YUT && result != YutResult.MO) break;   // 윷 또는 모가 나오면 한 번 더 던짐
        }
        return results;
    }

    /**
     * 수동 입력으로 특정 윷 값을 지정할 수 있게 하는 메서드
     * - 입력한 숫자에 해당하는 윷 결과를 반환
     * - 예: 1 → 도, 5 → 모
     */
    public YutResult rollManual(int value) {
        for (YutResult y : YutResult.values()) {    // 모든 윷 결과 순회
            if (y.getSteps() == value) return y;    // 일치하는 스텝 수 찾기
        }
        // 올바르지 않은 값이면 예외 발생
        throw new IllegalArgumentException("Invalid value for manual roll: " + value);
    }

    /**
     * 실제로 랜덤하게 윷 하나를 던지는 메서드
     * - 0~5 사이의 숫자를 생성하여 각 윷 결과에 매핑
     */
    private YutResult rollOnce() {
        int val = random.nextInt(6);    // 0~5 중 난수 생성
        switch (val) {
            case 0: return YutResult.BACKDO;    // 0: 백도 (-1칸)
            case 1: return YutResult.DO;        // 1: 도 (1칸)
            case 2: return YutResult.GAE;       // 2: 개 (2칸)
            case 3: return YutResult.GEOL;      // 3: 걸 (3칸)
            case 4: return YutResult.YUT;       // 4: 윷 (4칸, 보너스 턴)
            case 5: return YutResult.MO;        // 5: 모 (5칸, 보너스 턴)
            default:
                // 이론상 발생할 수 없지만, 안전장치로 예외 처리
                throw new IllegalStateException("Unexpected value: " + val);
        }
    }
    /**
     * YutResult(enum) → 이동 칸 수(int)로 변환해주는 메서드
     * - UI나 말 이동 로직에서 사용됨
     */
    public int convertToSteps(YutResult result) {
        return result.getSteps();
    }
}
