package ua.roffus.yooki.entities;

import ua.roffus.yooki.Game;

import java.awt.geom.Rectangle2D;

import static ua.roffus.yooki.utils.Constant.Directions.LEFT;
import static ua.roffus.yooki.utils.Constant.Directions.RIGHT;
import static ua.roffus.yooki.utils.Constant.EnemyConstants.*;

public class Scorpio extends Enemy {

    private Rectangle2D.Float attackBox;

    public Scorpio(float x, float y) {
        super(x, y, SCORPIO_WIDTH, SCORPIO_HEIGHT, SCORPIO);
        initHitBox(x, y, (int) (66 * Game.SCALE), (int) (32 * Game.SCALE));
        initAttackBox();
    }

    public void update(int[][] lvlData, Player player) {
        updateBehaviour(lvlData, player);
        updateAnimationTick();
        updateAttackBox();
    }

/*    public void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset){
        g.setColor(Color.BLACK);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y - yLvlOffset, (int) attackBox.width, (int) attackBox.height);
    }*/

    private void initAttackBox(){
        attackBox = new Rectangle2D.Float(x, y, (int) (56 * Game.SCALE), (int) (62 * Game.SCALE));
    }

    private void updateAttackBox(){
        if(walkDir == RIGHT)
            attackBox.x = hitBox.x + hitBox.width / 2;
        else if(walkDir == LEFT)
            attackBox.x = hitBox.x - hitBox.width / 2;
        attackBox.y = hitBox.y - (Game.SCALE * 50);
    }

    private void updateBehaviour(int[][] lvlData, Player player) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player))
                        turnTowardsPlayer(player);
                    if (isPlayerCloseForAttack(player))
                        newState(ATTACK);

                    move(lvlData);
                    break;
                case ATTACK:
                    if (aniIndex == 0)
                        attackChecked = false;
                    if (aniIndex == 2 && !attackChecked)
                        checkEnemyHit(attackBox, player);
                    break;
                case HIT:
                    break;
            }
        }
    }

    public int flipX(){
        if(walkDir == RIGHT)
            return width;
        else
            return 0;
    }

    public int flipW(){
        if(walkDir == RIGHT)
            return -1;
        else
            return 1;
    }
}
