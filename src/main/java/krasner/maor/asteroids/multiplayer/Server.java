package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

@Slf4j
public class Server {
    private ServerSocket server;
    public static LinkedList<ServerCommunication> clients;
    private boolean isServerOn;

    public Server() {
        try {
            this.server = new ServerSocket(Constants.PORT);
            this.isServerOn = true;
            Server.clients = new LinkedList<>();
            new Thread(() -> {
                getNewConnection();
            }).start();
            log.info("Server is listening on port : " + Constants.PORT);
        } catch (IOException ignored){}
    }

    public void getNewConnection() {
        while (this.isServerOn) {
            try {
                Socket newSocket = this.server.accept();
                ServerCommunication newClient = new ServerCommunication(newSocket);
                Server.clients.add(newClient);
                log.info("A new connection to the server " + newSocket);
                if (clients.size() >= 2)
                {
                    log.info("hey");
                    for (int i = 0; i < clients.size(); i++)
                    {
                        ServerCommunication current = clients.get(i);
                        current.getObjectOutputStream().writeObject("start");
                        new Thread(() -> {
                            while (clients.contains(current)) {
                                try {
                                    if (clients.size() >= 2)
                                        getMessagesFromClient(current);
                                } catch (SocketException ignored) {
                                    current.close();
                                    clients.remove(current);
                                    log.info("The client " + current + " has been removed.");
                                }
                            }
                        }).start();
                    }
                }
            } catch (IOException ignored){}
        }
    }

    private void getMessagesFromClient(ServerCommunication client) throws SocketException {
        DataObject d = client.recvMsgFromClient();
        if (d != null)
        {
            if (d.getIsExit()) {
                client.close();
                Server.clients.remove(client);
                log.info("The client : " + client + " has left the game");
            }
            else {
                log.info("Received a message from the client : " + client + ":\n");
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i) != client) {
                        clients.get(i).sendMsgToClient(d);
                    }
                }
            }
        }
    }

    public void turnOffServer() {
        this.isServerOn = false;
        for (ServerCommunication client : Server.clients)
            client.close();
        try {
            this.server.close();
        } catch (IOException ignored){}
    }

    public static void main(String[] args) {
        new Server();
    }
}