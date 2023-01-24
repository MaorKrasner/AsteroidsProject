package krasner.maor.asteroids.clientserver;

import krasner.maor.asteroids.util.Constants;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// to each client that enters the server, a new thread is instantiated to handle it
public class ClientManager extends Thread {
    static List<PrintStream> listOutClients = new ArrayList<PrintStream>();

    private Socket clientSocket = null;
    private Scanner in = null;
    private PrintStream out = null;
    private int id;

    CoordinatesController cc;

    public ClientManager(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
        (cc = new CoordinatesController(this.id)).start();

        try {
            System.out.print("Starting connection as player " + this.id + "...");
            this.in = new Scanner(clientSocket.getInputStream()); // to receive the client
            this.out = new PrintStream(clientSocket.getOutputStream(), true); // to send to the client
        } catch (IOException e) {
            System.out.println(" error : " + e + "\n");
            System.exit(1);
        }
        System.out.print(" ok\n");

        listOutClients.add(out);
        MyServer.players[id].logged = true;
        MyServer.players[id].alive = true;
        sendInitialSettings(); // sends a single string

        //notifies customers that are already logged in
        for (PrintStream outClient: listOutClients)
            if (outClient != this.out)
                outClient.println(id + " playerJoined");
    }

    public static void sendToAllClients(String outputLine) {
        for (PrintStream outClient : listOutClients)
            outClient.println(outputLine);
    }

    public void run() {
        while (in.hasNextLine()) { // established connection to the client this.id
            String str[] = in.nextLine().split(" ");


            if (str[0].equals("keyCodePressed") && MyServer.players[id].alive) {
                cc.keyCodePressed(Integer.parseInt(str[1]));
            }
            else if (str[0].equals("keyCodeReleased") && MyServer.players[id].alive) {
                cc.keyCodeReleased(Integer.parseInt(str[1]));
            }

        }
        clientDisconnected();
    }

    void sendInitialSettings() {
        out.print(id);

        for (int i = 0; i < Constants.MAX_PLAYERS; i++)
            out.print(" " + MyServer.players[i].alive);

        for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
            for (int j = 0; j < 3; j++) {
                out.print(" " + MyServer.players[i].p.xpoints[j] + " " + MyServer.players[i].p.ypoints[j]);
            }
        }
        out.print("\n");
    }

    void clientDisconnected() {
        listOutClients.remove(out);
        MyServer.players[id].logged = false;
        try {
            System.out.print("Terminating connection to the player " + this.id + "...");
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(" error: " + e + "\n");
            System.exit(1);
        }
        System.out.print(" ok\n");
    }
}
