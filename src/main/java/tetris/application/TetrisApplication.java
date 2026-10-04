package tetris.application;

import tetris.game.HighScoreEntry;
import tetris.game.HighScoreManager;
import tetris.game.Settings;
import tetris.game.Tetris;
import tetris.game.AudioManager;
import tetris.screens.ScreenLayout;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TetrisApplication extends Application {
    private record PlayerBoard(VBox panel, StackPane boardRoot) {}

    private static final double MIN_WINDOW_WIDTH = 900;
    private static final double MIN_WINDOW_HEIGHT = 650;
    private static final String TEAM_LABEL =
            "ADITYAPAMAR  •  JIGYASHU29K  •  BAGGAANMOL";

    private StackPane root;
    private Stage primaryStage;
    private Settings settings;
    private Settings twoPlayerSettings;
    private AudioManager audioManager;
    private final HighScoreManager highScoreManager = new HighScoreManager();
    private final List<Tetris> activeGames = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        loadBundledFont();
        settings = Settings.load();
        audioManager = new AudioManager();
        primaryStage = stage;
        root = new StackPane();

        Scene scene = new Scene(root, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        scene.getStylesheets().add(
                getClass().getResource("/styles/game.css").toExternalForm()
        );

        stage.setTitle("TETRIS 2006ICT");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setScene(scene);

        showSplashScreen();
    }

    private void loadBundledFont() {
        try (InputStream font = getClass().getResourceAsStream(
                "/fonts/SpaceGrotesk.ttf")) {
            if (font != null) {
                Font.loadFont(font, 14);
            }
        } catch (Exception exception) {
            System.err.println("Could not load bundled UI font: "
                    + exception.getMessage());
        }
    }

    private void showMainScreen() {
        if (twoPlayerSettings != null) {
            settings.setMusicEnabled(twoPlayerSettings.isMusicEnabled());
            settings.setSfxEnabled(twoPlayerSettings.isSfxEnabled());
            twoPlayerSettings = null;
        }
        disposeActiveGames();
        audioManager.setMusicEnabled(settings.isMusicEnabled());
        BorderPane page = ScreenLayout.createPage("HOME MENU", TEAM_LABEL);
        VBox menuCard = new VBox(16);
        menuCard.getStyleClass().add("menu-card");
        menuCard.setAlignment(Pos.CENTER);
        menuCard.setMaxWidth(540);

        ImageView startImage = loadStartImage();
        if (startImage != null) {
            menuCard.getChildren().add(startImage);
        }

        Label title = new Label("TETRIS");
        title.getStyleClass().add("hero-title");
        Label subtitle = new Label("STACK SMART. PLAY CLEAN.");
        subtitle.getStyleClass().add("subtitle");

        VBox actions = new VBox(10);
        actions.getStyleClass().add("menu-actions");
        actions.setAlignment(Pos.CENTER);
        actions.getChildren().addAll(
                menuButton("START GAME", this::beginGame),
                menuButton("TWO PLAYER SPLIT SCREEN", this::beginTwoPlayerGame),
                menuButton("TOP SCORES", this::showTopScoresScreen),
                menuButton("SETTINGS", this::showSettingsScreen),
                menuButton("CREDITS", this::showCreditsScreen),
                menuButton("EXIT", Platform::exit)
        );

        menuCard.getChildren().addAll(title, subtitle, actions);
        page.setCenter(menuCard);
        root.getChildren().setAll(page);
    }

    private ImageView loadStartImage() {
        var imageUrl = getClass().getResource("/assets/start-image.png");
        if (imageUrl == null) {
            return null;
        }

        ImageView imageView = new ImageView(
                new Image(imageUrl.toExternalForm()));
        imageView.setFitWidth(240);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.getStyleClass().add("start-image");
        return imageView;
    }

    private Button menuButton(String text, Runnable action) {
        Button button = new Button(text.toUpperCase());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void beginGame() {
        Optional<String> playerName = requestPlayerName();
        playerName.ifPresent(this::launchGame);
    }

    private void launchGame(String playerName) {
        try {
            disposeActiveGames();
            primaryStage.setWidth(Math.max(
                    MIN_WINDOW_WIDTH,
                    settings.getGameWidth() * Tetris.size + 270
            ));
            primaryStage.setHeight(Math.max(
                    MIN_WINDOW_HEIGHT,
                    settings.getGameHeight() * Tetris.size + 90
            ));

            Tetris game = new Tetris(
                    settings,
                    highScoreManager,
                    audioManager
            );
            activeGames.add(game);
            game.start(
                    root,
                    playerName,
                    () -> launchGame(playerName),
                    this::showMainScreen
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            disposeActiveGames();
            showMainScreen();
        }
    }

    private void beginTwoPlayerGame() {
        Optional<String> firstPlayer = requestPlayerName("PLAYER 1");
        if (firstPlayer.isEmpty()) {
            return;
        }
        Optional<String> secondPlayer = requestPlayerName("PLAYER 2");
        if (secondPlayer.isEmpty()) {
            return;
        }

        twoPlayerSettings = copySettings(settings);
        twoPlayerSettings.setAiPlay(false);
        twoPlayerSettings.setExternalPlayer(false);
        launchTwoPlayerGame(firstPlayer.get(), secondPlayer.get());
    }

    private void launchTwoPlayerGame(
            String firstPlayerName,
            String secondPlayerName
    ) {
        disposeActiveGames();
        if (twoPlayerSettings == null) {
            twoPlayerSettings = copySettings(settings);
            twoPlayerSettings.setAiPlay(false);
            twoPlayerSettings.setExternalPlayer(false);
        }

        int boardWidth = twoPlayerSettings.getGameWidth() * Tetris.size;
        int boardHeight = twoPlayerSettings.getGameHeight() * Tetris.size;
        int panelWidth = boardWidth + 190;
        primaryStage.setWidth(Math.max(
                MIN_WINDOW_WIDTH,
                panelWidth * 2 + 64
        ));
        primaryStage.setHeight(Math.max(
                MIN_WINDOW_HEIGHT,
                boardHeight + 100
        ));

        PlayerBoard firstBoard = createPlayerBoard(
                "PLAYER 1  •  " + firstPlayerName,
                panelWidth,
                boardHeight + 20
        );
        PlayerBoard secondBoard = createPlayerBoard(
                "PLAYER 2  •  " + secondPlayerName,
                panelWidth,
                boardHeight + 20
        );

        HBox splitScreen = new HBox(
                16, firstBoard.panel(), secondBoard.panel());
        splitScreen.setAlignment(Pos.CENTER);
        splitScreen.setPadding(new Insets(12));
        root.getChildren().setAll(splitScreen);

        Tetris firstGame = new Tetris(
                twoPlayerSettings, highScoreManager, audioManager);
        Tetris secondGame = new Tetris(
                twoPlayerSettings, highScoreManager, audioManager);
        activeGames.add(firstGame);
        activeGames.add(secondGame);

        Runnable restartMatch =
                () -> launchTwoPlayerGame(firstPlayerName, secondPlayerName);
        Runnable quitMatch = this::showMainScreen;
        try {
            firstGame.start(
                    firstBoard.boardRoot(),
                    firstPlayerName,
                    restartMatch,
                    quitMatch,
                    Tetris.Controls.playerOne()
            );
            secondGame.start(
                    secondBoard.boardRoot(),
                    secondPlayerName,
                    restartMatch,
                    quitMatch,
                    Tetris.Controls.playerTwo()
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            disposeActiveGames();
            twoPlayerSettings = null;
            showMainScreen();
        }
    }

    private PlayerBoard createPlayerBoard(
            String title,
            int width,
            int height
    ) {
        Label playerLabel = new Label(title);
        playerLabel.getStyleClass().add("setting-label");
        StackPane boardRoot = new StackPane();
        boardRoot.setMinSize(width, height);
        boardRoot.setPrefSize(width, height);
        VBox playerPanel = new VBox(6, playerLabel, boardRoot);
        playerPanel.setAlignment(Pos.TOP_CENTER);
        playerPanel.setPrefWidth(width);
        VBox.setVgrow(boardRoot, Priority.ALWAYS);
        return new PlayerBoard(playerPanel, boardRoot);
    }

    private Settings copySettings(Settings source) {
        Settings copy = new Settings();
        copy.setGameWidth(source.getGameWidth());
        copy.setGameHeight(source.getGameHeight());
        copy.setGameSpeed(source.getGameSpeed());
        copy.setDifficulty(source.getDifficulty());
        copy.setMusicEnabled(source.isMusicEnabled());
        copy.setSfxEnabled(source.isSfxEnabled());
        return copy;
    }

    private void disposeActiveGames() {
        for (Tetris game : activeGames) {
            game.dispose();
        }
        activeGames.clear();
    }

    private Optional<String> requestPlayerName() {
        return requestPlayerName("PLAYER");
    }

    private Optional<String> requestPlayerName(String playerLabel) {
        TextInputDialog dialog = new TextInputDialog("PLAYER");
        dialog.setTitle("START GAME");
        dialog.setHeaderText("CHOOSE " + playerLabel + " NAME");
        dialog.setContentText(playerLabel + " NAME:");
        dialog.initOwner(primaryStage);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/game.css").toExternalForm()
        );

        return dialog.showAndWait()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .map(name -> name.length() > 20
                        ? name.substring(0, 20)
                        : name);
    }

    private void showCreditsScreen() {
        BorderPane page = ScreenLayout.createPage("CREDITS", "TETRIS 2006ICT");
        VBox card = contentCard();
        Label names = new Label(
                "DEVELOPED BY\n\n"
                        + "ADITYAPAMAR\n"
                        + "JIGYASHU29K\n"
                        + "BAGGAANMOL"
        );
        names.getStyleClass().add("credits-text");
        names.setAlignment(Pos.CENTER);
        names.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        card.getChildren().addAll(names, backButton());
        page.setCenter(card);
        root.getChildren().setAll(page);
    }

    private void showSettingsScreen() {
        BorderPane page = ScreenLayout.createPage("SETTINGS", "TUNE YOUR PLAY STYLE");
        GridPane controls = new GridPane();
        controls.getStyleClass().add("settings-grid");
        controls.setHgap(18);
        controls.setVgap(12);

        Slider height = slider(
                Settings.MIN_HEIGHT,
                Settings.MAX_HEIGHT,
                settings.getGameHeight(),
                true,
                value -> settings.setGameHeight(value.intValue())
        );
        Slider width = slider(
                Settings.MIN_WIDTH,
                Settings.MAX_WIDTH,
                settings.getGameWidth(),
                true,
                value -> settings.setGameWidth(value.intValue())
        );
        Slider speed = slider(
                Settings.MIN_SPEED,
                Settings.MAX_SPEED,
                settings.getGameSpeed(),
                false,
                value -> settings.setGameSpeed(value.doubleValue())
        );
        Slider difficulty = slider(
                Settings.MIN_DIFFICULTY,
                Settings.MAX_DIFFICULTY,
                settings.getDifficulty(),
                true,
                value -> settings.setDifficulty(value.intValue())
        );

        addSetting(controls, 0, "BOARD HEIGHT", height);
        addSetting(controls, 1, "BOARD WIDTH", width);
        addSetting(controls, 2, "GAME SPEED", speed);
        addSetting(controls, 3, "DIFFICULTY", difficulty);

        CheckBox music = checkBox("MUSIC", settings.isMusicEnabled());
        music.setOnAction(event -> {
            settings.setMusicEnabled(music.isSelected());
            audioManager.setMusicEnabled(music.isSelected());
        });
        CheckBox sfx = checkBox("SOUND EFFECTS", settings.isSfxEnabled());
        sfx.setOnAction(event ->
                settings.setSfxEnabled(sfx.isSelected()));
        CheckBox aiPlay = checkBox("AI PLAY", settings.isAiEnabled());
        CheckBox externalPlayer = checkBox(
                "EXTERNAL PLAYER", settings.isExternalPlayerEnabled());

        aiPlay.setOnAction(event -> {
            settings.setAiPlay(aiPlay.isSelected());
            if (aiPlay.isSelected()) {
                externalPlayer.setSelected(false);
                settings.setExternalPlayer(false);
            }
        });
        externalPlayer.setOnAction(event -> {
            settings.setExternalPlayer(externalPlayer.isSelected());
            if (externalPlayer.isSelected()) {
                aiPlay.setSelected(false);
                settings.setAiPlay(false);
            }
        });

        VBox toggles = new VBox(10, music, sfx, aiPlay, externalPlayer);
        toggles.getStyleClass().add("toggle-card");

        VBox card = contentCard();
        card.setMaxWidth(680);
        card.getChildren().addAll(
                controls,
                toggles,
                new HBox(12, saveSettingsButton(), backButton())
        );
        page.setCenter(card);
        root.getChildren().setAll(page);
    }

    private Slider slider(
            double min,
            double max,
            double value,
            boolean integer,
            java.util.function.Consumer<Number> listener
    ) {
        Slider slider = new Slider(min, max, value);
        slider.setMaxWidth(Double.MAX_VALUE);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(integer ? 0 : 4);
        slider.setSnapToTicks(integer);
        slider.valueProperty().addListener(
                (observable, oldValue, newValue) -> listener.accept(newValue)
        );
        return slider;
    }

    private void addSetting(GridPane grid, int row, String name, Slider slider) {
        Label label = new Label(name);
        label.getStyleClass().add("setting-label");
        grid.add(label, 0, row);
        grid.add(slider, 1, row);
        GridPane.setHgrow(slider, Priority.ALWAYS);
    }

    private CheckBox checkBox(String text, boolean selected) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(selected);
        return checkBox;
    }

    private Button saveSettingsButton() {
        return menuButton("SAVE SETTINGS", () -> {
            saveSettings();
            showMainScreen();
        });
    }

    private void saveSettings() {
        try {
            settings.save();
        } catch (IllegalStateException exception) {
            exception.printStackTrace();
        }
    }

    private void showTopScoresScreen() {
        BorderPane page = ScreenLayout.createPage("TOP SCORES", "LOCAL LEADERBOARD");
        VBox card = contentCard();
        card.setMaxWidth(620);

        List<HighScoreEntry> scores = highScoreManager.loadScores();
        if (scores.isEmpty()) {
            for (int index = 0; index < HighScoreManager.MAX_SCORES; index++) {
                card.getChildren().add(scoreRow(
                        "HIGH SCORE " + (index + 1),
                        Integer.toString(2000 - (index * 100))
                ));
            }
        } else {
            for (int index = 0; index < scores.size(); index++) {
                HighScoreEntry entry = scores.get(index);
                card.getChildren().add(scoreRow(
                        (index + 1) + ". " + entry.getPlayerName(),
                        Integer.toString(entry.getScore())
                ));
            }
        }

        card.getChildren().add(backButton());
        page.setCenter(card);
        root.getChildren().setAll(page);
    }

    private HBox scoreRow(String name, String score) {
        HBox row = new HBox(12);
        row.getStyleClass().add("score-row");
        row.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(name);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        Label scoreLabel = new Label(score);
        scoreLabel.getStyleClass().add("score-value");
        row.getChildren().addAll(nameLabel, scoreLabel);
        return row;
    }

    private VBox contentCard() {
        VBox card = new VBox(18);
        card.getStyleClass().add("content-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setMaxWidth(760);
        return card;
    }

    private Button backButton() {
        return menuButton("BACK TO HOME MENU", this::showMainScreen);
    }

    private void showSplashScreen() {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        StackPane splashLayout = new StackPane();
        splashLayout.getStyleClass().add("splash-page");

        ImageView splashImage = loadStartImage();
        if (splashImage != null) {
            splashImage.setFitWidth(330);
            splashImage.setFitHeight(230);
        }

        Label loadingLabel = new Label("TETRIS 2006ICT");
        loadingLabel.getStyleClass().add("splash-label");
        VBox splashContent = new VBox(14);
        splashContent.setAlignment(Pos.CENTER);
        if (splashImage != null) {
            splashContent.getChildren().add(splashImage);
        }
        splashContent.getChildren().add(loadingLabel);
        splashLayout.getChildren().add(splashContent);

        Scene splashScene = new Scene(splashLayout, 420, 360);
        splashScene.getStylesheets().add(
                getClass().getResource("/styles/game.css").toExternalForm()
        );
        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                Thread.sleep(900);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splashStage.close();
                    primaryStage.show();
                    showMainScreen();
                });
            }
        };
        Thread loadingThread = new Thread(loadTask, "tetris-startup");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    @Override
    public void stop() {
        disposeActiveGames();
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}