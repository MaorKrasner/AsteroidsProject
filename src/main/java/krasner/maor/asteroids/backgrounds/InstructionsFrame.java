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
		JLabel returnToMainMenu = new JLabel();
		returnToMainMenu.setVisible(true);
		Rectangle r = new Rectangle(540, 400, 250, 100);
		returnToMainMenu.setBounds(r);
		returnToMainMenu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// sleep just a little bit for smoothness
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {}
				dispose(); // destroy current frame
				new StartingGameFrame(); // create a new game frame
			}
		});
		add(returnToMainMenu);
		
		add(new InstructionsPanel());
		
		setVisible(true);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
}
