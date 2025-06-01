package model;

import java.util.*;

public class HorseBackupManager {
    private final Map<Horse, HorseBackup> horseBackupMap = new HashMap<>();

    public void backupState(Horse horse) {
        HorseBackup backup = new HorseBackup(
                horse.getPosition(),
                horse.getState(),
                List.copyOf(horse.getGroupedHorses())
        );
        horseBackupMap.put(horse, backup);
    }

    public void rollback(Horse horse) {
        HorseBackup backup = horseBackupMap.get(horse);
        if (backup != null) {
            horse.setPosition(backup.getPosition());
            horse.setState(backup.getState());
            horse.getGroupedHorses().clear();
            horse.getGroupedHorses().addAll(backup.getGroupedHorses());
        } else {
            throw new IllegalStateException("백업 정보가 없습니다: " + horse.getId());
        }
    }
}
