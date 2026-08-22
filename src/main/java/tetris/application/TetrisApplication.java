package tetris.application;

import tetris.screens.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TetrisApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        
        stage.initStyle(StageStyle.UNDECORATED);
        SplashScreen.showSplash(stage);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        TetrisApplication.primaryStage = stage;
    }
}