package model;

import builder.BoardFactory;
import model.Node;
import model.HorseState;
import model.Team;

import java.util.ArrayList;
import java.util.List;

public class Horse {
    private final String	id;
    private final int   	teamID;
    private final int       horseIdx;
    private HorseState      state;
    private Node            position;
    private List<Horse>     groupedHorses;

    public Horse(int horseIdx, Team team) {
        this.teamID = team.getTeamID();
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

    public void setPosition(Node position) {
        if (this.position != null) {
            this.position.removeHorse(this);
        }
        this.position = position;
        if (position != null) {
            position.addHorse(this);
        }
    }

    private Node chooseNextNode(List <Node> candidates) {
        // TODO

        return new Node("test");
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
                ? nextList.getFirst()
                : chooseNextNode(nextList);  // 복수일 경우 선택

        setPosition(next); // 본인 위치 갱신

        // 그룹된 말들도 함께 이동
        for (Horse grouped : groupedHorses) {
            grouped.setPosition(next);
        }

        // 도착 지점인지 확인하여 상태 업데이트
        if (position.isGoal()) {
            this.state = HorseState.FINISHED;
        }
    }

    public void move(int steps, List<Node> board) {
        if (isFinished()) {
            System.out.println("Horse " + id + " is already finished"); // for test
            return;
        }

        if (position == null) {
            position = BoardFactory.getStartNode(board);
            state = HorseState.MOVING;
            if (steps < 0) return;
        }

        for (int i = 0; i < steps; i++) {
            moveStep();
            if (position.isGoal()){
                state = HorseState.FINISHED;
                return;
            }
        }

        assert position != null;
        List<Horse> others = position.getHorsesOnNode();
        for (Horse grouped : groupedHorses) {
            if (isCaptured(grouped)) {
                grouped.reset();
            }
        }

    }

    public void reset() {
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