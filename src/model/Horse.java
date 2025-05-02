package model;

import model.Node;
import model.HorseState;
import java.util.ArrayList;
import java.util.List;

public class Horse {
    private final String	id;
    private final int   	teamID;
    private final int       horseIdx;
    private HorseState      state;
    private Node            position;
    private List<Horse> groupedHorses;

    public Horse(int horseIdx, int teamID) {
        this.id = "T" + teamID + "-H" + horseIdx;
        this.teamID = teamID;
        this.horseIdx = horseIdx;
        this.state = HorseState.WAITING;
        this.position = null;
        this.groupHorses = new ArrayList<>();
    }

    public String getId() { return id; }

    public int getTeamID() { return teamID; }

    public HorseState getState() { return state; }

    public List<Horse> getGroupedHorses() { return groupedHorses; }

    public void setState(HorseState state) { this.state = state; }

    public void move(Node target) {
    }

    public boolean isCaptured(Horse other) {
        return !this.teamIdEquals(other) && this.currentPosition == other.currentPosition;
    }

    public boolean isWin() {
        return this.state == HorseState.FINISHED;
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamId == other.teamId;
    }
}