package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import net.md_5.bungee.api.ChatColor;

public class TreasureEvent implements Listener {

	private GameManager manager;

	public TreasureEvent(Main plugin)
	{
		this.plugin = plugin;
		this.manager = plugin.getGameManager();
	}
	
	private Main plugin;
	
	private IslandManager islandManager;
	
	private String treasureStoleHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE STOLEN > ";
	private String treasureReplacedHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE REPLACED > ";
	
	@EventHandler
	public void onTreasureTake(InventoryClickEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		islandManager = plugin.getIslandManager();
		
		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;
		
		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();
		
		
		Inventory redTreasureInv = redChest.getBlockInventory();
		Inventory blueTreasureInv = blueChest.getBlockInventory();
		
		//Red chest handler
		if(event.getInventory().equals(redTreasureInv))
		{
			if(manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
			{
				if(manager.getTeamRed().isTreasureStolen())
				{
					if(!(event.getClickedInventory() instanceof PlayerInventory) 
							|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
					{
						//manager.sendGameMessage(treasureReplacedHeader + ChatColor.AQUA + "Red team's treasure is replaced!");
						manager.sendGameMessages(TreasureMessages.getReplacedMessage("Red team", true));
						return;
					}
					return;
				}
				
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot take your own team treasure!");
				return;
			}

			//if treasure isn't stolen
			if(!manager.getTeamRed().isTreasureStolen())
			{
				if(!(event.getClickedInventory() instanceof PlayerInventory)
						|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
				{
					if(event.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA))
					{
						//manager.sendGameMessage(treasureStoleHeader + ChatColor.RED + "Red team's treasure is stolen!");
						manager.sendGameMessages(TreasureMessages.getStolenMessage("Red team", true));
						return;
					}
				}
			}
			//Blue put back treasure handler		
			if(manager.getTeamRed().isTreasureStolen() && manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
			{
				if(event.getClickedInventory() instanceof PlayerInventory)
				{
					event.setCancelled(true);
				}

			}
			
		}
	
		//Blue chest handler
		else if(event.getInventory().equals(blueTreasureInv)) 
		{
			if(manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
			{
				if(manager.getTeamBlue().isTreasureStolen())
				{
					if(!(event.getClickedInventory() instanceof PlayerInventory) 
							|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
					{
//						manager.sendGameMessage(treasureReplacedHeader + ChatColor.AQUA + "Blue team's treasure is replaced!");
						manager.sendGameMessages(TreasureMessages.getReplacedMessage("Blue team", true));
						return;
					}

					return;
				}
				
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot take your own team treasure!");
				return;
				
			}

			if(!manager.getTeamBlue().isTreasureStolen())
			{
				if (!(event.getClickedInventory() instanceof PlayerInventory)
						|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
				{
					if (event.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA))
					{
						manager.sendGameMessages(TreasureMessages.getStolenMessage("Blue team", true));
						return;
					}
				}
			}
			
			//Red put back treasure handler
			if(manager.getTeamBlue().isTreasureStolen() && manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
			{
				if(event.getClickedInventory() instanceof PlayerInventory)
				{
					event.setCancelled(true);
				}

			}
			
		}

	}
}
