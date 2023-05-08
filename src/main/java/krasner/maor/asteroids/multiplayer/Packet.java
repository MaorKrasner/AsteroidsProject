package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.objects.Spaceship;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;

/***
 * This class represents the data that we transfer with the clients and the server
 */

public class Packet implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //public Player player;
    public ArrayList<Polygon> asteroidsPolygons;
    public ArrayList<Polygon> spaceshipsPolygons;
    public ArrayList<Point> ballsPoints;

    public Polygon playerPolygon;

    public Packet(Polygon playerPolygon)
    {
        this.playerPolygon = playerPolygon;
        this.asteroidsPolygons = new ArrayList<>();
        this.spaceshipsPolygons = new ArrayList<>();
        this.ballsPoints = new ArrayList<>();
    }
}
