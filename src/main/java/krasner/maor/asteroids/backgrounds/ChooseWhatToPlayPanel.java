package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class ChooseWhatToPlayPanel extends JPanel {
    private final Image img;

    public ChooseWhatToPlayPanel()
    {
        super();
        ImageIcon ic = new ImageIcon("src/main/resources/images/asteroidsbackgif.gif");
        img = ic.getImage();
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    }

    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, 1280, 720, this);
        g.setFont(new Font("Segoe Script", Font.BOLD, 25));
        g.setColor(Color.white);
        g.drawString("Choose what game mode you want to play", 340, 300);
        g.drawString("Single player", 240, 500);
        g.drawString("Multiplayer", 840, 500);
    }
}
