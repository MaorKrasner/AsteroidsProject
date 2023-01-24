package krasner.maor.asteroids.clientserver;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class MyClient {
    private Socket socket = null;
    static PrintStream out = null;
    static Scanner in = null;
    public static int id;

    public static boolean[] alive = new boolean[Constants.MAX_PLAYERS];
    static Point spawn[][] = new Point[Constants.MAX_PLAYERS][3];

    public MyClient(String host, int port) {
        try {
            System.out.print("Establishing connection to the server...");
            this.socket = new Socket(host, port);
            out = new PrintStream(socket.getOutputStream(), true);  //to send to the server
            in = new Scanner(socket.getInputStream()); //to receive from the server
        } catch (IOException e) {
            System.out.println(" error: " + e + "\n");
            System.exit(1);
        }
        System.out.print(" ok\n");

        receiveInitialSettings();
        new Receiver().start();
    }

    void receiveInitialSettings() {
        id = in.nextInt();

        //initial (dead or alive) situation of all players
        for (int i = 0; i < Constants.MAX_PLAYERS; i++)
            MyClient.alive[i] = in.nextBoolean();

        //initial coordinates for the players
        for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
            for (int j = 0; j < 3; j++) {
                MyClient.spawn[i][j] = new Point(in.nextInt(), in.nextInt());
            }
        }
    }

    public static void main(String[] args) {
        new MyClient("127.0.0.1", 2358);
        Game.singlePlayerMode = false;
    }
}
