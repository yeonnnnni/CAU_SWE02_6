// view/ScoreboardPanel.java
package view.Swing;

import model.Horse;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 점수판 패널
 * - 각 팀의 말 상태(대기, 이동, 완주)를 표 형식으로 보여주는 컴포넌트
 */
public class ScoreboardPanel extends JPanel {

    private final JTable table;                  // 말 상태를 보여줄 표 컴포넌트
    private final DefaultTableModel model;      // 표 데이터 모델

    /**
     * 생성자: 점수판 UI 초기화
     */
    public ScoreboardPanel() {
        setLayout(new BorderLayout());  // 테이블을 중앙에 배치
        setBorder(BorderFactory.createTitledBorder("점수판"));     // 제목 테두리

        // 열 제목 정의: 팀 이름, 대기 중, 이동 중, 완주함
        String[] columnNames = {"팀 이름", "대기 중", "이동 중", "완주함"};
        model = new DefaultTableModel(columnNames, 0);  // 빈 테이블 모델 생성
        table = new JTable(model);                               // 테이블 생성
        table.setEnabled(false);                                 // 사용자 편집 불가능하도록 설정

        // 스크롤 가능하도록 JScrollPane에 테이블 추가
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 점수판 UI 업데이트
     * - 각 팀의 말 상태(WAITING, MOVING, FINISHED)를 세어서 표에 반영
     * @param teams 현재 게임에 참여 중인 팀 리스트
     */
    public void updateScoreboard(List<Team> teams) {
        model.setRowCount(0); // 기존 데이터 초기화

        for (Team team : teams) {
            int waiting = 0, moving = 0, finished = 0;  // 상태별 말 수 초기화
            // 각 팀의 말 상태 집계
            for (Horse h : team.getHorses()) {
                switch (h.getState()) {
                    case WAITING -> waiting++;
                    case MOVING -> moving++;
                    case FINISHED -> finished++;
                }
            }

            // 팀 이름과 상태별 개수를 테이블 행으로 추가
            Object[] row = {team.getName(), waiting, moving, finished};
            model.addRow(row);
        }
    }
}
