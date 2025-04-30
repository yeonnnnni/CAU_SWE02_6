package model;

public enum YutResult {
    BACKDO(-1), DO(1), GAE(2), GUL(3), YUT(4), MO(5);

    private final int steps;
    YutResult(int steps) { this.steps = steps; }
    public int getSteps() { return steps; }
}