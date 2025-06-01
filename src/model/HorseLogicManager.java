package model;

import builder.BoardFactory;
import model.*;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.awt.Color;
import java.util.*;

/**
 *
 * 이동 및 게임 로직
 | **메서드**                        | **설명**         |
 | ------------------------------ | -------------- |
 | move(...)                      | 전체 이동 처리 (n칸)  |
 | moveStep(...)                  | 말 한 칸 이동       |
 | chooseNextNode(...)            | 분기점에서 다음 노드 선택 |
 | setPositionWithoutHistory(...) | 경로 기록 없이 위치 변경 |
 | isCaptured(Horse other)        | 상대 말을 잡았는지     |
 | isGroupable(Horse other)       | 그룹핑 가능 여부      |
 | isFinished()                   | 완주 여부 확인       |
 | teamIdEquals(Horse other)      | 팀 일치 확인        |

 */

@Getter
@Setter
@AllArgsConstructor
public class HorseLogicManager{
    private static boolean teamIdEquals(Horse h, Horse other) {
        return h.getTeamID() == other.teamID;
    }

    public static boolean isFinished(Horse h) {
        return h.getState() == HorseState.FINISHED;
    }

    public boolean isGroupable(Horse h, Horse other) {
        return teamIdEquals(h, other)
                && h.getPosition() != null
                && other.getPosition() != null
                && h.getPosition().equals(other.getPosition())
                && !h.getGroupedHorses().contains(other);
    }

    public boolean isCaptured(Horse h, Horse other) {
        return !teamIdEquals(h, other) && h.getPosition() == other.getPosition();
    }

    private void setPositionWithoutHistory(Horse h, Node newPos) {
        if (h.getPosition() != null) {
            h.getPosition().removeHorse(h);
        }
        h.getPosition() = newPos;
        if (newPos != null) {
            newPos.addHorse(h);
        }
    }



    private void moveStep(horse h, boolean isRemain, boolean isFirstStep, int stepsLeft, ShortcutDecisionProvider provider) {

        //현재 위치가 없으면 오류
        if ( == null) throw new IllegalStateException("현재 위치가 설정되지 않았습니다.");

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


    public boolean move(Horse h, int steps, List<Node> board, String boardType, ShortcutDecisionProvider provider) {
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
            //printStatus();
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
        List<Horse> others = position.getHorsesOnNode();
        for (Horse other : others) {
            if (isCaptured(other)) {
                other.reset();
                capturedSomeone = true;
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



}
