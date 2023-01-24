package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InstructionsFrame extends JFrame
{
	public InstructionsFrame()
	{
		super("Asteroids - Instructions");
		JLabel rtrnToMainMenu = new JLabel();
		rtrnToMainMenu.setVisible(true);
		Rectangle r = new Rectangle(540, 400, 250, 100);
		rtrnToMainMenu.setBounds(r);
		rtrnToMainMenu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// sleep just a little bit for smoothness
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {}
				dispose();
				new StartingGameFrame();
			}
		});
		add(rtrnToMainMenu);
		
		add(new InstructionsPanel());
		
		setVisible(true);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
}
