package krasner.maor.asteroids.multiadmatai;

import krasner.maor.asteroids.util.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Lakoah {
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;

    public Lakoah() throws IOException {
        this.clientSocket = new Socket(Constants.SERVER_IP, Constants.PORT);
        this.in = new Scanner(this.clientSocket.getInputStream(), "UTF-8");
        this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    public void sendMessageToServer(String data) {
        out.println(data);
    }

    public String receiveMessageFromServer() {
        return in.nextLine();
    }

    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException ignored) {}
    }
}
