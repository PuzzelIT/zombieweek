package edu.chalmers.zombie.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import edu.chalmers.zombie.adapter.ZWVector;
import edu.chalmers.zombie.adapter.ZWWorld;
import edu.chalmers.zombie.utils.ZombieType;

import java.awt.*;

/**
 * Created by neda on 2015-05-19.
 */
public class EconZombie extends Zombie {

    private Sprite sprite;
    private ZWWorld world;
    private Point position;

    public EconZombie(ZWWorld world, int x, int y) {

        super(GameModel.getInstance().res.getTexture("zombie"),
                GameModel.getInstance().res.getTexture("zombie-still"),
                GameModel.getInstance().res.getTexture("zombie-dead"), world, x, y,32);
        setType(ZombieType.ECON);
        this.world = world;
        this.sprite = sprite;
        position = new Point(x, y);
        setDetectionRadius(10);
        setStartingHp(50);
        setSpeed(200);
        setAngularSpeed(200);
        super.setAngularVelocity(10);
        setDamage(75);
    }

    @Override
    public ZWVector getVelocity() {
        return null;
    }
}
