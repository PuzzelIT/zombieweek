package edu.chalmers.zombie.model;

import edu.chalmers.zombie.adapter.Vector;
import edu.chalmers.zombie.adapter.ZWBody;
import edu.chalmers.zombie.adapter.ZWSprite;
import edu.chalmers.zombie.adapter.ZWWorld;
import edu.chalmers.zombie.model.Entity;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.model.Room;
import edu.chalmers.zombie.utils.Constants;

/**
 * Created by daniel on 4/21/2015.
 */
public class Book extends Entity {
    private Vector force;
    private float direction;
    private int speed, omega, damage;
    private Vector initialVelocity;
    private float width, height;
    private long timeCreated;
    private boolean onGround;

    public Book(float x, float y, Room room){
        this(0,x,y,room.getWorld(),new Vector(0,0));
        speed = 0;
        ZWBody body = new ZWBody();
        body.createBodyDef(true, x+0.5f, y+0.5f, 0, 0, true);
        short maskBits = Constants.COLLISION_OBSTACLE | Constants.COLLISION_PLAYER | Constants.COLLISION_DOOR | Constants.COLLISION_ACTOR_OBSTACLE;
        body.setFixtureDef(8f, 0, (width/2/ Constants.PIXELS_PER_METER), (height/2/Constants.PIXELS_PER_METER), Constants.COLLISION_PROJECTILE, maskBits, false);
        getBody().setUserData(this);
    }

    /**
     * Creates a book in front of the player. Since it uses vectors the speed of the book will depend on the player's speed.
     * @param d The player's direction
     * @param x The player's x position
     * @param y The player's y position
     * @param world In which world to create the physical representation of the book
     * @param initialVelocity  The speed which to add to the throwing speed
     */
    public Book(float d, float x, float y, ZWWorld world, Vector initialVelocity) {
        super(world);

        //Set variables
        this.direction=d;
        this.initialVelocity = initialVelocity;
        force = new Vector(1,1); //if 0,0 setLength wont work

        //Update position to be in front of player
        Vector position = getUpdatedPosition(x,y);

        ZWBody body = new ZWBody();
        body.createBodyDef(true,x+0.5f,y+0.5f,0,0,true);
        short categoryBits = Constants.COLLISION_PROJECTILE;
        short maskBits = Constants.COLLISION_OBSTACLE | Constants.COLLISION_ZOMBIE | Constants.COLLISION_DOOR;
        body.setFixtureDef(8f,0,1/3f,1/2f,categoryBits,maskBits,false);

        //Set body
        super.setBody(body);
        speed = 7;
        omega= 20;  //TODO finns i entity?
        damage = 40;
        onGround = false;

        setInMotion();

        //Load sprite
        ZWSprite sprite = new ZWSprite(GameModel.getInstance().res.getTexture("book"));
        sprite.setSize(width, height);
        super.setSprite(sprite);
        super.scaleSprite(1f / Constants.TILE_SIZE);

        getBody().setUserData(this);

        //Set system time created
        timeCreated = System.currentTimeMillis();


    }
    public boolean isOnGround(){
        return this.onGround;
    }

    public void setOnGround(boolean onGround){
        this.onGround = onGround;
    }

    /**
     * @return The damage the book makes
     */
    public int getDamage(){
        return damage;
    }

    /**
     * Set the book's damage
     */
        public void setDamage(int damage){
            this.damage = damage;
        }

    @Override
    public Vector getVelocity() {
        return getBody().getLinearVelocity();
    }

    /**
     * @return The book's speed
     * @throws NullPointerException if no body is found
     */
    public float getSpeed() throws NullPointerException{
        if(getBody()==null)
            throw new NullPointerException("getSpeed: no body found");
        return getBody().getLinearVelocity().len();
    }

    @Override
    protected void setBodyVelocity(Vector velocity){
        super.setBodyVelocity(velocity);
    }
    @Override
    protected void setAngularVelocity(float omega){
        super.setAngularVelocity(omega);
    }


    /**
     * Get updated position to be in front of the coordinates given.
     * @return The new position for the book
     */
    //TODO move to EntityController
    public Vector getUpdatedPosition(float x, float y){
        float distance = 0f;
        Vector position = new Vector(x,y);
        position.setY((float)(y + distance*Math.sin(direction + Constants.PI/2)));
        position.setX((float)(x + distance*Math.cos(direction + Constants.PI/2)));

        return position;

    }


    /**
     *  Starts moving the book using forces and angular rotation. The velocity of the book depends on if the player is moving and in which direction she's moving.
     */
    //TODO move to EntityController
    public void setInMotion(){
        force.setLength(speed);
        force.setAngleRad(direction + Constants.PI*1/2);
        force.add(initialVelocity); // Add the player velocity
        setBodyVelocity(force);
        setAngularVelocity(omega);
    }

    /**
     * @return  The book's mass
     * @throws  NullPointerException if no body found
     */
    public float getMass() throws NullPointerException{
        if(getBody()==null)
            throw new NullPointerException("getMass: no body found");
        return getBody().getMass();
    }

    /**
     * @return System time created
     */
    public long getTimeCreated() {
        return timeCreated;
    }
}