package ua.roffus.yooki.states;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.ui.Button;

import java.awt.event.MouseEvent;

public class State {

    protected Game game;

    public State(Game game){
        this.game = game;
    }

    public boolean isIn(MouseEvent e, Button button){
        return button.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame(){
        return game;
    }

}
