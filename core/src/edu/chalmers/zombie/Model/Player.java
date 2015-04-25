package edu.chalmers.zombie.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.chalmers.zombie.utils.Constants;


import edu.chalmers.zombie.utils.Direction;

/**
 * Created by neda on 2015-03-31.
 * Modified by Tobias
 */
public class Player extends Entity implements CreatureInterface {

    private int killCount;
    private int lives;
    private int ammunition;
    private boolean isAttacked;
    private Body playerBody;
    private World world;
    private int width;
    private int height;
    private float x;
    private float y;
    private Vector2 force;
    //Sets the player's starting direction to east so that a thrown book will have a direction.
    private Direction direction = Direction.EAST;
    //Holds the players speed.
    private int speed = 7;


    protected Player(Sprite sprite, World world, float x, float y) {
        super(sprite, world, x, y);
        killCount = 0;
        ammunition = 100;
        lives = 100;
        force = new Vector2(0,0);
        getBody().setFixedRotation(true);   //Så att spelaren inte roterar
    }

    private int getKillCount() {

        return killCount;
    }

    private void incKillCount() {

        killCount = killCount + 1;
    }

    @Override
    public void move(Direction direction) {
        this.speed = 7;
        switch (direction){
            case NORTH:
                force.y = speed;
                break;
            case SOUTH:
                force.y = -speed;
                break;
            case WEST:
                force.x = -speed;
                break;
            case EAST:
                force.x = speed;
                break;
            default:
                break;
        }
        updateMovement();
    }

    /**
     * Updates Body rotation
     */
    private void updateRotation(){
        Body body = getBody();
        float rotation =  direction.getRotation();
        body.setTransform(body.getPosition(), rotation);
    }

    /**
     * @return Direction of player
     */
    public Direction getDirection(){
        return direction;
    }

    /**
     * Updates velocity, direction and rotation of body
     */
    private void updateMovement(){
        setBodyVelocity(force);
        updateDirecton();
        updateRotation();
    }

    /**
     * Sets Direction from variable force
     */
    private void updateDirecton(){
        if(force.y > 0){
            if (force.x > 0){
                direction = Direction.NORTH_EAST;
            } else if (force.x < 0){
                direction = Direction.NORTH_WEST;
            } else {
                direction = Direction.NORTH;
            }
        } else if (force.y < 0){
            if (force.x > 0){
                direction = Direction.SOUTH_EAST;
            } else if (force.x < 0){
                direction = Direction.SOUTH_WEST;
            } else {
                direction = Direction.SOUTH;
            }
        } else {
            if (force.x > 0){
                direction = Direction.EAST;
            } else if (force.x < 0){
                direction = Direction.WEST;
            }
        }
    }

    /**
     * Sets speed in x-axis to zero
     */
    public void stopX() {
        force.x = 0;
        if (force.y == 0) { this.speed = 0;}
        updateMovement();
    }

    /**
     * Sets speed in y-axis to zero
     */
    public void stopY(){
        force.y = 0;
        if (force.x == 0) { this.speed = 0;}
        updateMovement();
    }

    @Override
    protected void setBodyVelocity(Vector2 velocity){
        super.setBodyVelocity(velocity);
    }

    /**
     * Updates position of player
     */
    private void updatePosition(){
        setY((float)y);
        setX((float) x);
    }

    /**
     * Updates location of player
     */
    private void updateLocation(float deltaTime){
        setX(getX() + deltaTime * force.x);
        setY(getY() + deltaTime * force.y);
    }

    public void attack(Zombie zombie) {

        // TODO: fill in with attack of zombie instance
    }

    @Override
    public void KnockOut() {

        // TODO: game over
    }

    @Override
    public boolean hasBeenAttacked() {

        return isAttacked;
    }

    @Override
    public void setBody(Body body) {

       super.setBody(body);
    }

    @Override
    public Body getBody() {

        return super.getBody();
    }

    public void throwBook(){
        ammunition -= 1;
        Book book = new Book(direction, getX()-0.5f, getY()-0.5f, getWorld());
    }

    /**
     * Get player ammunition
     * @return ammunition
     */
    public int getAmmunition(){
        return ammunition;
    }

    /**
     * Get player lives
     * @return lives
     */
    public int getLives(){
        return lives;
    }



    public void setX(float x){
        this.x = x;
    }

     public void setY(float y){
         this.y = y;
     }

    public int getSpeed(){
        return this.speed;
    }

}
