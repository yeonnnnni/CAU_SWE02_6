package model;

import java.awt.Color;  // 색상 관련 import
import java.util.ArrayList;
import java.util.List;

public class Team {
    private final int teamID;
    private final List<Horse> horses;
    private final Color color;   // 팀별 색상 필드

    public Team(int teamID) {
        this.teamID = teamID;
        this.horses = new ArrayList<>();
        Color[] defaultColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};

        if (teamID >= 0 && teamID < defaultColors.length) {
            this.color = defaultColors[teamID];
        } else {
            this.color = Color.GRAY; // 예외 상황 대비
        }
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

    // 색상 반환 메서드
    public Color getColor(){
        return color;
    }

    // 팀 ID에 따른 색상 지정
    private Color assignColor(int id){
        return switch(id){
            case 0->Color.RED;
            case 1->Color.BLUE;
            case 2->Color.GREEN;
            case 3->Color.MAGENTA;
            default -> Color.BLACK;
        };
    }
}