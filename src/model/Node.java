package model;

import java.util.ArrayList;
import java.util.List;

//Node 클래스는 게임 보드의 각 지점(칸)을 나타냅니다.
public class Node {
    private final String id;  // 예: "N0", "A1", "OO"
    private final List<Node> nextNodes = new ArrayList<>();
    private boolean isGoal = false;
    private boolean isCenter = false;
    private final List<Horse> horsesOnNode = new ArrayList<>();

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public void addNextNode(Node node) {
        nextNodes.add(node);
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setGoal(boolean goal) {
        isGoal = goal;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public boolean isCorner() {
        if (id == null || id.startsWith("N")) return false;
        return id.length() >= 2 && id.charAt(1) == '2';
    }

    public void addHorse(Horse horse) {
        horsesOnNode.add(horse);
    }

    public void removeHorse(Horse horse) {
        horsesOnNode.remove(horse);
    }
    public void clearHorses(){
        horsesOnNode.clear();
    }

    public List<Horse> getHorsesOnNode() {
        return new ArrayList<>(horsesOnNode);
    }

    // 디버깅용 toString
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
