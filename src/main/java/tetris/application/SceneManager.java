package tetris.application;

import tetris.screens.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void showMainMenu(Stage stage) {
        MainMenuScreen mainMenu = new MainMenuScreen(stage);
        Scene scene = new Scene(mainMenu.getView(), 400, 600);
        setScene(stage, scene);
    }

    public static void showConfigurationScreen(Stage stage) {
        ConfigurationScreen configScreen = new ConfigurationScreen(stage);
        Scene scene = new Scene(configScreen.getView(), 400, 600);
        setScene(stage, scene);
    }
    
    public static void showHighScoreScreen(Stage stage) {
        HighScoreScreen highScoreScreen = new HighScoreScreen(stage);
        Scene scene = new Scene(highScoreScreen.getView(), 400, 600);
        setScene(stage, scene);
    }

    public static void showGameScreen(Stage stage) {
        GameScreen gameScreen = new GameScreen(stage);
        Scene scene = new Scene(gameScreen.getView(), 400, 600);
        setScene(stage, scene);
    }

    private static void setScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}