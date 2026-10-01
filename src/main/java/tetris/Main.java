package tetris;

import javafx.application.Application;
import tetris.application.TetrisApplication;

/**
 * Stable repository entry point. Keeping this class at the root tetris
 * package makes IDE run configurations and Maven launch settings predictable.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(TetrisApplication.class, args);
    }
}