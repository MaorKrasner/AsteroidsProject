package krasner.maor.asteroids.game;

import krasner.maor.asteroids.lastmulti.Client1;
import krasner.maor.asteroids.objects.Asteroid;
import krasner.maor.asteroids.objects.Ball;
import krasner.maor.asteroids.objects.Player;
import krasner.maor.asteroids.objects.Spaceship;
import krasner.maor.asteroids.util.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Random;

/***
 * This class represents the panel of the game that contains all of the objects in the game.
 * this class is also responsible for handling key events of the objects and to manage the
 * settings of the game.
 ***/

@Slf4j
public class Game extends JPanel
{
	@Serial
	private static final long serialVersionUID = 1L;

	private GameFrame frame;
	public final KeyboardListener keyboard;
	public static boolean singlePlayerMode = true; // flag to know if we want to play single player or multiplayer

	public ArrayList<Asteroid> asteroids;
	public ArrayList<Ball> balls;
	public ArrayList<Spaceship> spaceships;
	public ArrayList<Player> players;
	public Player player1, player2;
	public static Player singlePlayer;
	private final Random random = new Random();
	private volatile boolean isPaused = false; // flag to know when the game is paused
	public volatile boolean isGameFinished; // flag to know when the game is done
	public volatile boolean isIterating = false; // flag to know if we iterate through a list

	//private GameClient socketClient;
	//private GameServer socketServer;
	//private static Server1 gameServer;

	private Client1 gameClient;

	@Getter
	private int index = 0;

	private static volatile boolean Added = false;

	public Game(GameFrame frame)
	{
		this.frame = frame;

		setVisible(true);

		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		setBackground(Color.black);
		setFocusable(true);

		isGameFinished = false;

		asteroids = new ArrayList<>();
		balls = new ArrayList<>();
		spaceships = new ArrayList<>();
		players = new ArrayList<>();

		keyboard = new KeyboardListener();
		this.addKeyListener(keyboard);

		initializeGameObjects();

		clientStart();

		try {
			if (!singlePlayerMode)
			{
				Object obj;
				obj = gameClient.getObjectInputStream().readObject();
				if (obj instanceof Integer)
				{
					this.index = (Integer)obj;
				}
			}
		} catch (IOException | ClassNotFoundException ignored) {}

		System.out.println("INDEX : " + this.index);

		initializeClient();

		// thread to activate the game objects in case we are in multiplayer mode
		if (!singlePlayerMode) {
			Object obj;
			try {
				obj = gameClient.getObjectInputStream().readObject();
				if (obj instanceof String) {
					String command = (String)obj;
					if (command.equals("start")) {
						gameClient.t.start();
						log.info("started " + index + "!");
					}
				}
			} catch (IOException | ClassNotFoundException ignored) {}

			activateGameObjects();
		}

		else {
			activateGameObjects();
		}

		// Thread to update every 1 milliseconds the other player. This is the main thread of the panel
		new Thread(() -> {
			if (!singlePlayerMode) {
				while (true) { // PROBLEM HERE, MUST FIX THE WHILE TRUE
					gameClient.p1 = players.get(index).polygon;
					//gameClient.asteroids1 = asteroids;
					//gameClient.spaceships1 = spaceships;
					//gameClient.balls1 = balls;

					int indexOfOther = 1 - index; // find the opposite player

					players.get(indexOfOther).polygon = new Polygon();
					for (int i = 0; i < gameClient.p2.npoints; i++)
					{
						players.get(indexOfOther).polygon.addPoint(gameClient.p2.xpoints[i], gameClient.p2.ypoints[i]);
					}

					if (this.index == 1) {
						//this.asteroids = gameClient.asteroids2;
						//this.spaceships = gameClient.spaceships2;
						//this.balls = gameClient.balls2;
					}

					try {
						Thread.sleep(1);
					} catch (InterruptedException ignored){}
				}
			}
		}).start();
	}

	public class KeyboardListener implements KeyListener {
		public LinkedList<Integer> pressedKeys = new LinkedList<>();

		public LinkedList<Integer> getPressedKeys() { return pressedKeys; }

		@Override
		public void keyPressed(KeyEvent e) {
			if (!pressedKeys.contains(e.getKeyCode()))
				pressedKeys.addLast(e.getKeyCode());
			managePanelControls();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			pressedKeys.remove(pressedKeys.indexOf(e.getKeyCode())); // problem here
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		// manage the controls for escape and enter buttons
		private void managePanelControls() {
			for (Integer key : pressedKeys) {
				switch (key) {
					case KeyEvent.VK_ESCAPE:
						isPaused = !isPaused;
						if (!isPaused)
							freeGameObjects();
						break;
					case KeyEvent.VK_ENTER:
						if (isGameFinished)
							recreateGame();
						break;
				}
			}
		}
	}

	private void clientStart() {
		if (!singlePlayerMode) {
			try {
				this.gameClient = new Client1();
			} catch (IOException ignored) {}
		}
	}

	private void initializeClient()  {
		if (!singlePlayerMode) {
			// create for every game panel instance it's own client.
			Polygon currentPlayer = this.players.get(this.index).polygon;
			gameClient.p1 = currentPlayer;
		}
	}


	private void recreateGame() {
		destroyObjects();
		isGameFinished = false;
		initializeGameObjects();
	}

	private void initializeGameObjects()
	{
		if (!singlePlayerMode) {
			player1 = new Player(300, 330, this, keyboard, 0);
			player2 = new Player(Constants.SCREEN_WIDTH - 300, 330, this, keyboard, 1);
			players.add(player1);
			players.add(player2);
		}
		else {
			singlePlayer = new Player(600, 330, this, keyboard, 0);
			players.add(singlePlayer);
		}

		addAsteroidsToGame();

		addSpaceshipsToGame();
	}

	private void activateGameObjects()
	{
		for (Player player : players) player.start();
		for (Asteroid asteroid : asteroids) asteroid.start();
		for (Spaceship spaceship : spaceships) spaceship.start();
	}

	public synchronized void stop() {
		isGameFinished = true;
	}

	public void destroyObjects()
	{
		players.clear();

		for (Asteroid asteroid : asteroids) asteroid.foundHit = true;
		asteroids.clear();

		for (Spaceship spaceship : spaceships) spaceship.foundHit = true;
		spaceships.clear();

		for (Ball ball : balls) ball.setSize(Constants.DEAD_BALL_SIZE);
		balls.clear();
	}

	/***
	 * function to notify all the objects
	 ***/
	private void freeGameObjects() {
		for (Player player : players) {
			synchronized (player) {
				player.notify();
			}
		}

		for (Asteroid asteroid : asteroids) {
			synchronized (asteroid) {
				asteroid.notify();
			}
		}

		for (Ball ball : balls) {
			synchronized (ball) {
				ball.notify();
			}
		}

		for (Spaceship spaceship : spaceships) {
			synchronized (spaceship) {
				spaceship.notify();
			}
		}
	}
	
	public boolean getIsGamePaused() { return isPaused; }
	
	// add a new asteroid to the list
	public void addAsteroidsToGame()
	{
		int[] arrX = new int[] {-250, 1490};
		int[] arrY = new int[] {80, 450};
		int xPosition = 0, yPosition = 0;
				
		for (int j = 0; j < 200; j++)
		{
			if (j % 4 == 0) {
				xPosition = 0;
				yPosition = 0;
			}
			else if (j % 4 == 1) {
				xPosition = 0;
				yPosition = 1;
			}
			else if (j % 4 == 2) {
				xPosition = 1;
				yPosition = 0;
			}
			else {
				xPosition = 1;
				yPosition = 1;
			}

			Asteroid a = new Asteroid(arrX[xPosition], arrY[yPosition], Constants.SIZE_TYPES[random.nextInt(3)],
					Constants.ASTEROID_TYPES[random.nextInt(3)], this, Constants.asteroidsMonitor);
			asteroids.add(a);
		}
	}
	
	public void addSpaceshipsToGame()
	{
		for (int i = 0; i < 50; i++)
		{
			Spaceship tmp;
			if (i % 2 == 0) // if the index is even, set the spaceship from left to right
				tmp = new Spaceship(-100, 250, Constants.SIZE_TYPES[random.nextInt(3)], this, Constants.spaceshipsMonitor);
			else // otherwise (odd case), let it go from right to left
				tmp = new Spaceship(Constants.SCREEN_WIDTH + 100, 250, Constants.SIZE_TYPES[random.nextInt(3)], this, Constants.spaceshipsMonitor);
			spaceships.add(tmp);
		}
	}

	private void drawPlayers(Graphics g) throws ConcurrentModificationException {
		try {
			this.isIterating = true;
			if (!players.isEmpty()) {
				synchronized (players) {
					for (Player player : players) {
						if (player.isAlive() && player.visible)
							player.drawPlayer(g);
						player.drawLivesAndScore(g);
						drawTotalScore(g);
					}
				}
			}
			this.isIterating = false;
		} catch (ConcurrentModificationException ignored) {}
	}

	private void drawAsteroids(Graphics g) throws ConcurrentModificationException {
		try {
			this.isIterating = true;
			if (!asteroids.isEmpty()) {
				synchronized (asteroids) {
					for (Asteroid asteroid : asteroids) {
						if (asteroid.isAlive() && asteroid.getPolygon().npoints > 0 && asteroid.getIsRunningOnScreen())
							asteroid.drawAsteroid(g);
					}
				}
			}
			this.isIterating = false;
		} catch (ConcurrentModificationException ignored) {}
	}

	private void drawBalls(Graphics g) throws ConcurrentModificationException {
		try {
			this.isIterating = true;
			if (!balls.isEmpty()) {
				synchronized (balls) {
					for (Ball ball : balls) {
						if (ball.isAlive() && ball.getSize() > 0)
							ball.drawBall(g);
					}
				}
			}
			this.isIterating = false;
		} catch (ConcurrentModificationException ignored) {}
	}

	private void drawSpaceships(Graphics g) throws ConcurrentModificationException {
		try {
			this.isIterating = true;
			if (!spaceships.isEmpty()) {
				synchronized (spaceships) {
					for (Spaceship spaceship : spaceships) {
						if (spaceship.isAlive() && spaceship.getPolygon().npoints > 0)
							spaceship.drawSpaceship(g);
					}
				}
			}
			this.isIterating = false;
		} catch (ConcurrentModificationException ignored) {}
	}

	private int totalScore() {
		return players.stream().mapToInt(Player::getScore).sum();
	}

	private void drawTotalScore(Graphics g) {
		if (!Game.singlePlayerMode)
			g.drawString("score of all players : " + totalScore(), 555, 85);
	}

	@Override
	public void paintComponent(Graphics g) throws ConcurrentModificationException
	{
		super.paintComponent(g); // important

		if (!isGameFinished)
		{
			if (isPaused)
			{
				g.setFont(new Font("Segoe Script", Font.BOLD, 22));
				g.setColor(Color.WHITE);
				g.drawString("GAME PAUSED", 500, 100);
				g.drawString("To resume, press again ESC", 430, 135);
			}

			drawPlayers(g);

			drawAsteroids(g);

			drawBalls(g);

			drawSpaceships(g);
		}

		else
		{
			//g.drawImage(new ImageIcon("resources/images/gameover2.gif").getImage(), 0, 0, 1280, 720, this);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
			g.setColor(Color.WHITE);
			g.drawString("last score : " + totalScore(), 450, 400);
			g.drawString("To start a new game press enter", 450, 440);
		}

		Toolkit.getDefaultToolkit().sync();
	}
}
