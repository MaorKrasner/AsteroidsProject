package krasner.maor.asteroids.clientserver;

import java.awt.*;

public class PlayerObjectData {
    public boolean logged, alive; // variables to know if the player is logged into the game and if he is alive
    public int x,y; // coordinates

    public Polygon p;

    // constructor
    public PlayerObjectData(int x, int y, Polygon p) {
        this.x = x;
        this.y = y;
        this.p = p;
        this.logged = false;
        this.alive = false;
    }
}
