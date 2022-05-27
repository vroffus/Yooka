package ua.roffus.yooki.ui;

import java.awt.*;

public abstract class Button {

    protected int x, y, row;
    protected Rectangle bounds;

    protected boolean mousePressed;

    public Button(int x, int y, int row){
        this.x = x;
        this.y = y;
        this.row = row;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }
}
