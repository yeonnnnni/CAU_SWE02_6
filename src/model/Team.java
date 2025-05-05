package model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final int teamID;
    private final List<Horse> horses;

    public Team(int teamID) {
        this.teamID = teamID;
        this.horses = new ArrayList<>();
    }

    public int getTeamID() {
        return teamID;
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public void addHorse(Horse horse) {
        horses.add(horse);
    }

    public boolean isWin() {
        for (Horse horse : horses) {
            if (horse.getState() != HorseState.FINISHED) {
                return false;
            }
        }
        return true;
    }

    public void resetTeam() {
        for (Horse horse : horses) {
            horse.reset();
        }
    }
}