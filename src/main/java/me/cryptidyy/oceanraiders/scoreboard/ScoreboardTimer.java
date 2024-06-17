package me.cryptidyy.oceanraiders.scoreboard;


import org.bukkit.scheduler.BukkitRunnable;

import me.cryptidyy.oceanraiders.Main;

public class ScoreboardTimer extends BukkitRunnable {

	private final long milliseconds;
	private long currentTime = 0;
	private boolean isDone = false;
	
	private final boolean countUp;
	
	long min = 0;
	long sec = 0;
	
	String timeLabel = "00:00";
	
	public ScoreboardTimer(int seconds, boolean countUp)
	{
		this.milliseconds = seconds * 1000;
		this.countUp = countUp;
		
		if(countUp)
			this.currentTime = 0;
		else
			this.currentTime = milliseconds;

		
		timeLabel = toTimeLabel(this.currentTime);
	}

	public void run() 
	{			
		if(countUp)
		{
			if(currentTime >= milliseconds)
			{	
				isDone = true;
				this.cancel();
			}
			
			currentTime += 1000;
		}
		else
		{
			if(currentTime <= 0)
			{
				isDone = true;
				this.cancel();
			}
			
			currentTime -= 1000;
		}

		
		timeLabel = toTimeLabel(currentTime);
	}	

	public void count()
	{
		this.runTaskTimerAsynchronously(Main.getPlugin(Main.class), 0, 20);
	}
	
	public String toTimeLabel(long millisecond)
	{
		long min = (millisecond/60000) % 60;
		long sec = (millisecond/1000) % 60;
		
		String secString = String.format("%02d", sec);
		String minString = String.format("%02d", min);
		
		return new String(minString+":"+secString);	
	}
	
	public void stop()
	{
		this.cancel();
	}
	
	public String getCurrentTimeLabel() {
		return timeLabel;
	}
	
	public long getCurrentTimeInMilliseconds()
	{
		return currentTime;
	}
	
	public int getCurrentTimeInSeconds()
	{
		return (int) (currentTime/1000);
	}

	public boolean isDone() {
		return isDone;
	}
}
