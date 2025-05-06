package view;

import builder.BoardBuilder;
import builder.BoardFactory;
import controller.Board;
import controller.GameManager;
import model.Node;
import model.Horse;
import model.DiceManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private Node[] nodes;
    private GameManager gameManager;
    private Board board;
    private DiceManager diceManager;

    public MainFrame() {
        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. BoardBuilder 선택(square/pentagon/hexagon)
        BoardBuilder builder=BoardFactory.create("square");
        List<Node> nodes=builder.buildBoard();

        // 2. 보드 초기화
        boardPanel = new BoardPanel();
        boardPanel.renderBoard(nodes, builder.getNodePositions());
        add(boardPanel, BorderLayout.CENTER);

        // 3. DIcePanel UI 설정
        dicePanel=new DicePanel();
        add(dicePanel, BorderLayout.NORTH);

        //4. 게임 로직 클래스 초기화
        List<String> players=List.of("0","1");
        board=new Board();
        for(String player : players){
            board.registerPlayer(player);
        }

        // GameManager 연결
        gameManager = new GameManager(this, board, diceManager, players);

        // DiceManager 연결
        diceManager=new DiceManager();

        // 나중에 DicePanel 추가
        // dicePanel = new DicePanel();
        // add(dicePanel, BorderLayout.NORTH);

        // 게임 시작
        gameManager.startGame();

        setVisible(true);
    }

    // === 💡 말 선택 유도 메서드 추가 ===
    public void promptHorseSelection(List<Horse> horses, int steps) {
        if (horses.isEmpty()) return;

        Horse selected = (Horse) JOptionPane.showInputDialog(
            this,
            "이동할 말을 선택하세요 (" + steps + "칸 이동)",
            "말 선택",
            JOptionPane.PLAIN_MESSAGE,
            null,
            horses.toArray(),
            horses.get(0)
        );

        if (selected != null) {
            //추후 getPosition 연동 추가 예정
            Node from = selected.getPosition();         // 이동 전 위치 저장
            selected.move(steps);                       // 이동
            Node to = selected.getPosition();           // 이동 후 위치
            Color teamColor = selected.getTeamColor();  // 팀 색상 한 번만 호출

            boardPanel.updatePiecePosition(from, to, selected.getId(), teamColor);
            //boardPanel.repaint();         // 보드 화면 갱신
        }
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public DicePanel getDicePanel(){
        return dicePanel;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

