package krasner.maor.asteroids.objects;

import krasner.maor.asteroids.game.Game;
import krasner.maor.asteroids.util.AudioUtil;
import krasner.maor.asteroids.util.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.Serializable;

/**
 * this class represents the ball object in the game.
 ***/

@Slf4j
public class Ball extends Thread implements Hittable, Serializable
{
	@Getter
	@Setter
	private int x, y; // coordinates of the ball

	@Getter
	@Setter
	private int size;
	
	Game game;
	
	public volatile boolean isFromSpaceship = false; // does the ball came from a spaceship
	public volatile boolean isFromShooter = false; // does the ball came from the shooter
	
	public int indexFromSpaceship = -1; // index of the spaceship from the array

	// variables to represent the starting point of the ball
	private final int startX;
	private final int startY;
	
	public volatile boolean foundHit = false; // if the ball hit an object

	public volatile boolean isIterating = false; // variable to know if we iterate through this ball right now to handle ConcurrentModificationException

	private volatile boolean out = false;

	public volatile Player whichPlayerShooted;
	
	public Ball(int x, int y, Game game)
	{
		this.x = x;
		this.y = y;
		this.size = Constants.BALL_SIZE;
		this.startX = x;
		this.startY = y;
		this.game = game;
	}

	// just a default constructor to initialize the ball that is in the spaceship it won't throw errors
	public Ball()
	{
		this(-10, -10, null);
	}

	@Override
	public String toString()
	{
		return this.x + "," + this.y + "--" + "\n";
	}

	// draw the ball
	public void drawBall(Graphics g)
	{
		if (!foundHit) {
			g.setColor(Color.WHITE);
			g.fillOval(x, y, size, size);
		}
	}
	
	@Override
	public void chooseSoundOfBang() 
	{
		AudioUtil.playAudio("src/main/resources/sounds/Explosionball.wav");
	}
	
	public void run()
	{
		int dirx = 0, diry = 0; // speed values of x and y coordinates
		
		// temporary value that we will use in case that the spaceship dies, but the ball is still
		// alive. In that case, we will let him continue on its own as it did before until it dies.
		int temp = 0;
		
		boolean setTemp = false;

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

		log.info("BALL - OUT");
		*/

		while (!game.isGameFinished && (x < 1200 && x > 80 && y > 50 && y < 670) && !foundHit)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					}catch(InterruptedException ignored) {}
				}
			}

			// check for all spaceships
			for (int i = 0; i < game.spaceships.size() && !foundHit; i++)
			{
				if (game.spaceships.get(i).getPolygon().npoints > 0
						&& game.spaceships.get(i).getPolygon().xpoints[2] > 150
							&& game.spaceships.get(i).getPolygon().xpoints[7] < 1150)
				{
					if (Hits.isBallHittingSpaceship(this, game.spaceships.get(i)))
					{
						if (!game.isIterating) {
							foundHit = true;
							this.size = Constants.DEAD_BALL_SIZE;

							game.spaceships.get(i).foundHit = true;
							if (isFromShooter)
								game.spaceships.get(i).addPointsToShooter();
							game.spaceships.get(i).chooseSoundOfBang();
							// make the spaceship invisible
							game.spaceships.get(i).polygon = new Polygon(new int[]{}, new int[]{}, 0);
							log.info("Ball killed by spaceship");
						}
					}
				}
			}

			// check for all asteroids
			for (int i = 0; i < game.asteroids.size() && !foundHit; i++)
			{
				if (Hits.isBallHittingAsteroid(this, game.asteroids.get(i)))
				{
					if (!game.isIterating) {
						foundHit = true;
						this.size = Constants.DEAD_BALL_SIZE;

						game.asteroids.get(i).foundHit = true;
						if (isFromShooter)
							game.asteroids.get(i).addPointsToShooter();

						game.asteroids.get(i).chooseSoundOfBang();
						game.asteroids.get(i).isVisible = false;
						game.asteroids.get(i).setPolygon(new Polygon(new int[]{}, new int[]{}, 0)); // make the asteroid invisible
						log.info("Ball killed by asteroid");
					}
				}
			}

			// if the bullet came out from a spaceship
			if (isFromSpaceship)
			{
				diry = 1;
				if (indexFromSpaceship != -1)
				{
					dirx = game.spaceships.get(indexFromSpaceship).chosenDirX;
					if (!setTemp)
					{
						temp = dirx;
						setTemp = true;
					}
				}

				// means that the spaceship is dead
				if (dirx != temp)
					dirx = temp;
				x += dirx;
				y += diry;
			}

			else if (isFromShooter)
			{
				Polygon shp = whichPlayerShooted.getPolygon();
				Point center = whichPlayerShooted.findCentroidOfTriangle();

				if (!whichPlayerShooted.getIsInSlope())
				{
					// only if we didn't move, we can change the value of the direction
					if (startX == x && startY == y)
					{
						// same x
						if (Math.abs(shp.xpoints[0] - center.x) <= 1)
						{
							dirx = 0;
							diry = (shp.ypoints[0] > center.y) ? 1 : -1;
						}

						// same y
						else if (Math.abs(shp.ypoints[0] - center.y) <= 1)
						{
							diry = 0;
							dirx = (shp.xpoints[0] > center.x) ? 1 : -1;
						}
					}
				}

				else
				{
					if (startX == x && startY == y)
					{
						dirx = (center.x < shp.xpoints[0]) ? 1 : -1;
						diry = (center.y > shp.ypoints[0]) ? -1 : 1;
					}

				}

				x += dirx;
				y += diry;
			}

			try {
				Thread.sleep(3);
			} catch(InterruptedException ignored) {}
			game.repaint();
		}
	}
}
