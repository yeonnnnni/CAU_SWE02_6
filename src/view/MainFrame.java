package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        GameManager gameManager = new GameManager();
        DicePanel dicePanel = new DicePanel(gameManager);
        BoardPanel boardPanel = new BoardPanel();

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(dicePanel, BorderLayout.SOUTH);

        setTitle("Yutnori Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gameManager.startGame();
    }
}