package controller;

public class GameController {
}


/*
윷 결과값 이런식으로 받아오면 됨!
Queue<YutResult> results;

if (dicePanel.isRandomMode()) {
    results = diceManager.rollRandomQueue();
} else {
    int input = Integer.parseInt(dicePanel.getManualInputText());
    results = new LinkedList<>();
    results.add(diceManager.rollManual(input));
}

// GameManager로 전달해서 처리
gameManager.processYutResults(results); // (가상의 메서드)

 */