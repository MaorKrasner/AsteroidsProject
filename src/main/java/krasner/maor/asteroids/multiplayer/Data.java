package krasner.maor.asteroids.multiplayer;

import java.awt.*;
import java.io.Serial;

/***
 * This class represents the data that we transfer with the clients and the server
 */

public class Data implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Polygon playerPolygon; // polygon that represents the player

    //public ArrayList<Asteroid> asteroids;
    //public ArrayList<Spaceship> spaceships;
    //public ArrayList<Ball> balls;

    public Data(Polygon playerPolygon)
    {
        this.playerPolygon = playerPolygon;
        //this.asteroids = new ArrayList<>();
        //this.spaceships = new ArrayList<>();
        //this.balls = new ArrayList<>();
    }

    public Data()
    {
        this (new Polygon(new int[]{500}, new int[]{500},  1));
    }
}
