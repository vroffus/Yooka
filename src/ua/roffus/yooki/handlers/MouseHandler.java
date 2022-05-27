package ua.roffus.yooki.handlers;

import ua.roffus.yooki.GamePanel;
import ua.roffus.yooki.states.GameState;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private final GamePanel gamePanel;

    public MouseHandler(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (GameState.state == GameState.PLAY_STATE) {
            gamePanel.getGame().getPlayState().mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (GameState.state){
            case MENU_STATE:
                gamePanel.getGame().getMenuState().mousePressed(e);
                break;
            case PLAY_STATE:
                gamePanel.getGame().getPlayState().mousePressed(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (GameState.state){
            case MENU_STATE:
                gamePanel.getGame().getMenuState().mouseReleased(e);
                break;
            case PLAY_STATE:
                gamePanel.getGame().getPlayState().mouseReleased(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
