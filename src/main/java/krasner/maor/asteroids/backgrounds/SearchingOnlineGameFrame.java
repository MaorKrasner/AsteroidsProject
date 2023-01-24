package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.game.GameFrame;
import krasner.maor.asteroids.multiplayer.Client;
import krasner.maor.asteroids.util.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class SearchingOnlineGameFrame extends JFrame {

    private Client waitingClient;

    public SearchingOnlineGameFrame(Client client) {
        super ("Searching screen wait");

        waitingClient = client;

        waitingClient.sendMsgToServer("MULTIPLAYER");
        Thread responsesThread = new Thread(() -> {
            while (true) {
                String response = waitingClient.recvMsgFromServer();
                if (response.equals("FOUND")) {
                    waitingClient.sendMsgToServer("READY");
                }
                else if (response.equals("START")) {
                    dispose();
                    new GameFrame();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        });

        add(new SearchingOnlineGamePanel());

        setVisible(true);
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(Color.black);
    }
}
