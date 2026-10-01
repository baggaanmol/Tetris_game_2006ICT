package tetris.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * User-configurable game settings persisted as JSON.
 */
public class Settings {
    public static final int MIN_HEIGHT = 8;
    public static final int MAX_HEIGHT = 24;
    public static final int MIN_WIDTH = 4;
    public static final int MAX_WIDTH = 12;
    public static final double MIN_SPEED = 1;
    public static final double MAX_SPEED = 10;
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 5;

    private static final Path DEFAULT_FILE = Path.of("settings.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private int gameHeight = 20;
    private int gameWidth = 10;
    private double gameSpeed = 5;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private int difficulty = 1;
    private boolean aiPlay;
    private boolean externalPlayer;

    private transient Path file = DEFAULT_FILE;

    public Settings() {
    }

    private Settings(Path file) {
        this.file = file;
    }

    public static Settings load() {
        return load(DEFAULT_FILE);
    }

    public static Settings load(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Settings file cannot be null");
        }

        if (!Files.exists(file)) {
            return new Settings(file);
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            Settings loaded = GSON.fromJson(reader, Settings.class);
            if (loaded == null) {
                return new Settings(file);
            }
            loaded.file = file;
            loaded.normalise();
            return loaded;
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            System.err.println("Could not load settings from " + file + ": "
                    + exception.getMessage());
            return new Settings(file);
        }
    }

    public void save() {
        save(file == null ? DEFAULT_FILE : file);
    }

    public void save(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Settings file cannot be null");
        }

        normalise();
        Path absoluteFile = file.toAbsolutePath();
        Path parent = absoluteFile.getParent();
        Path temporaryFile = absoluteFile.resolveSibling(
                absoluteFile.getFileName() + ".tmp");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(temporaryFile)) {
                GSON.toJson(this, Settings.class, writer);
            }

            moveIntoPlace(temporaryFile, absoluteFile);
            this.file = file;
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw new IllegalStateException(
                    "Could not save settings to " + file,
                    exception
            );
        }
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight) {
        this.gameHeight = clamp(gameHeight, MIN_HEIGHT, MAX_HEIGHT);
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public void setGameWidth(int gameWidth) {
        this.gameWidth = clamp(gameWidth, MIN_WIDTH, MAX_WIDTH);
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(double gameSpeed) {
        if (!Double.isFinite(gameSpeed)) {
            return;
        }
        this.gameSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, gameSpeed));
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = clamp(
                difficulty,
                MIN_DIFFICULTY,
                MAX_DIFFICULTY
        );
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
    }

    public boolean isAiEnabled() {
        return aiPlay;
    }

    public void setAiPlay(boolean aiPlay) {
        this.aiPlay = aiPlay;
    }

    public boolean isExternalPlayerEnabled() {
        return externalPlayer;
    }

    public void setExternalPlayer(boolean externalPlayer) {
        this.externalPlayer = externalPlayer;
    }

    private void normalise() {
        gameHeight = clamp(gameHeight, MIN_HEIGHT, MAX_HEIGHT);
        gameWidth = clamp(gameWidth, MIN_WIDTH, MAX_WIDTH);
        if (!Double.isFinite(gameSpeed)) {
            gameSpeed = 5;
        } else {
            gameSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, gameSpeed));
        }
        difficulty = clamp(difficulty, MIN_DIFFICULTY, MAX_DIFFICULTY);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static void moveIntoPlace(Path temporaryFile, Path targetFile)
            throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}