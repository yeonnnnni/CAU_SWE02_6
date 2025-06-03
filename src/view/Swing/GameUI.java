package view.Swing;

import view.GameUIBase;

// Swing
public interface GameUI extends GameUIBase {
    view.Swing.BoardPanel getBoardPanel();
    view.Swing.DicePanel getDicePanel();
    view.Swing.ScoreboardPanel getScoreboardPanel();
}

