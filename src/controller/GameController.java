// controller/GameController.java — 지름길 및 승리 조건 업데이트, 재시작 지원 통합
package controller;

import controller.Board;
import model.YutResult;
import model.DiceManager;
import view.DicePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GameController: 게임 로직 담당
 */
public class GameController {
    private Board board;
    private DiceManager diceManager;
    private DicePanel dicePanel;

    public GameController(Board board, DiceManager diceManager, DicePanel dicePanel) {
        this.board = board;
        this.diceManager = diceManager;
        this.dicePanel = dicePanel;

        // 🎯 버튼 클릭 시 윷 던지기 처리하도록 연결
        dicePanel.addRollListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDiceRoll();
            }
        });
    }

    public void handleDiceRoll() {
        YutResult result = diceManager.roll();
        // TODO: 결과에 따라 말 이동 가능 상태로 UI 전환 등 처리
        System.out.println("윷 결과: " + result);
    }

    public void handlePieceMove(int pieceId, int steps) {
        // 말 이동 처리
    }

    public void handleShortcutSelection(int nodeId) {
        // 지름길 선택 처리
    }

    public void checkCaptureOrStack(int nodeId) {
        // 잡기 or 업기 처리
    }

    public void checkWinCondition() {
        // 승리 조건 검사
    }

    public void endTurn() {
        // 턴 종료 및 다음 플레이어로 전환
    }

    public void restartGame() {
        // 게임 재시작 처리
    }

    public void exitGame(){
        // 게임 종료 처리
    }
}
