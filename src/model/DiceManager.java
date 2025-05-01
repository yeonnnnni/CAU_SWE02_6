package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
- 랜덤 주사위 던지기 및 수동 입력 처리
- 윷/모가 나온 경우 보너스 턴(중복 이동) 처리까지 지원

사용 흐름:
1. rollRandomSequence() → 주사위를 던지고 YutResult 리스트 반환 (윷/모 연속 반영)
2. rollManual(int value) → 입력 숫자를 YutResult로 변환
3. convertToSteps(YutResult) → 윷 결과를 실제 이동 칸 수(int)로 변환
*/
public class DiceManager {

    private Random random = new Random();

    //주사위 던지기
    //윷 또는 모가 나오면 보너스 턴: 한 번 더 던짐
    //도/개/걸/백도 중 하나가 나오면 종료
    public List<YutResult> rollRandomSequence() {
        List<YutResult> results = new ArrayList<>();
        while (true) {
            YutResult result = rollOnce();
            results.add(result);
            if (result != YutResult.YUT && result != YutResult.MO) break; // 윷 or 모 아니면 중단
        }
        return results;
    }

    // 한 번 던져서 YutResult 하나 생성
    private YutResult rollOnce() {
        int val = random.nextInt(6);
        return YutResult.values()[val];
    }

    //사용자가 입력한 숫자(-1~5)로 결과 매칭
    public YutResult rollManual(int value) {
        switch (value) {
            case -1: return YutResult.BACKDO;
            case 1: return YutResult.DO;
            case 2: return YutResult.GAE;
            case 3: return YutResult.GEOL;
            case 4: return YutResult.YUT;
            case 5: return YutResult.MO;
            default:
                throw new IllegalArgumentException("유효하지 않은 윷 입력값: " + value);
        }
    }

    //YutResult를 이동 칸수(int)로 변환
    public int convertToSteps(YutResult result) {
        return result.getSteps();
    }
}

