// view/DicePanel.java — 전체 업데이트 버전
package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DicePanel extends JPanel {
    private JButton rollButton = new JButton("윷 던지기");

    public DicePanel() {
        setLayout(new FlowLayout());
        add(rollButton);
    }

    // Controller에서 버튼에 리스너 달 수 있도록 메서드 제공
    public void addRollListener(ActionListener listener) {
        rollButton.addActionListener(listener);
    }
}