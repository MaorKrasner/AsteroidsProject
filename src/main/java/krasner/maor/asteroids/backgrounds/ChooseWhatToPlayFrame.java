package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.game.GameFrame;
import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChooseWhatToPlayFrame extends JFrame {

    public ChooseWhatToPlayFrame() {
        super("Asteroids - Choose what to play");
        JLabel singlePlayer = new JLabel();
        JLabel multiplayer = new JLabel();
        singlePlayer.setVisible(true);
        multiplayer.setVisible(true);
        Rectangle r1 = new Rectangle(240, 400, 250, 100);
        Rectangle r2 = new Rectangle(840, 400, 250, 100);

        singlePlayer.setBounds(r1);
        singlePlayer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Game.singlePlayerMode = true;
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                dispose();
                new GameFrame();
            }
        });

        multiplayer.setBounds(r2);
        multiplayer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Game.singlePlayerMode = false;
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                dispose();
                new GameFrame();
            }
        });
        add(singlePlayer);
        add(multiplayer);

        add(new ChooseWhatToPlayPanel());

        setVisible(true);
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
}
