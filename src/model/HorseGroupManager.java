package model;

import java.util.*;
import model.*;
import builder.BoardFactory;


public class HorseGroupManager {
    private final Map<Horse, List<Horse>> groupedHorsesMap = new HashMap<>();

    public List<Horse> getGroupedHorses(Horse horse) {
        return groupedHorsesMap.computeIfAbsent(horse, k -> new ArrayList<>());
    }

    public void addGroupedHorse(Horse horse, Horse toAdd) {
        if (!getGroupedHorses(horse).contains(toAdd)) {
            getGroupedHorses(horse).add(toAdd);
        }
        if (!getGroupedHorses(toAdd).contains(horse)) {
            getGroupedHorses(toAdd).add(horse);
        }
    }

    public void removeGroupedHorse(Horse horse, Horse toRemove) {
        getGroupedHorses(horse).remove(toRemove);
        getGroupedHorses(toRemove).remove(horse);
    }

    public void resetGroupedHorses(Horse horse) {
        List<Horse> group = getGroupedHorses(horse);
        for (Horse other : new ArrayList<>(group)) {
            getGroupedHorses(other).remove(horse);
        }
        group.clear();
    }

    public void groupWith(Horse h, Horse other) {
        if (HorseLogicManager.isGroupable(h, other, this)) {
            addGroupedHorse(h, other);
            other.setPosition(h.getPosition());
        }
    }
}