package model;

public class Horse {
    private Node currentPosition;
    private boolean finished = false;

    public Horse(Node startPosition) {
        this.currentPosition = startPosition;
    }

    public Node getCurrentPosition() {
        return currentPosition;
    }

    public void moveTo(Node newPosition) {
        this.currentPosition = newPosition;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}