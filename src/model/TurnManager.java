package model;

import java.util.*;

public class TurnManager {
    private final List<String> players;
    private int currentIdx = 0;

    public TurnManager(List<String> players) {
        this.players = players;
    }

    public String getCurrentPlayer() {
        return players.get(currentIdx);
    }

    public void nextTurn() {
        currentIdx = (currentIdx + 1) % players.size();
    }

    public List<String> getPlayers() {
        return players;
    }
}
