package edu.chalmers.zombie;

import com.badlogic.gdx.Game;
import edu.chalmers.zombie.controller.MapController;
import edu.chalmers.zombie.view.GameScreen;
import edu.chalmers.zombie.view.MainMenuScreen;

public class ZombieWeek extends Game {

	public void create () {
        setScreen(new MainMenuScreen()); //Main menu screen
        //setScreen(new GameScreen()); //Game screen
	}

	@Override
	public void render () {
        super.render();
    }
}
