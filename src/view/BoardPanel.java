package view;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    public BoardPanel() {
        setLayout(new GridLayout(5, 5));
        for (int i = 0; i < 25; i++) {
            add(new JLabel("", SwingConstants.CENTER));
        }
    }
}