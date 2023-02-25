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
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * This class represents the spacecraft object that the player is playing with.
 */

@Slf4j
public class Player extends Thread implements Serializable, ActionListener
{
	private int x; // x coordinate of the player

	private int y; // y coordinate of the player

	private final int startX; // starting x coordinate of the player
	private final int startY; // starting y coordinate of the player
	
	private final Game game; // instance of the game panel

	public Game.KeyboardListener controls; // key listener for the player

	private int[] arrx; // array to represent the x coordinates of the player polygon
	private int[] arry; // array to represent the y coordinates of the player polygon

	public Polygon[] arrLives = new Polygon[3]; // array of polygons to represent the lives of the player

	int[] xLivesArray; // array to represent the x of the player
	int[] yLivesArray; // array to represent the y of the player

	@Getter
	@Setter
	public Polygon polygon; // polygon of the player

	private Polygon respawnPolygon; // polygon that will help to respawn the player after he is dead
	
	public volatile boolean isDead = false; // is the player already dead or not

	private Color playerColor = Color.WHITE;

	@Getter
	@Setter
	private int lives = 3; // how many lives are left for the player

	@Getter
	@Setter
	private int score = 0; // how many points the player scored
	
	private double angle = 0; // angle of rotation
	
	public volatile boolean visible = true; // variable to know whether the player is visible or not
	
	public volatile boolean collided = false; // variable to know whether the player collided with another object or not
	
	public volatile int direction = 0; // variable to know what is the direction the player is moving in
	
	private volatile boolean isInSlope = false; // variable to know when the player is positioned with a slope or not

	private volatile long startTime = 0; // variable to store the starting time when a player collided with another object

	private volatile long currTime = 0; // variable to store the current time

	private volatile long startTimeAfterRespawn = 0; // variable to know the time when the respawn process ended

	private volatile long currentTimeAfterRespawn = 0; // variable to know the time after the respawn process ended

	private Timer timer; // timer to notify every fixed amount of time about event

	public volatile boolean connected; // variable to check if we are connected to the server

	public volatile boolean canShoot = true; // variable to know if the player can shoot or no (the player can shoot every 150 milliseconds)

	private int index; // index of the player according to its game panel

	private volatile boolean transferred = false; // variable to know if we finished the process of transferring the player to its opposite edge

	public volatile boolean isPlayerSafelyRespawned = true; // variable to know whether the player is safe after he respawned

	/***
	 * constructor
	 * @param x - starting x coordinate of the player
	 * @param y - starting y coordinate of the player
	 * @param game - the game panel the player is in
	 * @param controls - the key listener the player gets updated with
	 * @param panelIndex - the index of the panel that belongs to the player
	 */
	public Player(int x, int y, Game game, Game.KeyboardListener controls, int panelIndex)
	{
		this.x = x;
		this.y = y;
		this.startX = x;
		this.startY = y;
		this.game = game;
		this.controls = controls;
		this.index = panelIndex;
		this.connected = true;
		arrx = new int[]{x, x - 20, x + 20};
		arry = new int[]{y, y + 70, y + 70};
		polygon = new Polygon(arrx, arry, 3);
		respawnPolygon = new Polygon();
		for (int i = 0; i < polygon.npoints; i++)
		{
			respawnPolygon.addPoint(polygon.xpoints[i], polygon.ypoints[i]);
		}
		this.initializePolygonLives();
	}

	/***
	 * another constructor
	 * @param x - starting x coordinate of the player
	 * @param y - starting y coordinate of the player
	 * @param controls - the key listener the player gets updated with
	 */
	public Player(int x, int y, Game.KeyboardListener controls)
	{
		this.x = x;
		this.y = y;
		this.startX = x;
		this.startY = y;
		this.game = null;
		this.controls = controls;
		this.connected = true;
		arrx = new int[]{x, x - 20, x + 20};
		arry = new int[]{y, y + 70, y + 70};
		polygon = new Polygon(arrx, arry, 3);
		this.initializePolygonLives();
	}

	/***
	 * copy constructor
	 * @param player - the player we copy its data from
	 */
	public Player(Player player)
	{
		this.x = player.x;
		this.y = player.y;
		this.startX = x;
		this.startY = y;
		this.game = player.game;
		arrx = new int[]{x, x - 20, x + 20};
		arry = new int[]{y, y + 70, y + 70};
		polygon = new Polygon(arrx, arry, 3);
		this.initializePolygonLives();
	}

	/***
	 * initialize the array of lives
 	 */
	public void initializePolygonLives()
	{
		int x = (Game.singlePlayerMode) ? 90 : 180, y = 15;
		for (int i = 0; i < this.arrLives.length; i++)
		{
			xLivesArray = new int[]{x, x - 5, x + 5};
			yLivesArray = new int[]{y + index * 50, y + 15 + index * 50, y + 15 + index * 50};
			arrLives[i] = new Polygon(xLivesArray, yLivesArray, 3);
			x += 15;
		}
	}

	/***
	 * action listener so that the player can shoot a limited rounds per second.
	 * @param e the event to be processed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.canShoot = true;

		if (this.visible && !this.isPlayerSafelyRespawned) {
			currentTimeAfterRespawn = System.nanoTime();

			// only if 2 seconds passed after the player respawned, we can safely let him get destroyed again
			if ((currentTimeAfterRespawn - startTimeAfterRespawn) / 1000000000 >= 2) {
				playerColor = Color.WHITE;

				startTimeAfterRespawn = 0;
				currentTimeAfterRespawn = 0;

				isPlayerSafelyRespawned = true;
			}
		}
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
	 * function to handle the keys that are pressed
	 * @param keys - list of the keys to handle
	 */
	private void manageControls(LinkedList<Integer> keys) throws ConcurrentModificationException {
		if (this.visible && this.index == game.getIndex()) {
			for (Integer key : keys) {
				switch (key) {
					case KeyEvent.VK_UP, KeyEvent.VK_W, KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
						this.direction = (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) ? 1 : -1;
						this.move();
					}
					case KeyEvent.VK_LEFT, KeyEvent.VK_A, KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
						if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
							this.rotateLeft();
						else
							this.rotateRight();
						this.rotateByAcceleration();
					}
					case KeyEvent.VK_SPACE -> {
						if (this.canShoot) {
							this.canShoot = false;
							this.addBulletToListForShooter();
							AudioUtil.playAudio("src/main/resources/sounds/fire.wav");
						}
					}
				}
			}
		}
	}

	public boolean getIsInSlope () { return isInSlope; }

	/***
	 * add bullet to the list of balls in the game panel
	 * we make this function here to make it easier to refer the player from which the ball came out
	 */
	public void addBulletToListForShooter()
	{
		Ball b = new Ball(this.getPolygon().xpoints[0],
				this.getPolygon().ypoints[0], game);
		b.isFromShooter = true;
		b.whichPlayerShot = this;
		game.balls.add(b);
		game.balls.get(game.balls.size() - 1).start();
	}

	/***
	 * check if the x value of a certain point is in bounds
	 * @param index - index that represent the x value of the point from the polygon we want to check from
	 * @return - return true if the x value is in bounds, otherwise return false.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private boolean checkXBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return polygon.xpoints[index] > 0 && polygon.xpoints[index] < Constants.SCREEN_WIDTH;
	}

	/***
	 * check if the y value of a certain point is in bounds
	 * @param index - index that represent the y value of the point from the polygon we want to check from
	 * @return - return true if the y value is in bounds, otherwise return false.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private boolean checkYBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return polygon.ypoints[index] > 0 && polygon.ypoints[index] < Constants.SCREEN_HEIGHT;
	}

	/***
	 * check if a certain point from the polygon is in bounds
	 * @param index - index that represent the ceratin point from the polygon that we want to check
	 * @return - return true if the point is in bounds, otherwise return false.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private boolean checkBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return checkXBounds(index) && checkYBounds(index);
	}

	/***
	 * move the player according to the commands from the key listener
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void move() throws ArrayIndexOutOfBoundsException
	{
		Point center = findCentroidOfTriangle();
		boolean isInBounds = checkBounds(0) || checkBounds(1) || checkBounds(2);

		if (isInBounds)
		{
			this.transferred = false;

			/***
			 * This condition means we need to move straight up/down/left/right
			 * We check with absolute function because when we do the rotation there
			 * might be some affect on the polygon in it's coordinates.
			 */
			if (Math.abs(polygon.xpoints[0] - center.x) <= 1
					|| Math.abs(polygon.ypoints[0] - center.y) <= 1)
			{
				if (Math.abs(polygon.ypoints[0] - center.y) <= 1) // means we need to move left or right
				{
					for (int i = 0; i < polygon.npoints; i++)
					{
						if (polygon.xpoints[0] > center.x)
							polygon.xpoints[i] += 10 * direction;
						else
							polygon.xpoints[i] += 10 * direction * (-1);
					}
				}

				else // means we need to move up or down
				{
					for (int i = 0; i < polygon.npoints; i++)
					{
						if (polygon.ypoints[0] > center.y)
							polygon.ypoints[i] += 10 * direction;
						else
							polygon.ypoints[i] += 10 * direction * (-1);
					}
				}
			}

			else
				accelerate();
		}

		else {
			if (!this.transferred)
				transferPlayer();
		}
	}

	/***
	 * function that moves the shooter only when the shooter is positioned with a slope
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void accelerate() throws ArrayIndexOutOfBoundsException
	{
		Point centerPt = findCentroidOfTriangle(); // center point calculation

		int speedX = (centerPt.x < polygon.xpoints[0]) ? 10 : -10;
		int speedY = (centerPt.y > polygon.ypoints[0]) ? -10 : 10;

		for (int i = 0; i < polygon.npoints; i++)
		{
			polygon.xpoints[i] += speedX * direction;
			polygon.ypoints[i] += speedY * direction;
		}
	}

	/***
	 * find the value that we need to add/sub from each x/y coordinate
	 * @param isOutOfWidth - is the polygon out of the sides of the screen
	 * @return - return the max - min value of the x/y according to isOutOfWidth
	 */
	private int findValueForChange(boolean isOutOfWidth) {
		// transfer left or right
		if (isOutOfWidth) {
			return Math.max(polygon.xpoints[0], Math.max(polygon.xpoints[1], polygon.xpoints[2]))
					- Math.min(polygon.xpoints[0], Math.max(polygon.xpoints[1], polygon.xpoints[2]));
		}

		// else, return the difference between the y values because we need to transfer up or down
		return Math.max(polygon.ypoints[0], Math.max(polygon.ypoints[1], polygon.ypoints[2]))
				- Math.min(polygon.ypoints[0], Math.max(polygon.ypoints[1], polygon.ypoints[2]));
	}

	/***
	 * transfer the player to the opposite edge of the screen
	 */
	private void transferPlayer() {

		boolean isOutOfWidth = !(checkXBounds(0) || checkXBounds(1) || checkXBounds(2));
		int toRemoveOrAdd = findValueForChange(isOutOfWidth);
		int sign = (polygon.xpoints[0] >= Constants.SCREEN_WIDTH || polygon.ypoints[0] >= Constants.SCREEN_HEIGHT) ? -1 : 1;
		int valueToTransfer = (isOutOfWidth) ? (Constants.SCREEN_WIDTH + toRemoveOrAdd) * sign : (Constants.SCREEN_HEIGHT + toRemoveOrAdd) * sign;

		log.info("TRANSFERRED FROM : ");
		log.info("x's : " + Arrays.toString(polygon.xpoints));
		log.info("y's : " + Arrays.toString(polygon.ypoints));

		for (int i = 0; i < polygon.npoints; i++) {
			if (isOutOfWidth)
				polygon.xpoints[i] += valueToTransfer;
			else
				polygon.ypoints[i] += valueToTransfer;
		}

		log.info("TO : ");
		log.info("x's : " + Arrays.toString(polygon.xpoints));
		log.info("y's : " + Arrays.toString(polygon.ypoints));

		this.transferred = true;
	}

	/***
	 * draw the player in the game panel
	 * @param g - the graphics variable we draw in this player
	 */
	public void drawPlayer(Graphics g)
	{
		g.setColor(this.playerColor);
		g.drawPolygon(polygon);
	}

	/***
	 * draw the lives left and the score of the player
	 * @param g - the graphics variable we draw in this player
	 */
	public void drawLivesAndScore(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 17));

		int ind = index + 1; // player number
		String toDisplay = (Game.singlePlayerMode) ? "lives left: " : "lives left for player" + ind + ": ";

		g.drawString(toDisplay, 5, 30 + index * 50);

		for (int i = 0; i < this.lives; i++) {
			Color clr = (this.lives == 3) ? Color.GREEN : (this.lives == 2) ? Color.YELLOW : Color.RED;
			g.setColor(clr);
			g.drawPolygon(arrLives[i]);
		}

		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 17));

		if (Game.singlePlayerMode)
			g.drawString("Score : " + this.score, 555, 25);
		else
			g.drawString("Score of player number " + ind + ": " + this.score, 555, 25 + index * 30);
	}

	/***
	 * function that finds the center point of the player polygon
	 * @return - new point that represents the center of the player
	 */
	public final Point findCentroidOfTriangle()
	{
		int x = (polygon.xpoints[0] + polygon.xpoints[1] + polygon.xpoints[2]) / 3;
		int y = (polygon.ypoints[0] + polygon.ypoints[1] + polygon.ypoints[2]) / 3;
		return new Point(x, y);
	}

	/***
	 * rotate each point of the polygon by the current angle value
	 */
	public void rotateByAcceleration() 
	{
		Point cent = findCentroidOfTriangle();
		
		for (int i = 0; i < polygon.npoints; i++)
		{
			double newX = cent.x + (polygon.xpoints[i] - cent.x) * Math.cos(angle) - (polygon.ypoints[i] - cent.y) * Math.sin(angle);
			double newY = cent.y + (polygon.xpoints[i] - cent.x) * Math.sin(angle) + (polygon.ypoints[i] - cent.y) * Math.cos(angle);
			
			polygon.xpoints[i] = (int)Math.floor(newX);
			polygon.ypoints[i] = (int)Math.floor(newY);
		}
		
		isInSlope = Math.abs(polygon.xpoints[0] - findCentroidOfTriangle().x) > 1 && Math.abs(polygon.ypoints[0] - findCentroidOfTriangle().y) > 1;
	}

	/***
	 * rotate the player clockwise 45 degrees celsius
	 */
	public void rotateRight() {
		angle = Math.PI / 4;
	}

	/***
	 * rotate the player counterclockwise 45 degrees celsius
	 */
	public void rotateLeft () {
		angle = -Math.PI / 4;
	}

	/***
	 * respawn the player
	 */
	private void respawn()
	{
		polygon = new Polygon(this.respawnPolygon.xpoints, this.respawnPolygon.ypoints, 3);
		this.isInSlope = false;
	}

	@Override
	public void run()
	{
		timer = new Timer(150, this);
		timer.start();

		while (!game.isGameFinished && lives > 0)
		{
			synchronized (this) {
				if (game.getIsGamePaused()) {
					try {
						wait();
					}catch(InterruptedException ignored) {}
				}
			}

			manageControls(controls.pressedKeys);

			// variable to know whether the player is out of bounds or in bounds
			boolean inBounds = checkBounds(0) && checkBounds(1) && checkBounds(2);

			// only if the player is in bounds, we can check if it makes collision
			if (inBounds)
			{
				// check for all asteroids
				for (int indAst = 0; indAst < game.asteroids.size() && !this.collided && this.isPlayerSafelyRespawned; indAst++)
				{
					if (!collided)
					{
						Asteroid tmp = game.asteroids.get(indAst);
						if (tmp.isAlive() && Hits.isShooterHittingAsteroid(this, tmp))
						{
							if (!game.isIterating) {
								startTime = System.nanoTime(); // set a start time for the collision
								this.visible = false;
								collided = true;

								game.asteroids.get(indAst).collided = true;
								game.asteroids.get(indAst).chooseSoundOfBang();

								game.asteroids.get(indAst).setPolygon(new Polygon(new int[]{}, new int[]{}, 0));

								this.lives--;
								log.info("Player got hit by asteroid");
							}
						}
					}
				}

				// check for all spaceships
				for (int indSpace = 0; indSpace < game.spaceships.size() && !this.collided && this.isPlayerSafelyRespawned; indSpace++)
				{
					if (game.spaceships.get(indSpace).getPolygon().npoints > 0
							&& !collided && game.spaceships.get(indSpace).getPolygon().xpoints[2] > 150
							&& game.spaceships.get(indSpace).getPolygon().xpoints[7] < 1150)
					{
						if (Hits.isSpaceshipHittingShooter(game.spaceships.get(indSpace), this))
						{
							if (!game.isIterating) {
								startTime = System.nanoTime(); // set a start time for the collision
								this.visible = false;
								collided = true;

								game.spaceships.get(indSpace).collided = true;
								game.spaceships.get(indSpace).chooseSoundOfBang();
								game.spaceships.get(indSpace).setPolygon(new Polygon(new int[]{}, new int[]{}, 0));

								this.lives--;
								log.info("Player got hit by spaceship");
							}
						}
					}
				}

				// check for all bullets
				for (int indBall = 0; indBall < game.balls.size() && !this.collided && this.isPlayerSafelyRespawned; indBall++)
				{
					if (game.balls.get(indBall).isFromSpaceship && game.balls.get(indBall).isAlive() &&
							Hits.isShooterHittingBall(game.balls.get(indBall), this))
					{
						if (!game.isIterating) {
							startTime = System.nanoTime(); // set a start time for the collision
							this.visible = false;
							collided = true;
							game.balls.get(indBall).collided = true;

							game.balls.get(indBall).setSize(Constants.DEAD_BALL_SIZE);
							game.balls.get(indBall).chooseSoundOfBang();
							game.balls.remove(indBall);

							this.lives--;
							log.info("player got hit by ball");
						}
					}
				}
			}

			if (this.collided || !this.visible)
			{
				currTime = System.nanoTime(); // get the current time

				// check if at least 3 seconds passed from the hit
				if ((currTime - startTime) / 1000000000 >= 3)
				{
					// get the time of the respawn
					startTimeAfterRespawn = currTime;

					// temporary color for the respawn
					playerColor = Color.BLUE;

					// nullify values of times
					currTime = 0;
					startTime = 0;

					// respawn the shooter
					this.respawn();

					// give back the original values to this variables
					this.isPlayerSafelyRespawned = false;
					this.collided = false;
					this.visible = true;
				}
			}

			if (this.lives > 0) {
				if (this.visible) {
					try {
						Thread.sleep(60);
					}catch(InterruptedException e) {}
					game.repaint();
				}
			}

			else {
				game.isGameFinished = true;
			}
		}
	}
}