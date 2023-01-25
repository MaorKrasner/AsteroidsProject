package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreditsFrame extends JFrame
{
	private final JLabel returnToMainMenu;
	private final CreditsPanel creditsPanel;
	
	public CreditsFrame()
	{
		super("Asteroids - Credits");
		returnToMainMenu = new JLabel();
		returnToMainMenu.setVisible(true);
		Rectangle r = new Rectangle(540, 400, 250, 100);
		returnToMainMenu.setBounds(r);
		returnToMainMenu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// sleep just a little bit for smoothness
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {}
				dispose();
				new StartingGameFrame();
			}
		});
		add(returnToMainMenu);
		
		creditsPanel = new CreditsPanel();
		add(creditsPanel);
		
		setVisible(true);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
}
