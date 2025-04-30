package controller;

public class TurnManager {
    private int currentPlayer = 0;
    private int totalPlayers;

    public TurnManager(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % totalPlayers;
    }
}