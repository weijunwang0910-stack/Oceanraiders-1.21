package me.cryptidyy.oceanraiders.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;

public class TransferedItemsManager {

	private final DataManager dataManager;
	private List<Material> transferedItems = new ArrayList<>();
	
	public TransferedItemsManager(Main plugin)
	{
		this.dataManager = new DataManager(plugin, "transfereditems.yml");
		this.dataManager.reloadConfig();
		
		ConfigurationSection section = dataManager.getConfig().getConfigurationSection("transfereditems");
		
		for(String itemName : section.getStringList("types"))
		{	
			if(Material.valueOf(itemName) != null)
				transferedItems.add(Material.valueOf(itemName));
		}
	}
	
	public List<Material> getTransferedItems()
	{
		return this.transferedItems;
	}
	
	
}
