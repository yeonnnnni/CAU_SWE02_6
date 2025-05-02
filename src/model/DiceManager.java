package model;
import java.util.Random;
public class DiceManager {
    private final Random rand = new Random();
    public YutResult throwRandom() {
        int r = rand.nextInt(16);
        if (r == 0) return YutResult.BACKDO;
        if (r <= 5) return YutResult.DO;
        if (r <= 9) return YutResult.GAE;
        if (r <= 12) return YutResult.GEOL;
        if (r == 13 || r == 14) return YutResult.YUT;
        return YutResult.MO;
    }
    public YutResult fromString(String s) {
        return YutResult.valueOf(s.toUpperCase());
    }
}