package view.JavaFX;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Horse;
import model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardPanelFX extends Pane {
    private final Map<Node, Button> nodeToButton = new HashMap<>();
    private final int buttonSize = 50;
    private String boardType = "square";
    private final Map<String, Image> horseIcons = new HashMap<>();

    public BoardPanelFX() {
        setPrefSize(800, 800);
        loadHorseIcons();
    }

    private void loadHorseIcons() {
        String[] colors = {"blue", "green", "red", "yellow", "pink"};
        for (String color : colors) {
            for (int i = 0; i <= 4; i++) {
                String key = color + "_h" + i;
                try {
                    Image img = new Image(getClass().getResourceAsStream("/horses/" + key + ".png"), 30, 30, true, true);
                    horseIcons.put(key, img);
                } catch (Exception e) {
                    System.err.println("❌ 아이콘 로딩 실패: " + key);
                }
            }
        }
    }

    public void renderBoard(List<Node> nodes, Map<String, Point2D> nodePositions) {
        getChildren().clear();
        nodeToButton.clear();

        for (Node node : nodes) {
            Point2D pt = nodePositions.get(node.getId());
            if (pt == null) continue;

            Button btn = new Button();
            btn.setPrefSize(buttonSize, buttonSize);
            btn.setLayoutX(pt.getX());
            btn.setLayoutY(pt.getY());
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: gray;");

            getChildren().add(btn);
            nodeToButton.put(node, btn);
        }
    }

    public void updatePiecePosition(Node from, Node to) {
        if (from != null && nodeToButton.containsKey(from)) {
            nodeToButton.get(from).setGraphic(null);
        }

        if (to != null && nodeToButton.containsKey(to)) {
            Button btn = nodeToButton.get(to);
            List<Horse> horses = to.getHorsesOnNode();

            GridPane horsePanel = new GridPane();
            horsePanel.setPrefSize(buttonSize, buttonSize);
            horsePanel.setHgap(2);
            horsePanel.setVgap(2);
            int row = 0, col = 0;

            for (Horse h : horses) {
                if (h.isFinished()) continue;
                int horseIdx = Integer.parseInt(h.getId().split("-H")[1]);
                String colorKey = getColorKey(h.getTeamColor());
                String iconKey = colorKey + "_h" + horseIdx;
                Image icon = horseIcons.get(iconKey);

                if (icon != null) {
                    ImageView iv = new ImageView(icon);
                    iv.setFitWidth(24);
                    iv.setFitHeight(24);
                    horsePanel.add(iv, col, row);

                    col++;
                    if (col > 1) {
                        col = 0;
                        row++;
                    }
                }
            }

            btn.setGraphic(horsePanel);
        }
    }

    public void resetBoardUI() {
        for (Button btn : nodeToButton.values()) {
            btn.setGraphic(null);
        }
    }

    private String getColorKey(java.awt.Color color) {
        if (java.awt.Color.BLUE.equals(color)) return "blue";
        if (java.awt.Color.RED.equals(color)) return "red";
        if (java.awt.Color.GREEN.equals(color)) return "green";
        if (java.awt.Color.YELLOW.equals(color)) return "yellow";
        if (java.awt.Color.PINK.equals(color)) return "pink";
        return "unknown";
    }

    public Map<Node, Button> getNodeToButtonMap() {
        return nodeToButton;
    }
}
