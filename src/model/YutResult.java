package model;

//윷 결과를 열거형으로 정의하고, 각 결과에 이동할 칸 수를 연결합니다.
public enum YutResult {
    BACKDO(-1),
    DO(1),
    GAE(2),
    GEOL(3),
    YUT(4),
    MO(5);

    private final int steps;

    YutResult(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return name() + " (" + steps + ")";
    }
}
