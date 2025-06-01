package model;
import builder.BoardFactory;

import java.awt.Color;
import java.util.*;
import java.awt.Point;

public class OldHorse {
    // 고유 ID (예: "T1-H2")
    private final String id;
    private final int teamID;
    private final int horseIdx;     // 팀 내 말 번호 (0~n)
    private final Team team;

    private HorseState state;       // 말 상태 (WAITING, MOVING, FINISHED 등)
    private Node position;          // 현재 위치한 노드
    private List<Horse> groupedHorses; // 함께 이동하는 말 리스트 (업기)
    private HorseBackup backup;     // 백업 상태 (롤백용)

    //빽도를 위한 경로 스택 생성
    private final Deque<Node> positionHistory = new ArrayDeque<>();

    /**
     * Horse 객체 생성자
     * - 팀과 인덱스를 기반으로 고유 ID 부여
     * - 상태 초기화 및 팀에 등록
     */
    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.groupedHorses = new ArrayList<>();
        this.position = null;
        team.addHorse(this);    // 팀 객체에 이 말 등록
    }

    // Getter / Setter
    public String getId() {
        return id;
    }

    public int getTeamID() {
        return teamID;
    }

    public HorseState getState() {
        return state;
    }

    public List<Horse> getGroupedHorses() {
        return groupedHorses;
    }

    public void setState(HorseState state) {
        this.state = state;
    }

    public Team getTeam() {
        return team;
    }

    public Node getPosition() {
        return position;
    }

    /**
     * 팀의 색상 반환 (UI 표시용)
     */
    public Color getTeamColor() {
        return team.getColor();
    }

    /**
     * 말 위치 설정 (현재 위치 제거 + 새 위치에 등록)
     * - 백도 처리를 위해 이전 위치를 스택에 저장
     */

    /**
     * 백도에서의 이동처럼, 스택 기록 없이 위치만 바꾸는 함수
     */
    /**
     * 분기점에서 다음 노드를 선택하는 로직
     * - 다양한 조건 (중심 노드, A/B 라인, 지름길, 외곽 N라인 등)에 따라 다르게 처리됨
     * - 지름길 진입 여부는 MainFrame에서 사용자에게 선택 받음
     */
    // 현재 상태 백업
    public void backupState() {
        this.backup = new HorseBackup(this.position, this.state, this.groupedHorses);
    }

    // 백업 상태로 롤백
    public void rollback() {
        if (backup != null) {
            this.setPosition(backup.position);
            this.state = backup.state;
            this.groupedHorses = new ArrayList<>(backup.groupedHorses);
        }
    }

    // 말 상태 초기화
    public void reset() {
        if (position != null) {
            position.removeHorse(this);
        }
        position = null;
        state = HorseState.WAITING;
        groupedHorses.clear();
        positionHistory.clear();
    }

    /**
     * 상대 말을 잡았는지 확인
     */

    /**
     * 그룹핑 가능한 상대인지 확인
     */
    /**
     * 말이 FINISHED 상태인지 확인
     */

    /**
     * 다른 말과 그룹핑 (업기)
     */
    public void groupWith(Horse other) {
        if (isGroupable(other)) {
            if (!groupedHorses.contains(other)) {
                groupedHorses.add(other);
            }
            if (!other.groupedHorses.contains(this)) {
                other.groupedHorses.add(this);
            }
            other.setPosition(this.position);
        }
    }


}