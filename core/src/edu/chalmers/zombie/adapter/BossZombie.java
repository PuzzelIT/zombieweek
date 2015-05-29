package edu.chalmers.zombie.adapter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.chalmers.zombie.model.Zombie;
import edu.chalmers.zombie.utils.ZombieType;

import java.awt.*;

/**
 * Created by neda on 2015-05-20.
 */
public class BossZombie extends Zombie {

    private Sprite sprite;
    //private int hp;
    private World world;
    private Point position;

    public BossZombie(World world, int x, int y) {

        super(null,null,null,world, x, y);
        setType(ZombieType.BOSS);
        this.world = world;
        this.sprite = sprite;
        position = new Point(x, y);
        setDetectionRadius(10);
        setStartingHp(200);
        setSpeed(100);
        setAngularSpeed(100);
        setDamage(100);
    }

    @Override
    public void attack() {

    }

    @Override
    public Zombie spawn(World world, int x, int y) {

        return new BossZombie(world, x, y);
    }

    @Override
    public Vector2 getVelocity() {
        return null;
    }
}
