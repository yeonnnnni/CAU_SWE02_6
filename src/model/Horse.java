package model;

import controller.ShortcutDecisionProvider;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Horse {
    private final String id;
    private final Team team;
    private Node position;
    private HorseState state;

    private final HorseLogicManager logicManager = new HorseLogicManager();
    private final HorseBackupManager backupManager = new HorseBackupManager();
    private final HorseGroupManager groupManager = new HorseGroupManager();

    public Horse(String id, Team team) {
        this.id = id;
        this.team = team;
        this.state = HorseState.WAITING;
        team.addHorse(this);
    }

    public String getId() { return id; }
    public Team getTeam() { return team; }
    public Node getPosition() { return position; }
    public void setPosition(Node position) {
        if (this.position != null) this.position.removeHorse(this);
        this.position = position;
        if (position != null) position.addHorse(this);
    }

    public HorseState getState() { return state; }
    public void setState(HorseState state) { this.state = state; }
    public Color getTeamColor() {
        return team.getColor(); // 팀의 색상을 반환
    }

    public boolean move(int steps, List<Node> board, String boardType, ShortcutDecisionProvider provider) {
        return logicManager.move(this, steps, board, boardType, provider);
    }

    public void backupState() { backupManager.backup(this); }
    public void rollback() { backupManager.restore(this); }

    public void groupWith(Horse other) {
        groupManager.groupWith(this, other);
    }

    public boolean isGroupable(Horse other) {
        return groupManager.isGroupable(this, other);
    }

    public boolean isCaptured(Horse other) {
        return logicManager.isCaptured(this, other);
    }

    public boolean isFinished() {
        return logicManager.isFinished(this);
    }

    public List<Horse> getGroupedHorses() {
        return groupManager.getGroupedHorses(this);
    }

    public void reset() {
        this.position = null;
        this.state = HorseState.WAITING;
        this.resetGroupedHorses();  // 그룹도 초기화
    }


    public void resetGroupedHorses() {
        groupManager.resetGroupedHorses(this);
    }

    /**
     * 말의 간단한 문자열 반환 (예: A,H2)
     */
    public String toString2() {
        char teamChar = (char) ('A' + getTeam().getTeamID());
        return teamChar + ",H" + getId();
    }

    /**
     * 말의 상세 문자열 반환 (예: Team A, H2)
     */
    @Override
    public String toString() {
        char teamChar = (char) ('A' + getTeam().getTeamID());
        return "Team " + teamChar + ", H" + getId();
    }
}