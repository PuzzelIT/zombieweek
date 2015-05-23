package edu.chalmers.zombie.controller;

import com.badlogic.gdx.audio.Sound;
import edu.chalmers.zombie.model.GameModel;

/**
 * Controller for audio
 *
 * Created by Tobias on 15-05-21.
 */
public class AudioController {


    /**
     * Play sound
     * @param sound The sound that should be played
     */
    public static void playSound(Sound sound){
        GameModel gameModel = GameModel.getInstance();
        if (gameModel.isSoundOn()){
            sound.play();
        }
    }

    /**
     * Toggle sound
     */
    public static void toggleSound(){
        GameModel gameModel = GameModel.getInstance();
        if (gameModel.isSoundOn()){
            GameModel.getInstance().setSoundOn(false);
        } else {
            GameModel.getInstance().setSoundOn(true);
        }
    }
}