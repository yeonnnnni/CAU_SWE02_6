package view;

import model.Horse;
import model.Node;
import model.YutResult;

import java.util.List;

public interface GameUI {
    boolean isRandomMode();
    String getManualInput();
    void showDiceResult(List<YutResult> results);
    void setRollListener(Runnable listener);

    void showMessage(String message);
    boolean confirmShortcut(String direction);
    Horse selectHorse(List<Horse> candidates, int steps);
    YutResult chooseYutResult(List<YutResult> options);
    boolean promptRestart(String winnerName);
    void updatePiece(Node from, Node to);
    void setDiceRollEnabled(boolean enabled);

    BoardPanel getBoardPanel();
    DicePanel getDicePanel();
    ScoreboardPanel getScoreboardPanel();
    void setCurrentPlayer(String name);
}
