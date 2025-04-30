package view;

import javax.swing.*;
import java.awt.*;

public class DicePanel extends JPanel {
    private JLabel resultLabel;
    private JButton randomButton;
    private JButton manualButton;

    public DicePanel() {
        setLayout(new FlowLayout());

        resultLabel = new JLabel("결과: -");
        randomButton = new JButton("랜덤 윷 던지기");
        manualButton = new JButton("지정 윷 결과 입력");

        add(resultLabel);
        add(randomButton);
        add(manualButton);

        // 이벤트 연결은 나중에 controller에서 할 예정
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public JButton getRandomButton() {
        return randomButton;
    }

    public JButton getManualButton() {
        return manualButton;
    }
}

