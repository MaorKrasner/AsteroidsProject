package krasner.maor.asteroids.lastmulti;

import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Spaceship;
import krasner.maor.asteroids.util.Constants;
import lombok.Getter;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client1 extends Thread {
    @Getter
    Socket socket;
    @Getter
    InputStream inputStream;
    @Getter
    OutputStream outputStream;
    @Getter
    ObjectInputStream objectInputStream;
    @Getter
    ObjectOutputStream objectOutputStream;

    public Polygon p1, p2;
    public ArrayList<Asteroid> asteroids1, asteroids2;
    public ArrayList<Spaceship> spaceships1, spaceships2;
    public ArrayList<Ball> balls1, balls2;

    @Getter
    public Thread t;

    public Client1 (Polygon p1) throws IOException
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
        connectToServer(Constants.PORT);
    }

    public Client1() throws IOException {
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
        connectToServer(Constants.PORT);
    }

    public void connectToServer (int port) throws IOException
    {
        socket = new Socket(Constants.SERVER_IP, port);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);
    }

    public void run()
    {
        while (true)
        {
            PlayerData data1, data2;

            try {
                Polygon p = new Polygon();
                for (int i = 0; i < this.p1.npoints; i++)
                {
                    p.addPoint(this.p1.xpoints[i], this.p1.ypoints[i]);
                }
                data1 = new PlayerData(p);
                //data1.asteroids = asteroids1;
                //data1.spaceships = spaceships1;
                //data1.balls = balls1;
                objectOutputStream.writeObject(data1);

                data2 = (PlayerData) objectInputStream.readObject();

                this.p2 = new Polygon();
                for (int i = 0; i < data2.playerPolygon.npoints; i++)
                {
                    this.p2.addPoint(data2.playerPolygon.xpoints[i], data2.playerPolygon.ypoints[i]);
                }

                //this.asteroids2 = data2.asteroids;
                //this.spaceships2 = data2.spaceships;
                //this.balls2 = data2.balls;

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
