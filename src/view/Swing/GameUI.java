package view.Swing;

import view.GameUIBase;
import view.Swing.BoardPanel;
import view.Swing.DicePanel;
import view.Swing.ScoreboardPanel;

// Swing
public interface GameUI extends GameUIBase {
    view.Swing.BoardPanel getBoardPanel();
    view.Swing.DicePanel getDicePanel();
    view.Swing.ScoreboardPanel getScoreboardPanel();
}

