package view;

import controller.GameManager;
import model.YutResult;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DicePanel extends JPanel {
    private JButton rollButton;
    private JLabel resultLabel;

    public DicePanel(GameManager gameManager) {
        rollButton = new JButton("Roll Yut");
        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);

        rollButton.addActionListener((ActionEvent e) -> {
            YutResult result = gameManager.rollYut();
            resultLabel.setText("Result: " + result.name());
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(rollButton);
        add(resultLabel);
    }
}