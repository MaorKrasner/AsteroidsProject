package krasner.maor.asteroids.multiadmatai;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LakoahCommunication {
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;

    public LakoahCommunication(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new Scanner(this.clientSocket.getInputStream(), "UTF-8");
        this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    public void sendMessageToClient(String data) {
        out.println(data);
    }

    public String receiveMessageFromClient() {
        try {
            return in.nextLine();
        } catch (Exception e) { return "";}
    }

    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException ignored) {}
    }
}
