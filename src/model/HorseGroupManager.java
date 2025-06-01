package model;

import java.util.*;

public class HorseGroupManager {
    private final Map<Horse, List<Horse>> groupedMap = new HashMap<>();

    public void groupWith(Horse base, Horse other) {
        groupedMap.computeIfAbsent(base, k -> new ArrayList<>()).add(other);
        groupedMap.computeIfAbsent(other, k -> new ArrayList<>()).add(base);
        other.setPosition(base.getPosition());
    }

    public boolean isGroupable(Horse a, Horse b) {
        return a.getTeam() == b.getTeam() && a != b && a.getPosition() == b.getPosition();
    }

    public List<Horse> getGroupedHorses(Horse horse) {
        return groupedMap.getOrDefault(horse, new ArrayList<>());
    }

    public void resetGroupedHorses(Horse horse) {
        groupedMap.remove(horse);
    }
}
