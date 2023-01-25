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
 * This class represents the spaceship object in the game.
 */

@Slf4j
public class Spaceship extends Thread implements ActionListener, Hittable, Serializable
{
	private int x; // x coordinate of the spaceship

	private int y; // y coordinate of the spaceship

	private Timer timer; // timer to notify every fixed amount of time about event

	private int counter = 0; // counter to know when to pop up an event of changing y direction

	private final Game game; // // instance of the game panel

	@Getter
	@Setter
	public Polygon polygon; // polygon shape of the spaceship

	int[] xAxios; // array to represent the x coordinates of the spaceship polygon

	int[] yAxios; // array to represent the y coordinates of the spaceship polygon

	private final SizeTypes size; // the size of the spaceship

	private final SpaceshipsMonitor spaceshipsMonitor; // the monitor that this specific spaceship is managed by

	private final int serialIndex; // the serial index of this spaceship
	
	private static int spaceshipIndex = 0; // static counter for the serial index of each spaceship
	
	public volatile boolean foundHit = false; // variable to know if the spaceship collided with another object

	private volatile boolean isRunning = false; // variable to know whether the spaceship is running or not

	Random rnd = new Random(); // random variable to generate values for the spaceship

	@Getter
	private int chosenDirY; 	// variable of the value of the movement in the y-axis

	@Getter
	private final int chosenDirX; // variable of the value of the movement in the y-axis
	
	private Ball b; // ball object to represent the ball that the spaceship shoots all the time

	private volatile boolean isOutOfBounds = false; // variable to know when the spaceship is out of bounds

	/***
	 * constructor
	 * @param x - starting x coordinate of the spaceship
	 * @param y - starting y coordinate of the spaceship
	 * @param size - the size of the spaceship
	 * @param game - the game panel the spaceship is in
	 * @param spaceshipsMonitor - the monitor this spaceship is managed by
	 */
	public Spaceship(int x, int y, SizeTypes size, Game game, SpaceshipsMonitor spaceshipsMonitor)
	{
		this.x = x;
		this.y = y;
		this.game = game;
		this.spaceshipsMonitor = spaceshipsMonitor;
		this.size = size;
		spaceshipIndex++;
		this.serialIndex = spaceshipIndex;
		initializeArraysValues();
		polygon = new Polygon(xAxios, yAxios, 8);
		b = new Ball(); // garbage value initialization for the ball
		chosenDirX = (x == -100) ? 1 : -1;
		chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
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

	/***
	 * function that builds the arrays of the axios according to the size of the spaceship
	 */
	private void initializeArraysValues()
	{
		switch (size) {
			case BIG -> {
				xAxios = new int[]{x, x - 30, x - 35, x - 60, x - 35, x + 5, x + 30, x + 5};
				yAxios = new int[]{y, y, y + 15, y + 40, y + 65, y + 65, y + 40, y + 15};
			}
			case MEDIUM -> {
				xAxios = new int[]{x, x - 15, x - 18, x - 30, x - 18, x + 3, x + 15, x + 3};
				yAxios = new int[]{y, y, y + 8, y + 20, y + 33, y + 33, y + 20, y + 8};
			}
			case SMALL -> {
				xAxios = new int[]{x, x - 8, x - 9, x - 15, x - 9, x + 1, x + 8, x + 1};
				yAxios = new int[]{y, y, y + 4, y + 10, y + 17, y + 17, y + 10, y + 4};
			}
		}
	}

	/***
	 * funnction to find the index of this spaceship in the array list of spaceships in the game
	 * @return - return the index in the list if the spaceship is found in the list, otherwise return -1.
	 */
	public int findIndexOfSpaceshipInArray()
	{
		for (int i = 0; i < game.spaceships.size(); i++)
		{
			if (game.spaceships.get(i).isAlive() && game.spaceships.get(i) == this)
				return i;
		}

		return -1; 
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (!foundHit && !isOutOfBounds)
		{
			counter++; // every 500 milliseconds (0.5 seconds) , the counter goes up by 1
			
			// if the counter goes up to 2 (1 second), we change the direction of y
			// and spawn a new ball from the correct coordinates
			if(counter == 2 && !foundHit)  
			{
				/***
				 * determine the extra value for the spawn of the ball
				 */
				int valueOfSpawn;
				if (this.size == SizeTypes.BIG)
					valueOfSpawn = Constants.HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_FIRST;
				else if (this.size == SizeTypes.MEDIUM)
					valueOfSpawn = Constants.HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_SECOND;
				else
					valueOfSpawn = Constants.HALF_GAP_BETWEEN_X_ARRAY_IN_IND_4_AND_5_THIRD;
				
				if (isAlive())
				{
					// create a new ball if the other one is already dead and run it immediately
					AudioUtil.playAudio("resources/sounds/fire.wav");
					b = new Ball(polygon.xpoints[4] + valueOfSpawn, polygon.ypoints[4] + 5, game);
					b.isFromSpaceship = true;
					if (!foundHit && !isOutOfBounds)
						b.indexFromSpaceship = findIndexOfSpaceshipInArray(); // FIX HERE
					game.balls.add(b); // add bullet to the list of bullets
					b.start();
				}

				counter = 0;
				int YofBefore = chosenDirY;
				chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
				while (YofBefore == chosenDirY) // while we didn't get other value, we randomize
					chosenDirY = Constants.MOVEMENT_YARR[rnd.nextInt(3)];
			}
		}
		else
			timer.stop(); // stop when we need to
	}

	/***
	 * draw the spaceship in the game panel
	 * @param g - the graphics variable we draw in this spaceship
	 */
	public void drawSpaceship(Graphics g)
	{
		if (!foundHit) {
			g.setColor(Color.WHITE);
			g.drawPolygon(polygon);
		}
	}

	/***
	 * function that add points to the score of the shooter according to the size of spaceship
	 */
	public void addPointsToShooter()
	{
		Player currentPlayer = game.players.get(game.getIndex());
		switch (size) {
			case BIG -> currentPlayer.setScore(currentPlayer.getScore() + Constants.BIG_HIT_SPACESHIP);
			case MEDIUM -> currentPlayer.setScore(currentPlayer.getScore() + Constants.MEDIUM_HIT_SPACESHIP);
			case SMALL -> currentPlayer.setScore(currentPlayer.getScore() + Constants.SMALL_HIT_SPACESHIP);
		}
	}
	
	@Override
	public void chooseSoundOfBang() 
	{
		AudioUtil.playAudio("src/main/resources/sounds/spaceshipexplode.wav");
	}

	@Override
	public void run()
	{
		timer = new Timer(500, this);
		timer.start();
		
		spaceshipsMonitor.waitForMyTurn(this.serialIndex);

		// sleep for 3 seconds as a starting barrier
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e) {}
		
		this.isRunning = true;

		while (!game.isGameFinished && !foundHit && !isOutOfBounds)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					} catch(InterruptedException ignored) {}
				}
			}

			// add for each x and y of the spaceship the value of movement
			polygon.translate(chosenDirX, chosenDirY);
			x += chosenDirX;
			y += chosenDirY;

			// just a condition to let the spaceship have a time to get into the area
			// and not just getting destroyed on the first second as it spawns
			if (polygon.xpoints[2] > 150 && polygon.xpoints[7] < 1150)
			{
				// check for asteroids
				if (!foundHit)
				{
					for (int i = 0; i < game.asteroids.size(); i++)
					{
						if (Hits.isSpaceshipHittingAsteroid(this, game.asteroids.get(i)))
						{
							foundHit = true;
							game.asteroids.get(i).foundHit = true;
							game.asteroids.get(i).chooseSoundOfBang();
							game.asteroids.remove(i);
						}
					}
				}
			}

			// check if the spaceship is out of bounds
			if (!foundHit)
				isOutOfBounds = polygon.xpoints[3] >= Constants.SCREEN_WIDTH + 90 || polygon.xpoints[6] <= -90 ||
						polygon.ypoints[4] <= 0 || polygon.ypoints[0] >= Constants.SCREEN_HEIGHT + 90;

			if (!foundHit && !isOutOfBounds)
			{
				try {
					Thread.sleep(4);
				}catch(InterruptedException ignored) {}
				game.repaint();
			}
			else
			{
				this.isRunning = false;
				log.info("Spaceship " + this.serialIndex + " is dead");
				timer.stop();
			}
		}
		spaceshipsMonitor.imDone(this.serialIndex);
	}
}
