package controller;

import java.util.*;

import model.Horse;
import model.Node;

public class Board {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Horse> allHorses = new ArrayList<>();

    public Board() {
        for (int i = 0; i < 20; i++) {
            Node node = new Node(i);
            nodes.add(node);
        }
        for (int i = 0; i < nodes.size() - 1; i++) {
            nodes.get(i).addNext(nodes.get(i + 1));
            nodes.get(i + 1).setPrevious(nodes.get(i));
        }
    }

    public Node getStartNode() {
        return nodes.get(0);
    }

    public Horse addHorse(String owner) {
        Horse horse = new Horse(getStartNode(), owner, this);
        allHorses.add(horse);
        return horse;
    }

    public List<Horse> getHorses() {
        return allHorses;
    }

    public List<Node> getAllNodes() {
        return nodes;
    }

    //플레이어의 말만 반환하는 메서드
    public List<Horse> getHorsesForPlayer(String owner) {
        List<Horse> result = new ArrayList<>();
        for (Horse h : allHorses) {
            if (h.getOwner().equals(owner) && !h.isFinished()) {
                result.add(h);
            }
        }
        return result;
    }

    //이동 가능성 판단(현재 단순 이동 가능 여부 체크)
    public boolean canMove(Horse horse, int steps) {
        Node current = horse.getCurrentNode();
        if (current == null) return false;
    
        Node target = current;
        if (steps > 0) {
            for (int i = 0; i < steps; i++) {
                List<Node> nexts = target.getNextNodes();
                if (nexts.isEmpty()) return false;
                target = nexts.get(0);
            }
        } else {
            for (int i = 0; i < -steps; i++) {
                if (target.getPrevious() == null) return false;
                target = target.getPrevious();
            }
        }
    
        return true;
    }

    //노드연결초기화
    public void initialize(String boardType, Node[] inputNodes) {
        nodes.clear();
        Collections.addAll(nodes, inputNodes);
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

    private void initSquare() {}
    private void initPolygon(int sides) {}
    
}

/*
 수정필요
public class Board {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Horse> allHorses = new ArrayList<>();

    public Board() {
        // 초기 노드 배열은 MainFrame에서 생성된 Node[]를 initialize 호출 시 세팅
    }

    public void initialize(String boardType, Node[] inputNodes) {
        nodes.clear();
        Collections.addAll(nodes, inputNodes);
        switch (boardType.toLowerCase()) {
            case "square":
                initSquare();
                break;
            case "circle":
                initLoop();
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

    private void initSquare() {
        // 기본 사각형 루프 (0~19 연결)
        for (int i = 0; i < 20; i++) {
            Node current = nodes.get(i);
            Node next = nodes.get((i + 1) % 20);
            current.addNext(next);
            next.setPrevious(current);
        }
        // 지름길, 중앙 결승점 연결 등은 추가 구현 가능
    }

    private void initLoop() {
        // 원형 루프: 모든 노드가 순환 연결
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            Node current = nodes.get(i);
            Node next = nodes.get((i + 1) % size);
            current.addNext(next);
            next.setPrevious(current);
        }
    }

    private void initPolygon(int sides) {
        // 다각형 루프: 노드 수를 sides보다 크거나 같은 수로 관리해야 함
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            Node current = nodes.get(i);
            Node next = nodes.get((i + 1) % size);
            current.addNext(next);
            next.setPrevious(current);
        }
    }

    public Node getStartNode() {
        return nodes.isEmpty() ? null : nodes.get(0);
    }

    public Horse addHorse(String owner) {
        Horse horse = new Horse(getStartNode(), owner, this);
        allHorses.add(horse);
        return horse;
    }

    public List<Horse> getHorsesForPlayer(String owner) {
        List<Horse> result = new ArrayList<>();
        for (Horse h : allHorses) {
            if (h.getOwner().equals(owner) && !h.isFinished()) {
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
                if (target.getPrevious() == null) return false;
                target = target.getPrevious();
            }
        }
        return true;
    }

    public List<Horse> getHorses() {
        return allHorses;
    }
}
 */
