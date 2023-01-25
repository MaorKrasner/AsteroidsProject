package krasner.maor.asteroids.backgrounds;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class InstructionsPanel extends JPanel
{
	private final Image instructionsImage;
	
	public InstructionsPanel()
	{
		super();
		ImageIcon ic = new ImageIcon("src/main/resources/images/asteroidsbackgif.gif");
		instructionsImage = ic.getImage();
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
	}
	
	public void paintComponent(Graphics g) 
	{ 
		g.drawImage(instructionsImage, 0, 0, 1280, 720, this);
		g.setFont(new Font("Segoe Script", Font.BOLD, 25));
		g.setColor(Color.white);
		g.drawString("You are the shooter, the player.", 150, 100);
		g.drawString("Destroy as many asteroids as you can.", 150, 150);
		g.drawString("There are also spaceships, they are fast,small and can shoot. be aware of them.", 150, 200);
		g.drawString("GOOD LUCK", 150, 250);
		g.setFont(new Font("Segoe Script", Font.BOLD, 20));
		g.drawString("RETURN TO GAME", 537, 432);
	}
}
