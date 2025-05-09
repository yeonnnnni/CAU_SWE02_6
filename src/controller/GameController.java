package controller;

import model.DiceManager;
import model.YutResult;
import view.DicePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameController {
    private final DiceManager diceManager;
    private final GameManager gameManager;
    private final DicePanel dicePanel;

    public GameController(DiceManager diceManager, GameManager gameManager, DicePanel dicePanel) {
        this.diceManager = diceManager;
        this.gameManager = gameManager;
        this.dicePanel = dicePanel;

        dicePanel.addRollListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDiceRoll();
            }
        });
    }

    public void handleDiceRoll() {
        List<YutResult> results;

        if (dicePanel.isRandomMode()) {
            results = diceManager.rollRandomSequence();
        } else {
            try {
                int input = Integer.parseInt(dicePanel.getManualInputText());
                results = List.of(diceManager.rollManual(input));
            } catch (NumberFormatException e) {
                System.err.println("숫자 형식이 잘못되었습니다.");
                return;
            } catch (IllegalArgumentException e) {
                System.err.println("유효하지 않은 윷 입력값: " + e.getMessage());
                return;
            }
        }

        dicePanel.showResult(results);
        gameManager.handleDiceRoll(); // 내부적으로 promptNextMove 호출
    }
}
