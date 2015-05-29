package edu.chalmers.zombie.controller;

import edu.chalmers.zombie.adapter.*;
import edu.chalmers.zombie.model.CollisionObject;
import edu.chalmers.zombie.model.GameModel;
import edu.chalmers.zombie.utils.Constants;

/** A custom contact listener. It registers the different contacts and forward the commands to the other controllers.
 * Created by Erik on 2015-04-18.
 */
public class ContactController {
    private GameModel gameModel;
    private MapController mapController;
    private EntityController entityController;

    /**
     * Instantiates the contact listener.
     */
    public ContactController(){
        this.gameModel = GameModel.getInstance();
        this.mapController = new MapController();
        this.entityController = new EntityController();
    }

    /**
     * This method decides what to do when two objects start colliding.
     *
     * There are several different contacts we are interested in. The first is if a book has struck a zombie. The second is
     * if the player collides with a book, which then, if appropriate, should be picked up. The third is if the player has
     * made contact with a door.
     *
     *  Regarding the book (and other entities), a reference the Book object is stored as user data in its body.
     *  Therefore, if we are certain the object is a book, we can call on the user data to retrieve it.
     *
     *  If the book has hit the player the book is marked for removal (the body can't be removed during the world step,
     *  which is why it's done this way) and the model is notified of this. The player ammunition is increased. If the
     *  book has hit a zombie the book is again marked for removal and the Zombie is marked as knocked out. Both entities
     *  are added to the entities to be removed list. If the book hits the wall friction is applied, to give the illusion
     *  that the book is now on the ground
     *
     * The next checks are too see if the player makes contact with a door. Due to the way Box2D is made we have to check for
     * both cases - either the player makes contact with a door or a door makes contact with the Player. This might seem to
     * be the same thing, but it's handled differently in Box2d. If the player has indeeed made contact with a door the
     * appropriate level is loaded via the map controller.
     *
     * @param fixtureA The first fixture
     * @param fixtureB  The second fixture
     */

    public void beginContact (ZWFixture fixtureA, ZWFixture fixtureB) {
        gameModel.clearEntitiesToRemove();
        switch(fixtureB.getCategoryBits()) {
            case Constants.COLLISION_PROJECTILE://Check if the fixture is a projectile, e.g. a book
                    Book b = (Book) fixtureB.getBodyUserData();
                    //Retrieve the book
                    switch(fixtureA.getCategoryBits()){
                        case Constants.COLLISION_PLAYER:
                            Player p = gameModel.getPlayer();
                            EntityController.pickUp(p, b);
                            break;
                        case Constants.COLLISION_ZOMBIE:
                            Zombie z = (Zombie) fixtureA.getBodyUserData();
                            EntityController.applyHit(z, b);
                            break;
                        case Constants.COLLISION_OBSTACLE:
                            EntityController.hitGround(b);
                            break;
                        case Constants.COLLISION_WATER:
                            //TODO plums
                            EntityController.remove(b);
                            break;
                    }


                                                            break;
            case (Constants.COLLISION_WATER):
                switch(fixtureA.getCategoryBits()){
                    case Constants.COLLISION_PLAYER:
                        Player player = gameModel.getPlayer();
                        EntityController.increaseWaterTilesTouching(player);
                        break;
                }
                break;

            case (Constants.COLLISION_SNEAK):
                switch(fixtureA.getCategoryBits()){
                    case Constants.COLLISION_PLAYER:
                        Player player = gameModel.getPlayer();
                        EntityController.increaseSneakTilesTouching(player);
                        break;
                }
                break;

            case (Constants.COLLISION_ZOMBIE):
                switch(fixtureA.getCategoryBits()) {
                    case Constants.COLLISION_PLAYER:
                        Player player = gameModel.getPlayer();
                        Zombie zombie = (Zombie)fixtureA.getBodyUserData();
                        ZombieController.attack(zombie, player);
                        break;
                    case Constants.COLLISION_PROJECTILE:
                        Book book = (Book) fixtureB.getBodyUserData();
                        Zombie z = (Zombie) fixtureA.getBodyUserData();
                        EntityController.applyHit(z, book);

                }

            case (Constants.COLLISION_PLAYER):
                Player player = gameModel.getPlayer();
                switch(fixtureA.getCategoryBits()){
                    case Constants.COLLISION_SNEAK:
                        EntityController.increaseSneakTilesTouching(player);
                        break;
                    case Constants.COLLISION_WATER:
                        EntityController.increaseWaterTilesTouching(player);
                        break;
                    case Constants.COLLISION_ZOMBIE:
                        Zombie zombie = (Zombie) fixtureA.getBodyUserData();
                        ZombieController.attack(zombie, player);
                        break;
                    case Constants.COLLISION_POTION:
                        Potion potion =  (Potion) fixtureA.getBodyUserData();
                        PlayerController.pickUpPotion(player,potion);
                        break;
                }
                break;
            case (Constants.COLLISION_POTION):
                Potion potion =  (Potion) fixtureB.getBodyUserData();
                switch(fixtureA.getCategoryBits()) {
                    case Constants.COLLISION_PLAYER:
                        PlayerController.pickUpPotion(gameModel.getPlayer(), potion);
                        break;
                }
        }
    }

    /**
     * Called when two objects stop colliding.
     * @param fixtureA The first fixture
     * @param fixtureB  The second fixture
     */
    public void endContact (ZWFixture fixtureA, ZWFixture fixtureB){
        switch(fixtureB.getCategoryBits()) {
            case (Constants.COLLISION_WATER):
                switch (fixtureA.getCategoryBits()) {        //Not made as an if-statement if more collision alternatives are to be added
                    case Constants.COLLISION_PLAYER:
                        Player player = gameModel.getPlayer();
                        EntityController.decreaseWaterTilesTouching(player);
                        break;
                }
            case (Constants.COLLISION_SNEAK):
                switch (fixtureA.getCategoryBits()) {        //Not made as an if-statement if more collision alternatives are to be added
                    case Constants.COLLISION_PLAYER:
                        Player player = gameModel.getPlayer();
                        EntityController.decreaseSneakTilesTouching(player);
                        break;
                }

            case (Constants.COLLISION_PLAYER):
                Player player = gameModel.getPlayer();
                switch(fixtureA.getCategoryBits()){
                    case Constants.COLLISION_SNEAK:
                        EntityController.decreaseSneakTilesTouching(player);
                        break;
                    case Constants.COLLISION_WATER:
                       EntityController.decreaseWaterTilesTouching(player);
                        break;
                    case Constants.COLLISION_PROJECTILE:
                        Book b = (Book) fixtureA.getBodyUserData();
                        Player p = gameModel.getPlayer();
                        EntityController.pickUp(p, b);
                        break;
                }
        }
     }

    /**
     * Called just before two fixtures collide
     * @param fixtureA The first fixture
     * @param fixtureB  The second fixture
     */
    public  void preSolve(ZWFixture fixtureA, ZWFixture fixtureB){
        switch(fixtureB.getCategoryBits()) {
            case (Constants.COLLISION_PLAYER):
                switch (fixtureA.getCategoryBits()){        //Not made as an if-statement if more collision alternatives are to be added
                    case Constants.COLLISION_DOOR:
                             CollisionObject door = (CollisionObject) fixtureA.getUserData();
                            int levelToLoad = Integer.parseInt(door.getProperty());
                          mapController.loadRoom(levelToLoad);
                        break;

                }
                break;
            case (Constants.COLLISION_DOOR):
                switch(fixtureA.getCategoryBits()){
                    case Constants.COLLISION_PLAYER:
                            CollisionObject door = (CollisionObject) fixtureB.getUserData();
                          int levelToLoad = Integer.parseInt(door.getProperty());
                         mapController.loadRoom(levelToLoad);
                        break;
                }
                break;
        }
    }








}