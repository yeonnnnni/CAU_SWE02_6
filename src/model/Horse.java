package model;

import java.awt.Color;
import java.util.*;

public class Horse {

    private final String id;
    private final int teamID;
    private final int horseIdx;
    private final Team team;

    private HorseState state;
    private Node position;
    private final Deque<Node> positionHistory = new ArrayDeque<>();

    // 매니저들
    private final HorseLogicManager logicManager = new HorseLogicManager();
    private final HorseGroupManager groupManager = new HorseGroupManager();
    private final HorseBackupManager backupManager = new HorseBackupManager();

    public String getId() {
        return id;
    }

    public int getTeamID() {
        return teamID;
    }

    public int getHorseIdx() {
        return horseIdx;
    }

    public Team getTeam() {
        return team;
    }

    public HorseState getState() {
        return state;
    }

    public void setState(HorseState state) {
        this.state = state;
    }

    public Node getPosition() {
        return position;
    }

    public List<Horse> getGroupedHorses() {
        return groupManager.getGroupedHorses(this);
    }

    public Deque<Node> getPositionHistory() {
        return positionHistory;
    }

    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.position = null;
        team.addHorse(this);
    }

    public Color getTeamColor() {
        return team.getColor();
    }

    public void setPosition(Node position) {
        if (this.position != null) {
            this.position.removeHorse(this);
            positionHistory.push(this.position);
        }
        this.position = position;
        if (position != null) {
            position.addHorse(this);
        }
    }

    private void setPositionWithoutHistory(Node newPos) {
        if (this.position != null) {
            this.position.removeHorse(this);
        }
        this.position = newPos;
        if (newPos != null) {
            newPos.addHorse(this);
        }
    }

    public boolean move(int steps, List<Node> board, String boardType, ShortcutDecisionProvider provider) {
        return logicManager.move(this, steps, board, boardType, provider, logicManager, groupManager, backupManager);
    }

    public void reset() {
        logicManager.resetHorse(this, groupManager);
    }

    public void backupState() {
        backupManager.backupState(this);
    }

    public void rollback() {
        backupManager.rollback(this);
    }

    public void groupWith(Horse other) {
        groupManager.groupWith(this, other);
    }

    public boolean isGroupable(Horse other) {
        return HorseLogicManager.isGroupable(this, other, groupManager);
    }

    public boolean isCaptured(Horse other) {
        return logicManager.isCaptured(this, other);
    }

    public boolean isFinished() {
        return state == HorseState.FINISHED;
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamID == other.teamID;
    }

    public String toString2() {
        char teamChar = (char) ('A' + teamID);
        return teamChar + ",H" + horseIdx;
    }

    @Override
    public String toString() {
        char teamChar = (char) ('A' + teamID);
        return "Team " + teamChar + ", H" + horseIdx;
    }
}