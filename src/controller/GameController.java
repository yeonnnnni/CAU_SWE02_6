// controller/GameController.java — 지름길 및 승리 조건 업데이트, 재시작 지원 통합
package controller;

import javax.swing.*;
import java.util.*;
import model.Horse;
import model.Node;
import model.YutResult;
import view.MainFrame;

/**
 * GameController: 게임 로직 담당
 */
public class GameController {
    private final MainFrame mainFrame;
    private Board board;
    private final List<String> players;
    private int currentPlayerIndex;
    private final Map<String, List<Horse>> playerHorses;
    // 지름길 분기는 노드 5와 10에서만 허용
    private static final Set<Integer> SHORTCUT_NODES = Set.of(5, 10);

    public GameController(MainFrame mainFrame, List<String> players) {
        this.mainFrame = mainFrame;
        this.players = new ArrayList<>(players);
        this.board = new Board();
        this.playerHorses = new HashMap<>();
        this.currentPlayerIndex = 0;
        // 말 초기화
        for (String p : players) {
            List<Horse> horses = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                horses.add(board.addHorse(p));
            }
            playerHorses.put(p, horses);
        }
    }

    public YutResult throwDice() {
        return YutResult.random();
    }

    public YutResult chooseResult(String name) {
        return YutResult.fromString(name);
    }
    
    /**
     * 말을 이동시키고, 지름길, 업기, 잡기, 완주, 재시작 처리
     * @return true if opponent was captured
     */
    public boolean moveHorse(Horse horse, YutResult result) {
        int steps = result.getMoveCount();
        // 이미 완주한 말은 무시
        if (horse.isFinished()) return false;

        Node origin = horse.getCurrentNode();
        boolean captureOccurred = false;

        
        // 지름길 분기 선택: 노드 5->24 or 6, 노드 10->20 or 11
        int branchChoice = -1;
        if (steps > 0 && SHORTCUT_NODES.contains(origin.getId())) {
            String[] opts = {"일반 경로", "지름길"};
            branchChoice = JOptionPane.showOptionDialog(null,
                    "노드 " + origin.getId() + "에서 지름길을 사용하시겠습니까?",
                    "지름길 분기", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        }

        // 업기(group) 판정
        List<Horse> moveGroup = new ArrayList<>();
        moveGroup.add(horse);
        if (origin.getId() != board.getStartNode().getId()) {
            List<Horse> friendly = new ArrayList<>();
            for (Horse h : origin.getHorses()) {
                if (!h.equals(horse) && h.getOwner().equals(horse.getOwner())) {
                    friendly.add(h);
                }
            }
            if (!friendly.isEmpty()) {
                int ans = JOptionPane.showConfirmDialog(null,
                        "같은 편 말을 업으시겠습니까?", "업기 선택",
                        JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    moveGroup.addAll(friendly);
                    moveGroup.forEach(h -> h.setGrouped(true));
                }
            }
        }

        // 이동 경로 계산 (origin → dest)
        Node dest = origin;
        for (int i = 0; i < Math.abs(steps); i++) {
            if (steps > 0) {
                List<Node> nexts = dest.getNextNodes();
                // **빈 리스트 방지**: 다음 노드가 없으면 더 이상 진행하지 않음
                if (nexts.isEmpty()) break; 
                int idx = 0;
                if (branchChoice == 1 && nexts.size() > 1) idx = 1;
                dest = nexts.get(idx);
                // 지름길은 첫 한 번만 적용
                branchChoice = -1;
            } else {
                Node prev = dest.getPrevious();
                if (prev == null) break;
                dest = prev;
            }
            // 중간에 시작점을 “지나쳤다면” 완주 처리 후 즉시 리턴
            if (dest.getId() == board.getStartNode().getId() && steps > 0) {
                moveGroup.forEach(h -> {
                    h.setFinished(true);
                    h.removeFromBoard();
                });
                // 잡기/추가로직 없이 바로 승리판정으로 이어지도록 함
                break;
            }
        }

        // 이동 전 occupants 저장 (잡기 대상 검사용)
        List<Horse> occupantsBefore = new ArrayList<>(dest.getHorses());

        // 실제 이동 (완주 처리된 말은 moveTo 호출 안 함)
        for (Horse h : moveGroup) {
            if (!(h.isFinished())) {
                h.moveTo(dest);
            }
        }

        // 잡기 처리: 이동 전 occupants 기준
        for (Horse occ : occupantsBefore) {
            if (!occ.getOwner().equals(horse.getOwner())) {
                captureOccurred = true;
                if (moveGroup.size() > 1) {
                    moveGroup.forEach(Horse::sendToStart);
                    moveGroup.forEach(h -> h.setGrouped(false));
                } else {
                    occ.sendToStart();
                }
            }
        }

        // 승리 판정: 모든 말이 finished 상태인지 체크
        String player = horse.getOwner();
        boolean win = playerHorses.get(player).stream().allMatch(Horse::isFinished);
        if (win) {
            int choice = JOptionPane.showOptionDialog(null,
                    player + "님이 승리하셨습니다!\n재시작 또는 종료를 선택하세요.",
                    "게임 종료", JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null,
                    new String[]{"재시작", "종료"}, "재시작");
            if (choice == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        }

        return captureOccurred;
    }

    /**
     * 게임 초기 상태로 리셋
     */
    private void resetGame() {
        // 보드 새로 생성 및 말 초기화
        this.board = new Board();
        // 기존 리스트 비우고 다시 채우기
        for (String p : players) {
            List<Horse> list = playerHorses.get(p);
            list.clear();
            for (int i = 0; i < 2; i++) {
                list.add(board.addHorse(p));
            }
        }
        currentPlayerIndex = 0;
        // UI 갱신
        mainFrame.setBoard(board);
        mainFrame.updateUIComponents();
    }

    public String getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public List<Horse> getPlayerHorses(String player) {
        return playerHorses.get(player);
    }

    public Board getBoard() {
        return board;
    }
}
