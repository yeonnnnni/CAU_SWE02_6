package model;
import java.util.ArrayList;
import java.util.List;

public class HorseBackup {
    public final Node position;
    public final HorseState state;
    public final List<Horse> groupedHorses;

    public HorseBackup(Node position, HorseState state, List<Horse> groupedHorses) {
        this.position = position;
        this.state = state;
        this.groupedHorses = new ArrayList<>(groupedHorses); // 깊은복사할것!!!
    }
    public Node getPosition() {
        return position;
    }

    public HorseState getState() {
        return state;
    }

    public List<Horse> getGroupedHorses() {
        return groupedHorses;
    }
}