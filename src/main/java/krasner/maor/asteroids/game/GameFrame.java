package krasner.maor.asteroids.game;

import krasner.maor.asteroids.util.Constants;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame
{
	private Game game;
	public GameFrame()
	{
		super("Asteroids - Live Game");
		this.game = new Game(this);
		add(game);
		setVisible(true);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFocusable(false);
		game.requestFocusInWindow();
		setBackground(Color.black);
		setResizable(false);
	}

	public Game getPanel() {
		return game;
	}
}
