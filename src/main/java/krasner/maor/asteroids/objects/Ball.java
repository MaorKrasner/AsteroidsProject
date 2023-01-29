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
 */

@Slf4j
public class Ball extends Thread implements Hittable, Serializable
{
	@Getter
	@Setter
	private int x; // x coordinate of the ball

	@Getter
	@Setter
	private int y; // y coordinate of the ball

	@Getter
	@Setter
	private int size; // size of the ball
	
	private final Game game; // instance of the game panel
	
	public volatile boolean isFromSpaceship = false; // does the ball came from a spaceship
	public volatile boolean isFromShooter = false; // does the ball came from the shooter
	
	public int indexFromSpaceship = -1; // index of the spaceship that shot this ball

	private final int startX; // x coordinate of the starting location of the ball
	private final int startY; // y coordinate of the starting location of the ball
	
	public volatile boolean collided = false; // variable to know whether the ball collided with another object or not

	public volatile boolean isIterating = false; // variable to know if we iterate through this ball in the panel right now to prevent ConcurrentModificationException

	public volatile Player whichPlayerShot; // player object variable to know from where the ball has came from

	/***
	 * constructor
	 * @param x - starting x coordinate of the ball
	 * @param y - starting y coordinate of the ball
	 * @param game - game panel that the ball is in
	 */
	public Ball(int x, int y, Game game)
	{
		this.x = x;
		this.y = y;
		this.size = Constants.BALL_SIZE;
		this.startX = x;
		this.startY = y;
		this.game = game;
	}

	/***
	 * just a default constructor to initialize the ball that is in the spaceship it won't throw errors
	 */
	public Ball()
	{
		this(-10, -10, null);
	}

	@Override
	public String toString()
	{
		return this.x + "," + this.y + "--" + "\n";
	}

	/***
	 * function to draw the ball
	 * @param g - graphics of the panel to draw with
	 */
	public void drawBall(Graphics g)
	{
		if (!collided) {
			g.setColor(Color.WHITE);
			g.fillOval(x, y, size, size);
		}
	}
	
	@Override
	public void chooseSoundOfBang() 
	{
		AudioUtil.playAudio("src/main/resources/sounds/Explosionball.wav");
	}

	/***
	 * function to check if the ball is still in the area of the screen
	 * @return - return true if the ball didn't pass the edges of the screen, otherwise return false.
	 */
	private boolean isBallOutOfBounds()
	{
		return x < 0 || x > Constants.SCREEN_WIDTH || y < 0 || y > Constants.SCREEN_HEIGHT;
	}
	
	public void run()
	{
		int dirx = 0, diry = 0; // speed values of x and y coordinates
		
		// temporary value that we will use in case that the spaceship dies, but the ball is still
		// alive. In that case, we will let him continue on its own as it did before until it dies.
		int temp = 0;
		
		boolean setTemp = false;

		while (!game.isGameFinished && !isBallOutOfBounds() && !collided)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					}catch(InterruptedException ignored) {}
				}
			}

			// check for all spaceships
			for (int i = 0; i < game.spaceships.size() && !this.collided; i++)
			{
				if (game.spaceships.get(i).getPolygon().npoints > 0
						&& game.spaceships.get(i).getPolygon().xpoints[2] > 150
							&& game.spaceships.get(i).getPolygon().xpoints[7] < 1150)
				{
					if (Hits.isBallHittingSpaceship(this, game.spaceships.get(i)))
					{
						if (!game.isIterating) {
							collided = true;
							game.spaceships.get(i).collided = true;
							this.size = Constants.DEAD_BALL_SIZE;

							if (isFromShooter)
								game.spaceships.get(i).addPointsToShooter();

							game.spaceships.get(i).chooseSoundOfBang();
							game.spaceships.get(i).polygon = new Polygon(new int[]{}, new int[]{}, 0); // make the spaceship invisible

							log.info("Ball killed by spaceship");
						}
					}
				}
			}

			// check for all asteroids
			for (int i = 0; i < game.asteroids.size() && !this.collided; i++)
			{
				Asteroid tmp = game.asteroids.get(i);
				if (!collided && Hits.isBallHittingAsteroid(this, game.asteroids.get(i)))
				{
					if (!game.isIterating) {
						collided = true;
						game.asteroids.get(i).collided = true;
						this.size = Constants.DEAD_BALL_SIZE;

						if (isFromShooter)
							game.asteroids.get(i).addPointsToShooter();

						game.asteroids.get(i).chooseSoundOfBang();
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
					dirx = game.spaceships.get(indexFromSpaceship).getChosenDirX();
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
				Polygon shp = whichPlayerShot.getPolygon();
				Point center = whichPlayerShot.findCentroidOfTriangle();

				if (!whichPlayerShot.getIsInSlope())
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

		x += 4000;
		y += 4000;
	}
}
