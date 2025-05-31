package model;

import java.util.ArrayList;
import java.util.List;

//Node 클래스는 게임 보드의 각 지점(칸)을 나타냅니다.
public class Node {
    private final String id;  // 예: "N0", "A1", "OO"
    // 다음에 이동할 수 있는 노드들의 리스트 (방향성 있음)
    private final List<Node> nextNodes = new ArrayList<>();
    // 이 노드가 목표 지점(완주 위치)인지 여부
    private boolean isGoal = false;
    // 이 노드가 중심 노드인지 여부 ("00" 같은 중앙 위치)
    private boolean isCenter = false;
    // 현재 이 노드에 위치한 말 목록
    private final List<Horse> horsesOnNode = new ArrayList<>();

    //생성자
    public Node(String id) {
        this.id = id;
    }

    //Getter/Setter
    public String getId() {
        return id;
    }

    //경로 및 상태 설정
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

    //Corner 노드 판별
    public boolean isCorner() {
        if (id == null || id.startsWith("N")) return false;
        return id.length() >= 2 && id.charAt(1) == '2';
    }

    //말 관리 기능
    public void addHorse(Horse horse) {
        horsesOnNode.add(horse);
    }

    public void removeHorse(Horse horse) {
        horsesOnNode.remove(horse);
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

    //id 값이 같은 노들 동등하게 인식
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node other = (Node) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
