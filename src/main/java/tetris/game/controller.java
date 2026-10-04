package tetris.game;

import javafx.scene.shape.Rectangle;


public class controller {
    private final int xMax;
    private final int yMax;
    private final int[][] mesh;

    public controller(int xMax, int yMax, int[][] mesh) {
        if (mesh == null || mesh.length == 0
                || xMax != mesh.length * Tetris.size
                || yMax <= 0 || yMax % Tetris.size != 0
                || mesh[0] == null
                || yMax != mesh[0].length * Tetris.size) {
            throw new IllegalArgumentException(
                    "Board dimensions must match the occupancy grid");
        }
        for (int[] column : mesh) {
            if (column == null || column.length != mesh[0].length) {
                throw new IllegalArgumentException(
                        "Board columns must have equal heights");
            }
        }
        this.xMax = xMax;
        this.yMax = yMax;
        this.mesh = mesh;
    }

    public boolean moveRight(form form) {
        if (form.a.getX() + Tetris.move <= xMax - Tetris.size &&
                form.b.getX() + Tetris.move <= xMax - Tetris.size &&
                form.c.getX() + Tetris.move <= xMax - Tetris.size &&
                form.d.getX() + Tetris.move <= xMax - Tetris.size) {
            int movea = mesh[((int) form.a.getX() / Tetris.size) + 1][((int) form.a.getY() / Tetris.size)];
            int moveb = mesh[((int) form.b.getX() / Tetris.size) + 1][((int) form.b.getY() / Tetris.size)];
            int movec = mesh[((int) form.c.getX() / Tetris.size) + 1][((int) form.c.getY() / Tetris.size)];
            int moved = mesh[((int) form.d.getX() / Tetris.size) + 1][((int) form.d.getY() / Tetris.size)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.moveBy(Tetris.move, 0);
                return true;
            }
        }
        return false;
    }

    public boolean moveLeft(form form) {
        if (form.a.getX() - Tetris.move >= 0 &&
                form.b.getX() - Tetris.move >= 0 &&
                form.c.getX() - Tetris.move >= 0 &&
                form.d.getX() - Tetris.move >= 0) {
            int movea = mesh[((int) form.a.getX() / Tetris.size) - 1][((int) form.a.getY() / Tetris.size)];
            int moveb = mesh[((int) form.b.getX() / Tetris.size) - 1][((int) form.b.getY() / Tetris.size)];
            int movec = mesh[((int) form.c.getX() / Tetris.size) - 1][((int) form.c.getY() / Tetris.size)];
            int moved = mesh[((int) form.d.getX() / Tetris.size) - 1][((int) form.d.getY() / Tetris.size)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.moveBy(-Tetris.move, 0);
                return true;
            }
        }

        return false;
    }

    //actually make the shapes
    //basicilly manually built each blocks instructions, then run for random to decide which one it makes 
    public form makeShape() {
        int block = (int) (Math.random() * 100); //what even is random
        String name;
        int size = Tetris.size;
        Rectangle a = new Rectangle(size - 1, size - 1), b = new Rectangle(size - 1, size - 1), c = new Rectangle(size - 1, size - 1), d = new Rectangle(size - 1, size - 1);
        if (block < 15) { //makes orange L %15
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2 - size);
            b.setY(size);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            d.setY(size);
            name = "l";

        } else if (block < 30) { //Makes Blue L 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2 - size);
            b.setY(size);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            d.setY(size);
            name = "ll";

        } else if (block < 45) { //square 15%
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2);
            c.setX(xMax / 2 - size);
            c.setY(size);
            d.setX(xMax / 2);
            d.setY(size);
            name = "square";

        } else if (block < 60) { //makes zag 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 - size);
            d.setY(size);
            name = "s";

        } else if (block < 70) { //makes zig 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2);
            c.setX(xMax / 2 + size);
            c.setY(size);
            d.setX(xMax / 2 + size + size);
            d.setY(size);
            name = "zig";
        } else if (block < 85) { //makes t 15%
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            name = "t";
        } else {  //CREATES THE HOLY LINE PIECE, CHOOSEN SAVIOR.
            a.setX(xMax / 2 - size - size);
            b.setX(xMax / 2 - size);
            c.setX(xMax / 2);
            d.setX(xMax / 2 + size);
            name = "line";
        }
        Rectangle[] blocks = {a, b, c, d};
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (Rectangle blockPart : blocks) {
            minX = Math.min(minX, blockPart.getX());
            maxX = Math.max(maxX, blockPart.getX());
        }
        double horizontalCorrection = 0;
        if (maxX > xMax - size) {
            horizontalCorrection = xMax - size - maxX;
        }
        if (minX + horizontalCorrection < 0) {
            horizontalCorrection = -minX;
        }
        if (horizontalCorrection != 0) {
            for (Rectangle blockPart : blocks) {
                blockPart.setX(blockPart.getX() + horizontalCorrection);
            }
        }
        return new form(a, b, c, d, name);
    }

}
