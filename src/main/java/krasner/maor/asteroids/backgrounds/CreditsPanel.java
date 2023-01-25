package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class CreditsPanel extends JPanel
{
	private final Image creditsImage;
	
	public CreditsPanel()
	{
		super();
		ImageIcon ic = new ImageIcon("src/main/resources/images/asteroidsbackgif.gif");
		creditsImage = ic.getImage();
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
	}
	
	public void paintComponent(Graphics g) 
	{ 
		// must be the first line, so we can draw on the gif and it will not "destroy" the strings
		g.drawImage(creditsImage, 0, 0, 1280, 720, this);
		
		g.setFont(new Font("Segoe Script", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("DEVELOPER NAME : Maor Krasner", 450, 100);
		g.drawString("DEVELOPED WITH : JAVA", 450, 150);
		g.drawString("IDE USED : Eclipse 2021-06", 450, 200);
		g.drawString("RETURN TO GAME", 537, 432);
	}
}
