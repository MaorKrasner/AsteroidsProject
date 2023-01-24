package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class SearchingOnlineGamePanel extends JPanel {
    public SearchingOnlineGamePanel() {
        super();
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setFont(new Font("Segoe Script", Font.BOLD, 25));
        g.setColor(Color.WHITE);
        g.drawString("SEARCHING FOR ANOTHER PLAYER ...", Constants.SCREEN_WIDTH / 2 - 250, Constants.SCREEN_HEIGHT / 2);
    }
}
