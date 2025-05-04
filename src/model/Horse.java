// model/Horse.java — 기본 윷놀이 말 클래스
package model;

import controller.Board;
import java.util.*;

public class Horse {
    private Node currentNode;
    private final String owner;
    private final Board board;
    private boolean grouped = false;
    private boolean finished = false;

    public Horse(Node startNode, String owner, Board board) {
        this.currentNode = startNode;
        this.owner = owner;
        this.board = board;
    }

    public void move(int steps) {
        Node current = this.currentNode;
        if (steps > 0) {
            for (int i = 0; i < steps; i++) {
                List<Node> nexts = current.getNextNodes();
                if (nexts.isEmpty()) break;
                current = nexts.get(0);
            }
        } else if (steps < 0) {
            for (int i = 0; i < -steps; i++) {
                Node prev = current.getPrevious();
                if (prev == null) break;
                current = prev;
            }
        }
        moveTo(current);
    }

    public void moveTo(Node node) {
        if (currentNode != null) currentNode.removeHorse(this);
        this.currentNode = node;
        node.addHorse(this);
        if (node.getId() == 28) finished = true;
    }

    public void sendToStart() {
        if (currentNode != null) currentNode.removeHorse(this);
        Node start = board.getStartNode();
        this.currentNode = start;
        start.addHorse(this);
        grouped = false;
    }

    public void removeFromBoard() {
        if (currentNode != null) {
            currentNode.removeHorse(this);
            currentNode = null;
        }
        grouped = false;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return owner + "의 말" + (finished ? "(완료)" : "");
    }
}

