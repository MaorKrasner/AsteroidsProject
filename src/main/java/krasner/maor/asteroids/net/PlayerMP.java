package krasner.maor.asteroids.net;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.objects.Player;

import java.net.InetAddress;

public class PlayerMP extends Player {

    public InetAddress inetAddress;
    public int port;

    public PlayerMP(int x, int y, Game game, Game.KeyboardListener controls, InetAddress inetAddress, int port)
    {
        super(x,y, game, controls, 0);
        this.inetAddress = inetAddress;
        this.port = port;
    }
}
