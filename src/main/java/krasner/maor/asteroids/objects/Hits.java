package krasner.maor.asteroids.objects;

import java.awt.*;

/***
 * class that contains all the functions of possible hits
 ***/
public class Hits
{
	private static boolean isPolygonCollidingWithAnotherPolygon(Polygon p1, Polygon p2) {
		for (int i = 0; i < p1.npoints; i++) {
			Point p = new Point(p1.xpoints[i], p1.ypoints[i]);
			if (p2.contains(p))
				return true;
		}

		for (int i = 0; i < p2.npoints; i++) {
			Point p = new Point(p2.xpoints[i], p2.ypoints[i]);
			if (p1.contains(p))
				return true;
		}

		return false;
	}
	
	// ball with spaceship
	public static boolean isBallHittingSpaceship(Ball b,Spaceship s)
	{
		return b.isFromShooter && s.getPolygon().contains(b.getX(),b.getY());
	}
	
	// ball with asteroid
	public static boolean isBallHittingAsteroid(Ball b,Asteroid a)
	{
		return a.getPolygon().contains(b.getX(),b.getY());
	}
	
	// ball with player
	public static boolean isShooterHittingBall(Ball b, Player player)
	{
		/*
		int i , j;
		boolean c = false;
		int num = player.getPolygon().npoints;
		Polygon shp = player.getPolygon();
		
		if (player.isVisible)
		{
			for (i = 0, j = num - 1; i < num; j = i++)
			{
				if (((shp.ypoints[i] > y) != (shp.ypoints[j] > y)) &&
		                 (x < (shp.xpoints[j] - shp.xpoints[i]) * (y - shp.ypoints[i]) / (shp.ypoints[j] - shp.ypoints[i]) + shp.xpoints[i]))
		                    c = !c;
			}
		}
		return c;
		*/

		return !b.isFromShooter && player.getPolygon().contains(b.getX(), b.getY());
	}
	
	// spaceship with asteroid
	public static boolean isSpaceshipHittingAsteroid(Spaceship spaceship ,Asteroid asteroid)
	{
		return isPolygonCollidingWithAnotherPolygon(spaceship.getPolygon(), asteroid.getPolygon());
		/*
		Polygon spaceshipPolygon = spaceship.getPolygon();
		Polygon asteroidPolygon = asteroid.getPolygon();

		int i,j,k;
		boolean c = false;

		for (k = 0; k < asteroidPolygon.npoints; k++)
		{
			for (i = 0, j = spaceshipPolygon.npoints - 1; i < spaceshipPolygon.npoints; j = i++)
			{
				if (((spaceshipPolygon.ypoints[i] > asteroidPolygon.ypoints[k]) != (spaceshipPolygon.ypoints[j] > asteroidPolygon.ypoints[k])) &&
						(asteroidPolygon.xpoints[k] < (spaceshipPolygon.xpoints[j] - spaceshipPolygon.xpoints[i]) *
								(asteroidPolygon.ypoints[k] - spaceshipPolygon.ypoints[i]) / (spaceshipPolygon.ypoints[j] - spaceshipPolygon.ypoints[i]) + spaceshipPolygon.xpoints[i]))
					c = !c;
			}
		}
		return c;
		*/
	}
	
	// asteroid with shooter
	public static boolean isShooterHittingAsteroid(Player player , Asteroid asteroid)
	{
		return isPolygonCollidingWithAnotherPolygon(player.getPolygon(), asteroid.getPolygon());
		/*
		Polygon playerPolygon = player.getPolygon();
		Polygon asteroidPolygon = asteroid.getPolygon();

		int i,j,k;
		boolean c = false;
		
		if (player.isVisible)
		{
			for (k = 0; k < asteroidPolygon.npoints; k++)
			{
				for (i = 0, j = playerPolygon.npoints - 1; i < playerPolygon.npoints; j = i++)
				{
					if (((playerPolygon.ypoints[i] > asteroidPolygon.ypoints[k]) != (playerPolygon.ypoints[j] > asteroidPolygon.ypoints[k])) &&
							(asteroidPolygon.xpoints[k] <  (playerPolygon.xpoints[j] - playerPolygon.xpoints[i]) *
									(asteroidPolygon.ypoints[k] - playerPolygon.ypoints[i]) / (playerPolygon.ypoints[j] - playerPolygon.ypoints[i]) + playerPolygon.xpoints[i]))
						c = !c;
				}
			}
		}
		return c;
		*/
	}

	// asteroid with asteroid
	public static boolean isAsteroidHittingAsteroid(Asteroid firstAsteroid, Asteroid secondAsteroid)
	{
		Polygon firstAsteroidPolygon = firstAsteroid.getPolygon();
		Polygon secondAsteroidPolygon = secondAsteroid.getPolygon();
		
		int i,j,k;
		boolean c = false;
		
		for (k = 0; k < secondAsteroidPolygon.npoints; k++)
		{
			for (i = 0, j = firstAsteroidPolygon.npoints - 1; i < firstAsteroidPolygon.npoints; j = i++)
			{
				if (((firstAsteroidPolygon.ypoints[i] > secondAsteroidPolygon.ypoints[k]) != (firstAsteroidPolygon.ypoints[j] > secondAsteroidPolygon.ypoints[k])) &&
		                 (secondAsteroidPolygon.xpoints[k] < (firstAsteroidPolygon.xpoints[j] - firstAsteroidPolygon.xpoints[i]) *
								 (secondAsteroidPolygon.ypoints[k] - firstAsteroidPolygon.ypoints[i]) / (firstAsteroidPolygon.ypoints[j] - firstAsteroidPolygon.ypoints[i]) + firstAsteroidPolygon.xpoints[i]))
		                    c = !c;
			}
		}
		
		return c;
	}
	
	// spaceship with player
	public static boolean isSpaceshipHittingShooter(Spaceship spaceship, Player player)
	{
		return isPolygonCollidingWithAnotherPolygon(spaceship.getPolygon(), player.getPolygon());
		/*
		Polygon spaceshipPolygon = spaceship.getPolygon();
		Polygon playerPolygon = player.getPolygon();

		int i,j,k;
		boolean c = false;

		for (k = 0; k < playerPolygon.npoints; k++)
		{
			for (i = 0, j = spaceshipPolygon.npoints - 1; i < spaceshipPolygon.npoints; j = i++)
			{
				if (((spaceshipPolygon.ypoints[i] > playerPolygon.ypoints[k]) != (spaceshipPolygon.ypoints[j] > playerPolygon.ypoints[k])) &&
						(playerPolygon.xpoints[k] < (spaceshipPolygon.xpoints[j] - spaceshipPolygon.xpoints[i]) *
								(playerPolygon.ypoints[k] - spaceshipPolygon.ypoints[i]) / (spaceshipPolygon.ypoints[j] - spaceshipPolygon.ypoints[i]) + spaceshipPolygon.xpoints[i]))
					c = !c;
			}
		}
		return c;
		*/
	}
}
