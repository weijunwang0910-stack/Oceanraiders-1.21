package me.cryptidyy.oceanraiders.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;

public class PlayerRollbackManager {

	public static Map<UUID, Location> previousLocationMap = new HashMap<>();
	public static Map<UUID, GameMode> previousGamemodeMap = new HashMap<>();
	public static Map<UUID, ItemStack[]> previousInventoryContents = new HashMap<>();
	public static Map<UUID, ItemStack[]> previousArmorContents = new HashMap<>();
	public static Map<UUID, Integer> previousHungerValue = new HashMap<>();
	public static Map<UUID, Integer> previousLevelMap = new HashMap<>();
	public static Map<UUID, Boolean> isInvulnerable = new HashMap<>();
	
	public static void save(Player player)
	{
		previousLocationMap.put(player.getUniqueId(), player.getLocation());
		previousGamemodeMap.put(player.getUniqueId(), player.getGameMode());
		previousInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
		previousArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
		previousHungerValue.put(player.getUniqueId(), player.getFoodLevel());
		previousLevelMap.put(player.getUniqueId(), player.getLevel());
		isInvulnerable.put(player.getUniqueId(), player.isInvulnerable());
		player.getInventory().clear();
	}
	
	public static void restore(Player player)
	{
		player.getInventory().clear();
		
		ItemStack[] inventoryContents = previousInventoryContents.get(player.getUniqueId());
		if(inventoryContents != null)
		{
			player.getInventory().setContents(inventoryContents);
		}
		
		ItemStack[] armorContents = previousArmorContents.get(player.getUniqueId());
		if(armorContents != null)
		{
			player.getInventory().setArmorContents(armorContents);
		}
		
		GameMode gamemode = previousGamemodeMap.get(player.getUniqueId());
		if(gamemode != null)
		{
			player.setGameMode(gamemode);
		}
		
		Location previousLocation = previousLocationMap.get(player.getUniqueId());
		if(previousLocation != null)
		{
			player.teleport(previousLocation);
		}
		
		player.setFoodLevel(previousHungerValue.get(player.getUniqueId()));
		player.setLevel(previousLevelMap.get(player.getUniqueId()));
		player.setInvulnerable(isInvulnerable.get(player.getUniqueId()));
		
		previousLocationMap.remove(player.getUniqueId());
		previousGamemodeMap.remove(player.getUniqueId());
		previousInventoryContents.remove(player.getUniqueId());
		previousArmorContents.remove(player.getUniqueId());
		previousHungerValue.remove(player.getUniqueId());
		previousLevelMap.remove(player.getUniqueId());

		save(player);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), (bukkitTask) -> {
			player.setFireTicks(0);
		}, 5);
	}
	
	
	
}
