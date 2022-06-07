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

import static ua.roffus.yooki.utils.Constant.ANI_SPEED;
import static ua.roffus.yooki.utils.Constant.GRAVITY;
import static ua.roffus.yooki.utils.Constant.PlayerConstants.*;
import static ua.roffus.yooki.utils.Constant.StatusBar.*;
import static ua.roffus.yooki.utils.Handler.*;

public class Player extends Entity{
    private ArrayList<ArrayList<BufferedImage>> actions;
    private final PlayingState playingState;

    //Animation moving
    private boolean moving = false, attacking = false;
    private boolean left, right, jump;
    private int flipX, flipW;

    //Collisions
    private int[][] lvlData;
    private float xDrawOffset = 70 * Game.SCALE, yDrawOffset = 42 * Game.SCALE;

    //Gravity
    private final float jumpSpeed = -0.48f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.02f  *Game.SCALE;

    //StatusBarUI
    private BufferedImage statusBarImg;
    private int healthWidth = HEALTH_BAR_WIDTH;

    //AttackBox
    private boolean attackChecked;
    private boolean hit;

    public Player(float x, float y, int width, int height, PlayingState playingState) {
        super(x, y, width, height);
        this.playingState = playingState;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = 0.2f * Game.SCALE;
        importImg();
        initHitBox(48, 62);
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
        g.drawImage(actions.get(state).get(aniId),
                (int) (hitBox.x - xDrawOffset) - lvlOffsetX + flipX,
                (int) (hitBox.y - yDrawOffset) - lvlOffsetY,
                width * flipW, height, null);
        //drawHitBox(g, lvlOffsetX, lvlOffsetY);
        //drawAttackBox(g, lvlOffsetX, lvlOffsetY);
        drawUI(g);
    }

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
        playingState.getGame().playSound(Constant.Sounds.ATTACK);
    }

    private void updateHealthBar(){
        healthWidth = (int) ((currentHealth / (float) (maxHealth)) * HEALTH_BAR_WIDTH);
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
        g.drawImage(statusBarImg, STATUS_BAR_X, STATUS_BAR_Y, STATUS_BAR_WIDTH, STATUS_BAR_HEIGHT, null);
        g.setColor(Color.RED);
        g.fillRect(HEALTH_BAR_X_START + STATUS_BAR_X, HEALTH_BAR_Y_START + STATUS_BAR_Y, healthWidth, HEALTH_BAR_HEIGHT);
    }

    public void updateAnimationTick(){
        aniTick++;
        if(aniTick >= ANI_SPEED){
            aniTick = 0;
            aniId++;
            if(aniId >= getSpriteAmount(state)){
                aniId = 0;
                attacking = false;
                hit = false;
                attackChecked = false;
            }
        }
    }

    public void setAnimation() {
        int startAni = state;

        if (moving)
            state = RUNNING;
        else
            state = IDLE;

        if (inAir) {
            if (airSpeed < 0)
                state = JUMPING;
            else
                state = FALLING;
        }

        if (attacking) {
            state = ATTACK1;
        }else if(hit){
            state = Constant.PlayerConstants.HIT;
        }

        if (startAni != state)
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

        if(!inAir) {
            if ((!left && !right) || (right && left))
                return;
            if (!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;
        }
        float xSpeed = 0;

        if (left) {
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (inAir) {
            if (CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
                hitBox.y += airSpeed;
                airSpeed += GRAVITY;
            } else {
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
        state = IDLE;
        currentHealth = maxHealth;

        hitBox.x = x;
        hitBox.y = y;

        if(!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    public void updateXPos(float xSpeed) {
        if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData))
            hitBox.x += xSpeed;
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