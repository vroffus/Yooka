package ua.roffus.yooki;

import ua.roffus.yooki.handlers.KeyHandler;
import ua.roffus.yooki.handlers.MouseHandler;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final Game game;

    public GamePanel(Game game){
        this.game = game;
        setSize();
        setFocusable(true);
        addKeyListener(new KeyHandler(this));

        MouseHandler mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setSize(){
        Dimension dimension = new Dimension(23 * Game.TILES_SIZE, 12 * Game.TILES_SIZE);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        setPreferredSize(dimension);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        game.render(g);
    }

    public Game getGame(){
        return game;
    }

}
