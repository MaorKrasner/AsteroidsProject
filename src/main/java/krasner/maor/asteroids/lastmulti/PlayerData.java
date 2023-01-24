package krasner.maor.asteroids.lastmulti;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public class PlayerData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Polygon playerPolygon;

    public PlayerData(Polygon playerPolygon)
    {
        this.playerPolygon = playerPolygon;
    }

    public PlayerData()
    {
        this (new Polygon(new int[]{500}, new int[]{500},  1));
    }
}
