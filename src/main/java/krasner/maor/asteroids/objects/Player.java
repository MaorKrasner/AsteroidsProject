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
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * This class represents the spacecraft object that the player is playing with.
 ***/

@Slf4j
public class Player extends Thread implements Serializable, ActionListener
{
	public int x, y; // starting coordinate of the polygon

	// first x and y coordinates
	private final int startX;
	private final int startY;
	
	public Game game;

	public Game.KeyboardListener controls;
	
	// arrays for representation of the coordinates shooter
	private int[] arrx;
	private int[] arry;

	// polygons to represent the lives of the shooter
	public Polygon[] arrLives = new Polygon[3];

	int[] xLivesArray; // array to represent the x of the shooter
	int[] yLivesArray; // array to represent the y of the shooter

	@Getter
	@Setter
	public Polygon polygon; // shape of the shooter
	
	public volatile boolean isDead = false; // is the shooter already dead or not

	@Getter
	@Setter
	private int lives = 3; // how many lives are left for the player


	@Getter
	@Setter
	private int score = 0; // how many points the shooter scored
	
	private double angle = 0; // angle of rotation
	
	private double slope = 0.0; // variable to store the slope value between center point and "top" point
	
	public volatile boolean visible = true; // variable to know if we need to represent the shooter or not
	
	public volatile boolean foundHit = false; // check if we found a hit
	
	public volatile int direction = 0; // variable to know what is the direction the player is moving in
	
	private volatile boolean isInSlope = false; // variable to know when the shooter has a slope or not
	
	// variables to validate the respawn of the shooter is valid
	private long startTime = 0;
	private long currTime = 0;

	private Timer t;

	public volatile boolean connected; // variable to check if we are connected to the server

	public volatile boolean canShoot = true;

	public volatile boolean isIterating = false;

	private volatile boolean out = false;

	private int index;

	private volatile boolean transfered = false;

	private volatile boolean outOfBounds;

	// constructor
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
		this.initializePolygonLives();
	}

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

	// copy constructor
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
 	 ***/
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
	 ***/
	@Override
	public void actionPerformed(ActionEvent e) {
		this.canShoot = true;
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
	 ***/
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

	// add bullet to the end of the list and run it
	public void addBulletToListForShooter()
	{
		Ball b = new Ball(this.getPolygon().xpoints[0],
				this.getPolygon().ypoints[0], game);
		b.isFromShooter = true;
		b.whichPlayerShooted = this;
		game.balls.add(b);
		game.balls.get(game.balls.size() - 1).start();
	}

	private boolean checkXBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return polygon.xpoints[index] > 0 && polygon.xpoints[index] < Constants.SCREEN_WIDTH;
	}

	private boolean checkYBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return polygon.ypoints[index] > 0 && polygon.ypoints[index] < Constants.SCREEN_HEIGHT;
	}

	private boolean checkBounds(int index) throws ArrayIndexOutOfBoundsException
	{
		return checkXBounds(index) && checkYBounds(index);
	}

	public void move() throws ArrayIndexOutOfBoundsException
	{
		Point center = findCentroidOfTriangle();
		boolean isInBounds = checkBounds(0) || checkBounds(1) || checkBounds(2);

		//System.out.println("x's : " +  Arrays.toString(polygon.xpoints));
		//System.out.println("y's : " +  Arrays.toString(polygon.ypoints));

		if (isInBounds)
		{
			this.transfered = false;
			/***
			 * This condition means we need to move straight up/down/left/right
			 * We check with absolute function because when we do the rotation there
			 * might be some affect on the polygon in it's coordinates.
			 ***/
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
			if (!this.transfered)
				transferPlayer();
		}
	}

	// move with the shooter according to the speed and the slope
	private void accelerate() throws ArrayIndexOutOfBoundsException
	{
		Point centerPt = findCentroidOfTriangle(); // center point calculation
		this.slope = calculateSlope();

		int speedX = (centerPt.x < polygon.xpoints[0]) ? 10 : -10;
		int speedY = (centerPt.y > polygon.ypoints[0]) ? -10 : 10;

		for (int i = 0; i < polygon.npoints; i++)
		{
			polygon.xpoints[i] += speedX * direction;
			polygon.ypoints[i] += speedY * direction;
		}
	}

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

	private void transferPlayer() {
		boolean isOutOfWidth = !(checkXBounds(0) || checkXBounds(1) || checkXBounds(2));
		int toRemoveOrAdd = findValueForChange(isOutOfWidth);
		int sign = (polygon.xpoints[0] >= Constants.SCREEN_WIDTH || polygon.ypoints[0] >= Constants.SCREEN_HEIGHT) ? -1 : 1;
		int valueToTransfer = (isOutOfWidth) ? (Constants.SCREEN_WIDTH + toRemoveOrAdd) * sign : (Constants.SCREEN_HEIGHT + toRemoveOrAdd) * sign;

		for (int i = 0; i < polygon.npoints; i++) {
			if (isOutOfWidth)
				polygon.xpoints[i] += valueToTransfer;
			else
				polygon.ypoints[i] += valueToTransfer;
		}
		this.transfered = true;
	}

	public void drawPlayer(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawPolygon(polygon);
	}

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
	
	// find the center point of a triangle
	public final Point findCentroidOfTriangle()
	{
		int x = (polygon.xpoints[0] + polygon.xpoints[1] + polygon.xpoints[2]) / 3;
		int y = (polygon.ypoints[0] + polygon.ypoints[1] + polygon.ypoints[2]) / 3;
		return new Point(x, y);
	}
	
	// calculate the slope of shooting
	// by the formula : m = (y2 - y1) / (x2 - x1)
	public final double calculateSlope()
	{
		Point center = findCentroidOfTriangle(); // find the center
		Point top = new Point(polygon.xpoints[0], polygon.ypoints[0]); // get the "top" point
		return (double)(center.y - top.y) / (center.x - top.x) + 5.0; // m = (y2 - y1)/(x2 - x1)
	}
	
	// rotate each point of the polygon by the given value of the angle variable
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
	
	public void rotateRight() { angle = Math.PI / 4; }
	    
	public void rotateLeft () { angle = -Math.PI / 4; }
	
	// respawn the polygon
	private void respawn()
	{
		// 600 , 330
		this.x = startX;
		this.y = startY;
		arrx[0] = x;
		arrx[1] = x - 20;
		arrx[2] = x + 20;
		arry[0] = y;
		arry[1] = y + 70;
		arry[2] = y + 70;
		polygon = new Polygon(arrx, arry, 3);
		this.isInSlope = false;
	}
	
	// calculate the distance between two points for shooter and asteroid
	public int distanceWithAsteroid(Asteroid a)
	{
		double dist = Math.pow(polygon.xpoints[0] - a.getPolygon().xpoints[0], 2) - Math.pow(polygon.ypoints[0] - a.getPolygon().ypoints[0], 2);
		return (int)(Math.sqrt(dist));
	}
	
	// calculate the distance between two points for shooter and spaceship
	public int distanceWithSpaceship(Spaceship s)
	{
		double dist = Math.pow(polygon.xpoints[0] - s.getPolygon().xpoints[0], 2) - Math.pow(polygon.ypoints[0] - s.getPolygon().ypoints[0], 2);
		return (int)(Math.abs(Math.sqrt(dist)));
	}
	
	// after the shooter dies for a couple of seconds, we check if his area
	// is "clean" to continue to play. it means to check if there are
	// no surrounding objects in a distance of 150 points value
	public boolean isAreaSafe()
	{
		boolean foundWrong = false;
		for (int i = 0; i < game.asteroids.size() && !foundWrong; i++)
		{
			if (this.distanceWithAsteroid(game.asteroids.get(i)) < 150)
				foundWrong = true;
		}
		
		if (!foundWrong)
		{
			for (int j = 0; j < game.spaceships.size() && !foundWrong; j++)
			{
				if (this.distanceWithSpaceship(game.spaceships.get(j)) < 150)
					foundWrong = true;
			}
		}
		
		return !foundWrong; // the variable represents the opposite value of the answer
	}

	@Override
	public void run()
	{
		t = new Timer(200, this);
		t.start();

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

			this.outOfBounds = checkBounds(0) && checkBounds(1) && checkBounds(2);

			// only if the player is in bounds, we can check if it makes collision
			if (this.outOfBounds)
			{
				// check for all asteroids
				for (int indAst = 0; indAst < game.asteroids.size() && !this.foundHit; indAst++)
				{
					if (!foundHit)
					{
						Asteroid tmp = game.asteroids.get(indAst);
						if (tmp.isAlive() && Hits.isShooterHittingAsteroid(this, tmp))
						{
							if (!game.isIterating) {
								startTime = System.nanoTime(); // set a start time for the collision
								this.visible = false;
								game.asteroids.get(indAst).isVisible = false;
								game.asteroids.get(indAst).chooseSoundOfBang();
								game.asteroids.remove(tmp);
								lives--;
								foundHit = true;
								log.info("Player got hit by asteroid");
							}
						}
					}
				}

				// check for all spaceships
				for (int indSpace = 0; indSpace < game.spaceships.size() && !this.foundHit; indSpace++)
				{
					if (game.spaceships.get(indSpace).getPolygon().npoints > 0
							&& !foundHit && game.spaceships.get(indSpace).getPolygon().xpoints[2] > 150
							&& game.spaceships.get(indSpace).getPolygon().xpoints[7] < 1150)
					{
						if (Hits.isSpaceshipHittingShooter(game.spaceships.get(indSpace), this))
						{
							if (!game.isIterating) {
								foundHit = true;
								game.spaceships.get(indSpace).foundHit = true;
								game.spaceships.get(indSpace).chooseSoundOfBang();
								game.spaceships.remove(indSpace);
								startTime = System.nanoTime(); // set a start time for the collision
								this.visible = false;
								this.lives--;
								log.info("Player got hit by spaceship");
							}
						}
					}
				}

				// check for all bullets
				for (int indBall = 0; indBall < game.balls.size() && !this.foundHit; indBall++)
				{
					if (game.balls.get(indBall).isFromSpaceship && game.balls.get(indBall).isAlive() &&
							Hits.isShooterHittingBall(game.balls.get(indBall), this))
					{
						if (!game.isIterating) {
							this.visible = false;
							foundHit = true;
							startTime = System.nanoTime(); // set a start time for the collision

							game.balls.get(indBall).setSize(Constants.DEAD_BALL_SIZE);
							game.balls.get(indBall).chooseSoundOfBang();

							this.lives--;
							log.info("player got hit by ball");
						}
					}
				}
			}

			if (this.foundHit || !this.visible)
			{
				currTime = System.nanoTime(); // get the current time

				// check if at least 3 seconds passed from the hit
				if ((currTime - startTime) / 1000000000 >= 3)
				{
					// nullify values
					currTime = 0;
					startTime = 0;

					// give back the original values to this variables
					this.foundHit = false;
					this.visible = true;

					// respawn the shooter
					this.respawn();
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
			else
				game.stop();
				//game.isGameFinished = true;
		}
	}
}