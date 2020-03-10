package utils;

public abstract class LoopingRunnable implements Runnable {
	protected volatile boolean running;
	protected volatile long loopDelayMillis;
	
	public LoopingRunnable() {
		this(100L);
	}
	
	public LoopingRunnable(long delayMillis) {
		setRunning(false);
		loopDelayMillis = delayMillis;
	}
	
	@Override
	public void run() {
		setRunning(true);
	}
	
	public void stop() {
		setRunning(false);
	}
	
	public long getLoopDelayMillis() {
		return loopDelayMillis;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean value) {
		running = value;
	}
}
