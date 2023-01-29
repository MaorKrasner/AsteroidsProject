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
import java.util.Arrays;
import java.util.Random;

/**
 * This class represents the asteroid object in the game.
 */
@Slf4j
public class Asteroid extends Thread implements Hittable, ActionListener, Serializable
{
	private final Game game; // instance of the game panel

	Random rnd = new Random(); // object to generate random values

	private int x; // x coordinate of the asteroid
	private int y; // y coordinate of the asteroid

	private final int startX; // the x coordinate value of the start location
	private final int startY; // the y coordinate value of the start location

	private final int chosenDirX; // value of the direction of movement of the asteroid in x-axis
	private int chosenDirY; // value of the direction of movement of the asteroid in y-axis

	private Timer timer; // timer to notify every fixed amount of time about event

	private final SizeTypes sizeTypes; // the size of the asteroid
	private final AsteroidType type; // the type of the asteroid

	private final AsteroidsMonitor asteroidsMonitor; // monitor to synchronize the asteroids

	@Getter
	@Setter
	private Polygon polygon;

	private int[] xAxios; // array to represent the x coordinates of the asteroid polygon
	private int[] yAxios; // array to represent the y coordinates of the asteroid polygon

	public volatile boolean collided = false; // variable to know if the asteroid collided with another object

	private volatile boolean isOutOfBounds = false; // variable to know when the asteroid is out of bounds

	private volatile boolean inTheZone = false; // variable to know whether the asteroid is in the bounds of the screen or not

	private final int serialIndex; // the index of the asteroid in the game

	private static int asteroidIndex = 0; // static counter to set the serial index of the asteroid


	/***
	 * constructor
	 * @param x - starting x coordinate of the asteroid
	 * @param y - starting y coordinate of the asteroid
	 * @param size - size of the asteroid
	 * @param type - type of the asteroid
	 * @param game - game panel that the asteroid is in
	 * @param asteroidsMonitor - monitor that the asteroid is managed by
	 */
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
		this.serialIndex = asteroidIndex++;
		initializeArrays();
		this.polygon = new Polygon(xAxios, yAxios, xAxios.length);
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
		if (!this.inTheZone) {
			chosenDirY = 0;
			this.hasTheAsteroidEnteredTheBounds();
		}
		else {
			int before = chosenDirY;
			chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
			while (before == chosenDirY) // while we didn't get other value, we randomize
				chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
		}
	}

	/***
	 * function that add points to the score of the shooter according to the size of asteroid
	 */
	public void addPointsToShooter()
	{
		Player currentPlayer = game.players.get(game.getIndex());
		switch (sizeTypes) {
			case BIG -> currentPlayer.setScore(currentPlayer.getScore() + Constants.BIG_HIT_ASTEROID);
			case MEDIUM -> currentPlayer.setScore(currentPlayer.getScore() + Constants.MEDIUM_HIT_ASTEROID);
			case SMALL -> currentPlayer.setScore(currentPlayer.getScore() + Constants.SMALL_HIT_ASTEROID);
		}
	}

	/***
	 * draw the asteroid in the game panel
	 * @param g - the graphics variable we draw in this asteroid
	 */
	public void drawAsteroid(Graphics g)
	{
		if (!collided) {
			g.setColor(Color.WHITE);
			g.drawPolygon(polygon);
		}
	}

	/***
	 * function that builds the arrays of the axios according to the size and type of the asteroid
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

	/***
	 * function that checks if the asteroid is out of bounds or not
	 * @return - return true if the asteroid is out of bounds, otherwise return false.
	 */
	private boolean isAsteroidOutOfBounds()
	{
		for (int i = 0; i < polygon.npoints; i++)
		{
			if (!(polygon.xpoints[i] < 0 || polygon.xpoints[i] > Constants.SCREEN_WIDTH || polygon.ypoints[i] < 0 || polygon.ypoints[i] > Constants.SCREEN_HEIGHT))
				return false;
		}
		return true;
	}

	/***
	 * function to check if the polygon of the asteroid has entered the area of the screen and update the variable inTheZone
	 */
	private void hasTheAsteroidEnteredTheBounds()
	{
		int rightestX = Integer.MIN_VALUE;
		int leftestX = Integer.MAX_VALUE;

		for (int i = 0; i < polygon.npoints; i++)
		{
			if (polygon.xpoints[i] > rightestX)
				rightestX = polygon.xpoints[i];
			if (polygon.xpoints[i] < leftestX)
				leftestX = polygon.xpoints[i];
		}

		this.inTheZone = (chosenDirX == 1) ? leftestX > 0 : rightestX < Constants.SCREEN_WIDTH;
	}

	public void run()
	{
		asteroidsMonitor.waitForMyTurn(this.serialIndex); // start the task of the thread

		timer = new Timer(750, this);
		timer.start();

		while (!game.isGameFinished && !collided && !isOutOfBounds)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					} catch(InterruptedException ignored) {}
				}
			}

			// check if the spaceship is out of bounds
			if (!collided)
				isOutOfBounds = this.inTheZone && isAsteroidOutOfBounds();

			if (!collided && !isOutOfBounds)
			{
				polygon.translate(chosenDirX, chosenDirY);
				x += chosenDirX;
				y += chosenDirY;

				try {
					Thread.sleep(5);
				}catch(InterruptedException ignored) {};

				game.repaint();
			}
		}

		timer.stop();
		polygon.translate(4000, 4000);
		log.info("Asteroid " + this.serialIndex + " is dead");

		asteroidsMonitor.imDone(this.serialIndex); // finish the task of the thread
	}
}
