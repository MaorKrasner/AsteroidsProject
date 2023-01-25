package krasner.maor.asteroids.lastmulti;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Spaceship;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;

public class PlayerData implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Polygon playerPolygon;

    //public ArrayList<Asteroid> asteroids;
    //public ArrayList<Spaceship> spaceships;
    //public ArrayList<Ball> balls;

    public PlayerData(Polygon playerPolygon)
    {
        this.playerPolygon = playerPolygon;
        //this.asteroids = new ArrayList<>();
        //this.spaceships = new ArrayList<>();
        //this.balls = new ArrayList<>();
    }

    public PlayerData()
    {
        this (new Polygon(new int[]{500}, new int[]{500},  1));
    }
}
