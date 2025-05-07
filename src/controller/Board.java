package controller;

import model.Node;
import model.Horse;
import java.util.*;

public class Board {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Horse> allHorses = new ArrayList<>();

    public Board() {
        // 생성자: 특별 초기화 없음
    }

    /**
     * 보드 타입에 맞춰 노드 간 연결 초기화
     * @param boardType "square", "pentagon", "hexagon"
     * @param inputNodes MainFrame 등에서 전달된 Node 배열
     */
    public void initialize(String boardType, Node[] inputNodes) {
        // 기본 데이터 세팅
        nodes.clear();
        Collections.addAll(nodes, inputNodes);
        // 기존 연결 해제
        for (Node node : nodes) {
            node.getNextNodes().clear();
            node.setPrevious(null);
        }
        // 타입별 연결 설정
        switch (boardType.toLowerCase()) {
            case "square":
                initSquare();
                break;
            case "pentagon":
                initPolygon(5);
                break;
            case "hexagon":
                initPolygon(6);
                break;
            default:
                initSquare();
        }
    }

    // 사각형 형태: 주변 20개 노드를 시계방향 순환
    private void initSquare() {
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            Node cur = nodes.get(i);
            Node nxt = nodes.get((i + 1) % size);
            cur.addNext(nxt);
            nxt.setPrevious(cur);
        }
        // 추가 지름길 로직은 확장 가능
    }


    // 다각형 형태: sides 각으로 단순 순환 연결
    private void initPolygon(int sides) {
        initSquare(); // 기본 순환
        // 다각형 꼭짓점 간 지름길 연결은 여기에 추가
    }

    public Node getStartNode() {
        return nodes.isEmpty() ? null : nodes.get(0);
    }

    public List<Horse> getHorsesForTeam(model.Team team) {
        List<Horse> result = new ArrayList<>();
        for (Horse h : allHorses) {
            if (h.getTeam().equals(team) && !h.isFinished()) {
                result.add(h);
            }
        }
        return result;
    }

    public boolean canMove(Horse horse, int steps) {
        Node target = horse.getCurrentNode();
        if (target == null) return false;
        if (steps > 0) {
            for (int i = 0; i < steps; i++) {
                List<Node> nexts = target.getNextNodes();
                if (nexts.isEmpty()) return false;
                target = nexts.get(0);
            }
        } else if (steps < 0) {
            for (int i = 0; i < -steps; i++) {
                Node prev = target.getPrevious();
                if (prev == null) return false;
                target = prev;
            }
        }
        return true;
    }

    public List<Horse> getAllHorses() {
        return allHorses;
    }
    
    public void addHorse(Horse h) { allHorses.add(h); }
}
