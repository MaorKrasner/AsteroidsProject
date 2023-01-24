package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class Client {
    private Socket socket;

    private Scanner in;

    private PrintWriter out;

    public Client()  {
        try {
            this.socket = new Socket(Constants.SERVER_IP, Constants.PORT);
            this.in = new Scanner(this.socket.getInputStream(), "UTF-8");
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {}
    }

    public void sendMsgToServer(String data) {
        out.println(data);
    }

    public String recvMsgFromServer() {
        return in.nextLine();
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {}
    }
}

