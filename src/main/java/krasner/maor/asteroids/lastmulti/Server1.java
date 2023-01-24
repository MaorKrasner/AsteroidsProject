package krasner.maor.asteroids.lastmulti;

import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

@Slf4j
public class Server1 {
    ServerSocket server;
    public static boolean isServerOn = false;

    public static int ClientCounter = 0;

    public static LinkedList<Socket> clients = new LinkedList<>();

    Socket socket1;
    Socket socket2;

    OutputStream outputStream1;
    ObjectOutputStream objectOutputStream1;
    InputStream inputStream1;
    ObjectInputStream objectInputStream1;

    OutputStream outputStream2;
    ObjectOutputStream objectOutputStream2;
    InputStream inputStream2;
    ObjectInputStream objectInputStream2;

    public Server1() throws IOException
    {
        server = new ServerSocket(Constants.PORT);
        isServerOn = true;
        serverConnection();
    }

    public void serverConnection() throws IOException
    {
        socket1 = server.accept();
        clients.add(socket1);
        log.info("AMOUNT OF CLIENTS : " + clients.size());
        log.info("AM : " + Server1.ClientCounter);
        outputStream1 = socket1.getOutputStream();
        objectOutputStream1 = new ObjectOutputStream(outputStream1);

        objectOutputStream1.writeObject(Server1.ClientCounter);
        ++Server1.ClientCounter;

        socket2 = server.accept();
        clients.add(socket2);
        log.info("AMOUNT OF CLIENTS : " + clients.size());
        log.info("AM : " + Server1.ClientCounter);
        outputStream2 = socket2.getOutputStream();
        objectOutputStream2 = new ObjectOutputStream(outputStream2);

        objectOutputStream2.writeObject(Server1.ClientCounter);
        ++Server1.ClientCounter;

        String s = "start";
        objectOutputStream1.writeObject(s);
        objectOutputStream2.writeObject(s);

        inputStream1 = socket1.getInputStream();
        objectInputStream1 = new ObjectInputStream(inputStream1);

        inputStream2 = socket2.getInputStream();
        objectInputStream2 = new ObjectInputStream(inputStream2);

        // handler thread for for client 1
        Thread handleClient1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try {
                        // read data from client1
                        PlayerData d1 = (PlayerData) objectInputStream1.readObject(); // problem here when client is closed

                        // send data to client2
                        objectOutputStream2.writeObject(d1);

                        try {
                            Thread.sleep(1);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // handler thread for for client 2
        Thread handleClient2 = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (true)
                {
                    try {
                        // read data from client2
                        PlayerData d2 = (PlayerData) objectInputStream2.readObject(); // problem here

                        // send data to client1
                        objectOutputStream1.writeObject(d2);

                        try {
                            Thread.sleep(1);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // start the threads that run concurrently in Server
        handleClient1.start();
        handleClient2.start();
    }

    public void closeServer() {
        this.isServerOn = false;
    }

    public static void main(String[] args) throws IOException {
        new Server1();
    }
}
