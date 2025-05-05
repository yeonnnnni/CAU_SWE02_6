package model;

//Enum 생성 → (BACKDO: -1, DO: 1, GAE: 2, GEOL: 3, YUT: 4, MO: 5)
//결과 필요할 때 → result.getSteps() 호출 → 이동할 칸수 리턴

//YutResult Enum : 윷 결과를 하나의 타입(Enum)으로 묶어서,
//각 결과가 이동해야 할 칸수까지 함께 저장하는 구조
public enum YutResult {
    BACKDO(-1), DO(1), GAE(2), GEOL(3), YUT(4), MO(5);

    private final int steps;

    YutResult(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
}

