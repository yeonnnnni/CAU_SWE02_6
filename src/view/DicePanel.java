package view;

import model.YutResult;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
윷 던지기 관련 UI 컴포넌트
랜덤 모드와 수동 입력 모드를 모두 지원합니다.
*/
public class DicePanel extends JPanel {

    private final JLabel resultLabel;
    private final JButton rollButton;
    private final JTextField manualInputField;
    private final JRadioButton randomMode;
    private final JRadioButton manualMode;
    private final Queue<YutResult> lastResults = new LinkedList<>();

    public DicePanel() {
        setLayout(new GridLayout(4, 1));

        resultLabel = new JLabel("결과: -");
        add(resultLabel);

        randomMode = new JRadioButton("랜덤", true);
        manualMode = new JRadioButton("지정");
        ButtonGroup group = new ButtonGroup();
        group.add(randomMode);
        group.add(manualMode);
        JPanel modePanel = new JPanel();
        modePanel.add(randomMode);
        modePanel.add(manualMode);
        add(modePanel);

        manualInputField = new JTextField(5);
        ((AbstractDocument) manualInputField.getDocument()).setDocumentFilter(new NumericFilter());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("입력 (-1~5):"));
        inputPanel.add(manualInputField);
        add(inputPanel);

        rollButton = new JButton("윷 던지기");
        add(rollButton);
    }

    //버튼 클릭 리스너 등록
    public void addRollListener(ActionListener listener) {
        rollButton.addActionListener(listener);
    }

    //윷 결과 표시
    public void showResult(List<YutResult> results) {
        lastResults.clear();
        lastResults.addAll(results);
        StringBuilder sb = new StringBuilder("결과: ");
        for (YutResult r : results) {
            sb.append(r.name()).append(" ");
        }
        resultLabel.setText(sb.toString().trim());
    }

    public boolean isRandomMode() {
        return randomMode.isSelected();
    }

    public String getManualInputText() {
        return manualInputField.getText();
    }

    public Queue<YutResult> getResultQueue() {
        return new LinkedList<>(lastResults);
    }

    private static class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string.matches("-?\\d*")) super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("-?\\d*")) super.replace(fb, offset, length, text, attrs);
        }
    }
}
