package edu.chalmers.zombie.controller;

import edu.chalmers.zombie.adapter.ZWImageButton;
import edu.chalmers.zombie.adapter.ZWTexture;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.model.ScreenModel;
import edu.chalmers.zombie.utils.MenuBuilder;

/**
 * Menu Controller, to controll menus
 *
 * Created by Tobias on 15-05-28.
 */
public class MenuController {

    public MenuController(){}

    /**
     * Creates new instances for the menus
     */
    public static void initializeMenus(){

        ScreenModel screenModel = GameModel.getInstance().getScreenModel();

        screenModel.setGameOverStage(MenuBuilder.createGameOverMenu());
        screenModel.setSoundAndSettingStage(MenuBuilder.createSoundAndSettingsMenu());
        screenModel.setPauseStage(MenuBuilder.createPauseMenu());

    }

    /**
     * Checks whether sound is on or not and adjust image for audio on/off icon thereafter
     */
    public static void updateSoundButton(ZWImageButton soundButton){
        boolean soundOn = GameModel.getInstance().isSoundOn();
        ZWTexture newTexture;
        if (soundOn){
            newTexture = GameModel.getInstance().res.getTexture("audio-on");
        } else {
            newTexture = GameModel.getInstance().res.getTexture("audio-off");
        }
        soundButton.setImageUp(newTexture);
    }
}
