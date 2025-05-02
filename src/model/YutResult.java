// model/YutResult.java — 윷 결과 정의 및 한글 표기 지원
package model;

public enum YutResult {
    BACKDO("빽도", -1),
    DO("도", 1),
    GAE("개", 2),
    GEOL("걸", 3),
    YUT("윷", 4),
    MO("모", 5);

    private final String displayName;
    private final int moveCount;

    YutResult(String displayName, int moveCount) {
        this.displayName = displayName;
        this.moveCount = moveCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public static YutResult fromString(String name) {
        return switch (name) {
            case "빽도" -> BACKDO;
            case "도" -> DO;
            case "개" -> GAE;
            case "걸" -> GEOL;
            case "윷" -> YUT;
            case "모" -> MO;
            default -> throw new IllegalArgumentException("Unknown YutResult: " + name);
        };
    }

    public static YutResult random() {
        int[] outcomes = {0, 1, 1, 1, 1, 2, 2, 3, 4, 5}; // 확률 조정용
        int pick = outcomes[(int) (Math.random() * outcomes.length)];
        return switch (pick) {
            case 0 -> BACKDO;
            case 1 -> DO;
            case 2 -> GAE;
            case 3 -> GEOL;
            case 4 -> YUT;
            case 5 -> MO;
            default -> DO;
        };
    }
}