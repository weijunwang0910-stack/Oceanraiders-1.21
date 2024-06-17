package me.cryptidyy.oceanraiders.activelisteners;

import java.util.Arrays;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import net.md_5.bungee.api.ChatColor;

public class IntrudeEvent implements Listener {

	private Main plugin;
	
	private IslandManager islandManager;
	private GameManager manager;
	
	public IntrudeEvent(Main plugin)
	{
		this.plugin = plugin;
		islandManager = this.plugin.getIslandManager();
		this.manager = plugin.getGameManager();
	}

	@EventHandler
	public void onIntrude(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		
		Location redCornerOne = islandManager.findIsland("Red Island").get().getCornerOne();
		Location redCornerTwo = islandManager.findIsland("Red Island").get().getCornerTwo();
		
		Location blueCornerOne = islandManager.findIsland("Blue Island").get().getCornerOne();
		Location blueCornerTwo = islandManager.findIsland("Blue Island").get().getCornerTwo();
		
		//if intruder is on red team
		if(manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
		{
			if(!isInRect(event.getFrom(), blueCornerOne, blueCornerTwo) && isInRect(event.getTo(), blueCornerOne, blueCornerTwo))
			{
				manager.getTeamBlue().getPlayers().forEach(uuid -> {
					Bukkit.getPlayer(uuid).sendTitle(ChatColor.RED + "Intruder Alert!", "Someone intruded your island!", 5, 20 * 2, 20);
				});
				player.setGlowing(true);
				PlayerManager.toOceanPlayer(player).setIntruding(true);
			}
			
			if(isInRect(event.getFrom(), blueCornerOne, blueCornerTwo) && !isInRect(event.getTo(), blueCornerOne, blueCornerTwo))
			{
				player.setGlowing(false);
				PlayerManager.toOceanPlayer(player).setIntruding(false);
			}
		}
		
		//if intruder is on blue team
		if(manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
		{
			if(!isInRect(event.getFrom(), redCornerOne, redCornerTwo) && isInRect(event.getTo(), redCornerOne, redCornerTwo))
			{
				manager.getTeamRed().getPlayers().forEach(uuid -> {
					Bukkit.getPlayer(uuid).sendTitle(ChatColor.RED + "Intruder Alert!", "Someone intruded your island!", 5, 20 * 2, 20);
				});
				player.setGlowing(true);
				PlayerManager.toOceanPlayer(player).setIntruding(true);
			}
			
			if(isInRect(event.getFrom(), redCornerOne, redCornerTwo) && !isInRect(event.getTo(), redCornerOne, redCornerTwo))
			{
				player.setGlowing(false);
				PlayerManager.toOceanPlayer(player).setIntruding(false);
			}
		}
	}
	
	public boolean isInRect(Location playerLoc, Location loc1, Location loc2)
	{
		double[] dim = new double[2];
	 
		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);
		
		if(playerLoc.getX() > dim[1] || playerLoc.getX() < dim[0])
			return false;
	 
		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);
		
		if(playerLoc.getZ() > dim[1] || playerLoc.getZ() < dim[0])
			return false;
		
		dim[0] = loc1.getY();
		dim[1] = loc2.getY();
		Arrays.sort(dim);
		
		if(playerLoc.getY() > dim[1] || playerLoc.getY() < dim[0])
			return false;
	 
	 
		return true;
	}
}
