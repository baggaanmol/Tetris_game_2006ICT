package tetris.game;

public class PureGame {

    private int width;
    private int height;
    private int[][] cells;
    private int[][] currentShape;
    private int[][] nextShape;

    public PureGame() {
    }

    public PureGame(
            int width,
            int height,
            int[][] cells,
            int[][] currentShape,
            int[][] nextShape
    ) {
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.currentShape = currentShape;
        this.nextShape = nextShape;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getCells() {
        return cells;
    }

    public int[][] getCurrentShape() {
        return currentShape;
    }

    public int[][] getNextShape() {
        return nextShape;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCells(int[][] cells) {
        this.cells = cells;
    }

    public void setCurrentShape(int[][] currentShape) {
        this.currentShape = currentShape;
    }

    public void setNextShape(int[][] nextShape) {
        this.nextShape = nextShape;
    }
}