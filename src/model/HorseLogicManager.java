package model;

import java.util.*;
import model.*;
import builder.BoardFactory;

public class HorseLogicManager {
    public boolean move(Horse h, int steps, List<Node> board, String boardType,
                        ShortcutDecisionProvider provider,
                        HorseLogicManager logicManager,
                        HorseGroupManager groupManager,
                        HorseBackupManager backupManager) {

        if (h.isFinished()) return false;
        backupManager.backupState(h);
        boolean capturedSomeone = false;

        if (h.getPosition() == null) {
            if (steps < 0) {
                h.setState(HorseState.WAITING);
                return false;
            }
            Node start = BoardFactory.getStartNode(board, boardType);
            h.setPosition(start);
            h.setState(HorseState.MOVING);
        }

        if (steps == -1) {
            if (!h.getPositionHistory().isEmpty()) {
                Node previous = h.getPositionHistory().pop();
                setPositionWithoutHistory(h, previous);
            }
            return false;
        }

        for (int i = 0; i < steps; i++) {
            boolean isFirst = (i == 0);
            boolean isLast = (i == steps - 1);
            int stepsLeft = steps - i;
            moveStep(h, isLast, isFirst, stepsLeft, provider, groupManager);

            if (h.getPosition() != null && h.getPosition().getId().equals("A2")) {
                String prevId = !h.getPositionHistory().isEmpty() ? h.getPositionHistory().peek().getId() : "";
                if (!"N0".equals(prevId)) {
                    h.setState(HorseState.FINISHED);
                    for (Horse grouped : groupManager.getGroupedHorses(h)) {
                        grouped.setState(HorseState.FINISHED);
                    }
                    return false;
                }
            }
        }

        List<Horse> others = h.getPosition().getHorsesOnNode();
        for (Horse other : others) {
            if (isCaptured(h, other)) {
                resetHorse(other, groupManager);
                capturedSomeone = true;
            }
        }

        if (!h.isFinished() && h.getPosition() != null) {
            for (Horse other : h.getPosition().getHorsesOnNode()) {
                if (h != other && isGroupable(h, other, groupManager)) {
                    groupManager.groupWith(h, other);
                }
            }
        }

        return capturedSomeone;
    }

    public void moveStep(Horse h, boolean isRemain, boolean isFirstStep, int stepsLeft, ShortcutDecisionProvider provider, HorseGroupManager groupManager) {
        List<Node> nextList = h.getPosition().getNextNodes();
        if (nextList == null || nextList.isEmpty()) {
            h.setState(HorseState.FINISHED);
            return;
        }

        Node next = (isRemain && h.getPosition().getId().startsWith("N") && nextList.size() == 3)
                ? nextList.get(2)
                : chooseNextNode(h, nextList, isFirstStep, stepsLeft, h.getTeam().getBoardType(), provider);

        h.setPosition(next);
        List<Horse> group = groupManager.getGroupedHorses(h);
        for (Horse grouped : group) {
            grouped.setPosition(next);
        }

        if (h.getPosition().isGoal()) {
            h.setState(HorseState.FINISHED);
        }
    }

    public Node chooseNextNode(Horse h, List<Node> candidates, boolean isFirstStep, int stepsLeft,
                               String boardType, ShortcutDecisionProvider provider) {
        // 현재 말의 위치 ID
        String currentId = h.getPosition().getId();  // position은 Node

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
                String prevId = h.getPositionHistory().isEmpty() ? null : h.getPositionHistory().peek().getId();

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
            String direction = h.getPosition().getId().substring(0, 1);    // D2 → "D" : 현재 노드의 맨 앞 알파벳 따옴.
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

    public void setPositionWithoutHistory(Horse h, Node newPos) {
        if (h.getPosition() != null) h.getPosition().removeHorse(h);
        h.setPosition(newPos);
        if (newPos != null) newPos.addHorse(h);
    }

    public boolean isCaptured(Horse h, Horse other) {
        return h.getTeamID() != other.getTeamID() && h.getPosition() == other.getPosition();
    }

    public static boolean isGroupable(Horse h, Horse other, HorseGroupManager groupManager) {
        return h.getTeamID() == other.getTeamID()
                && h.getPosition() != null
                && other.getPosition() != null
                && h.getPosition().equals(other.getPosition())
                && !groupManager.getGroupedHorses(h).contains(other);
    }

    public void resetHorse(Horse h, HorseGroupManager groupManager) {
        if (h.getPosition() != null) {
            h.getPosition().removeHorse(h);
        }
        h.setPosition(null);
        h.setState(HorseState.WAITING);
        h.getPositionHistory().clear();
        groupManager.resetGroupedHorses(h);
    }
}

