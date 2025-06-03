package controller;

import model.Horse;
import model.Node;
import model.Team;

import java.util.*;

/**
 * Board 클래스는 전체 팀, 말, 노드 상태를 종합적으로 관리한다.
 * -플레이어 ID 기반으로 팀을 추적
 * -말 객체를 모아 상태 변화 추적(잡기/완주 등)
 * -게임판의 노드 상태 초기화, 검색, 시작점 식별 기능 제공
 */
public class Board {
    // 플레이어 ID → 팀 객체 매핑
    private final Map<String, Team> playerToTeam = new HashMap<>();
    // 전체 말 객체 추적 (잡기/충돌 검사 시 사용)
    private final List<Horse> allHorses = new ArrayList<>();
    // 게임판에 존재하는 모든 노드 리스트
    private final List<Node> nodes = new ArrayList<>();

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

    /**
     * 팀 등록 메서드: 중복 없이 등록
     * -팀이 이미 등록되어 있으면 무시
     * 팀에 속한 말들도 allHorses에 등록
     * @param team
     */
    public void registerTeam(Team team) {
        String teamIdAsString = String.valueOf(team.getTeamID());  // 정수 ID->문자열 변환
        if (playerToTeam.containsKey(teamIdAsString)) return;    // 이미 등록되어 있으면 무시

        for (Horse h : team.getHorses()) {
            allHorses.add(h);  // 전체 말 리스트에 추가
        }

        playerToTeam.put(teamIdAsString, team);  // 팀 등록
    }

}
