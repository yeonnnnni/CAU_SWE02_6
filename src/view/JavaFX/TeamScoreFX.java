package view.JavaFX;
import javafx.beans.property.*;

public class TeamScoreFX {
    private final StringProperty teamName = new SimpleStringProperty();
    private final IntegerProperty waiting = new SimpleIntegerProperty();
    private final IntegerProperty moving = new SimpleIntegerProperty();
    private final IntegerProperty finished = new SimpleIntegerProperty();

    public TeamScoreFX(String name, int w, int m, int f) {
        teamName.set(name);
        waiting.set(w);
        moving.set(m);
        finished.set(f);
    }

    public StringProperty teamNameProperty() { return teamName; }
    public IntegerProperty waitingProperty() { return waiting; }
    public IntegerProperty movingProperty() { return moving; }
    public IntegerProperty finishedProperty() { return finished; }
}
