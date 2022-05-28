package ua.roffus.yooki.states;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.ui.MenuButton;
import ua.roffus.yooki.utils.Constant;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MenuState extends State implements Stateable {

    private final MenuButton[] STATE_BUTTONS = new MenuButton[2];
    private BufferedImage MENU_FRAME;

    public MenuState(Game game) {
        super(game);
        loadMenuFrame();
        loadButtons();
    }

    public void loadMenuFrame(){
        MENU_FRAME = LoadSave.GetSpriteAtlas(LoadSave.MENU_FRAME);
    }

    public void loadButtons(){
        //state buttons
        STATE_BUTTONS[0] = new MenuButton(700, 220, 0, GameState.PLAY_STATE);
        STATE_BUTTONS[1] = new MenuButton(700, 425, 1, GameState.QUIT);
    }

    public void  resetButtons() {
        //state
        for (MenuButton menuButton : STATE_BUTTONS)
            menuButton.setMousePressed(false);
    }

    @Override
    public void update() {
        //state
        for(MenuButton menuButton : STATE_BUTTONS)
            menuButton.update();
    }

    @Override
    public void draw(Graphics g) {
        //frame
        g.drawImage(MENU_FRAME, 420, 145, 580, 498, null);

        //state buttons
        for(MenuButton menuButton : STATE_BUTTONS)
            menuButton.draw(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton menuButton : STATE_BUTTONS) {
            if (isIn(e, menuButton)) {
                menuButton.setMousePressed(true);
                getGame().playSound(Constant.Sounds.CLICK);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton menuButton : STATE_BUTTONS) {
            if (isIn(e, menuButton)) {
                if (menuButton.isMousePressed())
                    menuButton.applyGameState();
                break;
            }
        }
        resetButtons();
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameState.state = GameState.PLAY_STATE;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
