package krasner.maor.asteroids.multiplayer;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

public class ServerCommunication {

    @Getter
    @Setter
    private Socket clientSocket;

    @Getter
    @Setter
    private InputStream inputStream;

    @Getter
    @Setter
    private ObjectInputStream objectInputStream;

    @Getter
    @Setter
    private OutputStream outputStream;

    @Getter
    @Setter
    private ObjectOutputStream objectOutputStream;

    public ServerCommunication(Socket newSocket) {
        try {
            this.clientSocket = newSocket;
            this.outputStream = clientSocket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            this.inputStream = clientSocket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException ignored) {}
    }

    public DataObject recvMsgFromClient() {
        DataObject d = null;
        try {
            d = (DataObject) this.objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException ignored){}
        return d;
    }

    public void sendMsgToClient(DataObject data) {
        try {
            this.objectOutputStream.writeObject(data);
        } catch (IOException ignored) {}
    }

    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException ignored) {}
    }
}
