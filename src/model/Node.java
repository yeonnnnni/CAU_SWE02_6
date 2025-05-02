package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {
    private final int id;
    private final List<Node> nextNodes = new ArrayList<>();
    private final CopyOnWriteArrayList<Horse> horses = new CopyOnWriteArrayList<>();
    private Node previous;

    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addNext(Node node) {
        nextNodes.add(node);
        node.setPrevious(this);
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public void addHorse(Horse horse) {
        // 업기: 같은 소유자의 말이 있으면 그룹으로 표시
        for (Horse h : horses) {
            if (h.getOwner().equals(horse.getOwner())) {
                h.setGrouped(true);
                horse.setGrouped(true);
            }
        }

        // 잡기: 다른 소유자면 잡아서 말 위치를 시작점으로 초기화
        for (Horse h : horses) {
            if (!h.getOwner().equals(horse.getOwner())) {
                horses.remove(h); // 안전하게 말 제거
                h.sendToStart();
            }
        }

        horses.add(horse); // 마지막에 말 추가
    }

    public void removeHorse(Horse horse) {
        horses.remove(horse);
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }
}
