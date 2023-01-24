package krasner.maor.asteroids.objects;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.util.AudioUtil;
import krasner.maor.asteroids.util.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Random;

/**
 * This class represents the asteroid object in the game.
 ***/
@Slf4j
public class Asteroid extends Thread implements Hittable, ActionListener, Serializable
{
	private Game game;

	Random rnd = new Random();

	private int x;
	private int y;

	private final int startX;
	private final int startY;

	private int chosenDirX, chosenDirY;

	private Timer t;

	private SizeTypes sizeTypes; // variable to know what is the size
	private AsteroidType type; // variable to know the type

	private AsteroidsMonitor asteroidsMonitor; // monitor to synchronize the asteroids

	private String name = "";

	@Getter
	@Setter
	private Polygon polygon;

	// 2 arrays to represent the asteroid points
	private int[] xAxios;
	private int[] yAxios;

	public volatile boolean isDead = false;

	public Asteroid[] astChildren; // children of the asteroid

	public Asteroid father;

	public volatile boolean foundHit = false; // check if we found a hit

	// variable to know when the thread is out of bounds
	private volatile boolean isOutOfBounds = false;

	// variable to know when the asteroid is running as a thread
	private volatile boolean isRunningOnScreen = false;

	public volatile boolean isVisible = true;

	public volatile boolean isIterating = false;

	private int serialIndex;

	private static int asteroidIndex = 0;

	private volatile boolean out = false;

	// constructor
	public Asteroid(int x, int y, SizeTypes size, AsteroidType type, Game game, AsteroidsMonitor asteroidsMonitor)
	{
		this.x = x;
		this.y = y;
		this.startX = x;
		this.startY = y;
		this.chosenDirX = (x < 100) ? 1 : -1;
		this.chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
		this.sizeTypes = size;
		this.type = type;
		this.game = game;
		this.asteroidsMonitor = asteroidsMonitor;
		this.name = "Asteroid" + Constants.IND_COUNTER++;
		this.serialIndex = asteroidIndex++;
		initializeArrays();
		this.polygon = new Polygon(xAxios, yAxios, xAxios.length);
		this.astChildren = new Asteroid[2];
	}

	@Override
	public String toString()
	{
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < polygon.npoints; i++) {
			data.append(polygon.xpoints[i]).append(",");
			data.append(polygon.ypoints[i]).append("--");
			data.append("\n");
		}
		return data.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int before = chosenDirY;
		chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
		while (before == chosenDirY) // while we didn't get other value, we randomize
			chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
	}

	// get the serial number of the asteroid thread
	public int getAsteroidNumber()
	{
		String stringSubAstNum = name.substring(8); // thread number
		String orString = name.replace("Asteroid","");
		return Integer.parseInt(stringSubAstNum);
	}

	public SizeTypes getSize() { return sizeTypes; }

	public AsteroidType getType() { return type; }

	public boolean getIsRunningOnScreen() { return isRunningOnScreen; }

	public void addPointsToShooter()
	{
		Player currentPlayer = game.players.get(game.getIndex());
		switch (sizeTypes) {
			case BIG -> currentPlayer.setScore(currentPlayer.getScore() + Constants.BIG_HIT_ASTEROID);
			case MEDIUM -> currentPlayer.setScore(currentPlayer.getScore() + Constants.MEDIUM_HIT_ASTEROID);
			case SMALL -> currentPlayer.setScore(currentPlayer.getScore() + Constants.SMALL_HIT_ASTEROID);
		}
	}

	public void drawAsteroid(Graphics g)
	{
		if (!foundHit) {
			g.setColor(Color.WHITE);
			g.drawPolygon(polygon);
		}
	}

	/***
	 * function that determines the values of the arrays for big size, medium size and small size
 	 */
	private void initializeArrays()
	{
		switch (type)
		{
		case CLOVER:
			switch (sizeTypes) {
				case BIG -> {
					xAxios = new int[]{x, x - 40, x - 3, x - 40, x - 6, x + 15, x + 13, x + 25, x + 62, x + 62, x + 30};
					yAxios = new int[]{y, y + 50, y + 70, y + 90, y + 130, y + 75, y + 130, y + 130, y + 75, y + 50, y};
				}
				case MEDIUM -> {
					xAxios = new int[]{x, x - 20, x - 2, x - 20, x - 3, x + 8, x + 7, x + 13, x + 31, x + 31, x + 15};
					yAxios = new int[]{y, y + 25, y + 35, y + 45, y + 65, y + 38, y + 65, y + 65, y + 38, y + 25, y};
				}
				case SMALL -> {
					xAxios = new int[]{x, x - 10, x - 1, x - 10, x - 1, x + 4, x + 4, x + 7, x + 16, x + 16, x + 8};
					yAxios = new int[]{y, y + 13, y + 18, y + 23, y + 33, y + 19, y + 33, y + 33, y + 19, y + 13, y};
				}
			}
			break;
		case TSTRAIGHT:
			switch (sizeTypes) {
				case BIG -> {
					xAxios = new int[]{x, x - 40, x - 80, x - 120, x - 120, x - 95, x - 15, x + 35, x + 20, x + 40};
					yAxios = new int[]{y, y + 50, y, y + 50, y + 130, y + 160, y + 160, y + 125, y + 80, y + 50};
				}
				case MEDIUM -> {
					xAxios = new int[]{x, x - 20, x - 40, x - 60, x - 60, x - 48, x - 8, x + 18, x + 10, x + 20};
					yAxios = new int[]{y, y + 25, y, y + 25, y + 65, y + 80, y + 80, y + 63, y + 40, y + 25};
				}
				case SMALL -> {
					xAxios = new int[]{x, x - 10, x - 20, x - 30, x - 30, x - 24, x - 4, x + 9, x + 5, x + 10};
					yAxios = new int[]{y, y + 13, y, y + 13, y + 33, y + 40, y + 40, y + 32, y + 20, y + 13};
				}
			}
			break;
		case CURVED:
			switch (sizeTypes) {
				case BIG -> {
					xAxios = new int[]{x, x - 40, x - 30, x - 70, x - 70, x - 50, x - 10, x + 20, x + 40, x - 10, x + 40, x + 40};
					yAxios = new int[]{y, y, y + 40, y + 40, y + 90, y + 150, y + 130, y + 150, y + 110, y + 70, y + 55, y + 40};
				}
				case MEDIUM -> {
					xAxios = new int[]{x, x - 20, x - 15, x - 35, x - 35, x - 25, x - 5, x + 10, x + 20, x - 5, x + 20, x + 20};
					yAxios = new int[]{y, y, y + 20, y + 20, y + 45, y + 75, y + 65, y + 75, y + 55, y + 35, y + 28, y + 20};
				}
				case SMALL -> {
					xAxios = new int[]{x, x - 10, x - 8, x - 17, x - 17, x - 13, x - 3, x + 5, x + 10, x - 3, x + 10, x + 10};
					yAxios = new int[]{y, y, y + 10, y + 10, y + 23, y + 38, y + 33, y + 38, y + 28, y + 18, y + 14, y + 10};
				}
			}
			break;
		}
	}

	/***
	 * play an audio according to the size of the asteroid
 	 */
	@Override
	public void chooseSoundOfBang()
	{
		String toDisplay = switch (sizeTypes) {
			case BIG -> "bangLarge.wav";
			case MEDIUM -> "bangMedium.wav";
			case SMALL -> "bangSmall.wav";
			default -> "";
		};
		AudioUtil.playAudio("src/main/resources/sounds/" + toDisplay);
	}

	private boolean isAsteroidOutOfBounds()
	{
		for (int i = 0; i < polygon.npoints; i++)
		{
			if (polygon.xpoints[i] < -250 || polygon.xpoints[i] > Constants.SCREEN_WIDTH + 250
					|| polygon.ypoints[i] < -250 || polygon.ypoints[i] > Constants.SCREEN_HEIGHT + 250)
				return true;
		}
		return false;
	}

	private boolean isAsteroidInBounds()
	{
		for (int i = 0; i < polygon.npoints; i++)
		{
			if (polygon.xpoints[i] > 70 && polygon.xpoints[i] < 1210 && polygon.ypoints[i] > 70 && polygon.ypoints[i] < 650)
				return true;
		}
		return false;
	}


	public void run()
	{
		/*
		while (!Game.singlePlayerMode && !out) {
			synchronized (this) {
				if (Constants.GAME_INSTANCE_COUNTER < 2) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException ignored){}
				}
				else
					out = true;
			}
		}

		log.info("ASTEROID - OUT");
		*/

		asteroidsMonitor.waitForMyTurn(this.serialIndex); // start the task of the thread

		// sleep for 0.2 seconds as a starting barrier
		try{
			Thread.sleep(200);
		}catch(InterruptedException e) {
			log.info(String.valueOf(e));
		}

		this.isRunningOnScreen = true;

		t = new Timer(1000, this);
		t.start();

		while (!game.isGameFinished && !foundHit && !isOutOfBounds)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					} catch(InterruptedException ignored) {}
				}
			}

			// check if the spaceship is out of bounds
			if (!foundHit)
				isOutOfBounds = isAsteroidOutOfBounds();

			if (!foundHit && !isOutOfBounds)
			{
				polygon.translate(chosenDirX, chosenDirY);
				x += chosenDirX;
				y += chosenDirY;

				try {
					Thread.sleep(5);
				}catch(InterruptedException ignored) {};
				game.repaint();
			}

			else
				log.info("Asteroid " + getAsteroidNumber() + " is dead");
		}

		asteroidsMonitor.imDone(this.serialIndex); // finish the task of the thread
	}
}
