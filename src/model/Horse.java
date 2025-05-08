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
        if (position.isCenter()) {
            return candidates.stream()
                    .filter(n -> n.getId().startsWith("A"))
                    .findFirst()
                    .orElse(candidates.getFirst());
        } else {
            String direction = position.getId().substring(0, 1);
            boolean useShortcut = MainFrame.getInstance().promptShortcutChoice(direction);

            if (useShortcut) {
                int level = Character.getNumericValue(position.getId().charAt(1));
                return candidates.stream()
                        .filter(n -> n.getId().startsWith(direction))
                        .filter(n -> n.getId().length() >= 2 &&
                                Character.getNumericValue(n.getId().charAt(1)) == level - 1)
                        .findFirst()
                        .orElse(candidates.getFirst());
            } else {
                return candidates.stream()
                        .filter(n -> n.getId().startsWith("N"))
                        .findFirst()
                        .orElse(candidates.getFirst());
            }
        }
    }

    // 한 칸 이동
    private void moveStep() {
        if (position == null) throw new IllegalStateException("현재 위치가 설정되지 않았습니다.");

        List<Node> nextList = position.getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            this.state = HorseState.FINISHED;
            return;
        }

        Node next = (nextList.size() == 1)
                ? nextList.getFirst()
                : chooseNextNode(nextList);

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
            moveStep();
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
