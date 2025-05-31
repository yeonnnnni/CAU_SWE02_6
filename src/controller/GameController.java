package controller;

import model.DiceManager;
import model.YutResult;
import view.GameUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GameController 클래스
 * - 사용자 UI 이벤트(DicePanel 버튼 클릭)를 처리하고,
 * - 주사위 결과를 GameManager에 전달하는 역할을 함.
 */

public class GameController {
    private final DiceManager diceManager; // 주사위 결과 생성기
    private final GameManager gameManager; // 게임의 전체 진행을 관리하는 클래스
    private final GameUI gameUI;

    // 생성자 : 컨트롤러 초기화 및 버튼 이벤트 리스너 등록
    public GameController(DiceManager diceManager, GameManager gameManager, GameUI gameUI) {
        this.diceManager = diceManager;
        this.gameManager = gameManager;
        this.gameUI = gameUI;

        // 윷 던지기 버튼 클릭 시 이벤트 처리
        gameUI.setRollListener(this::handleDiceRoll);
    }

    /**
     * 사용자가 윷 던지기 버튼을 눌렀을 때 호출됨
     * - 랜덤 또는 수동 입력값에 따라 윷 결과 생성
     * - UI에 결과 표시하고 GameManager로 전달
     */

    public void handleDiceRoll() {
        List<YutResult> results;

        // 랜덤 모드인지 수동 모드인지 확인
        if (gameUI.isRandomMode()) {
            // 랜덤 윷 결과 시퀀스 생성
            results = diceManager.rollRandomSequence();
        } else {
            try {
                // 수동 입력값 받아 반환
                int input = Integer.parseInt(gameUI.getManualInput());
                results = List.of(diceManager.rollManual(input));
            } catch (NumberFormatException e) {
                System.err.println("숫자 형식이 잘못되었습니다.");
                return;
            } catch (IllegalArgumentException e) {
                System.err.println("유효하지 않은 윷 입력값: " + e.getMessage());
                return;
            }
        }

        gameUI.showDiceResult(results); // Dice Panel에 결과 표시
        gameManager.handleDiceRoll(); // 내부적으로 promptNextMove 호출
    }
}
