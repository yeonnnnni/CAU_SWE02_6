// controller/GameController.java
package controller;

import model.DiceManager;
import model.YutResult;
import view.DicePanel;
import controller.GameManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameController {
    private final Board board;
    private final DiceManager diceManager;
    private final GameManager gameManager;
    private final DicePanel dicePanel;

    public GameController(Board board, DiceManager diceManager, GameManager gameManager, DicePanel dicePanel) {
        this.board = board;
        this.diceManager = diceManager;
        this.gameManager = gameManager;
        this.dicePanel = dicePanel;

        // 🎯 버튼 클릭 시 윷 던지기 처리
        dicePanel.addRollListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDiceRoll();
            }
        });
    }

    public void handleDiceRoll() {
    Queue<YutResult> results;

    if (dicePanel.isRandomMode()) {
        // 랜덤 윷 던지기
        results = diceManager.rollRandomQueue();
    } else {
        // 수동 입력 모드
        try {
            int input = Integer.parseInt(dicePanel.getManualInputText());
            YutResult result = diceManager.rollManual(input);
            results = new LinkedList<>();
            results.add(result);
        } catch (NumberFormatException e) {
            System.err.println("유효하지 않은 숫자 입력");
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("유효하지 않은 윷 입력값: " + e.getMessage());
            return;
        }
    }

    // 1. 결과 DicePanel에 표시
    dicePanel.showResult(new ArrayList<>(results));

    // 2. GameManager로 전달
    gameManager.handleMoveQueue(results);
}
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