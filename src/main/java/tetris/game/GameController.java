package tetris.game;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class GameController {
    private Board board;
    private Tetromino currentPiece;

    public GameController(){

        board = new Board(10,20);

        currentPiece = new Tetromino(1,4);

        board.setCurrentPiece(currentPiece);
    }

    public Pane getView() {
        return board.getView();
    }

    public void update() {
        // Move piece down automatically
        if (currentPiece != null) {
            if (!board.movePieceDown(currentPiece)) {
                board.lockPiece(currentPiece);
                currentPiece = board.spawnNewPiece();
            }
        }
        board.repaint();
    }

    public void handleKeyPress(KeyCode code) {
        if (code == KeyCode.LEFT) {
            board.movePieceLeft(currentPiece);
        } else if (code == KeyCode.RIGHT) {
            board.movePieceRight(currentPiece);
        } else if (code == KeyCode.UP) {
            board.rotatePiece(currentPiece);
        } else if (code == KeyCode.DOWN) {
            // Speeds up falling, but we can just call movePieceDown multiple times 
            board.movePieceDown(currentPiece);
        }
        board.repaint();
    }
}