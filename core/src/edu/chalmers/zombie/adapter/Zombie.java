package edu.chalmers.zombie.adapter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import edu.chalmers.zombie.controller.MapController;
import edu.chalmers.zombie.controller.PhysicsController;
import edu.chalmers.zombie.model.CreatureInterface;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.utils.Constants;
import edu.chalmers.zombie.utils.PathAlgorithm;
import edu.chalmers.zombie.utils.ZombieType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by neda on 2015-03-31.
 */
public abstract class Zombie extends Entity implements CreatureInterface {

    private int speed;
    private float radius;
    private ZombieType type;
    private boolean isKnockedOut;
    private boolean isAttacked;
    private boolean isMoving;
    private Vector2 force;
    private Vector2 point;
    //private Sprite sprite;
    private Point position;
    private MapController mapController;
    private int hp;
    private Point nextPathTile;

    /**
     * Creates a new zombie
     * @param sprite    Which sprite to use
     * @param world     In which world to create it
     * @param x     The zombie's x coordinate
     * @param y     The zombie's y coordinate
     */
    public Zombie(Sprite sprite, World world, float x, float y){

        super(sprite.getTexture(), world, x, y);


        //Set still image frame, TODO: should get still frame from constructor
        GameModel.getInstance().res.loadTexture("zombie-still","core/assets/Images/zombie-still.png"); //TODO: shouldnt be done here
        Texture stillTexture = GameModel.getInstance().res.getTexture("zombie-still");
        TextureRegion[] stillFrame = TextureRegion.split(stillTexture,32,32)[0];
        getAnimator().addStillFrame(stillFrame[0]);

        //Set dead image frame
        GameModel.getInstance().res.loadTexture("zombie-dead","core/assets/Images/zombie-dead.png"); //TODO: shouldnt be done here
        Texture deadTexture = GameModel.getInstance().res.getTexture("zombie-dead");
        TextureRegion[] deadFrame = TextureRegion.split(deadTexture,32,32)[0];
        getAnimator().addStillFrame(deadFrame[0]);




        int width = Constants.TILE_SIZE;
        int height = Constants.TILE_SIZE;
        //Load body def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x+0.5f,y+0.5f);
        bodyDef.linearDamping = 20f;
        bodyDef.angularDamping = 20f;

        //Load shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/ Constants.PIXELS_PER_METER, height/2/Constants.PIXELS_PER_METER);

        //Load fixture def
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = (float)Math.pow(width/Constants.PIXELS_PER_METER, height/Constants.PIXELS_PER_METER);
        fixDef.restitution = 0;
        fixDef.friction = .8f;
        fixDef.filter.categoryBits = Constants.COLLISION_ZOMBIE;
        fixDef.filter.maskBits = Constants.COLLISION_OBSTACLE | Constants.COLLISION_ENTITY | Constants.COLLISION_WATER | Constants.COLLISION_SNEAK;

        //Set vectors
        force = new Vector2(0,0);
        point = new Vector2(0,0);

        position = new Point((int)x,(int)y);
        
        //Set body
        super.setBody(bodyDef, fixDef);
        super.getBody().setUserData(this);

        super.scaleSprite(1f / Constants.TILE_SIZE);

        mapController = new MapController();
        PhysicsController.setCollisionObjects();
        mapController.setPlayerBufferPosition(GameModel.getInstance().getRoom().getPlayerSpawn());

        super.getBody().setAngularDamping(10000);

        isKnockedOut = false;


    }


    /**
     * A method which sets the zombie's speed to a new speed.
     * @param newSpeed
     */
    public void setSpeed(int newSpeed) {

        speed = newSpeed;
    }

    public int getSpeed() {

        return speed;
    }

    /**
     * A method which decreases the zombie's hp (life measured in points).
     */
    public  void decHp() {

        hp = hp--;
    }

    public void setStartingHp(int hp) {

        this.hp = hp;
    }

    public ZombieType getType() {

        return type;
    }

    public void setType(ZombieType type) {

        this.type = type;
    }

    //public abstract Vector2 getVelocity();

    public abstract void attack();

    public void setZombiePosition(Point pos) {

        position = pos;
    }

    public Point getZombiePosition() {

        return position;
    }

    /**
     * A method which sets force affording to chosen speed.
     * @param speed int.
     */
    public void setForceY(int speed) {

        force.y = speed;
    }

    public void setForceX(int speed) {

        force.x = speed;
    }

    public void remove(Zombie zombie) {

        //TODO: remove zombie
    }

    public void setDetectionRadius(float radius) {

        this.radius = radius;
    }

    public float getDetectionRadius() {

        return radius;
    }

    public Vector2 getForce() {

        return force;
    }

    public Vector2 getPoint() {

        return point;
    }

    //@Override
    public void moveToPlayer(PathAlgorithm path) {


        //point = new Vector2(playerPosition.x, playerPosition.y);

        Point zombiePosition = getZombiePosition();
        Point playerPosition = mapController.getPlayerPosition();

        setSpeed(80);
        setDetectionRadius(10);


/*
        for (int i = 0; i < (MapController.getPath(zombiePosition, playerPosition).size() - 1); i++) {

        Vector2 direction = new Vector2(playerPosition.x - zombiePosition.x, playerPosition.y - zombiePosition.y);


        ArrayList<Point> pathToPlayer = MapController.getPath(zombiePosition, playerPosition);

        if(pathToPlayer!=null && super.getBody() != null) {

            zombiePosition = new Point(Math.round(super.getX()), Math.round(super.getY()));
        }

            if (playerPosition.x == zombiePosition.x && playerPosition.y == zombiePosition.y) {

                // TODO: attack
            } else if (playerPosition.y > zombiePosition.y && playerPosition.x == zombiePosition.x) {

                setForceY(speed);
                setForceX(0);
            } else if (playerPosition.x > zombiePosition.x && playerPosition.y == zombiePosition.y) {

                setForceY(0);
                setForceX(speed);
            } else if (playerPosition.x < zombiePosition.x && playerPosition.y == zombiePosition.y) {

                setForceY(0);
                setForceX(-speed);
            } else if (zombiePosition.y < playerPosition.y && playerPosition.x == zombiePosition.x) {

                setForceY(-speed);
                setForceX(0);
            } else if (playerPosition.y > zombiePosition.y && playerPosition.x > zombiePosition.x) {

                setForceY(speed);
                setForceX(speed);
            } else if (playerPosition.y < zombiePosition.y && playerPosition.x > zombiePosition.x) {

                setForceY(-speed);
                setForceX(speed);
            } else if (playerPosition.y > zombiePosition.y && playerPosition.x < zombiePosition.x) {

                setForceY(speed);
                setForceX(-speed);
            } else if (playerPosition.y < zombiePosition.y && playerPosition.x < zombiePosition.x) {

                setForceY(-speed);
                setForceX(-speed);
            } else {
                // TODO: some exception management
            }

            Circle zcircle = new Circle(zombiePosition.x, zombiePosition.y, radius);
            Circle pcircle = new Circle(playerPosition.x, playerPosition.y, radius);

            if (super.getBody() != null) {

                if (zcircle.overlaps(pcircle)) {

                    super.getBody().applyForce(force, point, !isKnockedOut);
                    isMoving = true;
                }
            }*/
    }

    /**
     * Changes the sprite to a sleeping one
     */
    @Override
    public void knockOut() {
        isKnockedOut = true;
        getAnimator().setCurrentStillFrame(1);
    }

    public boolean isKnockedOut(){
        return isKnockedOut;
    }
    @Override
    public boolean hasBeenAttacked() {

        return isAttacked;
    }

    /**
     * A method which returns whether a zombie is currently moving or not.
     * @return boolean isMoving; true if moving, false if not.
     */
    public boolean isMoving() {

        return isMoving;
    }

    public void setIsMoving(boolean isMoving) {

        this.isMoving = isMoving;
    }

    public MapController getThisMapController() {

        return mapController;
    }

    public abstract Zombie spawn(World world, int x, int y);

    /**
     * @return  The next tile the zombie should traverse to. Add 0.5 to x and y for the center of the tile
     */
    public Point getNextPathTile(){
        return this.nextPathTile;
    }

    /**
     * Set the next tile the zombie should traverse to
     * @param nextPathTile  The next tile
     */
    public void setNextPathTile(Point nextPathTile){
        this.nextPathTile = nextPathTile;
    }


}
