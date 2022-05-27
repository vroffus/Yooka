package ua.roffus.yooki.entities;

import ua.roffus.yooki.states.PlayingState;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static ua.roffus.yooki.utils.Constant.EnemyConstants.*;

public class EnemyManager {

    private final PlayingState playingState;
    private BufferedImage[][] scorpioArr;

    private ArrayList<Scorpio> scorpions = new ArrayList<>();

    public EnemyManager(PlayingState playingState) {
        this.playingState = playingState;
        loadEnemyImages();
        addEnemies();
    }

    public void update(int[][] lvlData, Player player){
        for(Scorpio scorpio : scorpions){
            if(scorpio.isActive()) {
                scorpio.update(lvlData, player);
            }

            if(scorpio.getScore() == 100)
                playingState.setGameWin(true);
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset){
        drawScorpio(g, xLvlOffset, yLvlOffset);
    }

    public void drawScorpio(Graphics g, int xLvlOffset, int yLvlOffset){
        for(Scorpio scorpio : scorpions) {
            if (scorpio.isActive()) {
                g.drawImage(scorpioArr[scorpio.getEnemyState()][scorpio.getAniIndex()],
                        (int) (scorpio.getHitBox().x - SCORPIO_DRAWOFFSET_X) - xLvlOffset + scorpio.flipX(),
                        (int) (scorpio.getHitBox().y - SCORPIO_DRAWOFFSET_Y) - yLvlOffset,
                        SCORPIO_WIDTH * scorpio.flipW(), SCORPIO_HEIGHT, null);
                //scorpio.drawHitBox(g, xLvlOffset, yLvlOffset);
                //scorpio.drawAttackBox(g, xLvlOffset, yLvlOffset);
            }
        }
    }

    public void addEnemies() {
        scorpions = LoadSave.GetScorpio();
        //System.out.println("Added: " + scorpions.size() + " scorpios.");
    }

    public void loadEnemyImages() {
        scorpioArr = new BufferedImage[5][4];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SCORPIO);
        for(int i = 0; i < scorpioArr.length; i++){
            for (int j = 0; j < scorpioArr[i].length; j++) {
                scorpioArr[i][j] = temp.getSubimage(j * SCORPIO_WIDTH_DEFAULT, i * SCORPIO_HEIGHT_DEFAULT, SCORPIO_WIDTH_DEFAULT, SCORPIO_HEIGHT_DEFAULT);
            }
        }
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for (Scorpio scorpio : scorpions) {
            if (scorpio.isActive())
                if (attackBox.intersects(scorpio.getHitBox())) {
                    scorpio.hurt(10);
                    return;
                }
        }
    }

    public void resetAll(){
        for(Scorpio scorpio : scorpions)
            scorpio.resetEnemy();
    }
}
