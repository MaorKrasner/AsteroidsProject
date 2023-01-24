package krasner.maor.asteroids.lastmulti;

import krasner.maor.asteroids.util.Constants;
import lombok.Getter;

import java.awt.*;
import java.io.*;
import java.net.Socket;

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

    @Getter
    public Thread t;

    public Client1 (Polygon p1) throws IOException
    {
        this.p1 = p1;
        this.p2 = new Polygon();
        p2.addPoint(500, 500);

        t = new Thread(this);
        connectToServer(Constants.PORT);
    }

    public Client1() throws IOException {
        this.p1 = new Polygon();
        p1.addPoint(500, 500);
        this.p2 = new Polygon();
        p2.addPoint(500, 500);
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
                objectOutputStream.writeObject(data1);

                data2 = (PlayerData) objectInputStream.readObject();

                this.p2 = new Polygon();
                for (int i = 0; i < data2.playerPolygon.npoints; i++)
                {
                    this.p2.addPoint(data2.playerPolygon.xpoints[i], data2.playerPolygon.ypoints[i]);
                }

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

            /*
            try {
                Polygon p = d1.getPlayerPolygon();
                int lives = d1.getLivesLeft();
                data1 = new PlayerData(p, lives);

                //System.out.println("data 1 polygon x's : " + Arrays.toString(data1.getPlayerPolygon().xpoints));
                //System.out.println("data 1 polygon y's : " + Arrays.toString(data1.getPlayerPolygon().ypoints));

                String info = "";
                for (int i = 0; i < p.npoints; i++)
                {
                    info += p.xpoints[i];
                    info += " ";
                    info += p.ypoints[i];
                    info += " ";
                }
                info += lives;

                objectOutputStream.writeObject(info);

                String strdata2 = (String) objectInputStream.readObject();
                //data2 = (PlayerData) objectInputStream.readObject();

                this.d2.playerPolygon = new Polygon();
                String[] data_list = strdata2.split(" ");
                for (int i = 0; i < data_list.length / 2 - 1; i++)
                {
                    this.d2.playerPolygon.addPoint(Integer.parseInt(data_list[i]), Integer.parseInt(data_list[i + 1]));
                }
                //this.d2.playerPolygon = strdata2.playerPolygon;
                this.d2.livesLeft = Integer.parseInt(data_list[data_list.length - 1]);
                //this.d2.setPlayerPolygon(data2.getPlayerPolygon());
                //this.d2.setLivesLeft(data2.getLivesLeft());

                System.out.println("data 2 polygon x's : " + Arrays.toString(this.d2.playerPolygon.xpoints));
                System.out.println("data 2 polygon y's : " + Arrays.toString(this.d2.playerPolygon.ypoints));

            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.exit(0);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException ignored){}
            */
        }
    }
}
