package tetris.interfaces;

/**
 * Defines pixel-based movement for a game object on thet JavaFX board.
 */
public interface Movable {
    void moveBy(int deltaX, int deltaY);
}