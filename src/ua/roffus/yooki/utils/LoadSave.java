package ua.roffus.yooki.utils;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.entities.Scorpio;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadSave {

    //map
    public static final String LEVEL_ATLAS = "map/tileset.png";
    public static final String LEVEL_ONE_DATA = "map/level_one.png";
    //ui
    public static final String MENU_FRAME = "ui/menu_game.png";
    public static final String MENU_BUTTONS = "ui/big_buttons.png";
    public static final String STATUS_BAR = "ui/health_power_bar.png";
    //background
    public static final String BACKGROUND = "background.png";
    //enemies
    public static final String SCORPIO = "enemies/scorpio.png";
    //music
    public static final String BACKGROUND_MUSIC = "music/back.wav";
    public static final String ATTACK_MUSIC = "music/attack.wav";
    public static final String HIT_MUSIC = "music/hit.wav";
    public static final String GAMEOVER_MUSIC = "music/gameover.wav";
    public static final String CLICK_MUSIC = "music/click.wav";
    public static final String JUMP_MUSIC = "music/jump.wav";



    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static ArrayList<Scorpio> GetScorpio(){
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        ArrayList<Scorpio> list = new ArrayList<>();

        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getGreen();
                if (value == Constant.EnemyConstants.SCORPIO)
                    list.add(new Scorpio(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
            }
        System.out.println("List size: " + list.size());
        return list;
    }

    //get value oed color from each pixel in LEVEL_ONE_DATA
    public static int[][] GetLevelData() {
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];

        for (int j = 0; j < img.getHeight(); j++)
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();
                if (value >= 72)
                    value = 0;
                lvlData[j][i] = value;
            }
        return lvlData;

    }
}
