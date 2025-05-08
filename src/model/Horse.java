package model;

import builder.BoardFactory;
import view.MainFrame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.Point;

public class Horse {
    private final String id;
    private final int teamID;
    private final int horseIdx;
    private final Team team;

    private HorseState state;
    private Node position;
    private List<Horse> groupedHorses;
    private HorseBackup backup;

    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.groupedHorses = new ArrayList<>();
        this.position = null;
        team.addHorse(this);
    }

    public String getId() { return id; }
    public int getTeamID() { return teamID; }
    public HorseState getState() { return state; }
    public List<Horse> getGroupedHorses() { return groupedHorses; }
    public void setState(HorseState state) { this.state = state; }
    public Team getTeam() { return team; }

    public Node getPosition() {
        return position;
    }

    public Color getTeamColor() {
        return team.getColor();
    }

    // 말 위치 설정 (노드 등록 및 제거 포함)
    public void setPosition(Node position) {
        if (this.position != null) {
            this.position.removeHorse(this);
        }
        this.position = position;
        if (position != null) {
            position.addHorse(this);
        }
    }

    // 분기점에서 사용자에게 경로 선택 유도
    private Node chooseNextNode(List<Node> candidates) {
        String currentId = position.getId();  // position은 Node

        // center 노드인 경우
        if (currentId.equals("00")) {
            return candidates.stream()
                    .filter(n -> n.getId().startsWith("A"))
                    .findFirst()
                    .orElse(candidates.getFirst());
        } // center노드일경우
//        else if (currentId.endsWith("2") && !currentId.equals("A2")) {
//            System.out.println("here!!!!!!!!!!!!!!!");
//            String direction = position.getId().substring(0, 1);
//            boolean useShortcut = MainFrame.getInstance().promptShortcutChoice(direction);
//
//            if (useShortcut) {
//                int level = Character.getNumericValue(position.getId().charAt(1));
//                return candidates.stream()
//                        .filter(n -> n.getId().startsWith(direction))
//                        .filter(n -> n.getId().length() >= 2 &&
//                                Character.getNumericValue(n.getId().charAt(1)) == level - 1)
//                        .findFirst()
//                        .orElse(candidates.getFirst());
//            } else {
//                return candidates.stream()
//                        .filter(n -> n.getId().startsWith("N"))
//                        .findFirst()
//                        .orElse(candidates.getFirst());
//            }
//        }
        // 2. A2 노드인 경우 → 무조건 N0 선택
        else if (currentId.equals("A2")) {
            return candidates.stream()
                    .filter(n -> n.getId().equals("N0"))
                    .findFirst()
                    .orElse(candidates.getFirst());

        }
        // 3. 현재 노드가 N방향인 경우 → N방향 + 숫자 +1 노드로 이동
        else if (currentId.startsWith("N")) {
            try {
                int currentNum = Integer.parseInt(currentId.substring(1)); // "N3" → 3
                String targetId = "N" + (currentNum + 1);
                return candidates.stream()
                        .filter(n -> n.getId().equals(targetId))
                        .findFirst()
                        .orElse(candidates.getFirst());
            } catch (NumberFormatException e) {
                // 잘못된 형식이면 fallback
                return candidates.getFirst();
            }
        }
        return candidates.getFirst();
    }



    private void moveStep(boolean isRemain) {
        if (position == null) throw new IllegalStateException("현재 위치가 설정되지 않았습니다.");

        List<Node> nextList = position.getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            this.state = HorseState.FINISHED;
            return;
        }

        Node next = (isRemain && position.getId().startsWith("N") && nextList.size() == 3) ?
                nextList.get(2) : // 마지막칸이 남아있지않고, n이면서 sizerk가 3이라면
                chooseNextNode (nextList);

        System.out.println("$$$$$$next: " + next.getId());
        System.out.println("next: " + nextList.toString());
        setPosition(next);

        // 그룹 말도 함께 이동
        for (Horse grouped : groupedHorses) {
            grouped.setPosition(next);
        }

        // 도착 지점인지 확인
        if (position.isGoal()) {
            this.state = HorseState.FINISHED;
            return;
        }

        // 도착 후 자동 그룹핑
        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (this != other && isGroupable(other)) {
                groupWith(other);
            }
        }
    }

    // n칸 이동
    public void move(int steps, List<Node> board, String boardType) {
        if (isFinished()) return;

        backupState();

        if (position == null) {
            position = BoardFactory.getStartNode(board, boardType);
            state = HorseState.MOVING;
            if (steps < 0) return;
        }

        for (int i = 0; i < steps; i++) {
            moveStep(steps == i+1); //마지막 노드일경우 참
            if (isFinished()) return;
        }

        // 도착 후 말 잡기
        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (isCaptured(other)) {
                other.reset();
            }
        }

        printStatus(); // 디버깅 로그 출력
    }

    // 현재 상태 백업
    public void backupState() {
        this.backup = new HorseBackup(this.position, this.state, this.groupedHorses);
    }

    // 백업 상태로 롤백
    public void rollback() {
        if (backup != null) {
            this.setPosition(backup.position);
            this.state = backup.state;
            this.groupedHorses = new ArrayList<>(backup.groupedHorses);
        }
    }

    // 말 상태 초기화
    public void reset() {
        if (position != null) {
            position.removeHorse(this);
        }
        position = null;
        state = HorseState.WAITING;
        groupedHorses.clear();
    }

    public boolean isCaptured(Horse other) {
        return !teamIdEquals(other) && this.position == other.position;
    }

    public boolean isGroupable(Horse other) {
        return teamIdEquals(other) && this.position == other.position && !groupedHorses.contains(other);
    }

    public boolean isFinished() {
        return state == HorseState.FINISHED;
    }

    public void groupWith(Horse other) {
        if (isGroupable(other)) {
            groupedHorses.add(other);
            other.groupedHorses.clear();
            other.groupedHorses.add(this);
            other.setPosition(this.position);
        }
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamID == other.teamID;
    }

    // 디버깅용 말 상태 출력
    public void printStatus() {
        String positionId = (position != null) ? position.getId() : "null";
        String coord = "null";

        if (position != null) {
            JButton btn = MainFrame.getInstance()
                    .getBoardPanel()
                    .getNodeToButtonMap()
                    .get(position);
            if (btn != null) {
                Point p = btn.getLocation();
                coord = "(" + p.x + ", " + p.y + ")";
            } else {
                coord = "(버튼 없음)";
            }
        }

        System.out.printf(
                "[말 상태] %s | 상태: %s | 위치: %s | 좌표: %s\n",
                this.id, this.state, positionId, coord
        );
    }

    @Override
    public String toString() {
        return "T" + teamID + "-H" + horseIdx;
    }
}
