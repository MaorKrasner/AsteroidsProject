package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class StartingGamePanel extends JPanel
{
	private final Image backgroundImage;
	
	public StartingGamePanel()
	{
		super();
		ImageIcon ic = new ImageIcon("src/main/resources/images/asteroidsbackgif.gif");
		backgroundImage = ic.getImage();
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
	}
	
	public void paintComponent(Graphics graphics)
	{ 
		// must be the first line, so we can draw on the gif and it will not "destroy" the strings
		graphics.drawImage(backgroundImage, 0, 0, 1280, 720, this);
		
		graphics.setFont(new Font("Segoe Script", Font.BOLD, 50));
		graphics.setColor(Color.white);
		graphics.drawString("ASTEROIDS", 450, 50);
		graphics.drawString("play", 585, 550);
		graphics.drawString("instructions", 100, 550);
		graphics.drawString("credits", 963, 550);
	}
}
