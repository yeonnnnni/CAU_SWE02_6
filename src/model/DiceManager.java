package model;

import java.util.*;

//윷 던지기 처리 클래스: 랜덤 모드와 수동 입력 모두 지원합니다.
public class DiceManager {
    private final Random random = new Random();
    private Queue<YutResult> resultQueue = new LinkedList<>();

    //윷을 한 번 던져 결과 생성
    private YutResult rollOnce() {
        int val = random.nextInt(6); // 0~5
        return YutResult.values()[val];
    }


    //윷을 랜덤으로 연속 던지기 (윷/모가 나오면 반복)
    public Queue<YutResult> rollRandomQueue() {
        resultQueue.clear();
        while (true) {
            YutResult result = rollOnce();
            resultQueue.add(result);
            if (result != YutResult.YUT && result != YutResult.MO) break;
        }
        return resultQueue;
    }

    //수동 입력 값을 YutResult로 변환
    public YutResult rollManual(int value) {
        return switch (value) {
            case -1 -> YutResult.BACKDO;
            case 1 -> YutResult.DO;
            case 2 -> YutResult.GAE;
            case 3 -> YutResult.GEOL;
            case 4 -> YutResult.YUT;
            case 5 -> YutResult.MO;
            default -> throw new IllegalArgumentException("입력은 -1 ~ 5 사이여야 합니다.");
        };
    }

    public int convertToSteps(YutResult result) {
        return result.getSteps();
    }

    public void resetDice() {
        resultQueue.clear();
    }

    public Queue<YutResult> getResultQueue() {
        return resultQueue;
    }

    public void setResultQueue(Queue<YutResult> queue) {
        this.resultQueue = queue;
    }
}
