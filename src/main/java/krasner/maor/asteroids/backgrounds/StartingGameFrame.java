package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StartingGameFrame extends JFrame {
    public StartingGameFrame() {
        super("Asteroids - Main Menu");

        /***
         *  PLAY BUTTON
         ***/
        JLabel playBtn = new JLabel(); // NO TEXT SO WE WILL ONLY SEE CLEAR PLAY BUTTON
        playBtn.setBounds(595, 522, 110, 60);
        playBtn.setVisible(true); // ALWAYS set labels to be visible so we can actually "press them"
        playBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                dispose(); // destroy current Frame
                //new SharedFrame();
                new ChooseWhatToPlayFrame();
            }
        });
        add(playBtn);

        /***
         * INSTRUCTIONS BUTTON
         ***/
        JLabel instBtn = new JLabel();
        instBtn.setBounds(137, 522, 301, 60);
        instBtn.setVisible(true);
        instBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                dispose(); // destroy current frame
                new InstructionsFrame(); // make a new frame for the instructions page
            }
        });
        add(instBtn);

        /***
         * CREDITS BUTTON
         ***/
        JLabel credBtn = new JLabel();
        credBtn.setBounds(723, 522, 508, 60);
        credBtn.setVisible(true);
        credBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                dispose(); // destroy current frame
                new CreditsFrame(); // make a new frame for the instructions page
            }
        });
        add(credBtn);

        add(new StartingGamePanel());
        setVisible(true);
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
}
