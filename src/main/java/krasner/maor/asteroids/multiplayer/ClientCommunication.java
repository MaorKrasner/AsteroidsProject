package krasner.maor.asteroids.multiplayer;

import krasner.maor.asteroids.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

public class ClientCommunication {
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

    public ClientCommunication() {
        try {
            this.clientSocket = new Socket(Constants.SERVER_IP, Constants.PORT);
            this.outputStream = clientSocket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            this.inputStream = clientSocket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException ignored) {}
    }

    public void sendMsgToServer(DataObject data) {
        try {
            this.objectOutputStream.writeObject(data);
        } catch (IOException ignored) {}
    }

    public DataObject receiveMsgFromServer() {
        DataObject d = null;
        try {
            d = (DataObject) this.objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException ignored){}
        return d;
    }

    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException ignored){}
    }
}
