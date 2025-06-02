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

public class ScoreboardPanelFX extends VBox {
    private final TableView<TeamScoreFX> table;

    public ScoreboardPanelFX() {
        setPadding(new Insets(10));
        setSpacing(10);
        setPrefWidth(275);

        Label title = new Label("점수판");
        title.setFont(Font.font("Arial", 18));

        table = new TableView<>();
        TableColumn<TeamScoreFX, String> teamCol = new TableColumn<>("팀 이름");
        TableColumn<TeamScoreFX, Integer> waitCol = new TableColumn<>("대기 중");
        TableColumn<TeamScoreFX, Integer> moveCol = new TableColumn<>("이동 중");
        TableColumn<TeamScoreFX, Integer> finishCol = new TableColumn<>("완주함");

        teamCol.setCellValueFactory(param -> param.getValue().teamNameProperty());
        waitCol.setCellValueFactory(param -> param.getValue().waitingProperty().asObject());
        moveCol.setCellValueFactory(param -> param.getValue().movingProperty().asObject());
        finishCol.setCellValueFactory(param -> param.getValue().finishedProperty().asObject());

        table.getColumns().addAll(teamCol, waitCol, moveCol, finishCol);
        table.setPrefHeight(300);

        getChildren().addAll(title, table);
    }

    public void updateScoreboard(List<Team> teams) {
        table.getItems().clear();
        for (Team team : teams) {
            int wait = 0, move = 0, done = 0;
            for (Horse h : team.getHorses()) {
                switch (h.getState()) {
                    case WAITING -> wait++;
                    case MOVING -> move++;
                    case FINISHED -> done++;
                }
            }
            table.getItems().add(new TeamScoreFX(team.getName(), wait, move, done));
        }
    }
}