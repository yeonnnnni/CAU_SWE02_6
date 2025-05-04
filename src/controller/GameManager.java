package controller;

import model.YutResult;

public class GameManager {
    public void processRollResult(YutResult result) {
        // 윷 결과에 따라 처리 (예: 말 이동 가능 여부 저장, 추가 턴 판단 등)
        System.out.println("GameManager received: " + result);
    }
}