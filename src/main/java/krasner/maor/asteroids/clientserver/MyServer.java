package krasner.maor.asteroids.clientserver;

import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MyServer {
    public static PlayerObjectData[] players = new PlayerObjectData[Constants.MAX_PLAYERS];

    public MyServer(int portNumber) {
        ServerSocket ss;

        try {
            System.out.print("Listening to port " + portNumber + "...");
            ss = new ServerSocket(portNumber); // socket listens to the port
            log.info(" ok\n");

            for (int id = 0; !loggedIsFull(); id = (++id)% Constants.MAX_PLAYERS)
                if (!players[id].logged) {
                    Socket clientSocket = ss.accept();
                    new ClientManager(clientSocket, id).start();
                }
            //don't shut down the server while the thread is still running
        } catch (IOException e) {
            log.info(" error: " + e + "\n");
            System.exit(1);
        }
    }

    boolean loggedIsFull() {
        for (int i = 0; i < Constants.MAX_PLAYERS; i++)
            if (!players[i].logged)
                return false;
        return true;
    }

    public static void main(String[] args) { new MyServer(2358); }
}
