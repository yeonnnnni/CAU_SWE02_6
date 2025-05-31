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
}
