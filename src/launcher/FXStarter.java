package launcher;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameController;
import controller.GameManager;
import model.ShortcutDecisionProvider;
import model.*;
import view.JavaFX.MainFrameFX;

import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FXStarter extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. 사용자 입력 받기
        int pieceCount = promptPieceCount();
        int playerCount = promptPlayerCount();
        String boardType = promptBoardType();

        // 2. 보드 빌드
        BoardBuilder builder = BoardFactory.create(boardType);
        List<Node> nodeList = builder.buildBoard();

        // 3. 모델 생성
        Board board = new Board();
        board.setNodes(nodeList);
        List<Team> teams = createTeams(playerCount, pieceCount, boardType);
        for (Team t : teams) board.registerTeam(t);

        // 4. UI 및 게임 매니저 생성
        MainFrameFX view = new MainFrameFX();
        view.getBoardPanel().setBoardType(boardType);
        Map<String, Point2D> convertedPositions = new HashMap<>();
        for (var entry : builder.getNodePositions().entrySet()) {
            java.awt.Point awtPoint = entry.getValue();
            convertedPositions.put(entry.getKey(), new javafx.geometry.Point2D(awtPoint.x, awtPoint.y));
        }
        view.getBoardPanel().renderBoard(nodeList, convertedPositions);

        ShortcutDecisionProvider provider = direction -> view.confirmShortcut(direction);
        DiceManager diceManager = new DiceManager();
        GameManager gameManager = new GameManager(view, board, diceManager, teams, boardType, provider);
        new GameController(diceManager, gameManager, view);
        gameManager.startGame();

        // 5. JavaFX Stage 설정
        Scene scene = new Scene(view, 1000, 800);
        primaryStage.setTitle("윷놀이 게임 (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // JavaFX Application 실행
    }

    private int promptPieceCount() {
        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("설정");
        dialog.setHeaderText("말 개수 (2~5)를 입력하세요:");
        return dialog.showAndWait()
                .map(Integer::parseInt)
                .filter(n -> n >= 2 && n <= 5)
                .orElse(2);
    }

    private int promptPlayerCount() {
        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("설정");
        dialog.setHeaderText("플레이어 수 (2~5)를 입력하세요:");
        return dialog.showAndWait()
                .map(Integer::parseInt)
                .filter(n -> n >= 2 && n <= 5)
                .orElse(2);
    }

    private String promptBoardType() {
        List<String> options = List.of("square", "pentagon", "hexagon");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("square", options);
        dialog.setTitle("보드 타입 선택");
        dialog.setHeaderText("보드 타입을 선택하세요:");
        return dialog.showAndWait().orElse("square");
    }


    private List<Team> createTeams(int playerCount, int pieceCount, String boardType) {
        List<javafx.scene.paint.Color> fxColors = List.of(
                javafx.scene.paint.Color.BLUE,
                javafx.scene.paint.Color.RED,
                javafx.scene.paint.Color.GREEN,
                javafx.scene.paint.Color.YELLOW,
                javafx.scene.paint.Color.PINK
        );

        List<java.awt.Color> awtColors = List.of(
                java.awt.Color.BLUE,
                java.awt.Color.RED,
                java.awt.Color.GREEN,
                java.awt.Color.YELLOW,
                java.awt.Color.PINK
        );

        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            char name = (char)('A' + i);
            teams.add(new Team(i, String.valueOf(name), awtColors.get(i), pieceCount, boardType));
        }
        return teams;
    }
}
