package tetris.screens;

import tetris.application.SceneManager;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {
// showSplash
    public static void showSplash(Stage stage) {
        StackPane root = new StackPane();
        Text titleText = new Text("Tetris Game\nGroup: Tetris_Trio\nCourse: 2006ICT");
        titleText.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        root.getChildren().add(titleText);

        Scene scene = new Scene(root, 300, 300, Color.DARKSLATEBLUE);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            stage.initStyle(StageStyle.DECORATED);
            SceneManager.showMainMenu(stage);
        });
        delay.play();
    }
}