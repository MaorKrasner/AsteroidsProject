package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/***
 * this class represents the server in the multiplayer section
 */

@Slf4j
public class Server {
    ServerSocket server; // socket of the server
    public static boolean isServerOn = false; // variable to know whether the server is on or off

    public static int ClientCounter = 0; // counter to see how many clients connected to the server

    public static LinkedList<Socket> clients = new LinkedList<>(); // list of sockets that are connected to the server

    Socket socket1; // socket for the first client
    Socket socket2; // socket for the second client

    OutputStream outputStream1; // output stream for the first client
    ObjectOutputStream objectOutputStream1; // object output stream for the first client
    InputStream inputStream1; // input stream for the first client
    ObjectInputStream objectInputStream1; // object input stream for the first client

    OutputStream outputStream2; // output stream for the second client
    ObjectOutputStream objectOutputStream2; // object output stream for the second client
    InputStream inputStream2; // input stream for the second client
    ObjectInputStream objectInputStream2; // object input stream for the second client

    /***
     * constructor
     * @throws IOException
     */
    public Server() throws IOException
    {
        server = new ServerSocket(Constants.PORT);
        isServerOn = true;
        serverConnection();
    }

    /***
     * function that accepts the request of the clients to connect to the server
     * @throws IOException
     */
    public void serverConnection() throws IOException
    {
        socket1 = server.accept();
        clients.add(socket1);
        log.info("AMOUNT OF CLIENTS : " + clients.size());
        log.info("AM : " + Server.ClientCounter);
        outputStream1 = socket1.getOutputStream();
        objectOutputStream1 = new ObjectOutputStream(outputStream1);

        objectOutputStream1.writeObject(Server.ClientCounter);
        ++Server.ClientCounter;

        socket2 = server.accept();
        clients.add(socket2);
        log.info("AMOUNT OF CLIENTS : " + clients.size());
        log.info("AM : " + Server.ClientCounter);
        outputStream2 = socket2.getOutputStream();
        objectOutputStream2 = new ObjectOutputStream(outputStream2);

        objectOutputStream2.writeObject(Server.ClientCounter);
        ++Server.ClientCounter;

        String s = "start";
        objectOutputStream1.writeObject(s); // problem here
        objectOutputStream2.writeObject(s);

        inputStream1 = socket1.getInputStream();
        objectInputStream1 = new ObjectInputStream(inputStream1);

        inputStream2 = socket2.getInputStream();
        objectInputStream2 = new ObjectInputStream(inputStream2);

        // handler thread for client 1
        Thread handleClient1 = new Thread(() -> {
            while (true)
            {
                try {
                    Data d1 = (Data) objectInputStream1.readObject(); // read data from client1

                    objectOutputStream2.writeObject(d1); // send data to client2

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
        });

        // handler thread for client 2
        Thread handleClient2 = new Thread(() -> {
            while (true)
            {
                try {
                    Data d2 = (Data) objectInputStream2.readObject(); // read data from client2

                    objectOutputStream1.writeObject(d2);// send data to client1

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
        });

        // start the threads that run concurrently in Server
        handleClient1.start();
        handleClient2.start();
    }

    /***
     * close the server
     */
    public void closeServer() {
        isServerOn = false;
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
