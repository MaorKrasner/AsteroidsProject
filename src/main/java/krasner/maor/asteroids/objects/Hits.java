package krasner.maor.asteroids.objects;

import java.awt.*;

/***
 * class that contains all the functions for checks of possible collisions between objects
 */
public class Hits
{
	/***
	 * function to check the collision between two polygons
	 * @param polygon1 - The polygon of the first object
	 * @param polygon2 - The polygon of the second object
	 * @return - return if the polygons are colliding
	 */
	private static boolean isPolygonCollidingWithAnotherPolygon(Polygon polygon1, Polygon polygon2) {

		for (int i = 0; i < polygon1.npoints; i++) {
			Point p = new Point(polygon1.xpoints[i], polygon1.ypoints[i]);
			if (polygon2.contains(p))
				return true;
		}

		for (int i = 0; i < polygon2.npoints; i++) {
			Point p = new Point(polygon2.xpoints[i], polygon2.ypoints[i]);
			if (polygon1.contains(p))
				return true;
		}

		return false;
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
