package view.JavaFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Horse;
import model.Node;
import model.Team;
import model.YutResult;

import java.util.*;

public class MainFrameFX extends BorderPane implements GameUIFX {
    private final BoardPanelFX boardPanelFX;
    private final DicePanelFX dicePanelFX;
    private final ScoreboardPanelFX scoreboardPanelFX;
    private final Label currentPlayerLabel;

    public MainFrameFX() {
        scoreboardPanelFX = new ScoreboardPanelFX();
        setRight(scoreboardPanelFX);

        dicePanelFX = new DicePanelFX();
        setTop(dicePanelFX);

        boardPanelFX = new BoardPanelFX();
        setCenter(boardPanelFX);

        currentPlayerLabel = new Label("현재 플레이어: ");
        currentPlayerLabel.setFont(Font.font("Arial", 16));
        currentPlayerLabel.setPadding(new Insets(10));
        setBottom(currentPlayerLabel);

        setPadding(new Insets(10));
    }

    public ScoreboardPanelFX getScoreboardPanel() { return scoreboardPanelFX;}


    public view.JavaFX.BoardPanelFX getBoardPanel() {
        return boardPanelFX;
    }

    public DicePanelFX getDicePanel() {
        return dicePanelFX;
    }

    @Override
    public boolean isRandomMode() {
        return dicePanelFX.isRandomMode();
    }

    @Override
    public void showDiceResult(List<YutResult> results) {
        dicePanelFX.showResult(results);
    }

    @Override
    public void setRollListener(Runnable listener) {
        dicePanelFX.addRollListener(listener);
    }

    @Override
    public void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public String getManualInput() {
        return dicePanelFX.getManualInput();
    }

    @Override
    public boolean confirmShortcut(String direction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("지름길 선택");
        alert.setHeaderText(direction + " 방향의 지름길로 진입하시겠습니까?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    public Horse selectHorse(List<Horse> candidates, int steps) {
        ChoiceDialog<Horse> dialog = new ChoiceDialog<>(candidates.get(0), candidates);
        dialog.setTitle("말 선택");
        dialog.setHeaderText(steps + "칸 이동할 말을 선택하세요.");
        Optional<Horse> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @Override
    public YutResult chooseYutResult(List<YutResult> options) {
        ChoiceDialog<YutResult> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("윷 선택");
        dialog.setHeaderText("사용할 윷 결과를 선택하세요.");
        Optional<YutResult> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @Override
    public boolean promptRestart(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(winnerName + " 팀이 승리했습니다! 게임을 다시 시작하시겠습니까?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    @Override
    public void setCurrentPlayer(String name) {
        currentPlayerLabel.setText("현재 플레이어: " + name);
    }

    @Override
    public void updatePiece(Node from, Node to) {
        boardPanelFX.updatePiecePosition(from, to);
    }

    @Override
    public void setDiceRollEnabled(boolean enabled) {
        dicePanelFX.setRollEnabled(enabled);
    }

    public List<YutResult> promptYutOrder(List<YutResult> results) {
        // ListView로 사용자에게 결과 순서 재정렬 받기
        ListView<YutResult> listView = new ListView<>();
        listView.getItems().addAll(results);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setPrefHeight(150);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("윷 결과 순서 선택");
        alert.setHeaderText("사용할 윷 결과의 순서를 위에서 아래 순서대로 선택하세요.");
        alert.getDialogPane().setContent(listView);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<YutResult> selected = listView.getSelectionModel().getSelectedItems();
            if (selected.size() != results.size()) {
                showMessage("모든 결과를 선택해야 합니다. 기본 순서로 진행합니다.");
                return results;
            }
            return new ArrayList<>(selected); // 선택된 순서 반환
        }

        return results; // 취소 시 기본 순서
    }


}