public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new view.MainFrame().setVisible(true);
        });
    }
}