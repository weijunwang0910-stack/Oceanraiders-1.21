package me.cryptidyy.oceanraiders.islands;

import java.util.*;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class IslandSetupManager implements Listener {

	//Setup items here
	private static Map<UUID, TemporaryIsland> playerToTempIslandMap = new HashMap<>();
	
	private final IslandManager islandManager;
	
	private final String SET_LOCATION_CORNER_ONE_ITEM_NAME = ChatColor.GREEN + "Set Island Corner One " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_CORNER_TWO_ITEM_NAME = ChatColor.GREEN+"Set Island Corner Two " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_SPAWN_ITEM_NAME = ChatColor.GREEN + "Set Spawn Point " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_DROP_ITEM_NAME = ChatColor.GREEN + "Set Treasure Drop Location " + ChatColor.GRAY+ "(Right Click)";
	private final String SAVE_ITEM_NAME = ChatColor.GREEN + "Save " + ChatColor.GRAY+ "(Right Click)";
	private final String EXIT_ITEM_NAME = ChatColor.RED+"Cancel " + ChatColor.GRAY+ "(Right Click)";
	private final String CHEST_ITEM_NAME = ChatColor.AQUA + "Set Treasure Chest Location " + ChatColor.GRAY+"(Right Click Block)";
	private final String LOOT_ONE_NAME = ChatColor.AQUA + "Set Loot Chest One Location " + ChatColor.GRAY + "(Right Click Block)";
	private final String LOOT_TWO_NAME = ChatColor.AQUA + "Set Loot Chest Two Location " + ChatColor.GRAY + "(Right Click Block)";
	private final String LOOT_THREE_NAME = ChatColor.AQUA + "Set Loot Chest Three Location " + ChatColor.GRAY + "(Right Click Block)";
	private final String SET_LOCATION_BLACKSMITH = ChatColor.GREEN + "Set Blacksmith Location " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_LIBRARIAN = ChatColor.GREEN + "Set Librarian Location " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_FARMER = ChatColor.GREEN + "Set Farmer Location " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_WITCH = ChatColor.GREEN + "Set Witch Location " + ChatColor.GRAY+ "(Right Click)";
	private final String ADD_LOCATION_DOCKS = ChatColor.GREEN + "Add Dock Location " + ChatColor.GRAY+ "(Right Click)";
	private final String SET_LOCATION_DOCKS = ChatColor.GREEN + "Finish and save Dock Locations " + ChatColor.GRAY+ "(Right Click)";

	private List<Location> dockLocations = new ArrayList<>();

	public IslandSetupManager(IslandManager islandManager)
	{
		this.islandManager = islandManager;
	}
	
	public void addToSetup(Player player, TemporaryIsland tempIsland)
	{
		if(playerToTempIslandMap.containsKey(player.getUniqueId())) return;
		
		player.getInventory().clear();
		player.setGameMode(GameMode.CREATIVE);
		
		playerToTempIslandMap.put(player.getUniqueId(), tempIsland);
		//Bukkit.broadcastMessage(playerToTempIslandMap + "");
		
		player.getInventory().addItem(new ItemBuilder(Material.GOLD_INGOT)
				.setDisplayName(SET_LOCATION_SPAWN_ITEM_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.HEART_OF_THE_SEA)
				.setDisplayName(SET_LOCATION_DROP_ITEM_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_AXE)
				.setDisplayName(SET_LOCATION_CORNER_ONE_ITEM_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.DIAMOND_AXE)
				.setDisplayName(SET_LOCATION_CORNER_TWO_ITEM_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.CHEST)
				.setDisplayName(CHEST_ITEM_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.ENDER_CHEST)
				.setDisplayName(LOOT_ONE_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.ENDER_CHEST)
				.setDisplayName(LOOT_TWO_NAME)
				.toItemStack());
		
		player.getInventory().addItem(new ItemBuilder(Material.ENDER_CHEST)
				.setDisplayName(LOOT_THREE_NAME)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STICK)
				.setDisplayName(SET_LOCATION_BLACKSMITH)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STICK)
				.setDisplayName(SET_LOCATION_LIBRARIAN)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STICK)
				.setDisplayName(SET_LOCATION_FARMER)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STICK)
				.setDisplayName(SET_LOCATION_WITCH)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.STICK)
				.setDisplayName(ADD_LOCATION_DOCKS)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.OAK_BOAT)
				.setDisplayName(SET_LOCATION_DOCKS)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.LIME_DYE)
				.setDisplayName(SAVE_ITEM_NAME)
				.toItemStack());
		player.getInventory().addItem(new ItemBuilder(Material.BARRIER)
				.setDisplayName(EXIT_ITEM_NAME)
				.toItemStack());
		
		
		player.sendMessage(ChatColor.GREEN + "Moved to setup mode for " + tempIsland.getDisplayName());
	}
	
	public void removeFromSetup(Player player)
	{
		if(!playerToTempIslandMap.containsKey(player.getUniqueId()))
			return;
		
		playerToTempIslandMap.remove(player.getUniqueId());
		
		player.sendMessage(ChatColor.AQUA + "---Setup finished!---");
		
		//Restore player
		player.getInventory().clear();
	}

	public IslandManager getIslandManager() {
		return islandManager;
	}
	
	public boolean inSetupMode(Player player)
	{
		//Bukkit.broadcastMessage(playerToTempIslandMap + "");
		//Bukkit.broadcastMessage(playerToTempIslandMap.containsKey(player.getUniqueId()) + "");
		return playerToTempIslandMap.containsKey(player.getUniqueId());
		
	}
	
	public Location[] fixCorners(Location cornerOne, Location cornerTwo)
	{
		double x1 = cornerOne.getX();
		double x2 = cornerTwo.getX();
		
		double y1 = cornerOne.getY();
		double y2 = cornerTwo.getY();
		
		double z1 = cornerOne.getZ();
		double z2 = cornerTwo.getZ();
		
		
		Location newCornerTwo = new Location(cornerTwo.getWorld(), biggerNum(x1, x2), biggerNum(y1, y2), biggerNum(z1, z2));
		Location newCornerOne = new Location(cornerOne.getWorld(), smallerNum(x1, x2), smallerNum(y1, y2), smallerNum(z1, z2));
		return new Location[] {newCornerOne, newCornerTwo};
		
	}
	
	public double biggerNum(double n1, double n2)
	{
		if(n1 < n2)
		{
			return n2;
		}
		
		else if(n1 > n2)
		{
			return n1;
		}
		else
		{
			return n1;
		}
	}
	
	public double smallerNum(double n1, double n2)
	{
		if(n1 < n2)
		{
			return n1;
		}
		
		else if(n1 > n2)
		{
			return n2;
		}
		else
		{
			return n1;
		}
	}
	
	Location cornerOne;
	Location cornerTwo;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event)
	{
		if(!inSetupMode(event.getPlayer())) return;
		if(!event.hasItem()) return;
		//if(!event.getItem().hasItemMeta()) return;

		Player player = event.getPlayer();
		TemporaryIsland tempIsland = playerToTempIslandMap.get(player.getUniqueId());
		String itemName = event.getItem().getItemMeta().getDisplayName();

		if(itemName.equalsIgnoreCase(EXIT_ITEM_NAME))
		{
			player.sendMessage(ChatColor.RED+"Cancelled setup");
			removeFromSetup(player);
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_SPAWN_ITEM_NAME))
		{
			tempIsland.setSpawnLoc(player.getLocation());
			player.sendMessage(ChatColor.GOLD+"Set spawn location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_DROP_ITEM_NAME))
		{
			tempIsland.setDropLoc(player.getLocation());
			player.sendMessage(ChatColor.AQUA+"Set drop location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_CORNER_ONE_ITEM_NAME))
		{
			//tempIsland.setCornerOne(player.getLocation());
			cornerOne = player.getLocation();
			player.sendMessage(ChatColor.LIGHT_PURPLE+"Set island corner one location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_CORNER_TWO_ITEM_NAME))
		{
			//tempIsland.setCornerTwo(player.getLocation());
			cornerTwo = player.getLocation();
			player.sendMessage(ChatColor.LIGHT_PURPLE+"Set island corner two location!");
		}
		else if(itemName.equalsIgnoreCase(CHEST_ITEM_NAME))
		{
			if(event.getClickedBlock() == null)
			{
				player.sendMessage(ChatUtil.format("&cPlease click on a block for this setting!"));
				return;
			}
			tempIsland.setChestLoc(event.getClickedBlock().getLocation());
			player.sendMessage(ChatColor.AQUA+"Set treasure chest location!");
		}
		else if(itemName.equalsIgnoreCase(LOOT_ONE_NAME))
		{
			if(event.getClickedBlock() == null)
			{
				player.sendMessage(ChatUtil.format("&cPlease click on a block for this setting!"));
				return;
			}
			tempIsland.setLootChestOne(event.getClickedBlock().getLocation());
			player.sendMessage(ChatColor.AQUA+"Set loot chest one location!");
		}
		else if(itemName.equalsIgnoreCase(LOOT_TWO_NAME))
		{
			if(event.getClickedBlock() == null)
			{
				player.sendMessage(ChatUtil.format("&cPlease click on a block for this setting!"));
				return;
			}
			tempIsland.setLootChestTwo(event.getClickedBlock().getLocation());
			player.sendMessage(ChatColor.AQUA+"Set loot chest two location!");
		}
		else if(itemName.equalsIgnoreCase(LOOT_THREE_NAME))
		{
			if(event.getClickedBlock() == null)
			{
				player.sendMessage(ChatUtil.format("&cPlease click on a block for this setting!"));
				return;
			}
			tempIsland.setLootChestThree(event.getClickedBlock().getLocation());
			player.sendMessage(ChatColor.AQUA+"Set loot chest three location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_BLACKSMITH))
		{
			tempIsland.setBlacksmith(player.getLocation());
			player.sendMessage(ChatColor.AQUA+"Set blacksmith location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_LIBRARIAN))
		{
			tempIsland.setLibrarian(player.getLocation());
			player.sendMessage(ChatColor.AQUA+"Set librarian location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_FARMER))
		{
			tempIsland.setFarmer(player.getLocation());
			player.sendMessage(ChatColor.AQUA+"Set farmer location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_WITCH))
		{
			tempIsland.setWitch(player.getLocation());
			player.sendMessage(ChatColor.AQUA+"Set witch location!");
		}
		else if(itemName.equalsIgnoreCase(ADD_LOCATION_DOCKS))
		{
			dockLocations.add(player.getLocation());
			player.sendMessage(ChatColor.AQUA + "Added dock location!");
		}
		else if(itemName.equalsIgnoreCase(SET_LOCATION_DOCKS))
		{
			tempIsland.setDockLocations(new ArrayList<>(dockLocations));
			player.sendMessage(ChatColor.AQUA + "Set all dock locations!");
			dockLocations.clear();

		}
		else if(itemName.equalsIgnoreCase(SAVE_ITEM_NAME))
		{
			try
			{
				Location[] newCorners = fixCorners(cornerOne, cornerTwo);
				tempIsland.setCornerOne(newCorners[0]);
				tempIsland.setCornerTwo(newCorners[1]);
			}		
			catch(Exception e)
			{
				
			}

			if(tempIsland.toIsland() != null)
			{
				player.sendMessage(ChatColor.GREEN+"Saved island!");
				
				if(islandManager == null) player.sendMessage("Island manager is null!");
				islandManager.saveIslandtoConfig(tempIsland.toIsland());
				removeFromSetup(player);
				return;
			}
			
			player.sendMessage(ChatColor.RED+"Cannot save island! Make sure you've set everything correctly!");
		}
		else
		{
			Bukkit.broadcastMessage("No match for " + event.getItem().getItemMeta().getDisplayName());
			return;
		}
		
		event.setCancelled(true);
	}
	
}
