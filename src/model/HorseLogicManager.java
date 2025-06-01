package model;

import controller.ShortcutDecisionProvider;
import java.util.*;

public class HorseLogicManager {
    private final HorseGroupManager groupManager = new HorseGroupManager();
    private final HorseBackupManager backupManager = new HorseBackupManager();

    public boolean move(Horse horse, int steps, List<Node> board, String boardType, ShortcutDecisionProvider provider) {
        if (isFinished(horse)) return false;

        backupManager.backup(horse);
        Node current = horse.getPosition();
        Node next = current;

        for (int i = 0; i < steps; i++) {
            next = next.getNext(boardType, provider);
        }

        if (next == null) {
            horse.setState(HorseState.FINISHED);
            return true;
        }

        horse.setPosition(next);
        horse.setState(HorseState.MOVING);
        return true;
    }

    public boolean isCaptured(Horse a, Horse b) {
        return a.getTeam() != b.getTeam() && a.getPosition() == b.getPosition();
    }

    public boolean isFinished(Horse horse) {
        return horse.getState() == HorseState.FINISHED;
    }
}