package edu.chalmers.zombie.adapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.utils.Constants;
import javafx.scene.Camera;

/**
 * Created by daniel on 5/28/2015.
 */
public class Grenade extends Entity{
    private float targetX;
    private float targetY;
    private Vector2 originalPlayerPosition;
    private float width;
    private float height;
    private Texture grenadeTexture = new Texture("core/assets/grenadeBook.png");
    private Vector2 force;
    private float speed = 7;
    private float direction;
    private OrthographicCamera camera = new OrthographicCamera();
    public Grenade(int targetX, int targetY, float x, float y, World world){
        super(world);
        GameModel gameModel = GameModel.getInstance();
        Player player = gameModel.getPlayer();
        originalPlayerPosition = new Vector2(player.getX(), player.getY());
        height = Constants.TILE_SIZE/2f;
        width = Constants.TILE_SIZE/3f;
        force = new Vector2(1,1);

        this.targetX = targetX;
        this.targetY = targetY;

        Sprite grenadeSprite = new Sprite(grenadeTexture);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x+0.5f, y+0.5f);
        bodyDef.bullet = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/ Constants.PIXELS_PER_METER, height/2/Constants.PIXELS_PER_METER);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = (float)Math.pow(width/Constants.PIXELS_PER_METER, height/Constants.PIXELS_PER_METER);
        fixDef.restitution = 0;
        fixDef.friction = 0f;
        //fixDef.filter.categoryBits = Constants.COLLISION_PROJECTILE;
        fixDef.filter.maskBits = Constants.COLLISION_OBSTACLE | Constants.COLLISION_ZOMBIE;
        super.setBody(bodyDef, fixDef);
        super.setSprite(grenadeSprite);
        super.scaleSprite(1f / Constants.TILE_SIZE);
        getBody().setUserData(this);
        calculateDirection();
        setInMotion();
        unproject();

    }
    private void calculateDirection(){
        float deltaX = Gdx.graphics.getWidth() / 2 - targetX;
        float deltaY = Gdx.graphics.getHeight() / 2 - targetY;
        direction = (float) Math.atan2((double) deltaY, (double) deltaX) + Constants.PI / 2;
    }
    private void setInMotion(){
        force.setLength(speed);
        force.setAngleRad(direction + Constants.PI*1/2);
        setBodyVelocity(force);
        setAngularVelocity(0);
    }
    private void unproject(){
        this.targetX = camera.unproject(new Vector3(targetX,0,0)).x;
        this.targetY = camera.unproject(new Vector3(0,targetY,0)).y;
        float width = Gdx.graphics.getWidth()/Constants.TILE_SIZE;
        float height = Gdx.graphics.getHeight()/Constants.TILE_SIZE;
        this.targetX = originalPlayerPosition.x + this.targetX*width/2;
        this.targetY = originalPlayerPosition.y - this.targetY*height/2;
    }
    @Override
    protected void setBodyVelocity(Vector2 velocity){
        super.setBodyVelocity(velocity);
    }
    public Vector2 getVelocity(){
        return getBody().getLinearVelocity();
    }

    public void stopIfNeeded(){
        if ((targetX - 0.1 < this.getX() && this.getX() < targetX + 0.1) &&
                (targetY - 0.1 < this.getY() && this.getY() < targetY + 0.1)){
            stop();
        }
    }

    private void stop(){
        force.setLength(0);
        setBodyVelocity(force);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        stopIfNeeded();
    }

    public void explode(){

    }
}
