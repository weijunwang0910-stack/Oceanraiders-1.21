package me.cryptidyy.oceanraiders.islands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.utility.DataConfigUtil;

public class IslandManager {
	
	// Add/load entries here
	private Main plugin;
	
	private final DataManager islandData;
	
	private List<Island> islandList = new ArrayList<>();
	
	private final IslandSetupManager islandSetupManager;
	
	public IslandManager(Main plugin)
	{
		this.plugin = plugin;
		this.islandData = new DataManager(this.plugin, "islands.yml");
		
		this.islandData.reloadConfig();
		
		try
		{
			for(String islandName : this.islandData.getConfig().getKeys(false))
			{
				ConfigurationSection section = this.islandData.getConfig().getConfigurationSection(islandName);
				String displayName = section.getString("displayName");
				
				Location spawnLoc = DataConfigUtil.readLocation(section.getConfigurationSection("spawnLoc"));
				Location dropLoc = DataConfigUtil.readLocation(section.getConfigurationSection("dropLoc"));
				Location cornerOne = DataConfigUtil.readLocation(section.getConfigurationSection("cornerOne"));
				Location cornerTwo = DataConfigUtil.readLocation(section.getConfigurationSection("cornerTwo"));
				Location chestLoc = DataConfigUtil.readLocation(section.getConfigurationSection("chestLoc"));
				Location lootChestOne = DataConfigUtil.readLocation(section.getConfigurationSection("lootChestOne"));
				Location lootChestTwo = DataConfigUtil.readLocation(section.getConfigurationSection("lootChestTwo"));
				Location lootChestThree = DataConfigUtil.readLocation(section.getConfigurationSection("lootChestThree"));
				Location blackSmith = DataConfigUtil.readLocation(section.getConfigurationSection("blacksmith"));
				Location librarian = DataConfigUtil.readLocation(section.getConfigurationSection("librarian"));
				Location farmer = DataConfigUtil.readLocation(section.getConfigurationSection("farmer"));
				Location witch = DataConfigUtil.readLocation(section.getConfigurationSection("witch"));
				List<Location> docks = DataConfigUtil.readLocationList(section.getConfigurationSection("docks"));
				Location waitCornerOne = DataConfigUtil.readLocation(section.getConfigurationSection("waitCornerOne"));
				Location waitCornerTwo = DataConfigUtil.readLocation(section.getConfigurationSection("waitCornerTwo"));
				Location respawnLoc = DataConfigUtil.readLocation(section.getConfigurationSection("respawnLoc"));
				List<Location> lootContainers = DataConfigUtil.readLocationList(section.getConfigurationSection("lootContainers"));

				//Shops:
				//Farmer
				//Blacksmith
				//Librarian
				//Witch
				
				Island island = new Island(displayName, 
						cornerOne, 
						cornerTwo, 
						spawnLoc, 
						dropLoc, 
						chestLoc, 
						lootChestOne, 
						lootChestTwo, 
						lootChestThree,
						blackSmith,
						librarian,
						farmer,
						witch,
						docks,
						waitCornerOne,
						waitCornerTwo,
						respawnLoc,
						lootContainers);
				
				islandList.add(island);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.islandSetupManager = new IslandSetupManager(this);
		
	}
	
	public void saveIslandtoConfig(Island island)
	{
		this.islandList.removeIf(existing -> {
			return existing.getConfigName().equalsIgnoreCase(island.getConfigName());
		});
		
		this.islandList.add(island);
		FileConfiguration config = islandData.getConfig();
		
		config.set(island.getConfigName(), null);
		
		config.set(island.getConfigName() + ".displayName", island.getDisplayName());
		
		DataConfigUtil.saveLocation(island.getSpawnLoc(), config.createSection(island.getConfigName() + ".spawnLoc"));
		DataConfigUtil.saveLocation(island.getDropLoc(), config.createSection(island.getConfigName() + ".dropLoc"));
		DataConfigUtil.saveLocation(island.getCornerOne(), config.createSection(island.getConfigName() + ".cornerOne"));
		DataConfigUtil.saveLocation(island.getCornerTwo(), config.createSection(island.getConfigName() + ".cornerTwo"));
		DataConfigUtil.saveLocation(island.getChestLoc(), config.createSection(island.getConfigName() + ".chestLoc"));
		DataConfigUtil.saveLocation(island.getLootChestOne(), config.createSection(island.getConfigName() + ".lootChestOne"));
		DataConfigUtil.saveLocation(island.getLootChestTwo(), config.createSection(island.getConfigName() + ".lootChestTwo"));
		DataConfigUtil.saveLocation(island.getLootChestThree(), config.createSection(island.getConfigName() + ".lootChestThree"));
		DataConfigUtil.saveLocation(island.getBlackSmith(), config.createSection(island.getConfigName() + ".blacksmith"));
		DataConfigUtil.saveLocation(island.getLibrarian(), config.createSection(island.getConfigName() + ".librarian"));
		DataConfigUtil.saveLocation(island.getFarmer(), config.createSection(island.getConfigName() + ".farmer"));
		DataConfigUtil.saveLocation(island.getWitch(), config.createSection(island.getConfigName() + ".witch"));
		DataConfigUtil.saveLocationList(island.getDockLocations(), config.createSection(island.getConfigName() + ".docks"));
		DataConfigUtil.saveLocation(island.getWaitCornerOne(), config.createSection(island.getConfigName() + ".waitCornerOne"));
		DataConfigUtil.saveLocation(island.getWaitCornerTwo(), config.createSection(island.getConfigName() + ".waitCornerTwo"));
		DataConfigUtil.saveLocation(island.getRespawnLoc(), config.createSection(island.getConfigName() + ".respawnLoc"));
		DataConfigUtil.saveLocationList(island.getLootContainers(), config.createSection(island.getConfigName() + ".lootContainers"));
		
		//Shops:
		//Farmer
		//Blacksmith
		//Librarian
		//Witch
		
		this.islandData.saveConfig();
	}
	
	public List<Island> getIslands()
	{
		return islandList;
	}

	public IslandSetupManager getIslandSetupManager() {
		return islandSetupManager;
	}
	
	public Optional<Island> findIsland(String displayName)
	{
		return getIslands().stream().filter(island -> island.getDisplayName().equalsIgnoreCase(displayName)).findAny();
	}
	
	public void deleteIsland(Island island)
	{
		this.islandData.getConfig().set(island.getConfigName(), null);
		this.islandData.saveConfig();
		
		this.islandList.remove(island);
	}
}
