package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// Team 클래스는 팀의 ID, 이름, 색상, 보유한 말(Horse) 목록을 관리합니다.
public class Team {
    private final int teamID;           // 팀 고유 ID
    private final List<Horse> horses;   // 팀이 보유한 말들
    private final Color color;          // 팀 색상
    private final String name;          // 팀 이름

    // 이름과 색상 모두 지정할 수 있는 생성자
    public Team(int teamID, String name, Color color) {
        this.teamID = teamID;
        this.name = name;
        this.color = color;
        this.horses = new ArrayList<>();
    }

    // 팀 ID만 전달받을 경우: 이름과 색상은 자동으로 지정됨
    public Team(int teamID) {
        this.teamID = teamID;
        this.name = "팀" + teamID;
        this.horses = new ArrayList<>();
        Color[] defaultColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
        this.color = (teamID >= 0 && teamID < defaultColors.length) ? defaultColors[teamID] : Color.GRAY;
    }

    // 팀 고유 ID 반환
    public int getTeamID() {
        return teamID;
    }

    // 팀 이름 반환
    public String getName() {
        return name;
    }

    // 팀의 말 리스트 반환
    public List<Horse> getHorses() {
        return horses;
    }

    // 말 추가
    public void addHorse(Horse horse) {
        horses.add(horse);
    }

    // 팀 색상 반환
    public Color getColor() {
        return color;
    }

    // 모든 말이 FINISHED 상태인지 확인 (승리 조건)
    public boolean isWin() {
        for (Horse horse : horses) {
            if (!horse.isFinished()) {
                return false;
            }
        }
        return true;
    }

    // 팀 내 모든 말의 상태를 초기화
    public void resetTeam() {
        for (Horse horse : horses) {
            horse.reset();
        }
    }
}
