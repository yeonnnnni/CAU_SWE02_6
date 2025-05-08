package model;

import builder.BoardFactory;
import view.MainFrame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.Point;

public class Horse {
    private final String id;
    private final int teamID;
    private final int horseIdx;
    private final Team team;
    private int delStep;
    private int plusStep;

    private HorseState state;
    private Node position;
    private List<Horse> groupedHorses;
    private HorseBackup backup;

    public Horse(int horseIdx, Team team) {
        this.horseIdx = horseIdx;
        this.teamID = team.getTeamID();
        this.team = team;
        this.id = "T" + teamID + "-H" + horseIdx;
        this.state = HorseState.WAITING;
        this.groupedHorses = new ArrayList<>();
        this.position = null;
        team.addHorse(this);
        delStep = 0;
        plusStep = 0;
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
        }
        this.position = position;
        if (position != null) {
            position.addHorse(this);
        }
    }

    private Node getMaxNNode(List<Node> candidates) {
        return candidates.stream()
                .filter(n -> n.getId().startsWith("N"))
                .max((a, b) -> {
                    int numA = Integer.parseInt(a.getId().substring(1));
                    int numB = Integer.parseInt(b.getId().substring(1));
                    return Integer.compare(numA, numB);
                })
                .orElse(null); // 없을 경우 null 반환
    }

    public Node getDiagonalNextNode(Node current, List<Node> board) {
        String id = current.getId();

        List<String> diagonalPath1 = List.of("C2", "C1", "C0", "00", "A0", "A1", "A2");
        List<String> diagonalPath2 = List.of("D2", "D1", "D0", "00", "B0", "B1", "B2");

        int index = diagonalPath1.indexOf(id);
        if (index != -1 && index < diagonalPath1.size() - 1) {
            String nextId = diagonalPath1.get(index + 1);
            System.out.println("[getDiagonalNextNode] nextId: " + nextId);
            return Node.findById(board, nextId);
        }

        index = diagonalPath2.indexOf(id);
        if (index != -1 && index < diagonalPath2.size() - 1) {
            String nextId = diagonalPath2.get(index + 1);
            return Node.findById(board, nextId);
        }

        return null; // 대각선 경로에 포함되지 않거나 끝 지점이면 null
    }

    // 분기점에서 사용자에게 경로 선택 유도
    private Node chooseNextNode(List<Node> candidates, boolean isLast) {
        String currentId = position.getId();
        System.out.println("[chooseNextNode] 현재 노드: " + currentId + ", isLast: " + isLast);

        // 1. center → A 방향
        if (position.isCenter()) {
            System.out.println("[chooseNextNode] center → A 방향 선택");
            return candidates.stream()
                    .filter(n -> n.getId().startsWith("A"))
                    .findFirst()
                    .orElse(candidates.getFirst());
        }

        // 2. A2 → N0
        if (currentId.equals("A2")) {
            System.out.println("[chooseNextNode] A2 → N0로 강제 이동");
            return candidates.stream()
                    .filter(n -> n.getId().equals("N0"))
                    .findFirst()
                    .orElse(candidates.getFirst());
        }

        // 3. 코너 노드 + 마지막 이동
        if ((currentId.endsWith("2") && !currentId.equals("A2") && isLast && position.isCorner()) || delStep == 2) {
            System.out.println("[chooseNextNode] 코너 노드에서 지름길 여부 확인");
            if (delStep == 2){
                delStep = 0;
                plusStep = 1;
            }

            String direction = currentId.substring(0, 1);
            boolean useShortcut = MainFrame.getInstance().promptShortcutChoice(direction);

            if (useShortcut) {
                int currentLevel = Character.getNumericValue(currentId.charAt(1));
                int targetLevel = currentLevel - 1;
                String targetId = direction + targetLevel;

                System.out.println("[chooseNextNode] 일반 경로 선택 → " + targetId);

                return candidates.stream()
                        .filter(n -> n.getId().equals(targetId))
                        .findFirst()
                        .orElse(candidates.getFirst());
            } else {
                System.out.println("[chooseNextNode] 일반 경로 선택 → 외곽(N 방향)");
                return getMaxNNode(candidates);
//                candidates.stream()
//                        .filter(n -> n.getId().startsWith("N"))
//                        .findFirst()
//                        .orElse(candidates.getFirst());
            }
        }

        // 4. N방향 → N+1
        if (currentId.startsWith("N")) {
            try {
                if (position.getId().startsWith("N") && candidates.size() == 3){
                    delStep = 1;
                }
                int n = Integer.parseInt(currentId.substring(1));
                String nextId = "N" + (n + 1);
                System.out.println("[chooseNextNode] N 노드 → 다음 노드: " + nextId);
                return candidates.stream()
                        .filter(nNode -> nNode.getId().equals(nextId))
                        .findFirst()
                        .orElse(candidates.getFirst());
            } catch (NumberFormatException e) {
                System.out.println("[chooseNextNode] N 노드 번호 파싱 실패");
            }
        }

        if (!isLast && position.isDiagonal()) {
            System.out.println("[chooseNextNode] digonal");
            return getDiagonalNextNode(position, candidates);
        }


        // fallback
        System.out.println("[chooseNextNode] 기본 선택 (첫 번째 후보)");
        return candidates.getFirst();
    }

    private void moveStep(boolean isLast) { // == islast
        if (position == null) throw new IllegalStateException("현재 위치가 설정되지 않았습니다.");

        List<Node> nextList = position.getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            this.state = HorseState.FINISHED;
            return;
        }

        Node next = (isLast && position.getId().startsWith("N") && nextList.size() == 3) ?
                nextList.get(2) : // 마지막칸이 남아있지않고, n이면서 sizerk가 3이라면
                chooseNextNode (nextList, isLast);

        System.out.println("curr: " + position.getId());
        System.out.println("next: " + nextList.toString());
        System.out.println("islast: " + isLast);
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

        // 도착 후 자동 그룹핑
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

        for (int i = 0; i < steps; i++) {
//            System.out.println(steps-1 == i);
            moveStep(steps-1 == i); //마지막 노드일경우 참
            if (delStep == 1) {
                steps--;
                delStep = 0;
            }
            if (plusStep == 1) {
                steps++;
                plusStep = 0;
            }
            if (position.isCorner()) delStep = 2;
            if (isFinished()) return;
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
