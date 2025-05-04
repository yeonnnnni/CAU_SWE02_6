package model;

import java.util.Random;

public class DiceManager {
    private final Random random = new Random();

    public YutResult roll() {
        // 윷 4개를 던져서 앞면 개수를 셈
        int frontCount = 0;
        for (int i = 0; i < 4; i++) {
            boolean isFront = random.nextBoolean(); // true: 앞면, false: 뒷면
            if (isFront) frontCount++;
        }

        // 결과 해석
        switch (frontCount) {
            case 0: return YutResult.MO;
            case 1: return YutResult.DO;
            case 2: return YutResult.GAE;
            case 3: return YutResult.GEOL;
            case 4: return YutResult.YUT;
            default: return YutResult.BACKDO; // 예외 처리용 (실제로는 안 나옴)
        }
    }
}
