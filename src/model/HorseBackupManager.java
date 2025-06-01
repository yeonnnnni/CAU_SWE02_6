package model;

import java.util.*;

public class HorseBackupManager {
    private final Map<Horse, HorseBackup> horseBackupMap = new HashMap<>();

    public void backup(Horse horse) {
        horseBackupMap.put(horse, new HorseBackup(horse.getPosition(), horse.getState(), horse.getGroupedHorses()));
    }

    public void restore(Horse horse) {
        HorseBackup backup = horseBackupMap.get(horse);
        if (backup != null) {
            horse.setPosition(backup.position);
            horse.setState(backup.state);
            horse.resetGroupedHorses();
            for (Horse h : backup.groupedHorses) {
                horse.groupWith(h);
            }
        }
    }

    private static class HorseBackup {
        Node position;
        HorseState state;
        List<Horse> groupedHorses;

        HorseBackup(Node position, HorseState state, List<Horse> groupedHorses) {
            this.position = position;
            this.state = state;
            this.groupedHorses = new ArrayList<>(groupedHorses);
        }
    }
}
