package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.objects.Spaceship;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;

/***
 * This class represents the data that we transfer with the clients and the server
 */

public class Data implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //public Player player;
    //public Polygon playerPolygon; // polygon that represents the player
    public ArrayList<Polygon> asteroidsPolygons;
    //public ArrayList<Spaceship> spaceships;
    //public ArrayList<Ball> balls;

    public Polygon playerPolygon;

    public Data(Polygon playerPolygon)
    {
        this.playerPolygon = playerPolygon;
        this.asteroidsPolygons = new ArrayList<>();
        //this.player = player;
        //this.playerPolygon = playerPolygon;
        //this.asteroids = new ArrayList<>();
        //this.spaceships = new ArrayList<>();
        //this.balls = new ArrayList<>();
    }
}
