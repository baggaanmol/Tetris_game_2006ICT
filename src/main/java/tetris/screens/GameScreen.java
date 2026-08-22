package tetris.screens;

import tetris.application.SceneManager;
import tetris.game.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameScreen {

    private BorderPane view;
    private GameController gameController;
    private boolean isPaused = false;

    public GameScreen(Stage stage) {
        gameController = new GameController();

        view = new BorderPane();
        view.setCenter(gameController.getView());

        // Game Controls UI
        VBox controlsBox = new VBox(10);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setAlignment(Pos.TOP_RIGHT);

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> togglePause(stage));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            // Simple exit to main menu for Milestone 1
            SceneManager.showMainMenu(stage);
        });

        controlsBox.getChildren().addAll(pauseButton, exitButton);
        view.setTop(controlsBox);
        
        // Keyboard input
        view.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                togglePause(stage);
            } else if (!isPaused) {
                gameController.handleKeyPress(e.getCode());
            }
        });
        
        view.setFocusTraversable(true);

        // Game Loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    gameController.update();
                }
            }
        };
        gameLoop.start();
    }

    private void togglePause(Stage stage) {
        isPaused = !isPaused;
        // Show a pause label
        if (isPaused) {
            Label pauseLabel = new Label("PAUSED");
            pauseLabel.setStyle("-fx-font-size: 48; -fx-text-fill: white;");
            view.setCenter(pauseLabel);
        } else {
            view.setCenter(gameController.getView());
        }
    }

    public BorderPane getView() {
        return view;
    }
}