package krasner.maor.asteroids.objects;

/**
 * This class represents the monitor for the spaceships (the asteroids manager)
 * that implements the mechanism of a semaphore (a binary semaphore).
 */

public class SpaceshipsMonitor 
{
	private int nextTurn = 4;

	/***
	 * function to schedule spaceships
	 * @param threadNumber - The number of spaceship to manage (allow to run / wait)
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
	 * @param threadNumber - The number of spaceship that notifies
	 */
	public synchronized void imDone(int threadNumber)
	{
		nextTurn++;
		notifyAll();
	}
}
