package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.util.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/***
 * class that represents the client in the multiplayer section
 */

@Slf4j
public class Client extends Thread {
    @Getter
    Socket socket; // socket of the client

    public int index;

    @Getter
    InputStream inputStream; // input stream of the client

    @Getter
    OutputStream outputStream; // output stream of the client

    @Getter
    ObjectInputStream objectInputStream; // object input stream of the client

    @Getter
    ObjectOutputStream objectOutputStream; // object output stream of the client

    public Polygon playerPolygon1, playerPolygon2; // two polygons , one for each player
    public ArrayList<Polygon> asteroidsPolygons1, asteroidsPolygons2; // two array lists for each list of asteroids in the game
    public ArrayList<Polygon> spaceshipsPolygons1, spaceshipsPolygons2; // two array lists for each list of spaceships in the game
    public ArrayList<Point> ballsPoints1, ballsPoints2; // two array lists for each list of balls in the game

    @Getter
    public Thread t; // thread to manage the transmission of data in the client

    /***
     * constructor
     * @param p1 - the polygon of the player that is represented by the current client
     * @throws IOException
     */
    public Client(Polygon p1) throws IOException
    {
        this.playerPolygon1 = new Polygon();
        for (int i = 0; i < p1.npoints; i++)
            this.playerPolygon1.addPoint(p1.xpoints[i], p1.ypoints[i]);
        this.playerPolygon2 = new Polygon();
        playerPolygon2.addPoint(500, 500);

        asteroidsPolygons1 = new ArrayList<>();
        asteroidsPolygons2 = new ArrayList<>();
        spaceshipsPolygons1 = new ArrayList<>();
        spaceshipsPolygons2 = new ArrayList<>();
        ballsPoints1 = new ArrayList<>();
        ballsPoints2 = new ArrayList<>();

        t = new Thread(this);
        connectToServer();
    }

    /***
     * default constructor
     * @throws IOException
     */
    public Client() throws IOException {
        this.playerPolygon1 = new Polygon();
        playerPolygon1.addPoint(500, 500);

        this.playerPolygon2 = new Polygon();
        playerPolygon2.addPoint(500, 500);

        asteroidsPolygons1 = new ArrayList<>();
        asteroidsPolygons2 = new ArrayList<>();
        spaceshipsPolygons1 = new ArrayList<>();
        spaceshipsPolygons2 = new ArrayList<>();
        ballsPoints1 = new ArrayList<>();
        ballsPoints2 = new ArrayList<>();

        t = new Thread(this);
        connectToServer();
    }

    /***
     * connect to the server with a specific IP address and a specific port
     * @throws IOException
     */
    public void connectToServer () throws IOException
    {
        socket = new Socket(Constants.SERVER_IP, Constants.PORT);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);
    }

    public void sendInitialSettings() {
        /*
        Data d;
        try {
            Polygon p = new Polygon();
            for (int i = 0; i < this.p1.npoints; i++) {
                p.addPoint(this.p1.xpoints[i], this.p1.ypoints[i]);
            }
            d = new Data(p);
            //d.asteroids = asteroids;
            objectOutputStream.writeObject(d);
        } catch (IOException ignored) {}
        */
    }

    public void run()
    {
        while (true)
        {
            Packet packet1, packet2;

            try {
                Polygon p = new Polygon();
                for (int i = 0; i < playerPolygon1.npoints; i++)
                    p.addPoint(playerPolygon1.xpoints[i], playerPolygon1.ypoints[i]);

                packet1 = new Packet(p);

                ArrayList<Polygon> asteroidsPolygonsTemp = new ArrayList<>();
                ArrayList<Polygon> spaceshipsPolygonsTemp = new ArrayList<>();
                ArrayList<Point> ballsPointsTemp = new ArrayList<>();

                asteroidsPolygonsTemp.addAll(asteroidsPolygons1);
                spaceshipsPolygonsTemp.addAll(spaceshipsPolygons1);
                ballsPointsTemp.addAll(ballsPoints1);

                packet1.asteroidsPolygons = asteroidsPolygonsTemp;
                packet1.spaceshipsPolygons = spaceshipsPolygonsTemp;
                packet1.ballsPoints = ballsPointsTemp;

                objectOutputStream.writeObject(packet1);

                packet2 = (Packet) objectInputStream.readObject();
                playerPolygon2 = new Polygon();

                for (int i = 0; i < packet2.playerPolygon.npoints; i++)
                    playerPolygon2.addPoint(packet2.playerPolygon.xpoints[i], packet2.playerPolygon.ypoints[i]);

                asteroidsPolygons2 = new ArrayList<>();
                spaceshipsPolygons2 = new ArrayList<>();
                ballsPoints2 = new ArrayList<>();

                asteroidsPolygons2.addAll(packet2.asteroidsPolygons);
                spaceshipsPolygons2.addAll(packet2.spaceshipsPolygons);
                ballsPoints2.addAll(packet2.ballsPoints);

            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
                System.exit(0);
                e.printStackTrace();
            } catch (ClassNotFoundException ignored) {}


            try {
                Thread.sleep(2);
            } catch (InterruptedException ignored){}
        }
    }
}
