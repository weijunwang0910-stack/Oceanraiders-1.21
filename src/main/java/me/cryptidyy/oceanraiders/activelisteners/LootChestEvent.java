package me.cryptidyy.oceanraiders.activelisteners;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.loot.LootChestManager;
import me.cryptidyy.oceanraiders.state.GameManager;

public class LootChestEvent implements Listener {

	private Main plugin;
	
	private GameManager gameManager;
	private LootChestManager lootManager;
	
	private IslandManager islandManager;
	
	public LootChestEvent(Main plugin)
	{
		this.plugin = plugin;
		gameManager = this.plugin.getGameManager();
		islandManager = this.plugin.getIslandManager();
		this.lootManager = gameManager.getLootChestManager();
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent event)
	{	
		if(!(event.getInventory().getHolder() instanceof Chest)) return;
		
		Chest chest = (Chest) event.getInventory().getHolder();
		Player player = (Player) event.getPlayer();
		
		if(!gameManager.getPlayingPlayers().contains(player.getUniqueId())) return;
		
		String islandName = "Red Island";
		if(chest.getBlock().getLocation().equals(islandManager.findIsland(islandName).get().getLootChestOne()))
		{
			//Red chest 1
			event.setCancelled(true);
		
			if(lootManager.findLootChest(player, islandName, 0).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 0).get().getInventory());

			return;
			
		}
		else if(chest.getBlock().getLocation().equals(islandManager.findIsland("Red Island").get().getLootChestTwo()))
		{
			//Red chest 2
			event.setCancelled(true);
			
			if(lootManager.findLootChest(player, islandName, 1).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 1).get().getInventory());
			return;
			
		}
		else if(chest.getBlock().getLocation().equals(islandManager.findIsland("Red Island").get().getLootChestThree()))
		{
			//Red chest 3
			event.setCancelled(true);
			
			if(lootManager.findLootChest(player, islandName, 2).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 2).get().getInventory());
			return;
			
		}
		
		islandName = "Blue Island";
		if(chest.getBlock().getLocation().equals(islandManager.findIsland(islandName).get().getLootChestOne()))
		{
			//Blue chest 1
			event.setCancelled(true);
		
			if(lootManager.findLootChest(player, islandName, 0).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 0).get().getInventory());
			
			return;
			
		}
		else if(chest.getBlock().getLocation().equals(islandManager.findIsland("Blue Island").get().getLootChestTwo()))
		{
			//Blue chest 2
			event.setCancelled(true);
			
			if(lootManager.findLootChest(player, islandName, 1).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 1).get().getInventory());
			return;
			
		}
		else if(chest.getBlock().getLocation().equals(islandManager.findIsland("Blue Island").get().getLootChestThree()))
		{
			//Blue chest 3
			event.setCancelled(true);
			
			if(lootManager.findLootChest(player, islandName, 2).isPresent())
				player.openInventory(lootManager.findLootChest(player, islandName, 2).get().getInventory());
			return;
		
		}
	}
}
