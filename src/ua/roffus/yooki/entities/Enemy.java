package ua.roffus.yooki.entities;

import ua.roffus.yooki.Game;

import java.awt.geom.Rectangle2D;

import static ua.roffus.yooki.utils.Constant.Directions.LEFT;
import static ua.roffus.yooki.utils.Constant.Directions.RIGHT;
import static ua.roffus.yooki.utils.Constant.EnemyConstants.*;
import static ua.roffus.yooki.utils.Handler.*;

public abstract class Enemy extends Entity {

    protected int aniIndex;
    protected int enemyState = IDLE;
    protected final int enemyType;
    protected int aniTick, aniSpeed = 220;

    protected boolean firstUpdate = true;
    protected boolean inAir = false;
    protected float fallSpeed;
    protected float gravity = 0.04f * Game.SCALE;
    protected float walkSpeed = 0.1f * Game.SCALE;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;

    protected int maxHealth;
    protected int currentHealth;

    protected boolean active = true;
    protected boolean attackChecked;

    protected static int score = 0;


    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitBox(x, y, width, height);
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
    }

    protected void firstUpdateCheck(int[][] lvlData) {
        if (firstUpdate) {
            if (!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;
            firstUpdate = false;
        }
    }

    protected void updateInAir(int[][] lvlData) {
        if (CanMoveHere(hitBox.x, hitBox.y + fallSpeed, hitBox.width, hitBox.height, lvlData)) {
            hitBox.y += fallSpeed;
            fallSpeed += gravity;
        } else {
            inAir = false;
            hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, fallSpeed);
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

    protected boolean isPlayerInRange(Player player){
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x) + (int) Math.abs(player.hitBox.y - hitBox.y);
        return absValue <= attackDistance * 5;
    }

    protected boolean isPlayerCloseForAttack(Player player){
        int absValue = (int) Math.abs(player.hitBox.x - hitBox.x) + (int) Math.abs(player.hitBox.y - hitBox.y);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState){
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;

                if(enemyState == ATTACK)
                    enemyState = IDLE;
                else if(enemyState == HIT)
                    enemyState = IDLE;
                else if(enemyState == DEATH) {
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
        fallSpeed = 0;
        score = 0;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

    public boolean isActive(){
        return active;
    }

    public int getScore() {
        return score;
    }
}
