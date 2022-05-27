package ua.roffus.yooki.ui;

import ua.roffus.yooki.states.GameState;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ua.roffus.yooki.utils.Constant.UI.StateButtons.*;

public class MenuButton extends Button {

    private final GameState state;
    private BufferedImage[] stateImages;
    private int index;
    private final int xOffsetCenter = BUTTON_WIDTH / 2;

    public MenuButton(int x, int y, int row, GameState state){
        super(x, y, row);
        this.state = state;
        loadStateImages();
        initBounds();
    }

    private void loadStateImages(){
       stateImages = new BufferedImage[2];
       BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for (int i = 0; i < stateImages.length; i++) {
            stateImages[i] = temp.getSubimage(i*BUTTON_WIDTH_DEFAULT, row*BUTTON_HEIGHT_DEFAULT, BUTTON_WIDTH_DEFAULT, BUTTON_HEIGHT_DEFAULT);
        }
    }

    public void initBounds(){
        bounds = new Rectangle(x - xOffsetCenter, y, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    public void draw(Graphics g){
        g.drawImage(stateImages[index], x - xOffsetCenter, y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
    }

    public void update(){
        index = 0;
        if(mousePressed)
            index = 1;
    }

    public void applyGameState(){
        GameState.state = state;
    }
}
