package ua.roffus.yooki.music;

import ua.roffus.yooki.utils.Constant;
import ua.roffus.yooki.utils.LoadSave;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class MusicManager {

    private Clip clip;

    private final URL[] sounds = new URL[6];

    public MusicManager() {
        sounds[0] = getClass().getResource("/" + LoadSave.BACKGROUND_MUSIC);
        sounds[1] = getClass().getResource("/" + LoadSave.ATTACK_MUSIC);
        sounds[2] = getClass().getResource("/" + LoadSave.HIT_MUSIC);
        sounds[3] = getClass().getResource("/" + LoadSave.GAMEOVER_MUSIC);
        sounds[4] = getClass().getResource("/" + LoadSave.CLICK_MUSIC);
        sounds[5] = getClass().getResource("/" + LoadSave.JUMP_MUSIC);
    }

    public void setFile(int sound){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sounds[sound]);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl;
            switch (sound){
                case Constant.Sounds.BACK:
                case Constant.Sounds.ATTACK:
                case Constant.Sounds.HIT:
                case Constant.Sounds.JUMP:
                    gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-20.0f);
                    break;
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        clip.start();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(){
        clip.stop();
    }

}
