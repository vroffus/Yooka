package ua.roffus.yooki;

import ua.roffus.yooki.music.MusicManager;
import ua.roffus.yooki.states.GameState;
import ua.roffus.yooki.states.MenuState;
import ua.roffus.yooki.states.PlayingState;
import ua.roffus.yooki.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ua.roffus.yooki.utils.Constant.Sounds.*;

public class Game implements Runnable {

    private GameWindow gameWindow;
    private final GamePanel gamePanel;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    //STATES
    private PlayingState playingState;
    private MenuState menuState;

    //WINDOW
    public final static int TILES_DEFAULT_SIZE = 64;
    public final static float SCALE = 1f;
    public final static int TILES_IN_WIDTH = 46;
    public final static int TILES_IN_HEIGHT = 25;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
    public final static int WINDOW_WIDTH = 23 * TILES_SIZE;
    public static final int WINDOW_HEIGHT = 12 * TILES_SIZE;

    //BACKGROUND
    private BufferedImage background;

    //MUSIC
    private MusicManager musicManager;

    public Game() {
        loadBackground();
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    public void initClasses(){
        menuState = new MenuState(this);
        playingState = new PlayingState(this);
        musicManager = new MusicManager();
        playMusic(BACK);
    }

    public void loadBackground(){
        background = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND);
    }

    public void startGameLoop(){
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (GameState.state) {
            case MENU_STATE:
                menuState.update();
                break;
            case PLAY_STATE:
                playingState.update();
                break;
            case QUIT:
            default:
                System.exit(0);
                break;
        }
    }

    public void render(Graphics g){
        g.drawImage(background, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);

        switch (GameState.state){
            case MENU_STATE:
                menuState.draw(g);
                break;
            case PLAY_STATE:
                playingState.draw(g);
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        double timePerFrame = 100000000.0 / FPS_SET;
        double timePerUpdate = 100000000.0 / UPS_SET;

        long previousTime  = System.nanoTime();

        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while(true){
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if(deltaU >= 1){
                update();
                deltaU--;
            }

            if(deltaF >= 1){
                gamePanel.repaint();
                deltaF--;
            }

            if(System.currentTimeMillis() - lastCheck >= 100){
                lastCheck = System.currentTimeMillis();
                //System.out.println("FPS: " + frames + " UPS: " + updates);
            }
        }
    }

    public void windowFocusLost(){
        if(GameState.state == GameState.PLAY_STATE)
            playingState.getPlayer().resetDirBooleans();
    }

    public void playMusic(int sound){
        musicManager.setFile(sound);
        musicManager.play();
        musicManager.loop();
    }

    public void playSound(int sound){
        musicManager.setFile(sound);
        musicManager.play();
    }

    public MenuState getMenuState(){
        return menuState;
    }

    public PlayingState getPlayState(){
        return playingState;
    }

}
