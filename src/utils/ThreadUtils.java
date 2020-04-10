package utils;

public class ThreadUtils {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public static Thread createNewThreadAndStart(Runnable runnable) {
		return createNewThread(runnable, true);
	}
	
	public static Thread createNewThread(Runnable runnable, boolean start) {
		if(runnable == null)
			return null;
		Thread newThread = new Thread(runnable);
		if(start)
			newThread.start();
		return newThread;
	}
}
