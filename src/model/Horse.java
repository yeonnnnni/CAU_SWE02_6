package model;
import builder.BoardFactory;

import java.awt.Color;
import java.util.*;
import java.awt.Point;
import view.Swing.MainFrame;

public class Horse {
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
    public String getId() { return id; }
    public int getTeamID() { return teamID; }
    public HorseState getState() { return state; }
    public List<Horse> getGroupedHorses() { return groupedHorses; }
    public void setState(HorseState state) { this.state = state; }
    public Team getTeam() { return team; }

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

    /**
     * 백도에서의 이동처럼, 스택 기록 없이 위치만 바꾸는 함수
     */
    private void setPositionWithoutHistory(Node newPos) {
        if (this.position != null) {
            this.position.removeHorse(this);
        }
        this.position = newPos;
        if (newPos != null) {
            newPos.addHorse(this);
        }
    }


    /**
     * 분기점에서 다음 노드를 선택하는 로직
     * - 다양한 조건 (중심 노드, A/B 라인, 지름길, 외곽 N라인 등)에 따라 다르게 처리됨
     * - 지름길 진입 여부는 MainFrame에서 사용자에게 선택 받음
     */
    private Node chooseNextNode(List<Node> candidates, boolean isFirstStep, int stepsLeft, String boardType, ShortcutDecisionProvider provider) {
        // 현재 말의 위치 ID
        String currentId = position.getId();  // position은 Node

        // 중심 노드일 경우: "OO"
        // center 노드인 경우
        //"A" 방향으로 가는 지름길 선택 (우선순위) -> 도착지점에 가장 가까운게 A니까.
        //A방향 노드 (A1, A0 등)가 없다면 그냥 candidates의 첫 번째 노드 선택
        if (currentId.equals("00")) {
            if (isFirstStep) {
                return candidates.stream()
                        .filter(n -> n.getId().startsWith("A"))
                        .findFirst()
                        .orElse(candidates.getFirst());
            }
            else if (stepsLeft >= 1) {
                String prevId = positionHistory.isEmpty() ? null : positionHistory.peek().getId();

                if ("C0".equals(prevId)) {
                    return candidates.stream()
                            .filter(n -> n.getId().equals("A0"))
                            .findFirst()
                            .orElse(candidates.getFirst());
                } else if ("D0".equals(prevId)) {
                    return candidates.stream()
                            .filter(n -> n.getId().equals("B0"))
                            .findFirst()
                            .orElse(candidates.getFirst());
                } else {
                    // 오각형, 육각형은 B0
                    return candidates.stream()
                            .filter(n -> n.getId().equals("B0"))
                            .findFirst()
                            .orElse(candidates.getFirst());
                }
            }
            else {
                // 기본 A 라인으로 이동
                return candidates.stream()
                        .filter(n -> n         .getId().startsWith("A"))
                        .findFirst()
                        .orElse(candidates.getFirst());
            }
        }

        else if (currentId.startsWith("A") && !currentId.equals("A2")) {
            try {
                int level = Character.getNumericValue(currentId.charAt(1));
                String targetId = "A" + (level + 1);
                return candidates.stream()
                        .filter(n -> n.getId().equals(targetId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("A" + level + " → " + targetId + " 경로가 없습니다."));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("A 방향 노드 ID 형식이 잘못되었습니다: " + currentId);
            }
        }

        else if (currentId.startsWith("B") && !currentId.equals("B2")) {
            try {
                int level = Character.getNumericValue(currentId.charAt(1));
                String targetId = "B" + (level + 1);
                return candidates.stream()
                        .filter(n -> n.getId().equals(targetId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("B" + level + " → " + targetId + " 경로가 없습니다."));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("A 방향 노드 ID 형식이 잘못되었습니다: " + currentId);
            }
        }

        //각 방향의 "2"번 노드이면서 A2, B2는 아니면서 N2는 아니면서 시작이 vertex인 경우.
        else if (isFirstStep && currentId.endsWith("2") && !currentId.equals("A2")&& !currentId.equals("B2") && !currentId.startsWith("N")) {
            System.out.println("vertex!!");
            //사용자가 지름길을 쓸지 물어보고, 사용하면 "D1"로, 아니면 "N*"으로 감.
            String direction = position.getId().substring(0, 1);    // D2 → "D" : 현재 노드의 맨 앞 알파벳 따옴.
            boolean useShortcut = provider.shouldUseShortcut(direction);

            //지름길 선택한 경우
            if (useShortcut) {
                // 지름길로 갈 대상 ID를 명확히 만듦: "D2" → "D1"
                String shortcutId = direction + "1";

                return candidates.stream()
                        .filter(n -> n.getId().equals(shortcutId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("지름길 노드 " + shortcutId + "를 찾을 수 없습니다."));
            } else {
                // 지름길 안 쓰는 경우 → 가장 숫자가 큰 N 노드로 이동
                return candidates.stream()
                        .filter(n -> n.getId().startsWith("N"))
                        .max(Comparator.comparingInt(n -> Integer.parseInt(n.getId().substring(1))))
                        .orElseThrow(() -> new IllegalStateException("기본 경로에서 이동 가능한 N 노드를 찾을 수 없습니다. 후보: "
                                + candidates.stream().map(Node::getId).toList()));
            }
        }

        else if (currentId.endsWith("2") && !currentId.equals("A2")&& !currentId.startsWith("N")) {
            // 지름길 안 쓰는 경우 → 가장 숫자가 큰 N 노드로 이동
            return candidates.stream()
                    .filter(n -> n.getId().startsWith("N"))
                    .max(Comparator.comparingInt(n -> Integer.parseInt(n.getId().substring(1))))
                    .orElseThrow(() -> new IllegalStateException("기본 경로에서 이동 가능한 N 노드를 찾을 수 없습니다. 후보: "
                            + candidates.stream().map(Node::getId).toList()));

        }


        // 2. A2 노드인 경우 → 무조건 N0 선택
        //시작은 일단 무조건 앞으로 가야하니까.
        //백도 나오면? 뒤로 가야하는 거 아닌가?
        else if (currentId.equals("A2")) {
            return candidates.stream()
                    .filter(n -> n.getId().equals("N0"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("노드 " + currentId + "를 찾을 수 없습니다."));

        }
        // 3. 현재 노드가 N방향인 경우 → N방향 + 숫자 +1 노드로 이동
        else if (currentId.startsWith("N")) {
            try {
                int currentNum = Integer.parseInt(currentId.substring(1)); // "N3" → 3
                String targetId = "N" + (currentNum + 1);

                // 1. N+1 노드 있는지 확인
                for (Node n : candidates) {
                    if (n.getId().equals(targetId)) {
                        return n;  // 바로 반환
                    }
                }

                // 2. 그게 없으면 다른 후보 중에서 현재 또는 뒤로 가는 노드 제외
                List<Node> nonCurrent = new ArrayList<>();
                for (Node n : candidates) {
                    String id = n.getId();
                    if (!id.equals("N" + currentNum) && !id.equals("N" + (currentNum - 1))) {
                        nonCurrent.add(n);
                    }
                }

                if (nonCurrent.size() == 1) {
                    return nonCurrent.get(0);
                } else {
                    throw new IllegalStateException("N" + (currentNum + 1) + "도 없고, 유효한 분기 노드도 1개가 아님. 후보들: "
                            + candidates.stream().map(Node::getId).toList());
                }

            } catch (NumberFormatException e) {
                throw new IllegalStateException("현재 노드 ID 형식 오류: " + currentId, e);
            }
        }

        //지름길 안에 있으면서 A1, B1, 이런 거는 숫자를 하나 줄인 A0, B0 로 가야함.
        else if (currentId.endsWith("1") && !currentId.startsWith("N")) {
            // 예: 현재 D1이면 다음 D0으로 가야 함
            String direction = currentId.substring(0, 1);
            String targetId = direction + "0";

            return candidates.stream()
                    .filter(n -> n.getId().equals(targetId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("노드 " + targetId + "를 찾을 수 없습니다."));
        }

        else if (currentId.endsWith("0") && !currentId.startsWith("N")) {
            return candidates.stream()
                    .filter(n -> n.getId().equals("00"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("0단계 노드에서 중심(00)으로 가는 경로가 없습니다."));
        }


        //위 조건 모두 해당 안 되는 경우는 그냥 첫 번째 후보 노드로 이동
        return candidates.getFirst();
    }
    /**
     * 말이 한 칸 이동하는 동작 (말 상태, 위치 업데이트 등 포함)
     * - groupedHorses가 있는 경우 같이 이동
     * - 말이 도착 지점일 경우 상태를 FINISHED로 설정
     * - 동일 위치 말이 같은 팀이면 그룹핑 시도
     */
    private void moveStep(boolean isRemain, boolean isFirstStep, int stepsLeft, ShortcutDecisionProvider provider) {

        //현재 위치가 없으면 오류
        if (position == null) throw new IllegalStateException("현재 위치가 설정되지 않았습니다.");

        //다음으로 갈 수 있는 후보 노드 리스트
        List<Node> nextList = position.getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            this.state = HorseState.FINISHED;
            return;
        }

        //다음 이동 노드 선택
        /*
         * isRemain: 지금 이동이 마지막 칸인지 여부 (즉, steps 중 마지막)
         * position.getId().startsWith("N"): 현재 위치가 N0, N1, ... 등 바깥 테두리 노드인지 확인
         * nextList.size() == 3: 후보가 3개 있을 때 → 지름길 포함된 분기점이라는 뜻
         * */
        Node next = (isRemain && position.getId().startsWith("N") && nextList.size() == 3) ?
                nextList.get(2) :   // 지름길 조건
                chooseNextNode(nextList, isFirstStep, stepsLeft, team.getBoardType(), provider);


        System.out.println("$$$$$$next: " + next.getId());
        System.out.println("next: " + nextList.toString());
        setPosition(next);

        // 그룹 말도 함께 이동
        for (Horse grouped : groupedHorses) {
            grouped.setPosition(next);
        }

        // 도착 지점인지 확인
        if (position.isGoal()) {
            this.state = HorseState.FINISHED;
            return;
        }
    }

    /**
     * 말이 주어진 칸 수만큼 이동하는 함수
     * - 백도 처리
     * - A2 완주 처리
     * - 말 잡기 및 FINISHED 상태 반영
     * @return 상대 말을 잡았을 경우 true
     */

    // n칸 이동
    public boolean move(int steps, List<Node> board, String boardType, ShortcutDecisionProvider provider) {
        if (isFinished()) return false;

        backupState();
        boolean capturedSomeone = false;

        // 처음 출발이면 시작 위치에 놓기
        if (position == null) {
            if (steps < 0) {
                // 출발 전에 백도 → 대기 상태 유지
                state = HorseState.WAITING;
                return false;
            }
            position = BoardFactory.getStartNode(board, boardType);
            state = HorseState.MOVING;
        }

        // 백도 처리: 스택에서 되돌림
        if (steps == -1) {
            if (!positionHistory.isEmpty()) {
                Node previous = positionHistory.pop(); // 되돌아갈 위치
                setPositionWithoutHistory(previous);
                // 그룹 말도 각자 스택에서 되돌림
                for (Horse grouped : groupedHorses) {
                    if (!grouped.equals(this) && !grouped.positionHistory.isEmpty()) {
                        Node groupedPrev = grouped.positionHistory.pop();
                        grouped.setPositionWithoutHistory(groupedPrev);
                    }
                }

                // 백도 위치에서 상대 말 제거 처리
                List<Horse> others = new ArrayList<>(position.getHorsesOnNode());
                for (Horse other : others) {
                    if (isCaptured(other)) {
                        other.reset();
                        System.out.println("[백도 잡기] " + this.getId() + "이(가) " + other.getId() + "를 잡았습니다.");
                    }
                }

                System.out.println("[백도] 푸시 직전 스택 상태:");
                for (Node n : positionHistory) {
                    System.out.println(" - " + n.getId());
                }
                System.out.println("[백도] 푸시 직후 스택 상태:");
                for (Node n : positionHistory) {
                    System.out.println(" - " + n.getId());
                }
                System.out.println("[백도] " + id + "가 " + position.getId() + "로 되돌아감");

                // 스택 전체 출력 (디버깅용)
                System.out.println("[백도] 현재 스택 상태:");
                for (Node n : positionHistory) {
                    System.out.println(" - " + n.getId());
                }
            } else {
                System.out.println("[백도] 더 이상 되돌아갈 위치가 없습니다.");
            }
            return false;
        }

        // n칸만큼 순차적으로 이동
        for (int i = 0; i < steps; i++) {
            boolean isFirst = (i == 0);
            boolean isLast = (i == steps - 1);
            int stepsLeft = steps - i;
            moveStep(isLast, isFirst, stepsLeft, provider);

            // 완주 조건 (A2에 N0 경유 없이 도달)
            // A2 도달 + 직전 노드가 N0가 아니면 완주 처리
            if (position != null && position.getId().equals("A2")) {
                String prevId = !positionHistory.isEmpty() ? positionHistory.peek().getId() : "없음";
                if (!prevId.equals("N0")) {
                    this.state = HorseState.FINISHED;

                    // 여기서 groupedHorses도 완주 처리 필요
                    for (Horse grouped : groupedHorses) {
                        grouped.state = HorseState.FINISHED;
                    }

                    System.out.println("[완주] " + id + "가 A2에 도달했으며, " + prevId + "를 통해 A2로 들어왔습니다.");
                    return false;
                } else {
                    System.out.println("[진입] " + id + "가 N0를 통해 A2로 들어왔습니다. 계속 진행합니다.");
                }
            }
        }

        // 도착 후 말 잡기
        List<Horse> others = new ArrayList<>(position.getHorsesOnNode()); // 복사해서 안전하게 순회
        for (Horse other : others) {
            if (isCaptured(other)) {
                // 말 제거 및 리셋
                other.reset();
                capturedSomeone = true;

                // UI 반영을 위해 repaint가 필요할 수 있음
                System.out.println("[잡기] " + this.getId() + "이(가) " + other.getId() + "를 잡았습니다.");
            }
        }

        // 도착 위치에서만 같은 팀 말과 그룹핑 (업기)
        if (!isFinished() && position != null) {
            for (Horse other : position.getHorsesOnNode()) {
                if (this != other && isGroupable(other)) {
                    groupWith(other);
                }
            }
        }
        return capturedSomeone;
    }

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
        MainFrame.getInstance().updatePiece(this.position, null);  // UI에서 말 제거
    }

    /**
     * 상대 말을 잡았는지 확인
     */
    public boolean isCaptured(Horse other) {
        return !teamIdEquals(other) && this.position == other.position;
    }

    /**
     * 그룹핑 가능한 상대인지 확인
     */
    public boolean isGroupable(Horse other) {
        return teamIdEquals(other)
            && this.position != null
            && other.position != null
            && this.position.equals(other.position)  // equals 사용
            && !groupedHorses.contains(other);
    }

    /**
     * 말이 FINISHED 상태인지 확인
     */
    public boolean isFinished() {
        return state == HorseState.FINISHED;
    }

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
            // 그룹된 말도 동일한 히스토리를 공유하도록 설정
            other.positionHistory.clear();
            other.positionHistory.addAll(this.positionHistory);
        }
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamID == other.teamID;
    }

    /**
     * 말의 간단한 문자열 반환 (예: A,H2)
     */
    public String toString2() {
        char teamChar = (char) ('A' + teamID);
        return teamChar + ",H" + horseIdx;
    }

    /**
     * 말의 상세 문자열 반환 (예: Team A, H2)
     */
    @Override
    public String toString() {
        char teamChar = (char) ('A' + teamID);
        return "Team " + teamChar + ", H" + horseIdx;
    }}