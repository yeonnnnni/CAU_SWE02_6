package model;

import java.util.Random;

//랜덤 버튼 클릭 → rollRandom() 호출 → Enum(YutResult) 리턴
//수동 입력 숫자 → rollManual(value) 호출 → Enum(YutResult) 리턴
//YutResult 결과 → convertToSteps() 호출 → 칸수(int)로 변환

public class DiceManager {

    private Random random = new Random();

    //주사위 던지기 (무작위로 윷 결과 하나 생성성)
    public YutResult rollRandom() {
        int val = random.nextInt(6); // 0~5
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
            default: return null;
        }
    }

    //YutResult를 이동 칸수(int)로 변환
    public int convertToSteps(YutResult result) {
        return result.getSteps();
    }
}

