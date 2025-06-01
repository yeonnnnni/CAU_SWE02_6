package model;

import builder.BoardFactory;
import model.*;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.awt.Color;
import java.util.*;

/**
 *
 |   |   |
 |---|---|
 |groupWith(Horse other)|말끼리 그룹핑 수행|
 |getGroupedHorses()|그룹된 말 리스트 반환|

 */
@Getter
@Setter
@AllArgsConstructor
public class HorseGroupManager{
    private final Map<Horse, List<Horse>> groupedHorsesMap = new HashMap<>();

    public List<Horse> getGroupedHorses(Horse horse);
    public void addGroupedHorse(Horse horse, Horse toAdd);
    public void removeGroupedHorse(Horse horse, Horse toRemove);
    public void resetGroupedHorses(Horse horse);
}

