package me.cryptidyy.oceanraiders.utility;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.loot.LootChest;

public class Timer extends BukkitRunnable {

	private final long milliseconds;
	private long currentTime = 0;
	private boolean isDone = false;
	
	private float multiplier = 1.0f;
	
	//private Player owner;
	//private Hologram hologram;
	private String text;

	//private Map<UUID, List<LootChest>> playerLootChests;
	private LootChest chest;
	
	/*public Timer(List<LootChest> lootChestGroup, long milliseconds)
	{
		this.milliseconds = milliseconds;
		this.currentTime = milliseconds;
		
		this.hologram = lootChest.getHologram();
	}*/
	
	long min = 0;
	long sec = 0;
				
	String secString = String.format("%02d", sec);
	String minString = String.format("%02d", min);
	String timeLabel = "00:00";
	
	public Timer(LootChest chest, int seconds)
	{
		this.milliseconds = seconds * 1000;
		this.currentTime = milliseconds;
		this.chest = chest;
		
		timeLabel = toTimeLabel(milliseconds);
	}

	public void run() 
	{		
		if(!chest.isEmpty(chest.getInventory()))
		{
			chest.getHologram().setText(ChatColor.RED + "" + ChatColor.BOLD + "Refreshed!");
			return;
		}
		
		if(currentTime <= 0)
		{
			if(isDone && chest.isEmpty(chest.getInventory()))
			{
				restartCount();
				return;
			}	
			else if(!isDone)
			{	
				isDone = true;
				chest.generateInv(Math.round(chest.getManager().getGenerateTimes() * multiplier));
				text = (ChatColor.RED + "" + ChatColor.BOLD + "Refreshed!");
				
				multiplier += 0.5;
				chest.getHologram().setText(text);

				if(chest.getPlayer() == null) return;
				chest.getPlayer().sendMessage(ChatColor.AQUA 
						+ "One of your " + chest.getChestName() 
						+ ChatColor.AQUA + " chest refreshed with a " 
						+ ChatColor.GOLD + multiplier + "x" + ChatColor.AQUA + " multiplier!");

			}

		}
		
		else
		{
			text = (ChatColor.WHITE + "Refreshing in: " + ChatColor.AQUA+ getCurrentTimeLabel());
			chest.getHologram().setText(text);
		}

		//6 timers for 1 chest of each player
		//display for player
		
		currentTime -= 1000;
		
		min = (currentTime/60000) % 60;
		sec = (currentTime/1000) % 60;
		
		secString = String.format("%02d", sec);
		minString = String.format("%02d", min);
		timeLabel = (minString+":"+secString);	


	}	

	public void countDown()
	{
		this.runTaskTimerAsynchronously(Main.getPlugin(Main.class), 0, 20);
	}
	
	public void restartCount()
	{
		currentTime = milliseconds;
		min = 0;
		sec = 0;
		timeLabel = toTimeLabel(milliseconds);
		isDone = false;

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
