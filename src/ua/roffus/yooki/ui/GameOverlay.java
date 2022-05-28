package ua.roffus.yooki.ui;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.states.GameState;
import ua.roffus.yooki.states.PlayingState;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverlay {

    private final PlayingState playingState;

    public GameOverlay(PlayingState playingState){
        this.playingState = playingState;
    }

    public void draw(Graphics g, boolean isWin) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

        g.setColor(Color.WHITE);
        Font font = new Font("Monospaced", Font.PLAIN, 36);
        g.setFont(font);
        if(isWin)
            g.drawString("GAME WON!", 650, 245);
        else
            g.drawString("Game Over!", 650, 245);
        g.drawString("Press ESC to enter Main Menu!", 430, Game.WINDOW_HEIGHT / 2);
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            playingState.resetAll();
            GameState.state = GameState.MENU_STATE;
        }
    }
}
