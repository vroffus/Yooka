package ua.roffus.yooki.states;

import ua.roffus.yooki.Game;
import ua.roffus.yooki.entities.EnemyManager;
import ua.roffus.yooki.entities.Player;
import ua.roffus.yooki.levels.LevelManager;
import ua.roffus.yooki.ui.GameOverlay;
import ua.roffus.yooki.utils.Constant;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class PlayingState extends State implements Stateable{

    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private GameOverlay gameOverlay;
    private boolean gameOver, gameWin;

    //x border
    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.WINDOW_WIDTH);
    private int rightBorder = (int) (0.8 * Game.WINDOW_WIDTH);
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - 23;
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;
    //y border
    private int yLvlOffset;
    private int upBorder = (int) (0.45 * Game.WINDOW_HEIGHT);
    private int downBorder = (int) (0.45 * Game.WINDOW_HEIGHT);
    private int lvlTilesHigh = LoadSave.GetLevelData().length;
    private int maxTilesOffsetY = lvlTilesHigh - 12;
    private int maxLvlOffsetY = maxTilesOffsetY * Game.TILES_SIZE;

    public PlayingState(Game game) {
        super(game);
        initClasses();
    }

    public void initClasses(){
        levelManager = new LevelManager(game);
        player = new Player(50,107, (int) (200* Game.SCALE), (int) (110*Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        enemyManager = new EnemyManager(this);
        gameOverlay = new GameOverlay(this);
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitBox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

        int playerY = (int) player.getHitBox().y;
        int diffY = playerY - yLvlOffset;

        if (diffY > upBorder)
            yLvlOffset += diffY - upBorder;
        else if (diffY < downBorder)
            yLvlOffset += diffY - downBorder;

        if (yLvlOffset > maxLvlOffsetY)
            yLvlOffset = maxLvlOffsetY;
        else if (yLvlOffset < 0)
            yLvlOffset = 0;
    }

    public void resetAll(){
        gameOver = false;
        gameWin = false;
        player.resetAll();
        enemyManager.resetAll();
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        enemyManager.checkEnemyHit(attackBox);
    }

    @Override
    public void update() {
        if(!gameOver && !gameWin) {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
        }
    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g, xLvlOffset, yLvlOffset);
        enemyManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);

        if (gameOver)
            gameOverlay.draw(g, false);
        else if(gameWin)
            gameOverlay.draw(g, true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver || !gameWin)
            if (e.getButton() == MouseEvent.BUTTON1) {
                player.setAttacking(true);
            }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver || gameWin)
            gameOverlay.keyPressed(e);
        else
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    GameState.state = GameState.MENU_STATE;
                    getGame().playSound(Constant.Sounds.CLICK);
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver || !gameWin)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }
    }

    public Player getPlayer(){
        return player;
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public void setGameWin(boolean gameWin) {
        this.gameWin = gameWin;
    }
}
