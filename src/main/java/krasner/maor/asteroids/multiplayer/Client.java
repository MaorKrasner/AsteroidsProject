package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
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

    public Polygon p1, p2; // two polygons , one for each player
    public ArrayList<Asteroid> asteroids1, asteroids2; // two array lists for each list of asteroids in the game
    public ArrayList<Spaceship> spaceships1, spaceships2; // two array lists for each list of spaceships in the game
    public ArrayList<Ball> balls1, balls2; // two array lists for each list of balls in the game

    @Getter
    public Thread t; // thread to manage the transmission of data in the client

    /***
     * constructor
     * @param p1 - the polygon of the player that is represented by the current client
     * @throws IOException
     */
    public Client(Polygon p1, int index) throws IOException
    {
        this.p1 = p1;
        this.p2 = new Polygon();
        p2.addPoint(500, 500);


        this.asteroids1 = new ArrayList<>();
        this.asteroids2 = new ArrayList<>();

        this.spaceships1 = new ArrayList<>();
        this.spaceships2 = new ArrayList<>();

        this.balls1 = new ArrayList<>();
        this.balls2 = new ArrayList<>();


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


        this.asteroids1 = new ArrayList<>();
        this.asteroids2 = new ArrayList<>();

        this.spaceships1 = new ArrayList<>();
        this.spaceships2 = new ArrayList<>();

        this.balls1 = new ArrayList<>();
        this.balls2 = new ArrayList<>();



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

    public void run()
    {
        while (true)
        {
            Data data1, data2;

            try {
                Polygon p = new Polygon();
                for (int i = 0; i < this.p1.npoints; i++)
                {
                    p.addPoint(this.p1.xpoints[i], this.p1.ypoints[i]);
                }

                data1 = new Data(p);

                if (index == Constants.SENDER) {
                    data1.asteroids = asteroids1;
                    data1.spaceships = spaceships1;
                    data1.balls = balls1;
                }

                objectOutputStream.writeObject(data1);

                data2 = (Data) objectInputStream.readObject();

                this.p2 = new Polygon();
                for (int i = 0; i < data2.playerPolygon.npoints; i++)
                {
                    this.p2.addPoint(data2.playerPolygon.xpoints[i], data2.playerPolygon.ypoints[i]);
                }

                //if (index == Constants.RECEIVER) {
                this.asteroids2 = data2.asteroids;
                this.spaceships2 = data2.spaceships;
                this.balls2 = data2.balls;
                //}

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
