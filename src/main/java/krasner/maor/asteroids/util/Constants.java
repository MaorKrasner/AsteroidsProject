package krasner.maor.asteroids.util;

import krasner.maor.asteroids.objects.AsteroidType;
import krasner.maor.asteroids.objects.AsteroidsMonitor;
import krasner.maor.asteroids.objects.SizeTypes;
import krasner.maor.asteroids.objects.SpaceshipsMonitor;
import lombok.experimental.UtilityClass;

/***
 * class that contains all the constants that are used in the game
 */

@UtilityClass
public final class Constants
{
    /***
     * static monitors of the asteroids and spaceships of the game.
     ***/
    public static AsteroidsMonitor asteroidsMonitor;
    public static SpaceshipsMonitor spaceshipsMonitor;

    static {
        asteroidsMonitor = new AsteroidsMonitor();
        spaceshipsMonitor = new SpaceshipsMonitor();
    }

    /***
     * variable for the multiplayer section to know the number of the panel
     ***/
    public static int GAME_INSTANCE_COUNTER = 0;

    /***
     * variable to store the size of a ball that intersected with another game object
     ***/
    public static final int DEAD_BALL_SIZE = 0;

    /***
     * variables to represent the screen height & width
     ***/
    public final static int SCREEN_WIDTH = 1280;
    public final static int SCREEN_HEIGHT = 720;

    /***
     * Networking constants (port and server ip address)
     ***/
    public static final int PORT = 2358;
    public static final String SERVER_IP = "localhost";

    /***
     * array that includes all the sizes of the game objects (ball, asteroid, spaceship)
     ***/
    public final static SizeTypes[] SIZE_TYPES = {SizeTypes.BIG, SizeTypes.MEDIUM, SizeTypes.SMALL};

    /***
     * array that includes all the types of asteroids in the game
     ***/
    public final static AsteroidType[] ASTEROID_TYPES = {AsteroidType.CLOVER, AsteroidType.TSTRAIGHT, AsteroidType.CURVED};

    /***
     * size of a ball
     ***/
    public final static int BALL_SIZE = 4;

    /***
     * amount of maximum players that can join a multiplayer game
     ***/
    public final static int MAX_PLAYERS = 2;

    /***
     * array that has all the possible options for y value movement for the spaceships and asteroids
     ***/
    public final static int[] MOVEMENT_YARR = {0, -1, 1};

    /***
     * variables for the location of the spawn of a new ball from the spaceship
     ***/
    public final static int HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_FIRST = 17;
    public final static int HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_SECOND = 9;
    public final static int HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_THIRD = 5;

    /***
     * variables for adding points to the score of the player when a spaceship is dead
     ***/
    public final static int BIG_HIT_SPACESHIP = 250;
    public final static int MEDIUM_HIT_SPACESHIP = 350;
    public final static int SMALL_HIT_SPACESHIP = 500;

    /***
     * variables for adding points to the score of the player when an asteroid is dead
     ***/
    public final static int BIG_HIT_ASTEROID = 20;
    public final static int MEDIUM_HIT_ASTEROID = 50;
    public final static int SMALL_HIT_ASTEROID = 100;

    /***
     * counter for the name of an asteroid thread
     ***/
    public static int IND_COUNTER = 1;

    /***
     * counter for the name of an asteroid thread that is coming out of the original asteroids
     ***/
    public static int EXTRA_IND_COUNTER = 1;
}
