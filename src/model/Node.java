package model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Node> nextNodes = new ArrayList<>();
    private boolean isCenter = false;

    public void addNext(Node node) {
        nextNodes.add(node);
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public boolean isCenter() {
        return isCenter;
    }
}