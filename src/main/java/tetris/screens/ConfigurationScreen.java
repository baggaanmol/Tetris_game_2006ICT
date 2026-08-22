package tetris.screens;

import tetris.application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigurationScreen {


    private VBox view;

    public ConfigurationScreen(Stage stage) {
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);

        // Slider with a label
        Label speedLabel = new Label("Speed: 5");
        Slider speedSlider = new Slider(1, 10, 5);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            speedLabel.setText("Speed: " + newVal.intValue())
        );

        CheckBox musicCheck = new CheckBox("Music");
        CheckBox sfxCheck = new CheckBox("Sound Effects");
        CheckBox aiCheck = new CheckBox("AI Play");
        CheckBox extendedCheck = new CheckBox("Extended Mode");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> SceneManager.showMainMenu(stage));

        view.getChildren().addAll(speedLabel, speedSlider, musicCheck, sfxCheck, aiCheck, extendedCheck, backButton);
    }

    public VBox getView() {
        return view;
    }
}