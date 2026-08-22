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


    public void setCurrentPiece(Tetromino piece){
        this.currentPiece = piece;
    }


    private void drawBoard(){

        view.getChildren().clear();


        for(int row = 0; row < height; row++){

            for(int col = 0; col < width; col++){

                Rectangle cell = new Rectangle(30,30);

                cell.setFill(Color.BLACK);
                cell.setStroke(Color.GRAY);

                view.add(cell,col,row);
            }
        }


        if(currentPiece != null){

            for(int[] block : currentPiece.getBlocks()){

                Rectangle piece = new Rectangle(30,30);

                piece.setFill(Color.CYAN);
                piece.setStroke(Color.WHITE);

                view.add(piece,block[1],block[0]);
            }
        }
    }



    public boolean movePieceDown(Tetromino piece){

        piece.moveDown();

        return true;
    }



    public void movePieceLeft(Tetromino piece){

        piece.moveLeft();
    }



    public void movePieceRight(Tetromino piece){

        piece.moveRight();
    }



    public void rotatePiece(Tetromino piece){

        piece.rotate();
    }



    public void repaint(){

        drawBoard();
    }


}