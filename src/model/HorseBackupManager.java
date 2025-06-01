package model;

import builder.BoardFactory;
import model.*;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.awt.Color;
import java.util.*;

/**
 * 롤백 및 복원
 *
 *    |   |   |
 *    |---|---|
 *    |backupState()|현재 상태 백업|
 *    |rollback()|백업 상태로 복원|
 */


@Getter
@Setter
@AllArgsConstructor
public class HorseBackupManager{
    private final Map<Horse, HorseBackup> horseBackupMap = new HashMap<>();

    public void backup(Horse horse);
    public void restore(Horse horse);
}
