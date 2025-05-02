// view/DicePanel.java — 전체 업데이트 버전
package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Horse;
import model.YutResult;
import controller.GameController;

public class DicePanel extends JPanel {
    private GameController controller;  // final 제거하여 재할당 가능
    private BoardPanel boardPanel;
    private JLabel resultLabel;
    private boolean extraUsed = false;  // 이번 턴에서 추가 던지기 이미 사용했는지

    public DicePanel(GameController controller, BoardPanel boardPanel) {
        this.controller = controller;
        this.boardPanel = boardPanel;
        setLayout(new BorderLayout());

        // 랜덤 윷 던지기 버튼
        JButton throwButton = new JButton("윷 던지기");
        throwButton.addActionListener(e -> handleThrow(controller.throwDice()));

        // 지정 윷 결과 버튼
        JButton testButton = new JButton("지정 윷");
        testButton.addActionListener(e -> {
            String[] opts = {"빽도","도","개","걸","윷","모"};
            String choice = (String) JOptionPane.showInputDialog(
                this,
                "결과 선택:",
                "테스트용 윷 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opts,
                opts[0]
            );
            if (choice != null) {
                handleThrow(controller.chooseResult(choice));
            }
        });

        resultLabel = new JLabel("결과: -", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel top = new JPanel();
        top.add(throwButton);
        top.add(testButton);
        add(top, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
    }

    private void handleThrow(YutResult result) {
        // 1) 결과 표시
        resultLabel.setText("결과: " + result.getDisplayName());

        // 2) 말 선택
        String player = controller.getCurrentPlayer();
        List<Horse> horses = controller.getPlayerHorses(player);
        Horse selected = (Horse) JOptionPane.showInputDialog(
            this,
            player + "님, 이동할 말을 선택하세요:",
            "말 선택",
            JOptionPane.PLAIN_MESSAGE,
            null,
            horses.toArray(),
            horses.get(0)
        );
        if (selected == null) {
            nextTurn();
            return;
        }

        // 3) 말 이동
        boolean captured = controller.moveHorse(selected, result);
        boardPanel.refresh();

        // 4) 추가 던지기 판단
        if (!extraUsed && (result == YutResult.YUT || result == YutResult.MO)) {
            extraUsed = true;
            JOptionPane.showMessageDialog(this, 
                "윷/모 추가 던지기 기회! (" + result.getDisplayName() + ")");
        }
        else if (!extraUsed && captured) {
            extraUsed = true;
            JOptionPane.showMessageDialog(this, 
                "상대 말 잡음! 추가 던지기 기회!");
        }
        else {
            nextTurn();
        }
    }

    private void nextTurn() {
        controller.nextPlayer();
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame mf) mf.updateTurnLabel();
        extraUsed = false;
    }

    /**
     * 재시작 시 새로운 컨트롤러 할당을 지원
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }
}