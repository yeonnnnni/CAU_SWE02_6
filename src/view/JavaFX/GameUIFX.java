package view.JavaFX;

import view.GameUIBase;

public interface GameUIFX extends GameUIBase {
    BoardPanelFX getBoardPanel();
    DicePanelFX getDicePanel();
    ScoreboardPanelFX getScoreboardPanel();
}
