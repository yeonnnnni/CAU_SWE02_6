package model;

import builder.BoardFactory;
import view.MainFrame;

import java.awt.Color;
import java.util.*;
import javax.swing.JButton;
import java.awt.Point;

public class Horse {
    private final String id;
    private final int teamID;
    private final int horseIdx;
    private final Team team;

    private HorseState state;
    private Node position;
    private List<Horse> groupedHorses;
    private HorseBackup backup;

    //빽도를 위한 경로 스택 생성
    private final Deque<Node> positionHistory = new ArrayDeque<>();


    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.groupedHorses = new ArrayList<>();
        this.position = null;
        team.addHorse(this);
    }

    public String getId() { return id; }
    public int getTeamID() { return teamID; }
    public HorseState getState() { return state; }
    public List<Horse> getGroupedHorses() { return groupedHorses; }
    public void setState(HorseState state) { this.state = state; }
    public Team getTeam() { return team; }

    public Node getPosition() {
        return position;
    }

    public Color getTeamColor() {
        return team.getColor();
    }

    // 말 위치 설정 (노드 등록 및 제거 포함)
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

    // 분기점에서 사용자에게 경로 선택 유도
    /*
     * candidates: 현재 위치에서 갈 수 있는 노드들 → position.getNextNodes()가 넘겨주는 리스트
     * 반환값: 다음에 이동할 Node
     * 지금 위치한 노드(position)의 nextNodes 목록(candidates) 중에서 어디로 이동할지를 결정해주는 함수
     * */
    private Node chooseNextNode(List<Node> candidates, boolean isFirstStep, int stepsLeft) {
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
                // 👉 B 라인으로 이동
                return candidates.stream()
                        .filter(n -> n.getId().equals("B0"))
                        .findFirst()
                        .orElse(candidates.getFirst());
            } else {
                // 👉 기본 A 라인으로 이동
                return candidates.stream()
                        .filter(n -> n.getId().startsWith("A"))
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

        //각 방향의 "2"번 노드이면서 A2는 아니면서 N2는 아니면서 시작이 vertex인 경우.
        else if (isFirstStep && currentId.endsWith("2") && !currentId.equals("A2")&& !currentId.startsWith("N")) {
            System.out.println("vertex!!");
            //사용자가 지름길을 쓸지 물어보고, 사용하면 "D1"로, 아니면 "N*"으로 감.
            String direction = position.getId().substring(0, 1);    // D2 → "D" : 현재 노드의 맨 앞 알파벳 따옴.
            boolean useShortcut = MainFrame.getInstance().promptShortcutChoice(direction);

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
        //⚠️백도 나오면? 뒤로 가야하는 거 아닌가?
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

    private void moveStep(boolean isRemain, boolean isFirstStep, int stepsLeft) {
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
                //⚠️이러면 사용자의 선택 없이 무조건 지름길로 감.
                nextList.get(2) : // 마지막칸이 남아있지않고, n이면서 sizerk가 3이라면
                chooseNextNode (nextList, isFirstStep, stepsLeft);

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

        // 현재 위치에 같은 팀 말이 있으면 그룹핑
        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (this != other && isGroupable(other)) {
                groupWith(other);
            }
        }
    }

    // n칸 이동
    public void move(int steps, List<Node> board, String boardType) {
        if (isFinished()) return;

        backupState();

        if (position == null) {
            position = BoardFactory.getStartNode(board, boardType);
            state = HorseState.MOVING;
            if (steps < 0) return;
        }

        if (steps == -1) {
            if (!positionHistory.isEmpty()) {
                Node waste = positionHistory.pop();
                Node previous = positionHistory.pop(); // 되돌아갈 위치
                System.out.println("[백도] 푸시 직전 스택 상태:");
                for (Node n : positionHistory) {
                    System.out.println(" - " + n.getId());
                }
                setPosition(previous);
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
            printStatus();
            return;
        }

        for (int i = 0; i < steps; i++) {
            boolean isFirst = (i == 0);
            boolean isLast = (i == steps - 1);
            int stepsLeft = steps - i;
            moveStep(isLast, isFirst, stepsLeft);
        }

        // 도착 후 말 잡기
        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (isCaptured(other)) {
                other.reset();
            }
        }

        printStatus(); // 디버깅 로그 출력
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
    }

    public boolean isCaptured(Horse other) {
        return !teamIdEquals(other) && this.position == other.position;
    }

    public boolean isGroupable(Horse other) {
        return teamIdEquals(other) && this.position == other.position && !groupedHorses.contains(other);
    }

    public boolean isFinished() {
        return state == HorseState.FINISHED;
    }

    public void groupWith(Horse other) {
        if (isGroupable(other)) {
            groupedHorses.add(other);
            other.groupedHorses.clear();
            other.groupedHorses.add(this);
            other.setPosition(this.position);
        }
    }

    private boolean teamIdEquals(Horse other) {
        return this.teamID == other.teamID;
    }

    // 디버깅용 말 상태 출력
    public void printStatus() {
        String positionId = (position != null) ? position.getId() : "null";
        String coord = "null";

        if (position != null) {
            JButton btn = MainFrame.getInstance()
                    .getBoardPanel()
                    .getNodeToButtonMap()
                    .get(position);
            if (btn != null) {
                Point p = btn.getLocation();
                coord = "(" + p.x + ", " + p.y + ")";
            } else {
                coord = "(버튼 없음)";
            }
        }

        System.out.printf(
                "[말 상태] %s | 상태: %s | 위치: %s | 좌표: %s\n",
                this.id, this.state, positionId, coord
        );
    }

    @Override
    public String toString() {
        return "T" + teamID + "-H" + horseIdx;
    }
}
