package tetris.screens;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import tetris.application.SceneManager;

public class HighScoreScreen {

    private VBox view;

    public HighScoreScreen(Stage stage){

        view = new VBox(20);
        view.setAlignment(Pos.CENTER);

        Label title = new Label("High Scores");

        Label scores = new Label(
                "1. Player - 1000\n" +
                        "2. Player - 800\n" +
                        "3. Player - 600\n" +
                        "4. Player - 500\n" +
                        "5. Player - 400"
        );

        Button back = new Button("Back");

        back.setOnAction(e ->
                SceneManager.showMainMenu(stage)
        );

        view.getChildren().addAll(title,scores,back);
    }

    public VBox getView(){
        return view;
    }
}