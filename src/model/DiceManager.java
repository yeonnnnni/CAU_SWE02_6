package model;

import java.util.*;

public class DiceManager {
    private final Random random = new Random();

    public List<YutResult> rollRandomSequence() {
        List<YutResult> results = new ArrayList<>();
        while (true) {
            YutResult result = rollOnce();
            results.add(result);
            if (result != YutResult.YUT && result != YutResult.MO) break;
        }
        return results;
    }

    public YutResult rollManual(int value) {
        for (YutResult y : YutResult.values()) {
            if (y.getSteps() == value) return y;
        }
        throw new IllegalArgumentException("Invalid value for manual roll: " + value);
    }

    private YutResult rollOnce() {
        int val = random.nextInt(6);
        switch (val) {
            case 0: return YutResult.BACKDO;
            case 1: return YutResult.DO;
            case 2: return YutResult.GAE;
            case 3: return YutResult.GEOL;
            case 4: return YutResult.YUT;
            case 5: return YutResult.MO;
            default: throw new IllegalStateException("Unexpected value: " + val);
        }
    }

    public int convertToSteps(YutResult result) {
        return result.getSteps();
    }
}
