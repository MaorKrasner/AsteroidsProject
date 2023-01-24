package krasner.maor.asteroids.objects;

/**
 * This class represents the monitor for the spaceships (the asteroids manager)
 * that implements the mechanism of a semaphore (a binary semaphore).
 * 
 * @author Maor Krasner
 *
 */

public class SpaceshipsMonitor 
{
	private int nextTurn = 2;
	
	public synchronized void waitForMyTurn(int n)
	{
		while (n >= nextTurn)
		{
			try {
				wait();
			}catch(InterruptedException e) {};
		}
	}
	
	public synchronized void imDone(int n)
	{
		nextTurn++;
		notifyAll();
	}
}
