package ua.roffus.yooki.handlers;

import ua.roffus.yooki.GamePanel;
import ua.roffus.yooki.states.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private final GamePanel gamePanel;

    public KeyHandler(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (GameState.state){
            case MENU_STATE:
                gamePanel.getGame().getMenuState().keyReleased(e);
                break;
            case PLAY_STATE:
                gamePanel.getGame().getPlayState().keyReleased(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (GameState.state){
            case MENU_STATE:
                gamePanel.getGame().getMenuState().keyPressed(e);
                break;
            case PLAY_STATE:
                gamePanel.getGame().getPlayState().keyPressed(e);
                break;
            default:
                break;
        }
    }

}
