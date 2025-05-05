package controller;

import model.Horse;
import model.Team;

import java.util.*;

public class Board {
    private Map<String, Team> playerToTeam;

    public Board() {
        this.playerToTeam = new HashMap<>();
    }

    // 플레이어 등록 시 Team과 말 4개 생성
    public void registerPlayer(String playerId) {
        int teamID = Integer.parseInt(playerId);      // 예: "0", "1" → 0, 1
        Team team = new Team(teamID);
        for (int i = 0; i < 4; i++) {
            new Horse(i, team);                       // Horse 생성 시 team에 자동 등록됨
        }
        playerToTeam.put(playerId, team);
    }

    // 특정 플레이어의 말 리스트 반환
    public List<Horse> getHorsesForPlayer(String playerId) {
        Team team = playerToTeam.get(playerId);
        if (team == null) return new ArrayList<>();
        return team.getHorses();
    }

    // 이동 가능 여부 (확장 가능)
    public boolean canMove(Horse horse, int steps) {
        return !horse.isWin();
    }

    // 말 이동
    public void moveHorse(Horse horse, int steps) {
        horse.move(steps);
    }

    // 전체 리셋 시 팀 초기화도 가능하게 추가 (선택)
    public void resetAll() {
        for (Team team : playerToTeam.values()) {
            team.resetTeam();
        }
    }
}

