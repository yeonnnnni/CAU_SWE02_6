package launcher;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameController;
import controller.GameManager;
import model.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwingStarter {
    public static void main(String[] args) {
        String mode = System.getProperty("ui.mode", "swing");

        if (mode.equalsIgnoreCase("javafx")) {
            System.out.println("JavaFX!");
//            여기서 Javafx 실행
//            javafx.application.Application.launch(view.javafx.JavaFXMain.class, args);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // 1. 사용자 설정 입력 받기
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
            MainFrame view = new MainFrame(nodeList, builder.getNodePositions(), boardType);
            ShortcutDecisionProvider provider = direction -> view.promptShortcutChoice(direction);
            DiceManager diceManager = new DiceManager();
            GameManager gameManager = new GameManager(view, board, diceManager, teams, boardType, provider);
            new GameController(diceManager, gameManager, view);

            gameManager.startGame();
        });
    }

    private static int promptPieceCount() {
        try {
            String input = JOptionPane.showInputDialog(null, "말 개수 (2~5):", "설정", JOptionPane.QUESTION_MESSAGE);
            int value = Integer.parseInt(input);
            return (value < 2 || value > 5) ? 2 : value;
        } catch (Exception e) {
            return 2;
        }
    }

    private static int promptPlayerCount() {
        try {
            String input = JOptionPane.showInputDialog(null, "플레이어 수 (2~5):", "설정", JOptionPane.QUESTION_MESSAGE);
            int value = Integer.parseInt(input);
            return (value < 2 || value > 5) ? 2 : value;
        } catch (Exception e) {
            return 2;
        }
    }

    private static String promptBoardType() {
        String[] types = {"square", "pentagon", "hexagon"};
        String selected = (String) JOptionPane.showInputDialog(
                null,
                "보드 유형을 선택하세요:",
                "판 설정",
                JOptionPane.PLAIN_MESSAGE,
                null,
                types,
                types[0]
        );
        return selected != null ? selected : "square";
    }

    private static List<Team> createTeams(int playerCount, int pieceCount, String boardType) {
        List<Color> colors = List.of(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK);
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            char name = (char)('A' + i);
            teams.add(new Team(i, String.valueOf(name), colors.get(i), pieceCount, boardType));
        }
        return teams;
    }


}
