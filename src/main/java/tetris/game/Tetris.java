package tetris.game;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Tetris {
    public static final int move = 30;
    public static final int size = 30;
    private static final int FAST_FALL_INTERVAL = 60;
    private static final List<Tetris> ACTIVE_GAMES = new ArrayList<>();

    public record Controls(
            KeyCode left,
            KeyCode right,
            KeyCode rotate,
            KeyCode softDrop,
            KeyCode hardDrop,
            boolean arrowAliases
    ) {
        public static Controls solo() {
            return new Controls(
                    KeyCode.A, KeyCode.D, KeyCode.W,
                    KeyCode.DOWN, KeyCode.SPACE, true);
        }

        public static Controls playerOne() {
            return new Controls(
                    KeyCode.A, KeyCode.D, KeyCode.W,
                    KeyCode.R, KeyCode.SPACE, false);
        }

        public static Controls playerTwo() {
            return new Controls(
                    KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP,
                    KeyCode.DOWN, KeyCode.ENTER, false);
        }
    }

    private int xMax;
    private int yMax;
    private int[][] mesh;
    private Pane groupe = new Pane();
    private form object;
    private Scene scene;
    private int score;
    private boolean game = true;
    private boolean paused;
    private form nextObj;
    private int linesNo;
    private Text pausedText;
    private Text externalWarningText;
    private Text audioStatusText;
    private Runnable onRestart;
    private Runnable onQuit;
    private int fallInterval;
    private boolean fastFall;
    private Timer fallTimer;
    private EventHandler<KeyEvent> keyPressedHandler;
    private EventHandler<KeyEvent> keyReleasedHandler;
    private final Set<KeyCode> toggleKeysDown = EnumSet.noneOf(KeyCode.class);
    private Controls controls = Controls.solo();
    private controller pieceController;
    private final Settings settings;
    private final AudioManager audioManager;
    private final AI ai;
    private final ExternalPlayer externalPlayer;
    private final HighScoreManager highScoreManager;

    public Tetris(Settings settings) {
        this(settings, new HighScoreManager(), new AudioManager());
    }

    public Tetris(Settings settings, HighScoreManager highScoreManager) {
        this(settings, highScoreManager, new AudioManager());
    }

    public Tetris(
            Settings settings,
            HighScoreManager highScoreManager,
            AudioManager audioManager
    ) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        if (highScoreManager == null) {
            throw new IllegalArgumentException(
                    "High score manager cannot be null");
        }
        if (audioManager == null) {
            throw new IllegalArgumentException("Audio manager cannot be null");
        }
        this.settings = settings;
        this.audioManager = audioManager;
        this.ai = new AI(new boardeval());
        this.externalPlayer = new ExternalPlayer();
        this.highScoreManager = highScoreManager;
    }


    public void start(StackPane root, Runnable onGameOver) throws Exception {
        start(root, "PLAYER", onGameOver, onGameOver);
    }

    public void start(
            StackPane root,
            String playerName,
            Runnable onGameOver
    ) throws Exception {
        start(root, playerName, onGameOver, onGameOver);
    }

    public void start(
            StackPane root,
            String playerName,
            Runnable onRestart,
            Runnable onQuit
    ) throws Exception {
        start(root, playerName, onRestart, onQuit, Controls.solo());
    }

    public void start(
            StackPane root,
            String playerName,
            Runnable onRestart,
            Runnable onQuit,
            Controls controls
    ) throws Exception {
        Objects.requireNonNull(root, "Game root cannot be null");
        Objects.requireNonNull(controls, "Controls cannot be null");
        dispose();
        this.onRestart = onRestart;
        this.onQuit = onQuit;
        this.controls = controls;
        xMax = settings.getGameWidth() * size;
        yMax = settings.getGameHeight() * size;
        fallInterval = 600 - (int)(settings.getGameSpeed() * 50);
        mesh = new int[xMax / size][yMax / size];
        pieceController = new controller(xMax, yMax, mesh);

        audioManager.setMusicEnabled(settings.isMusicEnabled());
        audioManager.setSfxEnabled(settings.isSfxEnabled());

        groupe = new Pane();
        groupe.getStyleClass().add("game-pane");
        groupe.setPrefSize(xMax + 190, yMax + 10);
        score = 0;
        linesNo = 0;
        game = true;
        paused = false;
        fastFall = false;
        for (int[] a : mesh) {
            Arrays.fill(a, 0);
        }

        root.getChildren().setAll(groupe);
        scene = root.getScene();
        if (scene == null) {
            throw new IllegalStateException(
                    "The game root must be attached to a JavaFX Scene");
        }

        Platform.runLater(() -> {
                    groupe.setFocusTraversable(true);
                    groupe.requestFocus();
                });

        nextObj = pieceController.makeShape();

        Line line = new Line(xMax, 0, xMax, yMax);
        Text scoretext = new Text("SCORE: 0");
        scoretext.setStyle("-fx-font: 16 'Space Grotesk'; -fx-font-weight: bold;");
        scoretext.setY(36);
        scoretext.setX(xMax + 10);
        Text linesText = new Text("LINES: 0");
        linesText.setStyle("-fx-font: 14 'Space Grotesk'; -fx-font-weight: bold;");
        linesText.setY(66);
        linesText.setX(xMax + 10);
        Text levelText = new Text("LEVEL: 1");
        levelText.setStyle("-fx-font: 14 'Space Grotesk'; -fx-font-weight: bold;");
        levelText.setY(94);
        levelText.setX(xMax + 10);
        audioStatusText = new Text();
        audioStatusText.setStyle(
                "-fx-font: 10 'Space Grotesk'; -fx-font-weight: bold;");
        audioStatusText.setY(116);
        audioStatusText.setX(xMax + 10);
        scoretext.setFill(Color.web("#172033"));
        linesText.setFill(Color.web("#172033"));
        levelText.setFill(Color.web("#172033"));
        audioStatusText.setFill(Color.web("#475569"));
        line.setStroke(Color.web("#64748b"));
        pausedText = new Text("PAUSED");
        pausedText.setFill(Color.RED);
        pausedText.setStyle("-fx-font: 40 'Space Grotesk'; -fx-font-weight: bold;");
        pausedText.setY(yMax / 2.0);
        pausedText.setX(Math.max(
                0,
                (xMax - pausedText.getLayoutBounds().getWidth()) / 2
        ));
        pausedText.setVisible(false);
        externalWarningText = new Text("External Player server unavailable");
        externalWarningText.setFill(Color.RED);
        externalWarningText.setStyle(
                "-fx-font: 18 'Space Grotesk'; -fx-font-weight: bold;");
        externalWarningText.setX(
                (xMax - externalWarningText.getLayoutBounds().getWidth()) / 2);
        externalWarningText.setY(yMax / 2.0);
        externalWarningText.setVisible(false);
        groupe.getChildren().addAll(
                scoretext, linesText, levelText, audioStatusText, line,
                pausedText, externalWarningText);
        ACTIVE_GAMES.add(this);
        refreshAudioStatusForSession();

        form a = nextObj;
        groupe.getChildren().addAll(a.a, a.b, a.c, a.d);
        object = a;
        if (settings.isAiEnabled()) {
            prepareAiPiece(object);
        }
        nextObj = pieceController.makeShape();

        if (settings.isExternalPlayerEnabled()) {
            requestExternalMove();
        }

        installInputHandlers();
        fallTimer = new Timer("tetris-fall", true);
        Button menuButton = new Button("HOME MENU");
        menuButton.setLayoutX(xMax + 10);
        menuButton.setLayoutY(140);
        menuButton.setOnAction(e -> {
            dispose();
            if (onQuit != null) {
                onQuit.run();
            }
        });
        groupe.getChildren().add(menuButton);
        final int tickMs = 30;
        final int[] elapsed = {0};
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (!game) {
                            return;
                        }
                        if (paused)
                            return;
                        elapsed[0] += tickMs;
                        int currentFallInterval = fastFall
                                ? FAST_FALL_INTERVAL
                                : Math.max(60, fallInterval - (getLevel() - 1) * 30);
                        if (elapsed[0] < currentFallInterval)
                            return;
                        elapsed[0] = 0;
                        if (overlapsLockedCells(object)) {
                            game = false;
                            fallTimer.cancel();
                            removeInputHandlers();
                            try {
                                highScoreManager.updateHighScore(playerName, score);
                            } catch (IllegalStateException exception) {
                                System.err.println(
                                        "Could not save the final score: "
                                                + exception.getMessage());
                            }
                            showGameOverOverlay(root, playerName);
                            return;
                        }

                        if (game) {
                            moveDown(object);
                            scoretext.setText("SCORE: " + Integer.toString(score));
                            linesText.setText("LINES: " + linesNo);
                            levelText.setText("LEVEL: " + getLevel());
                        }
                    }
                });
            }
        };
        fallTimer.schedule(task, 0, tickMs);
    }

    private void showGameOverOverlay(StackPane root, String playerName) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("game-over-overlay");

        VBox card = new VBox(14);
        card.getStyleClass().add("game-over-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(360);

        Label title = new Label("GAME OVER");
        title.getStyleClass().add("game-over-title");
        Label result = new Label(
                playerName.toUpperCase() + "  •  SCORE " + score);
        result.getStyleClass().add("game-over-score");

        Button restart = new Button("RESTART GAME");
        restart.setMaxWidth(Double.MAX_VALUE);
        restart.setOnAction(event -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });

        Button quit = new Button("QUIT TO HOME MENU");
        quit.setMaxWidth(Double.MAX_VALUE);
        quit.setOnAction(event -> {
            if (onQuit != null) {
                onQuit.run();
            }
        });

        card.getChildren().addAll(title, result, restart, quit);
        overlay.getChildren().add(card);
        root.getChildren().add(overlay);
    }

    private PureGame createPureGame() {

        int width = xMax / size;
        int height = yMax / size;

        int[][] cells = new int[height][width];

        // Convert mesh[x][y] into cells[y][x]
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[y][x] = mesh[x][y];
            }
        }

        int[][] currentShape = shapeToArray(object);
        int[][] nextShape = shapeToArray(nextObj);

        return new PureGame(
                width,
                height,
                cells,
                currentShape,
                nextShape
        );
    }

    private int[][] shapeToArray(form piece) {

        Rectangle[] blocks = {
                piece.a,
                piece.b,
                piece.c,
                piece.d
        };

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Rectangle block : blocks) {

            int x = (int) block.getX() / size;
            int y = (int) block.getY() / size;

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int[][] shape =
                new int[maxY - minY + 1][maxX - minX + 1];

        for (Rectangle block : blocks) {

            int x = (int) block.getX() / size - minX;
            int y = (int) block.getY() / size - minY;

            shape[y][x] = 1;
        }

        return shape;
    }

    private void requestExternalMove() {

        PureGame gameState = createPureGame();

        OpMove move = externalPlayer.requestMove(gameState);

        if (move == null) {

            System.out.println(
                    "External Player unavailable - piece will continue without external control."
            );

            showExternalWarning();

            return;
        }

        hideExternalWarning();

        System.out.println(
                "External move: X = " + move.opX()
                        + ", rotations = " + move.opRotate()
        );

        applyExternalMove(move);
    }

    private void applyExternalMove(OpMove move) {

        if (move == null) {
            return;
        }

        // Rotate the current piece
        for (int i = 0; i < move.opRotate(); i++) {
            MoveTurn(object);
        }

        // Find the current left-most X position
        int currentX = getPieceLeftX(object);

        // Move toward the server's target X position
        while (currentX < move.opX()) {

            int before = getPieceLeftX(object);

            pieceController.moveRight(object);

            int after = getPieceLeftX(object);

            // Movement failed, so stop to avoid an infinite loop
            if (before == after) {
                break;
            }

            currentX = after;
        }

        while (currentX > move.opX()) {

            int before = getPieceLeftX(object);

            pieceController.moveLeft(object);

            int after = getPieceLeftX(object);

            if (before == after) {
                break;
            }

            currentX = after;
        }

        System.out.println(
                "External player applied: target X = "
                        + move.opX()
                        + ", rotations = "
                        + move.opRotate()
        );
    }

    private void showExternalWarning() {

        if (externalWarningText != null) {
            externalWarningText.setVisible(true);
            externalWarningText.toFront();
        }
    }

    private void hideExternalWarning() {

        if (externalWarningText != null) {
            externalWarningText.setVisible(false);
        }
    }

    private int getPieceLeftX(form piece) {

        int aX = (int) piece.a.getX() / size;
        int bX = (int) piece.b.getX() / size;
        int cX = (int) piece.c.getX() / size;
        int dX = (int) piece.d.getX() / size;

        return Math.min(
                Math.min(aX, bX),
                Math.min(cX, dX)
        );
    }

    private void playClearSound() {
        if (settings != null && settings.isSfxEnabled()) {
            audioManager.playClearSound();
        }
    }

    private void installInputHandlers() {
        keyPressedHandler = event -> {
            if (event.isConsumed() || !game) {
                return;
            }

            KeyCode key = event.getCode();
            if (key == KeyCode.M || key == KeyCode.S) {
                if (toggleKeysDown.add(key)) {
                    if (key == KeyCode.M) {
                        settings.setMusicEnabled(!settings.isMusicEnabled());
                        audioManager.setMusicEnabled(settings.isMusicEnabled());
                    } else {
                        settings.setSfxEnabled(!settings.isSfxEnabled());
                        audioManager.setSfxEnabled(settings.isSfxEnabled());
                    }
                    refreshAudioStatusForSession();
                }
                event.consume();
                return;
            }

            if (key == KeyCode.P) {
                if (toggleKeysDown.add(KeyCode.P)) {
                    paused = !paused;
                }
                pausedText.setVisible(paused);
                return;
            }
            if (paused || settings.isAiEnabled()
                    || settings.isExternalPlayerEnabled()) {
                return;
            }

            boolean left = key == controls.left()
                    || (controls.arrowAliases() && key == KeyCode.LEFT);
            boolean right = key == controls.right()
                    || (controls.arrowAliases() && key == KeyCode.RIGHT);
            boolean rotate = key == controls.rotate()
                    || (controls.arrowAliases() && key == KeyCode.UP);

            if (left) {
                pieceController.moveLeft(object);
            } else if (right) {
                pieceController.moveRight(object);
            } else if (rotate) {
                MoveTurn(object);
            } else if (key == controls.softDrop()) {
                fastFall = true;
            } else if (key == controls.hardDrop()) {
                hardDrop(object);
            }
        };
        keyReleasedHandler = event -> {
            KeyCode key = event.getCode();
            if (key == KeyCode.M || key == KeyCode.S || key == KeyCode.P) {
                toggleKeysDown.remove(key);
            }
            if (key == controls.softDrop()) {
                fastFall = false;
            }
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    private void removeInputHandlers() {
        if (scene == null) {
            return;
        }
        if (keyPressedHandler != null) {
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
            keyPressedHandler = null;
        }
        if (keyReleasedHandler != null) {
            scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
            keyReleasedHandler = null;
        }
    }

    public void dispose() {
        game = false;
        paused = false;
        fastFall = false;
        toggleKeysDown.clear();
        if (fallTimer != null) {
            fallTimer.cancel();
            fallTimer.purge();
            fallTimer = null;
        }
        removeInputHandlers();
        ACTIVE_GAMES.remove(this);
    }

    private void refreshAudioStatusForSession() {
        for (Tetris activeGame : ACTIVE_GAMES) {
            if (activeGame.settings == settings
                    && activeGame.audioStatusText != null) {
                activeGame.audioStatusText.setText(
                        "M MUSIC: "
                                + (settings.isMusicEnabled() ? "ON" : "OFF")
                                + "   S SOUND: "
                                + (settings.isSfxEnabled() ? "ON" : "OFF")
                );
            }
        }
    }

    private void prepareAiPiece(form piece) {
        AI.Move move = ai.bestMove(piece, mesh);
        ai.positionAtSpawn(piece, move);
    }

    private int getLevel() {
        return (linesNo / 10) + 1;
    }

    private void MoveTurn(form form) {
        int f = form.form;
        Rectangle a = form.a;
        Rectangle b = form.b;
        Rectangle c = form.c;
        Rectangle d = form.d;
        switch (form.getName()) { //converted to enhanced switch case for marking, double check this if it doesn't work
            case "ll" -> {
                if (f == 1 && cB(a, 0, -2) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveRight(form.b);
                    moveUp(form.b);
                    moveLeft(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, -2, 0) && cB(b, 1, -1) && cB(d, -1, 1)) {
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveRight(form.b);
                    moveDown(form.b);
                    moveLeft(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, 0, 2) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveLeft(form.b);
                    moveDown(form.b);
                    moveRight(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, 2, 0) && cB(b, -1, 1) && cB(d, 1, -1)) {
                    moveRight(form.a);
                    moveRight(form.a);
                    moveLeft(form.b);
                    moveUp(form.b);
                    moveRight(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }

            }
            case "l" -> {
                if (f == 1 && cB(a, 2, 0) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveRight(form.a);
                    moveRight(form.a);
                    moveRight(form.b);
                    moveUp(form.b);
                    moveLeft(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, 0, -2) && cB(b, 1, -1) && cB(d, -1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveRight(form.b);
                    moveDown(form.b);
                    moveLeft(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, -2, 0) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveLeft(form.b);
                    moveDown(form.b);
                    moveRight(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, 0, 2) && cB(b, -1, 1) && cB(d, 1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveLeft(form.b);
                    moveUp(form.b);
                    moveRight(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
            }
            case "square" -> {
            }
            case "s" -> {
                if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveUp(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveDown(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveUp(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveDown(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
            }
            case "t" -> {
                if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveDown(form.d);
                    moveLeft(form.d);
                    moveLeft(form.c);
                    moveUp(form.c);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
                    moveRight(form.a);
                    moveDown(form.a);
                    moveLeft(form.d);
                    moveUp(form.d);
                    moveUp(form.c);
                    moveRight(form.c);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveUp(form.d);
                    moveRight(form.d);
                    moveRight(form.c);
                    moveDown(form.c);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
                    moveLeft(form.a);
                    moveUp(form.a);
                    moveRight(form.d);
                    moveDown(form.d);
                    moveDown(form.c);
                    moveLeft(form.c);
                    form.changeForm();
                    break;
                }
                break;
            }
            case "zig" -> {
                if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
                    moveUp(form.b);
                    moveRight(form.b);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveRight(form.d);
                    moveRight(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
                    moveUp(form.b);
                    moveRight(form.b);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveRight(form.d);
                    moveRight(form.d);
                    form.changeForm();
                }
            }
            case "line" -> {
                if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.a);
                    moveUp(form.b);
                    moveRight(form.b);
                    moveDown(form.d);
                    moveLeft(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveUp(form.d);
                    moveRight(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.a);
                    moveUp(form.b);
                    moveRight(form.b);
                    moveDown(form.d);
                    moveLeft(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveUp(form.d);
                    moveRight(form.d);
                    form.changeForm();
                    break;
                }
                break;
            }
        }
    }

    private void moveDown(Rectangle rect) {
        if (rect.getY() + move < yMax)
            rect.setY(rect.getY() + move);

    }

    private void moveDown(form form) {
        if (isBlockedBelow(form)) {
            lockPiece(form);
        } else {
            form.moveBy(0, move);
        }
    }

    private void hardDrop(form activePiece) {
        if (overlapsLockedCells(activePiece)) {
            return;
        }
        while (!isBlockedBelow(activePiece)) {
            activePiece.moveBy(0, move);
        }
        lockPiece(activePiece);
    }

    private void lockPiece(form lockedPiece) {
        mesh[(int) lockedPiece.a.getX() / size]
                [(int) lockedPiece.a.getY() / size] = 1;
        mesh[(int) lockedPiece.b.getX() / size]
                [(int) lockedPiece.b.getY() / size] = 1;
        mesh[(int) lockedPiece.c.getX() / size]
                [(int) lockedPiece.c.getY() / size] = 1;
        mesh[(int) lockedPiece.d.getX() / size]
                [(int) lockedPiece.d.getY() / size] = 1;
        clearCompletedRows(groupe);

        form nextPiece = nextObj;
        nextObj = pieceController.makeShape();
        object = nextPiece;
        if (settings.isAiEnabled()) {
            prepareAiPiece(object);
        }
        if (settings.isExternalPlayerEnabled()) {
            requestExternalMove();
        }
        groupe.getChildren().addAll(
                nextPiece.a,
                nextPiece.b,
                nextPiece.c,
                nextPiece.d
        );
    }

    private boolean overlapsLockedCells(form piece) {
        Rectangle[] blocks = {piece.a, piece.b, piece.c, piece.d};
        for (Rectangle block : blocks) {
            int column = (int) block.getX() / size;
            int row = (int) block.getY() / size;
            if (column < 0 || column >= mesh.length
                    || row < 0 || row >= mesh[0].length
                    || mesh[column][row] != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlockedBelow(form form) {
        return form.a.getY() == yMax - size || form.b.getY() == yMax - size || form.c.getY() == yMax - size
                || form.d.getY() == yMax - size || moveA(form) || moveB(form) || moveC(form) || moveD(form);
    }

    private void moveRight(Rectangle rect) {
        if (rect.getX() + move <= xMax - size)
            rect.setX(rect.getX() + move);
    }

    private void moveLeft(Rectangle rect) {
        if (rect.getX() - move >= 0)
            rect.setX(rect.getX() - move);
    }

    private void moveUp(Rectangle rect) {
        if (rect.getY() - move >= 0)
            rect.setY(rect.getY() - move);
    }

    private boolean cB(Rectangle rect, int x, int y) {
        boolean xb = false;
        boolean yb = false;
        if (x >= 0)
            xb = rect.getX() + x * move <= xMax - size;
        if (x < 0)
            xb = rect.getX() + x * move >= 0;
        if (y > 0)
            yb = rect.getY() - y * move >= 0;
        else if (y < 0)
            yb = rect.getY() - y * move < yMax;
        else
            yb = true;
        return xb && yb && mesh[((int) rect.getX() / size) + x][((int) rect.getY() / size) - y] == 0;
    }

    private int clearCompletedRows(Pane pane) {
        java.util.List<Integer> clearedRows =
                BoardModel.clearCompletedRows(mesh);
        if (clearedRows.isEmpty()) {
            return 0;
        }

        playClearSound();
        for (Node node : new ArrayList<>(pane.getChildren())) {
            if (!(node instanceof Rectangle block)) {
                continue;
            }

            int row = (int) block.getY() / size;
            if (clearedRows.contains(row)) {
                pane.getChildren().remove(block);
                continue;
            }

            int rowsBelowBlock = 0;
            for (int clearedRow : clearedRows) {
                if (clearedRow > row) {
                    rowsBelowBlock++;
                }
            }
            block.setY(block.getY() + (rowsBelowBlock * size));
        }

        score += ScoreRules.pointsForClearedLines(clearedRows.size());
        linesNo += clearedRows.size();
        return clearedRows.size();
    }
    private boolean moveA(form form) {
        return (mesh[(int) form.a.getX() / size][((int) form.a.getY() / size) + 1] == 1);
    }

    private boolean moveB(form form) {
        return (mesh[(int) form.b.getX() / size][((int) form.b.getY() / size) + 1] == 1);
    }

    private boolean moveC(form form) {
        return (mesh[(int) form.c.getX() / size][((int) form.c.getY() / size) + 1] == 1);
    }

    private boolean moveD(form form) {
        return (mesh[(int) form.d.getX() / size][((int) form.d.getY() / size) + 1] == 1);
    }

    }
