package ua.roffus.yooki.entities;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.states.PlayingState;
import ua.roffus.yooki.utils.Constant;
import ua.roffus.yooki.utils.LoadSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import static ua.roffus.yooki.utils.Constant.PlayerConstants.*;
import static ua.roffus.yooki.utils.Constant.Sounds.ATTACK;
import static ua.roffus.yooki.utils.Handler.*;

public class Player extends Entity{
    private ArrayList<ArrayList<BufferedImage>> actions;
    private PlayingState playingState;

    //Animation moving
    private int aniTick, aniId, aniSpeed = 200;
    private int playerAction = IDLE;
    private boolean moving = false, attacking = false;
    private boolean left, right, jump;
    private float playerSpeed = 0.2f;
    private int flipX, flipW;

    //Collisions
    private int[][] lvlData;
    private float xDrawOffset = 70 * Game.SCALE, yDrawOffset = 42 * Game.SCALE;

    //Gravity
    private float airSpeed = 0f;
    private float gravity = 0.0009f * Game.SCALE;
    private float jumpSpeed = -0.48f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.02f  *Game.SCALE;
    private boolean inAir = false;

    //StatusBarUI
    private BufferedImage statusBarImg;
    private int statusBarWidth = (int) (288 * Game.SCALE);
    private int statusBarHeight = (int) (87 * Game.SCALE);
    private int statusBarX = (int) (15 * Game.SCALE);
    private int statusBarY = (int) (15 * Game.SCALE);
    private int healthBarWidth = (int) (225 * Game.SCALE);
    private int healthBarHeight = (int) (6 * Game.SCALE);
    private int healthBarXStart = (int) (51 * Game.SCALE);
    private int healthBarYStart = (int) (21 * Game.SCALE);

    //Health
    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;

    //AttackBox
    private Rectangle2D.Float attackBox;
    private boolean attackChecked;
    private boolean hit;

    public Player(float x, float y, int width, int height, PlayingState playingState) {
        super(x, y, width, height);
        this.playingState = playingState;
        importImg();
        initHitBox(x, y, (int) (48*Game.SCALE), (int) (62*Game.SCALE));
        initAttackBox();
    }

    public void update() {
        updateHealthBar();

        if (currentHealth <= 0) {
            playingState.setGameOver(true);
            playingState.getGame().playSound(Constant.Sounds.GAMEOVER);
            return;
        }
        updateAttackBox();
        updatePos();
        if(attacking)
            checkAttack();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g, int lvlOffsetX, int lvlOffsetY) {
        g.drawImage(actions.get(playerAction).get(aniId),
                (int) (hitBox.x - xDrawOffset) - lvlOffsetX + flipX,
                (int) (hitBox.y - yDrawOffset) - lvlOffsetY,
                width * flipW, height, null);

        //drawHitBox(g, lvlOffsetX, lvlOffsetY);
        //drawAttackBox(g, lvlOffsetX, lvlOffsetY);
        drawUI(g);
    }

/*    private void drawAttackBox(Graphics g, int lvlOffsetX, int lvlOffsetY){
        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y - lvlOffsetY, (int) attackBox.width, (int) attackBox.height);
    }*/

    private void initAttackBox(){
        attackBox = new Rectangle2D.Float(x, y, (int)(55 * Game.SCALE), (int)(55 * Game.SCALE));
    }

    private void updateAttackBox(){
        if(right){
            attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
        }else if(left){
            attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
        }

        attackBox.y = hitBox.y + (Game.SCALE * 25);
    }

    private void checkAttack(){
        if(attackChecked || aniId != 1)
            return;
        attackChecked = true;
        playingState.checkEnemyHit(attackBox);
        playingState.getGame().playSound(ATTACK);
    }

    private void updateHealthBar(){
        healthWidth = (int) ((currentHealth / (float) (maxHealth)) * healthBarWidth);
    }

    public void changeHealth(int value){
        currentHealth -= value;
        hit = true;
        playingState.getGame().playSound(Constant.Sounds.HIT);
        if(currentHealth <= 0){
            currentHealth = 0;
        }else if(currentHealth >= maxHealth)
            currentHealth = maxHealth;
    }

    private void drawUI(Graphics g){
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    public void updateAnimationTick(){
        aniTick++;
        if(aniTick >= aniSpeed){
            aniTick = 0;
            aniId++;
            if(aniId >= getSpriteAmount(playerAction)){
                aniId = 0;
                attacking = false;
                hit = false;
                attackChecked = false;
            }
        }
    }

    public void setAnimation() {
        int startAni = playerAction;

        if (moving)
            playerAction = RUNNING;
        else
            playerAction = IDLE;

        if (inAir) {
            if (airSpeed < 0)
                playerAction = JUMPING;
            else
                playerAction = FALLING;
        }

        if (attacking) {
            playerAction = ATTACK1;
        }else if(hit){
            playerAction = Constant.PlayerConstants.HIT;
        }

        if (startAni != playerAction)
            resetAnimation();
    }

    public void resetAnimation(){
        aniTick = 0;
        aniId = 0;
    }

    public void updatePos() {
        moving = false;

        if (jump)
            jump();

        if(!inAir)
            if((!left && !right) || (right && left))
                return;

        float xSpeed = 0;

        if (left) {
            xSpeed -= playerSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (!inAir)
            if (!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;

        if (inAir) {
            if (CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
                hitBox.y += airSpeed;
                airSpeed += gravity;
            } else {
                hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
            }

        }
        updateXPos(xSpeed);
        moving = true;
    }


    public void resetInAir(){
        inAir = false;
        airSpeed = 0;
    }

    public void jump(){
        if(inAir)
            return;
        inAir = true;
        playingState.getGame().playSound(Constant.Sounds.JUMP);
        airSpeed = jumpSpeed;
    }

    public void resetAll(){
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = IDLE;
        currentHealth = maxHealth;

        hitBox.x = x;
        hitBox.y = y;

        if(!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    public void updateXPos(float xSpeed) {
        if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData))
            hitBox.x += xSpeed;
        else
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
    }

    public void resetDirBooleans(){
        left = false;
        right = false;
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    public void importImg() {
        //player
        ArrayList<BufferedImage> idle = new ArrayList<>();
        ArrayList<BufferedImage> running = new ArrayList<>();
        ArrayList<BufferedImage> jumping = new ArrayList<>();
        ArrayList<BufferedImage> falling = new ArrayList<>();
        ArrayList<BufferedImage> atk1 = new ArrayList<>();
        ArrayList<BufferedImage> death = new ArrayList<>();
        ArrayList<BufferedImage> take_hit = new ArrayList<>();

        File[] folders = new File[0];
        try {
            folders = (new File(Objects.requireNonNull(getClass().getResource("/hero")).toURI())).listFiles();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assert folders != null;
        for(File folder : folders){
            if(folder.isDirectory()){
                switch(folder.getName()){
                    case "idle":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    idle.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "run":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    running.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "jump":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    jumping.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "falling":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    falling.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "1atk":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    atk1.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "take_hit":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    take_hit.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case "death":
                        for(File file : Objects.requireNonNull(folder.listFiles())) {
                            if (file.isFile()) {
                                try {
                                    death.add(ImageIO.read(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                }

                actions = new ArrayList<>();
                actions.add(idle);
                actions.add(running);
                actions.add(jumping);
                actions.add(falling);
                actions.add(atk1);
                actions.add(death);
                actions.add(take_hit);

                //status bar
                statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
            }
        }
    }

    public void setAttacking(boolean attacking){
        this.attacking = attacking;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setJump(boolean jump){
        this.jump = jump;
    }
}