package model;

import builder.BoardFactory;
import model.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import view.MainFrame;

public class Horse {
    private final String	id;
    private final int   	teamID;
    private final int       horseIdx;
    private final Team      team;
    private HorseState      state;
    private Node            position;
    private List<Horse>     groupedHorses;
    private HorseBackup     backup;

    public Horse(int horseIdx, Team team) {
        this.teamID = team.getTeamID();
        this.team=team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.horseIdx = horseIdx;
        this.state = HorseState.WAITING;
        this.position = null;
        this.groupedHorses = new ArrayList<>();
        team.addHorse(this);
    }

    public String getId() { return id; }
    public int getTeamID() { return teamID; }
    public HorseState getState() { return state; }
    public List<Horse> getGroupedHorses() { return groupedHorses; }
    public void setState(HorseState state) { this.state = state; }

    public Node getPosition(){
        return this.position;
    }

    public Color getTeamColor(){
        return team.getColor();
    }

    public void setPosition(Node position) {
        if (this.position != null) {
            this.position.removeHorse(this);
        }
        this.position = position;
        if (position != null) {
            position.addHorse(this);
        }
    }

    private Node chooseNextNode(List<Node> candidates) {
        if (position.isCenter()) {
            // 무조건 A 방향으로 진행
            return candidates.stream()
                    .filter(n -> n.getId().startsWith("A"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No A-direction node from center"));
        } else {
            String currentDir = position.getId().substring(0, 1);
            int currentLevel = Character.getNumericValue(position.getId().charAt(1));

            boolean useShortcut = MainFrame.getInstance().promptShortcutChoice(currentDir);

            if (useShortcut) {
                return candidates.stream()
                        .filter(n -> n.getId().startsWith(currentDir))
                        .filter(n -> {
                            if (n.getId().length() < 2) return false;
                            int level = Character.getNumericValue(n.getId().charAt(1));
                            return level == currentLevel - 1;
                        })
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No shortcut node found"));
            } else {
                return candidates.stream()
                        .filter(n -> n.getId().startsWith("N"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No normal path node found"));
            }
        }
    }


    private void moveStep() {
        if (position == null) throw new IllegalStateException("Position has not been set");

        List<Node> nextList = position.getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            // 더 이상 이동할 곳이 없으면 완주로 간주
            this.state = HorseState.FINISHED;
            return;
        }

        Node next = (nextList.size() == 1)
                ? nextList.getFirst() // 첫번째 노드
                : chooseNextNode(nextList);  // 복수일 경우 선택

        setPosition(next); // 본인 위치 갱신

        // 그룹된 말들도 함께 이동
        for (Horse grouped : groupedHorses) {
            grouped.setPosition(next);
        }

        // 도착 지점인지 확인하여 상태 업데이트
        if (position.isGoal()) {
            this.state = HorseState.FINISHED;
            return;
        }

        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (this != other && this.isGroupable(other)) {
                this.groupWith(other);
            }
        }
    }

    public void move ( int steps, List<Node > board) { // 사용자 상호작용 지점
        if (isFinished()) {
            System.out.println("Horse " + id + " is already finished"); // for test
            return;
        }

        backupState();

        if (position == null) {
            position = BoardFactory.getStartNode(board);
            state = HorseState.MOVING;
            if (steps < 0) return;
        }


        for (int i = 0; i < steps; i++) {
            moveStep(); // 지름길 or 외곽경로 선택 필요시 콜백함수 -> 나중에 ui랑 ㄱㄱ
            if (this.isFinished()) {
                return;
            } // 말 완주
        }

        assert position != null; // 말 잡기
        List<Horse> others = position.getHorsesOnNode();
        for (Horse oth : others) {
            if (isCaptured(oth)) {
                oth.reset();
            }
        }
    }

    public void backupState() {
        this.backup = new HorseBackup(this.position, this.state, this.groupedHorses);
    }

    public void rollback() {
        if (backup != null) {
            this.setPosition(backup.position);
            this.state = backup.state;
            this.groupedHorses = new ArrayList<>(backup.groupedHorses);
        } else {
            IllegalAccessError e = new IllegalAccessError("No backup state to rollback.");
        }
    }

    public void reset () {
        if (position != null) {
            position.removeHorse(this);
        }

        position = null;
        state = HorseState.WAITING;
        groupedHorses.clear();
    }

    public boolean isCaptured(Horse other) {
        return !this.teamIdEquals(other) && this.position == other.position;
    }

    public boolean isGroupable(Horse other) {
        return this.teamIdEquals(other) && this.position == other.position && !groupedHorses.contains(other);
    }

    public boolean isWin() {
        return this.state == HorseState.FINISHED;
    }

    public void groupWith(Horse other) {
        if (isGroupable(other)) { // this 대표말, oth 부속말
            groupedHorses.add(other);
            other.groupedHorses.clear(); // 말 하나는 여러 그룹에 속할 수 없다.
            other.groupedHorses.add(this);
            other.setPosition(this.position);
        }
    }

    public boolean isFinished() {
        return this.state == HorseState.FINISHED;
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamID == other.teamID;
    }
}