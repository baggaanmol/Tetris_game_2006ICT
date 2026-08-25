package tetris.game;

import tetris.interfaces.Movable;

public class Tetromino implements Movable {
    private int[][] blocks; // [row][col] coordinates relative to piece's origin
    private int x, y; // Position on board

    public Tetromino(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        // I-Piece shape
        blocks = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}};
    }

    public int[][] getBlocks() {
        // Return absolute board positions
        int[][] absBlocks = new int[blocks.length][2];
        for (int i = 0; i < blocks.length; i++) {
            absBlocks[i][0] = blocks[i][0] + x;
            absBlocks[i][1] = blocks[i][1] + y;
        }
        return absBlocks;
    }

    @Override
    public void moveLeft() {
        y--;
    }

    @Override
    public void moveRight() {
        y++;
    }

    @Override
    public void moveDown() {
        x++;
    }

    @Override
    public void rotate() {
        // Simple rotation (for demo)
        int[][] rotated = new int[blocks.length][2];
        for (int i = 0; i < blocks.length; i++) {
            rotated[i][0] = blocks[i][1];
            rotated[i][1] = -blocks[i][0];
        }
        blocks = rotated;
    }
}