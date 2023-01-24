package krasner.maor.asteroids.clientserver;

public class BallData  {
    boolean alive; // variable to know if the ball is alive
    int x,y; // coordinates

    // constructor
    public BallData(int x, int y) {
        this.x = x;
        this.y = y;
        this.alive = false;
    }
}
