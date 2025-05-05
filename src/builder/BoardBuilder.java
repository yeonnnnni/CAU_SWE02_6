package builder;

import model.Node;
import java.awt.Point;
import java.util.List;
import java.util.Map;

public interface BoardBuilder {
    java.util.List<Node> buildBoard();
    Map<String, Point> getNodePositions();
}
