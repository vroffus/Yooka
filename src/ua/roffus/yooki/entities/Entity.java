package ua.roffus.yooki.entities;

import ua.roffus.yooki.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitBox;

    protected int aniTick, aniId;

    protected int state;

    protected float airSpeed;
    protected float walkSpeed;
    protected boolean inAir = false;

    protected int maxHealth;
    protected int currentHealth;

    protected Rectangle2D.Float attackBox;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void initHitBox(int width, int height)  {
        hitBox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
    }

    protected void drawHitBox(Graphics g, int xLvlOffset, int yLvlOffset){
        g.setColor(Color.ORANGE);
        g.drawRect((int) hitBox.x - xLvlOffset, (int) hitBox.y - yLvlOffset, (int) hitBox.width, (int) hitBox.height);
    }

    protected void drawAttackBox(Graphics g, int lvlOffsetX, int lvlOffsetY){
        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y - lvlOffsetY, (int) attackBox.width, (int) attackBox.height);
    }

    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }

    public int getState() {
        return state;
    }

    public int getAniId() {
        return aniId;
    }
}
