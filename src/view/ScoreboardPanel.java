// view/ScoreboardPanel.java
package view;

import model.Horse;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScoreboardPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;

    public ScoreboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("점수판"));

        String[] columnNames = {"팀 이름", "대기 중", "이동 중", "완주함"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setEnabled(false); // 사용자 편집 막기

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateScoreboard(List<Team> teams) {
        model.setRowCount(0); // 기존 데이터 초기화

        for (Team team : teams) {
            int waiting = 0, moving = 0, finished = 0;
            for (Horse h : team.getHorses()) {
                switch (h.getState()) {
                    case WAITING -> waiting++;
                    case MOVING -> moving++;
                    case FINISHED -> finished++;
                }
            }

            Object[] row = {team.getName(), waiting, moving, finished};
            model.addRow(row);
        }
    }
}
