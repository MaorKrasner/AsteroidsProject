package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreditsFrame extends JFrame
{
	private JLabel rtrnToMainMenu;
	private CreditsPanel cp;
	
	public CreditsFrame()
	{
		super("Asteroids - Credits");
		rtrnToMainMenu = new JLabel();
		rtrnToMainMenu.setVisible(true);
		Rectangle r = new Rectangle(540, 400, 250, 100);
		rtrnToMainMenu.setBounds(r);
		rtrnToMainMenu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// sleep just a little bit for smoothness
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {}
				dispose();
				new StartingGameFrame();
			}
		});
		add(rtrnToMainMenu);
		
		cp = new CreditsPanel();
		add(cp);
		
		setVisible(true);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
}
