package krasner.maor.asteroids;

import krasner.maor.asteroids.backgrounds.StartingGameFrame;

/***
 * class that contains the main program
 */

public class AsteroidsApplication {
    public static void main(String[] args) throws InterruptedException {
        // always make sure to change that we can make multiple instances!!!!!!!!!!!!!
        // otherwise, the multiplayer section won't work!!!
        new StartingGameFrame();
    }
}