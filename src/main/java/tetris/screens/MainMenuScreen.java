package tetris.screens;

import tetris.application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MainMenuScreen {

    private VBox view;

    public MainMenuScreen(Stage stage) {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);

        Button playButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button highScoresButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        playButton.setOnAction(e -> SceneManager.showGameScreen(stage));
        configButton.setOnAction(e -> SceneManager.showConfigurationScreen(stage));
        highScoresButton.setOnAction(e -> SceneManager.showHighScoreScreen(stage));
        exitButton.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Exit Game");
            alert.setHeaderText("Are you sure you want to exit?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");

            alert.getButtonTypes().setAll(yes,no);


            alert.showAndWait().ifPresent(response -> {

                if(response == yes){
                    stage.close();
                }

            });

        });

        view.getChildren().addAll(playButton, configButton, highScoresButton, exitButton);
    }

    public VBox getView() {
        return view;
    }
}