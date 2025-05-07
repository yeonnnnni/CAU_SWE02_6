package model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final int        id; // 위치 식별자 (e.g., "S", "A1", "C", "E")
    private final List<Node>    nextNodes;
    private boolean             isGoal;
    private boolean             isCenter;
    private List<Horse>         horsesOnNode = new ArrayList<>();

    public Node(int id) {
        this.id = id;
        this.nextNodes = new ArrayList<>();
        this.isGoal = false;
        this.isCenter = false;
    }

    // 다음 노드 연결
    public void addNextNode(Node next) {
        nextNodes.add(next);
    }

    // Getter/Setter
    public String getId() { return id; }

    public List<Node> getNextNodes() { return nextNodes; }

    public boolean isGoal() { return isGoal; }

    public void setGoal(boolean goal) { isGoal = goal; }

    public boolean isCenter() { return isCenter; }

    public void setCenter(boolean center) { isCenter = center; }

    public void addHorse(Horse horse) { horsesOnNode.add(horse); }

    public void removeHorse(Horse horse) { horsesOnNode.remove(horse); }

    public List<Horse> getHorsesOnNode() { return new ArrayList<>(horsesOnNode); }

    // toString() for Debugging
    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", next=" + nextNodes.stream().map(Node::getId).toList() +
                ", isGoal=" + isGoal +
                ", isCenter=" + isCenter +
                '}';
    }
}

