package krasner.maor.asteroids.objects;

import java.awt.*;

/***
 * class that contains all the functions for checks of possible collisions between objects
 */
public class Hits
{
	/***
	 * function that creates a rectangle based on the width and height of the polygon if it is surrounded by a virtual rectangle
	 * @param polygon - the polygon that we make surround with the virtual rectangle
	 * @return - return the rectangle created based on the values
	 */
	private static Rectangle getBounds(Polygon polygon) {

		int leftestX = Integer.MAX_VALUE;
		int highestY = Integer.MAX_VALUE;
		int rightestX = Integer.MIN_VALUE;
		int lowestY = Integer.MIN_VALUE;

		for (int i = 0; i < polygon.npoints; i++) {
			leftestX = Math.min(polygon.xpoints[i], leftestX);
			highestY = Math.min(polygon.ypoints[i], highestY);
			rightestX = Math.max(polygon.xpoints[i], rightestX);
			lowestY = Math.max(polygon.ypoints[i], lowestY);
		}

		return new Rectangle(leftestX, highestY, rightestX - leftestX, lowestY - highestY);
	}

	/***
	 * function to check the collision between two polygons
	 * @param polygon1 - The polygon of the first object
	 * @param polygon2 - The polygon of the second object
	 * @return - return if the polygons are colliding
	 */
	private static boolean isPolygonCollidingWithAnotherPolygon(Polygon polygon1, Polygon polygon2) {

		Rectangle rectangle1 = getBounds(polygon1);
		Rectangle rectangle2 = getBounds(polygon2);

		return polygon1.intersects(rectangle2) || polygon2.intersects(rectangle1);
	}

	/***
	 * function to check if a collision is detected between a ball and a spaceship
	 * @param ball - The ball object
	 * @param spaceship - The spaceship object
	 * @return - return if the ball is colliding with the spaceship
	 */
	public static boolean isBallHittingSpaceship(Ball ball, Spaceship spaceship)
	{
		return ball.isFromShooter && spaceship.getPolygon().contains(ball.getX(),ball.getY());
	}

	/***
	 * function to check if a collision is detected between a ball and an asteroid
	 * @param ball - The ball object
	 * @param asteroid - The asteroid object
	 * @return - return if the ball is colliding with the asteroid
	 */
	public static boolean isBallHittingAsteroid(Ball ball, Asteroid asteroid)
	{
		return asteroid.getPolygon().contains(ball.getX(),ball.getY());
	}

	/***
	 * function to check if a collision is detected between a ball and a player
	 * @param ball - The ball object
	 * @param player - The player object
	 * @return - return if the ball is colliding with the player
	 */
	public static boolean isShooterHittingBall(Ball ball, Player player)
	{
		return !ball.isFromShooter && player.getPolygon().contains(ball.getX(), ball.getY());
	}

	/***
	 * function to check if a collision is detected between a spaceship and an asteroid
	 * @param spaceship - The spaceship object
	 * @param asteroid - The asteroid object
	 * @return - return if the spaceship is colliding with the asteroid
	 */
	public static boolean isSpaceshipHittingAsteroid(Spaceship spaceship ,Asteroid asteroid)
	{
		return isPolygonCollidingWithAnotherPolygon(spaceship.getPolygon(), asteroid.getPolygon());
	}

	/***
	 * function to check if a collision is detected between a player and an asteroid
	 * @param player - The player object
	 * @param asteroid - the asteroid object
	 * @return - return if the player is colliding with the asteroid
	 */
	public static boolean isShooterHittingAsteroid(Player player , Asteroid asteroid)
	{
		return isPolygonCollidingWithAnotherPolygon(player.getPolygon(), asteroid.getPolygon());
	}

	/***
	 * function to check if a collision is detected between a spaceship and a player
	 * @param spaceship - The spaceship object
	 * @param player - The player object
	 * @return - return if the spaceship is colliding with the player
	 */
	public static boolean isSpaceshipHittingShooter(Spaceship spaceship, Player player)
	{
		return isPolygonCollidingWithAnotherPolygon(spaceship.getPolygon(), player.getPolygon());
	}
}
