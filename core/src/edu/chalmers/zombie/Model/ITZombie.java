package edu.chalmers.zombie.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import edu.chalmers.zombie.adapter.ZWVector;
import edu.chalmers.zombie.adapter.ZWWorld;
import edu.chalmers.zombie.utils.ZombieType;

import java.awt.*;

/**
 * Created by neda on 2015-05-20.
 */
public class ITZombie extends Zombie {

    private Sprite sprite;
    private ZWWorld world;
    private Point position;

    public ITZombie(ZWWorld world, int x, int y) {

        super(GameModel.getInstance().res.getTexture("zombie-it"),
                GameModel.getInstance().res.getTexture("zombie-it-still"),
                GameModel.getInstance().res.getTexture("zombie-it-dead"), world, x, y,32);
        setType(ZombieType.IT);
        this.world = world;
        this.sprite = sprite;
        position = new Point(x, y);
        setDetectionRadius(5);
        setStartingHp(100);
        setSpeed(30);
        setAngularSpeed(50);
        setDamage(65);
    }

    @Override
    public ZWVector getVelocity() {
        return null;
    }
}
