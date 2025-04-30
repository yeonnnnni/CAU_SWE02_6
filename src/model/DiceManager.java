package model;

import java.util.Random;

public class DiceManager {
    private static final Random random = new Random();

    public static YutResult rollYut() {
        int value = random.nextInt(6);
        return YutResult.values()[value];
    }
}