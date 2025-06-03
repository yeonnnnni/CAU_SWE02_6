package view.JavaFX;

import model.YutResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

class DicePanelFX extends VBox {
    private final Label resultLabel;
    private final Button rollButton;
    private final TextField manualInput;
    private final RadioButton randomMode;
    private final RadioButton manualMode;

    public DicePanelFX() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        resultLabel = new Label("결과: -");

        rollButton = new Button("윷 던지기");

        manualInput = new TextField();
        manualInput.setPromptText("숫자 입력 (-1~5)");
        manualInput.setMaxWidth(80);

        randomMode = new RadioButton("랜덤");
        manualMode = new RadioButton("수동");
        ToggleGroup group = new ToggleGroup();
        randomMode.setToggleGroup(group);
        manualMode.setToggleGroup(group);
        randomMode.setSelected(true);

        HBox modeRow = new HBox(10, randomMode, manualMode);
        modeRow.setAlignment(Pos.CENTER);
        HBox inputRow = new HBox(10, new Label("입력:"), manualInput);
        inputRow.setAlignment(Pos.CENTER);

        getChildren().addAll(resultLabel, rollButton, modeRow, inputRow);
    }

    public void showResult(List<YutResult> results) {
        StringBuilder sb = new StringBuilder("결과: ");
        for (YutResult y : results) {
            sb.append(y.name()).append(" ");
        }
        resultLabel.setText(sb.toString().trim());
    }

    public boolean isRandomMode() {
        return randomMode.isSelected();
    }

    public String getManualInput() {
        return manualInput.getText();
    }

    public void addRollListener(Runnable listener) {
        rollButton.setOnAction(e -> listener.run());
    }

    public void setRollEnabled(boolean enabled) {
        rollButton.setDisable(!enabled);
    }
}