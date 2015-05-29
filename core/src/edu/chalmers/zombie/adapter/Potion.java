package edu.chalmers.zombie.adapter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import edu.chalmers.zombie.utils.Constants;
import edu.chalmers.zombie.utils.PotionType;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by neda on 2015-05-19.
 * Modified by Neda
 */
public class Potion extends Entity {

    private Sprite sprite;
    private Point position;
    private World world;
    private boolean hasBeenRemoved;
    private Vector2 velocity;
    private PotionType type;

    public Potion(Sprite sprite, World world, int x, int y) {

        super(sprite, world, x, y);
        this.world = world;
        this.sprite = sprite;
        position = new Point(x, y);
        randomizePotion();
    }

    public Potion(PotionType potionType, Sprite sprite, World world, int x, int y) {

        super(sprite, world, x, y);
        velocity = new Vector2(0,0);
        this.sprite = sprite;
        this.world = world;
        type = potionType;
        position = new Point(x, y);

        ZWBody potionBody = new ZWBody();
        short categoryBits = Constants.COLLISION_POTION;
        short maskBits = Constants.COLLISION_PLAYER;
        potionBody.createBodyDef(true, x, y, 0, 0);
        potionBody.setFixtureDef(0, 0, 0.5f, 0.5f, categoryBits, maskBits, true);
        super.setBody(potionBody.getBodyDef(),potionBody.getFixtureDef());
        super.scaleSprite(0.5f / Constants.TILE_SIZE);
        super.getBody().setUserData(this);

        switch (potionType) {
            case HEALTH:

                break;
            case SPEED:

                break;
            case IMMUNITY:

                break;
            case SUPER_STRENGTH:

                break;
            default:

                break;
        }

        hasBeenRemoved = false;

    }

    @Override
    public Vector2 getVelocity() {

        return velocity;
    }

    public Potion spawn(PotionType type, World world, int x, int y) {

        return new Potion(type, sprite, world, x, y);
    }

    public void setHasBeenRemoved(boolean hasBeenRemoved) {

        this.hasBeenRemoved = hasBeenRemoved;
    }

    public void removeIfNecessary() {

        if (hasBeenRemoved) {
            this.removeBody();
        }
    }

    /**
     * A method which returns a potion at random.
     */
    public void randomizePotion() {

        ArrayList<PotionType> types = new ArrayList<PotionType>();

        types.add(PotionType.HEALTH);
        types.add(PotionType.SPEED);
        types.add(PotionType.SUPER_STRENGTH);
        types.add(PotionType.IMMUNITY);

        System.out.println(((int)Math.random()*11) + " "); // <-- For debugging

        int i = (((int)Math.random()*11) / 4);

        PotionType pt = types.get(i);

        new Potion(pt, sprite, world, position.x, position.y);
    }

    /**
     * A method which returns the type of potion in question.
     * @return PotionType type (enum).
     */
    public PotionType getType() {
        return type;
    }
}
