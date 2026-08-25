package tetris.game;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board {

    private int width;
    private int height;

    private int[][] grid;

    private GridPane view;

    private Tetromino currentPiece;


    public Board(int width, int height) {

        this.width = width;
        this.height = height;

        grid = new int[height][width];

        view = new GridPane();

        drawBoard();
    }


    public GridPane getView() {
        return view;
    }


    public void setCurrentPiece(Tetromino piece) {

        this.currentPiece = piece;
    }


    /*
     * Creates a new Tetromino and places it at the top
     */
    public Tetromino spawnNewPiece() {

        return new Tetromino(0, width / 2 - 2);
    }


    /*
     * Stores the current falling piece permanently on the board
     */
    public void lockPiece(Tetromino piece) {

        int[][] blocks = piece.getBlocks();

        for (int[] block : blocks) {

            int row = block[0];
            int col = block[1];

            if(row >= 0 && row < height &&
                    col >= 0 && col < width) {

                grid[row][col] = 1;
            }
        }
    }



    private void drawBoard() {

        view.getChildren().clear();


        // Draw empty board
        for(int row = 0; row < height; row++) {

            for(int col = 0; col < width; col++) {

                Rectangle cell = new Rectangle(30,30);

                if(grid[row][col] == 1) {

                    cell.setFill(Color.CYAN);

                } else {

                    cell.setFill(Color.BLACK);
                }

                cell.setStroke(Color.GRAY);

                view.add(cell,col,row);
            }
        }


        // Draw moving piece
        if(currentPiece != null) {

            for(int[] block : currentPiece.getBlocks()) {

                Rectangle piece = new Rectangle(30,30);

                piece.setFill(Color.CYAN);
                piece.setStroke(Color.WHITE);

                view.add(
                        piece,
                        block[1],
                        block[0]
                );
            }
        }
    }



    public boolean movePieceDown(Tetromino piece) {

        piece.moveDown();

        return true;
    }



    public void movePieceLeft(Tetromino piece) {

        piece.moveLeft();
    }



    public void movePieceRight(Tetromino piece) {

        piece.moveRight();
    }



    public void rotatePiece(Tetromino piece) {

        piece.rotate();
    }



    public void repaint() {

        drawBoard();
    }

}
