package me.cryptidyy.oceanraiders.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class HealthBar {

	private ScoreboardManager manager = Bukkit.getScoreboardManager();
	
	private Scoreboard scoreboard = manager.getNewScoreboard();
	
	private Objective health = scoreboard.registerNewObjective("PlayerHealth", Criterias.HEALTH, ChatColor.RED + "❤");
	
	public void setHealthBar(Player player)
	{
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		player.setScoreboard(scoreboard);
	}
	
	public void removeHealthBar(Player player)
	{
		player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
	}
}
