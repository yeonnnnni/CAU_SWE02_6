package model;

import builder.BoardFactory;
import model.*;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.awt.Color;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class Horse{
    private final String id;
    private final int teamID;
    private final int horseIdx;
    private final Team team;

    private HorseState state;
    private Node position;
    private final Deque<Node> positionHistory = new ArrayDeque<>();

    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.position = null;
        team.addHorse(this);
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