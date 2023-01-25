package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StartingGameFrame extends JFrame {
    public StartingGameFrame() {
        super("Asteroids - Main Menu");

        JLabel playButton = new JLabel(); // play button (there is  no text so we will only see clear real play button)
        playButton.setBounds(595, 522, 110, 60);
        playButton.setVisible(true); // ALWAYS set labels to be visible so we can actually "press them"
        playButton.addMouseListener(new MouseAdapter() {
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
        add(playButton);

        /***
         * INSTRUCTIONS BUTTON
         */
        JLabel instructionsButton = new JLabel();
        instructionsButton.setBounds(137, 522, 301, 60);
        instructionsButton.setVisible(true);
        instructionsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                dispose(); // destroy current frame
                new InstructionsFrame(); // make a new frame for the instructions page
            }
        });
        add(instructionsButton);

        /***
         * CREDITS BUTTON
         */
        JLabel creditsButton = new JLabel();
        creditsButton.setBounds(723, 522, 508, 60);
        creditsButton.setVisible(true);
        creditsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // sleep just a little bit for smoothness
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                dispose(); // destroy current frame
                new CreditsFrame(); // make a new frame for the instructions page
            }
        });
        add(creditsButton);

        add(new StartingGamePanel());
        setVisible(true);
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
}
