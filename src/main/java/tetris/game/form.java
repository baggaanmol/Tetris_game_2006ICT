package tetris.game;

import tetris.interfaces.Movable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class form implements Movable {
    // each part of the shape 
    Rectangle a;
    Rectangle b;
    Rectangle c;
    Rectangle d;
    Color color;
    private String name;
    public int form = 1;

    //shape instructions
    public form(Rectangle a, Rectangle b, Rectangle c, Rectangle d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public form(Rectangle a, Rectangle b, Rectangle c, Rectangle d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.name = name;

        //switch instead of ifelse and establishing the type colours
        switch (name) {
            case "ll":
                color = Color.web("#2563eb");
                break;
            case "l":
                color = Color.web("#f97316");
                break;
            case "square":
                color = Color.web("#eab308");
                break;
            case "zig":
                color = Color.web("#22c55e");
                break;
            case "t":
                color = Color.web("#ec4899");
                break;
            case "s":
                color = Color.web("#ef4444");
                break;
            case "line":
                color = Color.web("#06b6d4");
                break;
        }
        //applies the above colours to each dimension.
        this.a.setFill(color);
        this.b.setFill(color);
        this.c.setFill(color);
        this.d.setFill(color);

    }

    public String getName() {
        return name;
    }

    @Override
    public void moveBy(int deltaX, int deltaY) {
        for (Rectangle block : new Rectangle[]{a, b, c, d}) {
            block.setX(block.getX() + deltaX);
            block.setY(block.getY() + deltaY);
        }
    }

    public void changeForm() {
        if (form != 4) {
            form++;
        } else {
            form = 1;
        }
    }
}
