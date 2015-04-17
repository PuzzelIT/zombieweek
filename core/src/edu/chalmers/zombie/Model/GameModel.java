package edu.chalmers.zombie.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.chalmers.zombie.model.Player;
import edu.chalmers.zombie.utils.Direction;

import java.util.ArrayList;

/**
 * Created by Tobias on 15-04-02.
 */
public class GameModel {

    private Player player;
    private World world;
    private ArrayList<TiledMap> tiledMap;
    private TiledMapTileLayer metaLayer;
    private int currentLevel;

    public GameModel(){
        currentLevel = 0;
        world = new World(new Vector2(), true);
        player = new Player(new Sprite(new Texture("core/assets/player_professional_final_version.png")),world,1,1);
        tiledMap = new ArrayList<TiledMap>();
        tiledMap.add(new TmxMapLoader().load("core/assets/Map/Test_v2.tmx"));
    }

    public Player getPlayer(){
        return player;
    }

    public World getWorld(){return world; }

    public TiledMap getMap(int levelIndex){
        if(levelIndex >= tiledMap.size())
            throw new IndexOutOfBoundsException("GameModel: getMap index exceeds array size");
        currentLevel = levelIndex;
        return tiledMap.get(levelIndex);
    }

    public TiledMap getNextMap(){
        if(this.currentLevel ==this.tiledMap.size()-1)
            throw new IndexOutOfBoundsException("GameModel: already at last indexed map");
        currentLevel+=1;
        return this.tiledMap.get(currentLevel);
    }

    public TiledMap getPreviousMap(){
        if(this.currentLevel == 0)
            throw new IndexOutOfBoundsException("GameModel: already at first indexed map");
        currentLevel-=1;
        return this.tiledMap.get(currentLevel);

    }

    public void movePlayer(Direction direction){
        player.move(direction);
    }

}
