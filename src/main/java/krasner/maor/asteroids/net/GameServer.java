package krasner.maor.asteroids.net;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GameServer extends Thread {
    private DatagramSocket socket;
    private Game game;

    private static List<PlayerMP> connectedPlayers = new ArrayList<>();

    public GameServer(Game game)  {
        this.game = game;
        try {
            this.socket = new DatagramSocket(Constants.PORT);
        } catch (SocketException e) {}
    }

    public void run() {
        while (true) {
            byte[] data = new byte[Constants.CHUNK_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {}
            String message = new String(packet.getData());
            log.info("Client [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " > ] " + message.trim());
            if (message.trim().equalsIgnoreCase("ping")) {
                sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
            }
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {}
    }


    public static void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
           // p.sendData(data, p.inetAddress, p.port);
        }
    }
}
