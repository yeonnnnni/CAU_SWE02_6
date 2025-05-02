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
}
