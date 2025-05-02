package view;

import javax.swing.*;
import java.awt.*;

public class DicePanel extends JPanel {

    private JLabel resultLabel;
    private JButton rollButton;
    private JTextField manualInputField;
    private JRadioButton randomMode;
    private JRadioButton manualMode;
    private ButtonGroup modeGroup;

    public DicePanel() {
        setLayout(new GridLayout(4, 1));

        // 결과 출력 라벨
        resultLabel = new JLabel("결과: -");

        // 랜덤/수동 모드 라디오 버튼
        randomMode = new JRadioButton("랜덤", true);
        manualMode = new JRadioButton("수동");
        modeGroup = new ButtonGroup();
        modeGroup.add(randomMode);
        modeGroup.add(manualMode);

        JPanel modePanel = new JPanel();
        modePanel.add(randomMode);
        modePanel.add(manualMode);

        // 수동 입력 필드
        manualInputField = new JTextField(5);
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("수동 입력 (-1~5):"));
        inputPanel.add(manualInputField);

        // 굴리기 버튼
        rollButton = new JButton("주사위 굴리기");

        // 패널 배치
        add(resultLabel);
        add(modePanel);
        add(inputPanel);
        add(rollButton);
    }

    // --- Getter 메서드 ---

    public boolean isRandomMode() {
        return randomMode.isSelected();
    }

    public String getManualInputText() {
        return manualInputField.getText();
    }

    public JButton getRollButton() {
        return rollButton;
    }

    public void setResultText(String text) {
        resultLabel.setText("결과: " + text);
    }
}
