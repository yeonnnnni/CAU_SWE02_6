package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import controller.Board;
import controller.GameController;

public class MainFrame extends JFrame {
    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JLabel turnLabel;
    private GameController controller;
    private List<String> players;

    public MainFrame() {
        this(List.of("Player1","Player2"));
    }

    public MainFrame(List<String> players) {
        this.players = players;
        initUI();
    }

    private void initUI() {
        setTitle("Java Swing 윷놀이 게임");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controller = new GameController(this, players);
        boardPanel = new BoardPanel(controller.getBoard());
        dicePanel = new DicePanel(controller, boardPanel);

        turnLabel = new JLabel("현재 턴: " + controller.getCurrentPlayer(), SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        add(turnLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(dicePanel, BorderLayout.SOUTH);

        pack(); setLocationRelativeTo(null); setVisible(true);
    }

    public void setBoard(Board board) { boardPanel.setBoard(board); }
    public void resetController(GameController newController) {
        this.controller = newController;
        dicePanel.setController(newController);
    }
    public void updateUIComponents() {
        boardPanel.refresh();
        turnLabel.setText("현재 턴: " + controller.getCurrentPlayer());
    }
    public void updateTurnLabel() {
        turnLabel.setText("현재 턴: " + controller.getCurrentPlayer());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
