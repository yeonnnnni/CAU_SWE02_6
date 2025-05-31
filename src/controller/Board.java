package controller;

import model.Horse;
import model.Node;
import model.Team;

import java.util.*;

/*
Board 클래스는 전체 팀과 말을 관리하고,
게임판의 노드 및 말의 상태를 제어합니다.*/
public class Board {
    // 플레이어 ID → 팀 객체 매핑
    private final Map<String, Team> playerToTeam = new HashMap<>();
    // 전체 말 객체 추적 (잡기/충돌 검사 시 사용)
    private final List<Horse> allHorses = new ArrayList<>();
    // 게임판에 존재하는 모든 노드 리스트
    private final List<Node> nodes = new ArrayList<>();

    // // 플레이어 등록 시 Team과 말 4개 생성
    // public void registerPlayer(String playerId) {
    //     int teamID = Integer.parseInt(playerId);       // 예: "0", "1" → 0, 1
    //     Team team = new Team(teamID);
    //     for (int i = 0; i < 4; i++) {
    //         Horse h = new Horse(i, team);    // Horse 생성 시 team에 자동 등록됨
    //         allHorses.add(h);   // 전체 말 리스트에 추가
    //     }
    //     playerToTeam.put(playerId, team);
    // }

    // 플레이어 ID를 기준으로 말 리스트 반환
    public List<Horse> getHorsesForPlayer(String playerId) {
        Team team = playerToTeam.get(playerId);
        if (team == null) return new ArrayList<>();
        return team.getHorses();
    }

    // 말 이동
    public void moveHorse(Horse horse, int steps, List<Node> board, String boardType) {
        horse.move(steps, board, boardType);
    }

    //외부에서 생성된 노드 리스트를 설정 (Builder에서 주입)
    public void setNodes(List<Node> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
    }

    //노드 리스트 반환 (Horse 이동용)
    public List<Node> getNodes() {
        return nodes;
    }

    //특정 팀에 속한 말 리스트를 반환합니다.
    public List<Horse> getHorsesForTeam(Team team) {
        return team.getHorses();
    }

    //전체 말 리스트 반환 (잡기/그룹핑 등에서 사용)
    public List<Horse> getAllHorses() {
        return allHorses;
    }

    //특정 말이 이동 가능한지 여부 반환
    public boolean canMove(Horse horse, int steps) {
        return !horse.isFinished();  // 완주한 말은 이동 불가
    }

    //전체 팀과 말 상태 초기화
    public void resetAll() {
        for (Team team : playerToTeam.values()) {
            team.resetTeam();  // 팀 내 말들 초기화
        }
        for (Node node : nodes) {
            node.clearHorses(); // 말 위치 초기화
        }
    }

    //시작 노드 반환 (ex: ID가 "A2"인 노드를 기준)
    public Node getStartNode(List<Node> board) {
        return board.stream()
                .filter(n -> "A2".equals(n.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("A2 노드를 찾을 수 없습니다."));
    }

    // // Board.java 내부에 아래 메서드 추가
    // public void registerTeam(Team team) {
    //     String teamIdAsString = String.valueOf(team.getTeamID());
    //     if (playerToTeam.containsKey(teamIdAsString)) return;

    //     for (int i = 0; i < 4; i++) {
    //         Horse h = new Horse(i, team);   // 팀에 자동 등록
    //         allHorses.add(h);               // 전체 말 목록에도 추가
    //     }

    //     playerToTeam.put(teamIdAsString, team);  // 팀 등록
    // }
}
