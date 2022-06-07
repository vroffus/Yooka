package ua.roffus.yooki.entities;

import ua.roffus.yooki.Game;

import java.awt.geom.Rectangle2D;

import static ua.roffus.yooki.utils.Constant.ANI_SPEED;
import static ua.roffus.yooki.utils.Constant.Directions.LEFT;
import static ua.roffus.yooki.utils.Constant.Directions.RIGHT;
import static ua.roffus.yooki.utils.Constant.EnemyConstants.*;
import static ua.roffus.yooki.utils.Constant.GRAVITY;
import static ua.roffus.yooki.utils.Handler.*;

public abstract class Enemy extends Entity {

    protected final int enemyType;
    protected boolean firstUpdate = true;

    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;

    protected boolean active = true;
    protected boolean attackChecked;

    protected static int score = 0;


    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
        walkSpeed = 0.1f * Game.SCALE;
    }

    protected void firstUpdateCheck(int[][] lvlData) {
        if (firstUpdate) {
            if (!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;
            firstUpdate = false;
        }
    }

    protected void updateInAir(int[][] lvlData) {
        if (CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
            hitBox.y += airSpeed;
            airSpeed += GRAVITY;
        } else {
            inAir = false;
            tileY = (int) (hitBox.y / Game.TILES_SIZE);
        }
    }

    protected void move(int[][] lvlData) {
        float xSpeed = 0;
        if (walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData))
            if (IsFloor(hitBox, xSpeed, lvlData)) {
                hitBox.x += xSpeed;
                return;
            }
        changeWalkDir();
    }

    public void hurt(int amount){
        currentHealth -= amount;
        if(currentHealth <= 0)
            newState(DEATH);
        else
            newState(HIT);
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox, Player player){
        if(attackBox.intersects(player.hitBox))
            player.changeHealth(GetEnemyDamage(enemyType));
        attackChecked = true;
    }

    protected void turnTowardsPlayer(Player player){
        if(player.hitBox.x > hitBox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }


    protected boolean canSeePlayer(int[][] lvlData, Player player){
        int playerTileY = (int) (player.getHitBox().y / Game.TILES_SIZE);
        if(playerTileY == tileY)
            if(isPlayerInRange(player)) {
                return IsSightClear(lvlData, hitBox, player.hitBox, tileY);
            }
        return false;
    }

    //checking if the player is in range of the entity
    protected boolean isPlayerInRange(Player player){
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x) + (int) Math.abs(player.hitBox.y - hitBox.y);
        return absValue <= attackDistance * 5;
    }

    //checking if the player is close enough for the entity to hit
    protected boolean isPlayerCloseForAttack(Player player){
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x) + (int) Math.abs(player.hitBox.y - hitBox.y);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState){
        this.state = enemyState;
        aniTick = 0;
        aniId = 0;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniId++;
            if (aniId >= getSpriteAmount(enemyType, state)) {
                aniId = 0;

                if(state == ATTACK)
                    state = IDLE;
                else if(state == HIT)
                    state = IDLE;
                else if(state == DEATH) {
                    active = false;
                    score += 25;
                    System.out.println(score);
                }
            }
        }
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public void resetEnemy(){
        hitBox.x = x;
        hitBox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        airSpeed = 0;
        score = 0;
    }

    public boolean isActive(){
        return active;
    }

    public int getScore() {
        return score;
    }
}
