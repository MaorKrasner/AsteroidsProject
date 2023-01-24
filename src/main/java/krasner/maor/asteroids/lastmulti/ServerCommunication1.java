package krasner.maor.asteroids.lastmulti;

import lombok.Getter;

import java.io.*;
import java.net.Socket;

public class ServerCommunication1 {
    private Socket clientSocket;

    @Getter
    private InputStream inputStream;

    @Getter
    private ObjectInputStream objectInputStream;

    @Getter
    private OutputStream outputStream;

    @Getter
    private ObjectOutputStream objectOutputStream;

    public ServerCommunication1 (Socket clientSocket) throws IOException {
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
