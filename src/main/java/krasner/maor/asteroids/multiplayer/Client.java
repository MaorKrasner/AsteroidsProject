package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.objects.Spaceship;
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

    public Polygon p1, p2;

    //public Polygon p1, p2; // two polygons , one for each player
    public ArrayList<Polygon> asteroidsPolygons; // two array lists for each list of asteroids in the game
    //public ArrayList<Spaceship> spaceships; // two array lists for each list of spaceships in the game
    //public ArrayList<Ball> balls; // two array lists for each list of balls in the game

    @Getter
    public Thread t; // thread to manage the transmission of data in the client

    /***
     * constructor
     * @param p1 - the polygon of the player that is represented by the current client
     * @throws IOException
     */
    public Client(Polygon p1) throws IOException
    {
        this.p1 = p1;
        this.p2 = new Polygon();
        p2.addPoint(500, 500);

        this.asteroidsPolygons = new ArrayList<>();

        /*
        this.asteroids = new ArrayList<>();

        this.spaceships = new ArrayList<>();

        this.balls = new ArrayList<>();
        */

        t = new Thread(this);
        connectToServer();
    }

    /***
     * default constructor
     * @throws IOException
     */
    public Client() throws IOException {
        this.p1 = new Polygon();
        p1.addPoint(500, 500);

        this.p2 = new Polygon();
        p2.addPoint(500, 500);

        this.asteroidsPolygons = new ArrayList<>();

        /*
        this.asteroids = new ArrayList<>();

        this.spaceships = new ArrayList<>();

        this.balls = new ArrayList<>();
        */

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
            Data data1, data2;

            try {
                Polygon p = new Polygon();
                for (int i = 0; i < this.p1.npoints; i++)
                    p.addPoint(this.p1.xpoints[i], this.p1.ypoints[i]);

                ArrayList<Polygon> temporaryAsteroids = new ArrayList<>(this.asteroidsPolygons);

                data1 = new Data(p);
                data1.asteroidsPolygons = temporaryAsteroids;
                objectOutputStream.writeObject(data1);

                data2 = (Data) objectInputStream.readObject();

                this.p2 = new Polygon();
                for (int i = 0; i < data2.playerPolygon.npoints; i++)
                    this.p2.addPoint(data2.playerPolygon.xpoints[i], data2.playerPolygon.ypoints[i]);

                this.asteroidsPolygons = data2.asteroidsPolygons;

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
