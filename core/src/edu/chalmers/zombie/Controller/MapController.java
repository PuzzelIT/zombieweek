package edu.chalmers.zombie.controller;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.chalmers.zombie.adapter.*;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.utils.Constants;
import edu.chalmers.zombie.utils.PathAlgorithm;
import edu.chalmers.zombie.utils.TileRayTracing;
import edu.chalmers.zombie.view.GameScreen;

import java.awt.*;
import java.util.ArrayList;

/**
 * This controller class makes all the different calculations regarding the maps, rooms, worlds and objects in them.
 */
public class MapController {
    GameModel gameModel;

    /**
     * Constructor
     */
    public MapController(){
        this.gameModel = GameModel.getInstance();
    }

     /**
     * @return the current map from the model
     */
    public TiledMap getMap(){return gameModel.getRoom().getMap();}

    /**
     * @return the current world from the model
     */
    public World getWorld(){return gameModel.getRoom().getWorld();}

    /**
     * @return the current room from the model
     */
    public Room getRoom(){return gameModel.getRoom();}

    /**
     * Creates the different rooms and stores them in the model
     */

    public void initializeRooms(){ //TODO varifrån ska vi hämta dessa?
        gameModel.res.loadTiledMap("room0","core/assets/Map/Test_world_2_previous.tmx");
        gameModel.res.loadTiledMap("room1","core/assets/Map/Test_world_3.tmx");
        gameModel.res.loadTiledMap("room2","core/assets/Map/Test_world_2_next.tmx");

        gameModel.addRoom(new Room(gameModel.res.getTiledMap("room0"))); //0
        gameModel.addRoom(new Room(gameModel.res.getTiledMap("room1"))); //1
        gameModel.addRoom(new Room(gameModel.res.getTiledMap("room2"))); //2
    }

    /**
     * @param roomIndex the room index that will be accessed
     * @return The room specified by the index
     * @throws  IndexOutOfBoundsException if the user tries to access a room not in range
     */
    public Room getRoom(int roomIndex){
        int maxSize = gameModel.getRooms().size() -1;
        if(roomIndex<0 ||roomIndex > maxSize)
            throw new IndexOutOfBoundsException("Not a valid room index, must be between " + 0 + " and  " + maxSize);
        return gameModel.getRoom(roomIndex);
    }

    /**
     * @param roomIndex the room index which bottom layer will be accessed
     * @return The bottom layer specified by the index
     */
    public TiledMapImageLayer  getMapBottomLayer(int roomIndex){
        return getRoom(roomIndex).getBottomLayer();
    }

    /**
     * @param roomIndex the room index which top layer will be accessed
     * @return The top layer specified by the index
     */
    public TiledMapImageLayer  getMapTopLayer(int roomIndex){
        return getRoom(roomIndex).getTopLayer();
    }

    /**
     * @param roomIndex the room index which meta layer will be accessed
     * @return The meta layer specified by the index
     */
    public TiledMapTileLayer getMapMetaLayer(int roomIndex){
        return getRoom(roomIndex).getMetaLayer();
    }

    /**
     * @return The current bottom layer
     */
    public TiledMapImageLayer getMapBottomLayer(){
        return getMapBottomLayer(gameModel.getCurrentRoomIndex());
    }

    /**
     * @return The current top layer
     */
    public TiledMapImageLayer getMapTopLayer(){
        return getMapTopLayer(gameModel.getCurrentRoomIndex());
    }

    /**
     * @return The current meta layer
     */
    public TiledMapTileLayer getMapMetaLayer(){
        return getMapMetaLayer(gameModel.getCurrentRoomIndex());
    }

    /**
     * Loads the new room in the game model, creates bodies if needed and sets that the renderer needs to update the
     * world. It also updates where the player will be placed after the world step function (it's not possible to do it
     * at the same time, thus a temporary point is stored in the model). This is decided based on if the room that's
     * being loaded is before or after the one just shown to the user. It then creates the collision bodies for the new
     * room, if needed and sets a variable in the game model that the renderer needs to update the world in the next
     * world step.
     *
     * @param roomIndex the room to load
     * @throws  IndexOutOfBoundsException if the user tries to access a room not in range
     */
    public void loadRoom(int roomIndex) {
        int maxSize = gameModel.getRooms().size() - 1;
        if (roomIndex < 0 || roomIndex > maxSize){
        throw new IndexOutOfBoundsException("Not a valid room index, must be between " + 0 + " and  " + maxSize);
        }
        gameModel.setWorldNeedsUpdate(true);
        gameModel.getPlayer().setSneakTilesTouching(0);
        gameModel.getPlayer().setWaterTilesTouching(0);
        gameModel.getPlayer().setHidden(false);
        //TODO sluta simma, sluta sneaka
        EntityController.setFriction(gameModel.getPlayer(), Constants.PLAYER_FRICTION_DEFAULT, Constants.PLAYER_FRICTION_DEFAULT);
        int oldRoomIndex = gameModel.getCurrentRoomIndex();
        gameModel.setCurrentRoomIndex(roomIndex);
        GameModel.getInstance().addEntityToRemove(GameModel.getInstance().getPlayer());
        for(Book book : gameModel.getBooks()){
            book.markForRemoval();
            gameModel.addEntityToRemove(book);
        }
        gameModel.clearBookList();
        PhysicsController.traverseRoomIfNeeded(getRoom());
        if(oldRoomIndex>roomIndex){
            if(getRoom().getPlayerReturn() == null)        //If the spawn and return points are the same point in the map file
                setPlayerBufferPosition(getRoom().getPlayerSpawn());
            else
                setPlayerBufferPosition(getRoom().getPlayerReturn());
        }
        else
            setPlayerBufferPosition(getRoom().getPlayerSpawn());

    }

    /**
     * @return true if the world needs to be updated, false if not
     */
    public boolean worldNeedsUpdate(){
        return gameModel.worldNeedsUpdate();
    }

    /**
     * If the world needs to update the next step, this variable is set in the model
     * @param bool true if the world needs to be updated, false if not
     */
    public void setWorldNeedsUpdate(boolean bool){
        gameModel.setWorldNeedsUpdate(bool);
    }

    /**
     * @return The player's current (rounded) position as a point
     */
    public Point getPlayerPosition(){
        return new Point(Math.round(gameModel.getPlayer().getX()), Math.round(gameModel.getPlayer().getY()));
    }

    /**
     * Updates the player's position
     * @param point Where the player will be placed
     */
    public void updatePlayerPosition(Point point){
               gameModel.getPlayer().setPosition(point);
    }

    /**
     * Sets where the player should be when the world step is done
     * @param point Where the player will be placed after the step
     */
    public void setPlayerBufferPosition(Point point){
        gameModel.setPlayerBufferPosition(point);
    }

    /**
     * @return where the player will be placed after the step
     */
    public Point getPlayerBufferPosition(){
        return gameModel.getPlayerBufferPosition();
    }

    /**
     * Checks if the path is obstructed by a wall using a modified version of Bresenham's line algorithm while taking into account how the maps are constructed.
     * @param position  The original position
     * @param room The map
     * @param distance  The distance in which to check
     * @param angle the angle to check, in radians (0 is east, pi is west)
     * @return  true if the path is obstructed, false otherwise
     */
    public static boolean pathObstructed(Vector2 position, Room room, float distance, float angle){
    /* Input checks */
        if(position == null ||room== null)
            throw new NullPointerException("The input mustn't be null");
        if(position.x < 0 || position.y < 0)
            throw new IndexOutOfBoundsException("The position must be positive");   //TODO kanske inte behövs i och med att det checkas i relevant metod
        if(position.x > room.getTiledWidth() || position.y >room.getTiledHeight())
            throw new IndexOutOfBoundsException("The position must be within meta layer bounds");//TODO kanske inte behövs i och med att det checkas i relevant metod
        if(distance < 0)
            throw new IndexOutOfBoundsException("The distance must be positive");
        Vector2 endPosition = position.add(distance * (float) Math.cos(angle), distance * (float) Math.sin(angle));
        /* Extract and convert the positions to map coordinates */
        int x_origin = Math.round(position.x - position.x % 1 - 0.5f);
        int y_origin = Math.round(position.y - position.y % 1 - 0.5f);
        int x_end = Math.round(endPosition.x - endPosition.x % 1 - 0.5f);
        int y_end = Math.round(endPosition.y - endPosition.y % 1 - 0.5f);

        return TileRayTracing.pathObstructed(new Point(x_origin, y_origin), new Point(x_end, y_end), room.getCollisionTileGrid(), Constants.COLLISION_OBSTACLE);
    }

    /**
     * Checks if there's a wall tile at the given position
     * @param position  the position to check at
     * @param metaLayer the map's meta layer
     * @return true if there's a wall there, false otherwise
     * @throws NullPointerException if the position or meta layer are empty
     */
    public static boolean wallAt(Point position, TiledMapTileLayer metaLayer){
        if(position == null  ||metaLayer == null)
            throw new NullPointerException("The input mustn't be null");

        int x = Math.round(position.x - position.x%1);
        int y = Math.round(position.y - position.y%1);

        return wallAt(x, y, metaLayer);
    }

    /**
     * Checks if there is a collision_all tile at a given position
     * @param x The x coordinate
     * @param y The  coordinate
     * @param metaLayer The map's meta layer
     * @return  true if there's a collision tile at that position, false otherwise
     */
    public static boolean wallAt(int x, int y, TiledMapTileLayer metaLayer){
        if(metaLayer == null)
            throw new NullPointerException("The meta layer mustn't be null");
        if(x<0 ||y < 0 || x > metaLayer.getWidth()-1 ||y > metaLayer.getHeight()-1 )
            throw new IndexOutOfBoundsException("The input coordinates must be withing the meta layer bounds");
        return (metaLayer.getCell(x,y) != null && metaLayer.getCell(x,y) != null && metaLayer.getCell(x,y).getTile().getProperties().get(Constants.COLLISION_PROPERTY_ALL) != null);
    }

    /**
     * @return The current room's zombie navigation mesh
     */
    public short[][] getCollisionTileGrid(){
        return getRoom().getCollisionTileGrid();

    }


    public void printCollisionTileGrid(){       //TODO debugmetod
        System.out.println("\nRoom nr " + (gameModel.getCurrentRoomIndex()+1) +": printing collision detection tiles.");
        System.out.println("Width: " + getRoom().getCollisionTileGrid().length    + " Height: " + getRoom().getCollisionTileGrid()[0].length);
        for(int y = getRoom().getCollisionTileGrid()[0].length-1; y >= 0; y--){
            for(int x = 0; x < getRoom().getCollisionTileGrid().length; x++){
                if(getRoom().getCollisionTileGrid()[x][y] == 0)
                    System.out.print("\t ");
                else
                    System.out.print("\t" + getRoom().getCollisionTileGrid()[x][y]);
            }
            System.out.println("");
        }
    }


    public void printPath(Room room, Point start, Point end) throws NullPointerException, IndexOutOfBoundsException{  //TODO debugmetod
            ArrayList<Point> path = getPath(room, start, end);
            System.out.println("\nRoom nr " + (gameModel.getCurrentRoomIndex()+1) +
                    ": printing collision detection tiles and path from " + start.x + ", " + start.y + " to " + end.x + ", " + end.y + ".");
        if(path != null) {
            System.out.println("Width: " + getRoom().getCollisionTileGrid().length + " Height: " + getRoom().getCollisionTileGrid()[0].length);
            for (int y = getRoom().getCollisionTileGrid()[0].length - 1; y >= 0; y--) {
                for (int x = 0; x < getRoom().getCollisionTileGrid().length; x++) {
                    Point point = new Point(x, y);
                    if (getRoom().getCollisionTileGrid()[x][y] == 0 && !path.contains(point)) {
                        System.out.print("\t ");
                    } else if (path.contains(point)) {
                        if (point.equals(start)) {
                            System.out.print("\t o");
                        } else if(point.equals(end)) {
                            System.out.print("\t x");
                        } else {
                            System.out.print("\t -");
                        }
                    } else {
                        System.out.print("\t" + getRoom().getCollisionTileGrid()[x][y]);
                    }
                }
                System.out.println("");
            }
            System.out.print("Points in path:\n");
            for (Point p : path) {
                System.out.println(p.x + ", " + p.y);
            }
            System.out.print("Number of points: " + path.size());
        }
        else System.out.print("No path found");

    }

    /**
     * Returns the shortest path between two points. Takes obstacles into account. Since the algorithm is layout a bit different from the map tiles, 1 must be subtracted from the x and y values.
     * @param room  The specific room
     * @param start The start point
     * @param end   The end point
     * @return  The shortest path between the two points in the room
     * @throws NullPointerException if either parameter is null or of the path algorithm or navigational mesh haven't been initialized
     * @throws IndexOutOfBoundsException if any point is out of bounds
     */
    public static ArrayList<Point> getPath(Room room, Point start, Point end) throws NullPointerException, IndexOutOfBoundsException{
        if(room==null){
            throw new NullPointerException("the room pointer was null");
        }
        if(room.getCollisionTileGrid()==null){
            throw new NullPointerException("getPath: the room's navigation mesh was null, can't create path without one");
        }
        if(start==null ||end == null){
            throw new NullPointerException("getPath: The points mustn't be null");
        }
        if(start.x < 0 || start.x >= room.getCollisionTileGrid().length || start.y < 0 ||start.y >= room.getCollisionTileGrid()[0].length)
            throw new IndexOutOfBoundsException("getPath: start point out of bounds");
        if(end.x < 0 || end.x >= room.getCollisionTileGrid().length || end.y < 0 ||end.y >= room.getCollisionTileGrid()[0].length)
            throw new IndexOutOfBoundsException("getPath: end point out of bounds");
        return PathAlgorithm.getPath(start, end, room.getCollisionTileGrid(), Constants.COLLISION_ZOMBIE);
    }

    /**
     *
     * Return the shortest path between two points in the current room
     * @param start The start point
     * @param end   The end point
     * @return  The shortest path between the two points in the current room
     * @throws NullPointerException if either parameter is null or of the path algorithm or navigational mesh haven't been initialized
     *      * @throws IndexOutOfBoundsException if any point is out of bounds
     */
    public static ArrayList<Point> getPath(Point start, Point end) throws NullPointerException, IndexOutOfBoundsException {
        MapController controller = new MapController(); //TODO gör de andra statiska
        return getPath(controller.getRoom(), start, end);
    }

    /**
     * If the room has changed the map and renderer need to change as well
     */
    public void updateRoomIfNeeded(GameScreen screen) {
        if (worldNeedsUpdate()) {
            World currentWorld = getWorld();
            Player player = GameModel.getInstance().getPlayer();

            /* ------ Update physics ------ */
            PhysicsController.traverseRoomIfNeeded(getRoom());

            /* ------ Update player ------ */
            if(player == null){
               player = EntityController.createNewPlayer();
            }
            if(player.getBody() == null){
                player.createDefaultBody(currentWorld, getPlayerBufferPosition());
            }

            /* ------ Update screen ------ */
            screen.setDisplayedWorld(currentWorld);
            screen.setMapRenderer(new OrthogonalTiledMapRenderer(getMap(), 1f/(float)Constants.TILE_SIZE));

            /* ------ Save game ------ */
            SaveLoadController saveLoadController = new SaveLoadController();
            saveLoadController.saveGame();

            /* ------ Mark as updated ------ */
            setWorldNeedsUpdate(false);

            //TODO debug
            printCollisionTileGrid();
        }
    }

}
