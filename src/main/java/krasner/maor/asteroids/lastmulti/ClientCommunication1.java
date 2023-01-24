package krasner.maor.asteroids.lastmulti;

import java.io.*;
import java.net.Socket;

public class ClientCommunication1 {
    private Socket clientSocket;

    private InputStream inputStream;
    private ObjectInputStream objectInputStream;

    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientCommunication1(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
        this.objectInputStream = new ObjectInputStream(inputStream);
        this.outputStream = clientSocket.getOutputStream();
        this.objectOutputStream = new ObjectOutputStream(outputStream);
    }

    public void sendMessageToClient(PlayerData playerData) throws IOException{
        objectOutputStream.writeObject(playerData);
    }

    public PlayerData receiveMessageFromClient() throws IOException, ClassNotFoundException {
        PlayerData playerData = (PlayerData) objectInputStream.readObject();
        return playerData;
    }

    public void close() throws IOException {
        clientSocket.close();
    }
}
