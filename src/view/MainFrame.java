package view;

import model.Node;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("윷놀이 게임");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Node[] nodes = new Node[23]; // 현재 구현된 23칸만
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i);
        }

        BoardPanel boardPanel = new BoardPanel();
        boardPanel.initialize("square", nodes);

        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
