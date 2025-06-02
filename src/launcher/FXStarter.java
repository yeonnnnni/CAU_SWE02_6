package launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.JavaFX.MainFrameFX;

public class FXStarter extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MainFrameFX root = new MainFrameFX(); // JavaFX용 루트 UI
            Scene scene = new Scene(root, 1000, 800);
            primaryStage.setTitle("윷놀이 게임 (JavaFX)");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // JavaFX Application 실행
    }
}
