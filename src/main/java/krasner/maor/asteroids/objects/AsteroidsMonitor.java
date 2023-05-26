package krasner.maor.asteroids.objects;

/**
 * This class represents the monitor for the asteroids (the asteroids manager)
 * that implements the mechanism of a semaphore (a binary semaphore).
 */

public class AsteroidsMonitor 
{
	private int nextTurn = 7;

	/***
	 * function to schedule spaceships
	 * @param threadNumber - The number of asteroid to manage (allow to run / wait)
	 */
	public synchronized void waitForMyTurn(int threadNumber)
	{
		while (threadNumber >= nextTurn)
		{
			try {
				wait();
			}catch(InterruptedException e) {};
		}
	}

	/***
	 * function to notify that the spaceship has finished its job
	 * @param threadNumber - The number of asteroid that notifies
	 */
	public synchronized void imDone(int threadNumber)
	{
		nextTurn++;
		notifyAll();
	}
}
