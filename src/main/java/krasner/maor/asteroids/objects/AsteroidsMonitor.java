package krasner.maor.asteroids.objects;

/**
 * This class represents the monitor for the asteroids (the asteroids manager)
 * that implements the mechanism of a semaphore (a binary semaphore).
 ***/
public class AsteroidsMonitor 
{
	private int nextTurn = 3;
	
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
