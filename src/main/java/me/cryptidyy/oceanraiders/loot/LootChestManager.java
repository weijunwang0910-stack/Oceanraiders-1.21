package me.cryptidyy.oceanraiders.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.cryptidyy.oceanraiders.islands.Island;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class LootChestManager {

	private Main plugin;
	
	private LootRefresher lootRefresherOne;
	private LootRefresher lootRefresherTwo;
	private LootRefresher lootRefresherThree;

	private List<LootChest> lootChestOne = new ArrayList<>();
	private List<LootChest> lootChestTwo = new ArrayList<>();
	private List<LootChest> lootChestThree = new ArrayList<>();
	
	public static List<LootChest> allLootChests = new ArrayList<>();

	private List<GenericLootContainer> genericLootContainers = new ArrayList<>();
	
	private LootTable lootOne;
	private LootTable lootTwo;
	private LootTable lootThree;
	private LootTable genericLootTable;
	
	private String[] lootNames = new String[3];
	
	private final DataManager lootConfig;
	private final DataManager genericLootConfig;
	
	private int generateTimes = 0;
	private int refreshSeconds = 0;
	
	public LootChestManager(Main plugin)
	{
		this.plugin = plugin;
		this.lootConfig = new DataManager(this.plugin, "loot.yml");
		this.lootConfig.reloadConfig();
		
		//loop thru all loot chests
		for(String lootChestName : this.lootConfig.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.lootConfig.getConfig().getConfigurationSection(lootChestName);
			if(lootChestName.equalsIgnoreCase("globalsettings"))
			{
				this.generateTimes = section.getInt("generateTimes");
				this.refreshSeconds = section.getInt("refreshInSeconds");
			}
			
			List<ItemStack> items = new ArrayList<>();
			List<Integer> weights = new ArrayList<>();
			
			//lootchest 1
			if(lootChestName.equalsIgnoreCase("basicloot"))
			{
				for(String materialName : section.getKeys(false))
				{
					ConfigurationSection itemData = section.getConfigurationSection(materialName);
					if(Material.valueOf(materialName) == null) 
						Bukkit.getLogger().severe("Invalid material name in loot.yml: " + materialName);
					
					items.add(new ItemBuilder(Material.valueOf(materialName), itemData.getInt("amount")).toItemStack());
					weights.add(itemData.getInt("weight"));
					
				}
				lootOne = new LootTable.LootTableBuilder().addList(items, weights).build();
				lootNames[0] = ChatColor.GREEN + "EMERALD";
			}
			
			if(lootChestName.equalsIgnoreCase("enchantloot"))
			{
				for(String materialName : section.getKeys(false))
				{
					ConfigurationSection itemData = section.getConfigurationSection(materialName);
					if(Material.valueOf(materialName) == null) 
						Bukkit.getLogger().severe("Invalid material name in loot.yml: " + materialName);
					
					items.add(new ItemBuilder(Material.valueOf(materialName), itemData.getInt("amount")).toItemStack());
					weights.add(itemData.getInt("weight"));
					
				}
				lootTwo = new LootTable.LootTableBuilder().addList(items, weights).build();
				lootNames[1] = ChatColor.BLUE + "LAPIS";
			}
			if(lootChestName.equalsIgnoreCase("potionloot"))
			{
				for(String materialName : section.getKeys(false))
				{
					ConfigurationSection itemData = section.getConfigurationSection(materialName);
					if(Material.valueOf(materialName) == null) 
						Bukkit.getLogger().severe("Invalid material name in loot.yml: " + materialName);
					
					items.add(new ItemBuilder(Material.valueOf(materialName), itemData.getInt("amount")).toItemStack());
					weights.add(itemData.getInt("weight"));
					
				}
				lootThree = new LootTable.LootTableBuilder().addList(items, weights).build();
				lootNames[2] = ChatColor.GOLD + "GLOWSTONE";
			}

		}

		for(UUID id : plugin.getGameManager().getPlayingPlayers())
		{
			Player player = Bukkit.getPlayer(id);
			LootChest redLootchestOne = new LootChest(plugin.getIslandManager().findIsland("Red Island").get().getLootChestOne(),
					player, lootOne, this, "Red Island", lootNames[0], 0);
			LootChest redLootchestTwo = new LootChest(plugin.getIslandManager().findIsland("Red Island").get().getLootChestTwo(),
					player, lootTwo, this, "Red Island", lootNames[1] , 1);
			LootChest redLootchestThree = new LootChest(plugin.getIslandManager().findIsland("Red Island").get().getLootChestThree(),
					player, lootThree, this, "Red Island", lootNames[2] , 2);
			LootChest blueLootchestOne = new LootChest(plugin.getIslandManager().findIsland("Blue Island").get().getLootChestOne(),
					player, lootOne, this, "Blue Island", lootNames[0] , 0);
			LootChest blueLootchestTwo = new LootChest(plugin.getIslandManager().findIsland("Blue Island").get().getLootChestTwo(),
					player, lootTwo, this, "Blue Island", lootNames[1] , 1);
			LootChest blueLootchestThree = new LootChest(plugin.getIslandManager().findIsland("Blue Island").get().getLootChestThree(),
					player, lootThree, this, "Blue Island", lootNames[2] , 2);

			lootChestOne.add(redLootchestOne);
			lootChestTwo.add(redLootchestTwo);
			lootChestThree.add(redLootchestThree);
			lootChestOne.add(blueLootchestOne);
			lootChestTwo.add(blueLootchestTwo);
			lootChestThree.add(blueLootchestThree);

			allLootChests.add(redLootchestOne);
			allLootChests.add(redLootchestTwo);
			allLootChests.add(redLootchestThree);
			allLootChests.add(blueLootchestOne);
			allLootChests.add(blueLootchestTwo);
			allLootChests.add(blueLootchestThree);
		}

		genericLootConfig = new DataManager(this.plugin, "genericloot.yml");
		int generateTimes = genericLootConfig.getConfig().getInt("generateTimes");
		ConfigurationSection lootSection = genericLootConfig.getConfig().getConfigurationSection("loot");

		List<ItemStack> items = new ArrayList<>();
		List<Integer> weights = new ArrayList<>();

		for(String materialName : lootSection.getKeys(false))
		{
			ConfigurationSection itemData = lootSection.getConfigurationSection(materialName);
			if(Material.valueOf(materialName) == null)
				Bukkit.getLogger().severe("Invalid material name in loot.yml: " + materialName);

			items.add(new ItemBuilder(Material.valueOf(materialName), itemData.getInt("amount")).toItemStack());
			weights.add(itemData.getInt("weight"));

		}
		genericLootTable = new LootTable.LootTableBuilder().addList(items, weights).build();

		for(Island island : plugin.getIslandManager().getIslands())
		{
			for(Location loc : island.getLootContainers())
			{
				if(!loc.getBlock().getType().equals(Material.CHEST) && !loc.getBlock().getType().equals(Material.BARREL)) continue;
				GenericLootContainer lootContainer = new GenericLootContainer(loc, genericLootTable, generateTimes);
				genericLootContainers.add(lootContainer);
			}
		}
	}
	
	public List<LootChest> getLootChest(int number)
	{
		switch(number)
		{
			case 0:
				return lootChestOne;
			case 1:
				return lootChestTwo;
			case 2:
				return lootChestThree;
			default:
				return null;
		}
	}
	
	public List<LootChest> getAllLootChests()
	{
		return allLootChests;
	}
	
	public LootRefresher getLootRefresher(int number)
	{
		switch(number)
		{
			case 0:
				return lootRefresherOne;
			case 1:
				return lootRefresherTwo;
			case 2:
				return lootRefresherThree;
			default:
				return null;
		}
	}
	
	public Optional<LootChest> findLootChest(Player player, String islandName, int number)
	{
		return allLootChests.stream().filter(lootChest -> lootChest.getPlayer().getUniqueId().equals(player.getUniqueId())
				&& lootChest.getIslandName().equalsIgnoreCase(islandName)
				&& lootChest.getIndex() == number).findAny();
	}

	public int getGenerateTimes() {
		return generateTimes;
	}

	public int getRefreshSeconds() {
		return refreshSeconds;
	}

	public List<GenericLootContainer> getGenericLootContainers()
	{
		return genericLootContainers;
	}
	public Main getPlugin()
	{
		return plugin;
	}

}
