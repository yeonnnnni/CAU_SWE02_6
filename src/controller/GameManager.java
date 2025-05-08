package controller;

import model.YutResult;
import model.DiceManager;
import model.Horse;
import view.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import java.util.LinkedList;
import javax.swing.JOptionPane;


public class GameManager {
    private MainFrame mainFrame;
    private Board board;
    private DiceManager diceManager;

    private List<String> players;
    private int currentPlayerIndex;

    public GameManager(MainFrame mainFrame, Board board, DiceManager diceManager, List<String> players) {
        this.mainFrame = mainFrame;
        this.board = board;
        this.diceManager = diceManager;
        this.players = players;
        this.currentPlayerIndex = 0;
    }

    /** 게임 시작 시 호출 */
    public void startGame() {
        // 초기화 및 시작 알림
    }

    /** 현재 플레이어가 윷을 던질 때 호출 */
    public void startTurn() {
        // DiceManager를 이용해 결과 받고 → 큐에 저장
    }

    /** 윷 결과 큐를 처리하여 말 이동 */
    public void handleMoveQueue(Queue<YutResult> resultQueue) {
        while (!resultQueue.isEmpty()) {
            YutResult result = resultQueue.poll();
            int steps = diceManager.convertToSteps(result);
            
            List<Horse> movable = getMovableHorses(steps);
            if (movable.isEmpty()) {
                System.out.println("이동 가능한 말 없음. 턴 넘김.");
                continue;
            }
        
            // 👉 사용자에게 movable 리스트를 넘겨서 선택하게 하기
            mainFrame.promptHorseSelection(movable, steps);
            return; // 선택 후 다시 이어지도록 흐름 잠시 멈춤
        }
    
        checkWin();
        nextTurn();
    }
    

    /** 턴 종료 후 다음 플레이어로 넘김 */
    public void nextTurn() {
        // currentPlayerIndex 증가 및 UI 갱신
    }

    /** 플레이어가 승리 조건을 만족했는지 체크 */
    public void checkWin() {
        // 말 4개 모두 도착했는지 등
    }

    /** 게임을 완전히 초기화하고 새로 시작 */
    public void restartGame() {
        // 말 위치 초기화, 턴 초기화 등
    }

    /** 현재 플레이어 이름 반환 */
    public String getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** 윷 결과를 받아 처리 (GameController에서 호출) */
    public void processRollResult(YutResult result) {
        // TODO: 한 결과에 대해 칸 수 계산 → 이동 처리 (예: moveHorse)
    }

    //이동 가능 말 필터 메서드
    public List<Horse> getMovableHorses(int steps) {
        List<Horse> horses = board.getHorsesForPlayer(getCurrentPlayer());
        List<Horse> movable = new ArrayList<>();
        for (Horse h : horses) {
            if (board.canMove(h, steps)) {
                movable.add(h);
            }
        }
        return movable;
    }

    /** 윷 버튼 클릭 시 호출됨: 윷 결과 생성 + 처리 시작 */
    public void handleDiceRoll() {
        if (mainFrame.getDicePanel().isRandomMode()) {
            Queue<YutResult> resultQueue = diceManager.rollRandomQueue();
            mainFrame.getDicePanel().showResult(List.copyOf(resultQueue));
            handleMoveQueue(resultQueue);
        } else {
            // 👉 수동 모드
            try {
                int input = Integer.parseInt(mainFrame.getDicePanel().getManualInputText().trim());
                YutResult result = diceManager.rollManual(input);
                Queue<YutResult> resultQueue = new LinkedList<>();
                resultQueue.add(result);
                mainFrame.getDicePanel().showResult(List.of(result));
                handleMoveQueue(resultQueue);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "수동 입력은 -1부터 5 사이의 정수를 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(mainFrame, "잘못된 수치입니다: " + e.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
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

(예시본)
public void startTurn(DiceManager diceManager) {
    List<YutResult> results = diceManager.rollRandomSequence();

    for (YutResult result : results) {
        int steps = diceManager.convertToSteps(result);
        System.out.println("말 이동 거리: " + steps);
        // TODO: 이동 처리 → game.moveCurrentHorse(steps);
    }
}

메서드 역할
startGame() 전체 초기화 및 첫 턴 준비
startTurn() 윷 던지고 큐 준비
handleMoveQueue()   YutResult 큐를 순서대로 처리
processRollResult() 단일 결과 처리용 (직접 호출 시)
checkWin()  말 4개 도착 등 조건 검사
nextTurn()  다음 플레이어로 전환
restartGame()   판 전체 리셋
getCurrentPlayer()  UI에서 현재 사용자 표시용
*/
