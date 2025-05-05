package controller;

import view.MainFrame;
public class GameManager {
    private MainFrame mainFrame;
    public GameManager(MainFrame mainFrame){
        this.mainFrame=mainFrame;
    }
}


//GameManager.handleDiceRoll() 또는 startTurn() 같은 함수 안에서
//List<YutResult> results = diceManager.rollRandomSequence();
//이런 식으로 처리할 수 있도록 만들어야 함.
//YutResult 리스트는 반드시 순서대로 처리해야 함. (윷/모 보너스 포함된 상태니까!)

/*
(예시본)
List<YutResult> results = diceManager.rollRandomSequence();

for (YutResult result : results) {
    int steps = diceManager.convertToSteps(result);
    // → 여기서 이동 처리
    game.moveCurrentHorse(steps); // 예시 함수
}
*/