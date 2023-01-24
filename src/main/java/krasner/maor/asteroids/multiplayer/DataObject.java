package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.objects.Spaceship;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DataObject implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public ArrayList<Asteroid> asteroids;
    public ArrayList<Spaceship> spaceships;
    public ArrayList<Ball> balls;

    public Player player;

    @Getter
    private int[] xs;

    @Getter
    private int[] ys;

    @Getter
    @Setter
    public volatile boolean isExit;

    public DataObject(Player p)
    {
        this.xs = p.getPolygon().xpoints;
        this.ys = p.getPolygon().ypoints;
        this.isExit = false;
    }

    /*
    public DataObject(Player player, ArrayList<Asteroid> asteroids, ArrayList<Spaceship> spaceships, ArrayList<Ball> balls)
    {
        this.player = player;
        this.asteroids = asteroids;
        this.spaceships = spaceships;
        this.balls = balls;
    }
    */

    public boolean getIsExit() {
        return isExit;
    }
}
