package view.Swing;

import model.YutResult;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 윷 던지기 관련 UI 컴포넌트
 * - 랜덤 모드와 수동 입력 모드 모두 지원
 * - 결과 출력, 윷 던지기 버튼 포함
 */
public class DicePanel extends JPanel {

    private final JLabel resultLabel;           // 결과 출력 라벨
    private final JButton rollButton;           // 윷 던지기 버튼
    private final JTextField manualInputField;  // 수동 입력 필드
    private final JRadioButton randomMode;      // 랜덤 모드 선택 버튼
    private final JRadioButton manualMode;      // 수동 모드 선택 버튼
    private final Queue<YutResult> lastResults = new LinkedList<>();     // 마지막 윷 결과 저장

    /**
     * DicePanel 생성자
     * - 레이아웃 구성 및 UI 컴포넌트 초기화
     */
    public DicePanel() {
        setLayout(new GridLayout(4, 1));    // 4행 1열 그리드 레이아웃

        // 윷 결과 출력 라벨
        resultLabel = new JLabel("결과: -");
        add(resultLabel);

        // 라디오 버튼 그룹 설정 (랜덤 / 수동)
        randomMode = new JRadioButton("랜덤", true);      // 기본 선택
        manualMode = new JRadioButton("지정");
        ButtonGroup group = new ButtonGroup();      // 두 라디오 버튼을 그룹화
        group.add(randomMode);
        group.add(manualMode);
        JPanel modePanel = new JPanel();        // 라디오 버튼을 담는 패널
        modePanel.add(randomMode);
        modePanel.add(manualMode);
        add(modePanel);

        // 수동 입력 필드 설정 (-1 ~ 5 입력 가능)
        manualInputField = new JTextField(5);
        ((AbstractDocument) manualInputField.getDocument()).setDocumentFilter(new NumericFilter());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("입력 (-1~5):"));
        inputPanel.add(manualInputField);
        add(inputPanel);

        // 윷 던지기 버튼 추가
        rollButton = new JButton("윷 던지기");
        add(rollButton);
    }

    /**
     * 윷 던지기 버튼 클릭 이벤트 등록
     * @param listener 버튼 클릭 시 실행될 리스너
     */
    public void addRollListener(ActionListener listener) {
        rollButton.addActionListener(listener);
    }

    /**
     * 윷 결과를 화면에 표시
     * @param results YutResult 리스트 (도, 개, 걸 등)
     */
    public void showResult(List<YutResult> results) {
        lastResults.clear();
        lastResults.addAll(results);        // 결과 저장

        // 결과 문자열 생성
        StringBuilder sb = new StringBuilder("결과: ");
        for (YutResult r : results) {
            sb.append(r.name()).append(" ");
        }
        resultLabel.setText(sb.toString().trim());  // 결과 출력
    }

    /**
     * 현재 랜덤 모드인지 확인
     * @return true → 랜덤 모드, false → 수동 모드
     */
    public boolean isRandomMode() {
        return randomMode.isSelected();
    }

    /**
     * 수동 입력 필드의 텍스트 반환
     * @return 사용자 입력 문자열
     */
    public String getManualInputText() {
        return manualInputField.getText();
    }

    /**
     * 수동 입력 필드에 숫자만 입력되도록 제한하는 필터 클래스
     * - 음수 기호(-)와 숫자만 허용
     */
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
