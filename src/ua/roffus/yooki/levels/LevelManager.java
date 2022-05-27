package ua.roffus.yooki.levels;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    //create array of textures from LEVEL_ATLAS
    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[72];
        for (int j = 0; j < 6; j++)
            for (int i = 0; i < 12; i++) {
                int index = j * 12 + i;
                levelSprite[index] = img.getSubimage(i * 64, j * 64, 64, 64);
            }
    }

    public void draw(Graphics g, int lvlOffsetX, int lvlOffsetY) {
        for (int j = 0; j < levelOne.getLvlData().length; j++)
            for (int i = 0; i < levelOne.getLvlData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], Game.TILES_SIZE * i - lvlOffsetX, Game.TILES_SIZE * j - lvlOffsetY, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
    }

    public void update() {

    }

    public Level getCurrentLevel(){
        return levelOne;
    }

}
