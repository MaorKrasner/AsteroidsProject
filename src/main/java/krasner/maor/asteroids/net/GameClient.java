package krasner.maor.asteroids.net;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

@Slf4j
public class GameClient extends Thread {
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException ignored) {}
    }

    public void run() {
        while (true) {
            byte[] data = new byte[Constants.CHUNK_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException ignored) {}
            String message = new String(packet.getData());
            log.info("Server > " + message.trim());
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, Constants.PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {}
    }
}
