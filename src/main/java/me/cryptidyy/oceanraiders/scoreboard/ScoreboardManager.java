package me.cryptidyy.oceanraiders.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;

public class ScoreboardManager {

	private JPerPlayerScoreboard board;
	
	private JScoreboardTeam redTeam;
	private JScoreboardTeam blueTeam;
	
	private BukkitTask updateTask;

	private Main plugin;
	
	private List<UUID> viewers = new ArrayList<>();

	private GameManager manager;
	
	public ScoreboardManager(Main plugin, List<UUID> players)
	{
		this.viewers = players;
		this.plugin = plugin;
		this.manager = plugin.getGameManager();
	}
	
	public void makeTeams(List<UUID> red, List<UUID> blue)
	{
		if(board == null)
			show();
		
		redTeam = board.createTeam("Red", "&c&lR ", ChatColor.RED);
		blueTeam = board.createTeam("Blue", "&1&lB ", ChatColor.DARK_BLUE);
		
		red.forEach(uuid -> redTeam.addPlayer(Bukkit.getPlayer(uuid)));
		blue.forEach(uuid -> blueTeam.addPlayer(Bukkit.getPlayer(uuid)));
		
	}
	
	public void addPlayerToTeamRed(Player player)
	{
		redTeam.addPlayer(player);
	}
	
	public void addPlayerToTeamBlue(Player player)
	{
		blueTeam.addPlayer(player);
	}
	
	public void removePlayerFromAllTeams(Player player)
	{
		redTeam.removePlayer(player);
		blueTeam.removePlayer(player);
	}
	
	public void show()
	{
		ScoreboardTimer timer = new ScoreboardTimer(3600, true);
		timer.count();
		
		board = new JPerPlayerScoreboard(
			
			(player) -> {
				return "&b&lOceanraiders";
			},
			
			(player) -> {

				String redTreasureLine = "&cRed Treasure: &aINTACT";
				String blueTreasureLine = "&1Blue Treasure: &aINTACT";
				
				if(manager.getTeamRed().isTreasureStolen())
					redTreasureLine = "&cRed Treasure: &cSTOLEN";
				
				if(manager.getTeamBlue().isTreasureStolen())
					blueTreasureLine = "&1Blue Treasure: &cSTOLEN";			
				
				return Arrays.asList(
					"",
					"&7Time elapsed: " + timer.getCurrentTimeLabel(),
					"", 
					redTreasureLine,
					blueTreasureLine,
					"",
					"&eCryptidyy & Diamonddrawer");
			}
				
		);
		
		viewers.forEach(this::addPlayer);
		startUpdateTask();
	}
	
	public void hide()
	{
		board.destroy();
	}
	
	public void removePlayer(Player player)
	{
		board.removePlayer(player);
		board.updateScoreboard();
	}

	public void startUpdateTask()
	{
		updateTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				board.updateScoreboard();
			}
	
		}, 0, 10);
	}
	
	public void addPlayer(UUID id)
	{
		board.addPlayer(Bukkit.getPlayer(id));
		board.updateScoreboard();
	}
	
	public JPerPlayerScoreboard getScoreboard()
	{
		return this.board;
	}
	
	public JScoreboardTeam findTeam(Player player)
	{	
		return redTeam.getEntities().contains(player.getUniqueId()) ? redTeam 
				: blueTeam.getEntities().contains(player.getUniqueId()) ? blueTeam : null;
	}
	
	public List<UUID> getViewers()
	{
		return this.viewers;
	}
	
	public void cancelUpdateTask()
	{
		if(!updateTask.isCancelled())
			updateTask.cancel();
	}
}
