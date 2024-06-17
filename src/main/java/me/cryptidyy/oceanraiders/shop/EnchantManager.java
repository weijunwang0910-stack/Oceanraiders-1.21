package me.cryptidyy.oceanraiders.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;

public class EnchantManager {

	private final Map<Material, List<EnchantFamily>> itemToFamilies = new HashMap<>();
	private final DataManager enchantData;
	
	public EnchantManager(Main plugin)
	{
		enchantData = new DataManager(plugin, "enchants.yml");
		enchantData.reloadConfig();
		
		ConfigurationSection enchantSection = enchantData.getConfig().getConfigurationSection("enchants");
		for(String itemTypeName : enchantSection.getKeys(false))
		{
			ConfigurationSection itemSection = enchantSection.getConfigurationSection(itemTypeName);
			
			if(itemSection.getBoolean("generic"))
			{				
				for(Material match : containsName(itemTypeName))
				{
					if(itemSection.getStringList("exclude").contains(match.name())) continue;

					List<EnchantFamily> families = new ArrayList<>();
					ConfigurationSection enchantmentSection = itemSection.getConfigurationSection("enchantments");
					
					for(String enchantName : enchantmentSection.getKeys(false))
					{
						ConfigurationSection enchantData = enchantmentSection.getConfigurationSection(enchantName);

						int maxLevel = enchantData.getInt("maxlevel");
						int startingPrice = enchantData.getInt("startingprice");
						
						if(toEnchantment(enchantName).isPresent())
						{
							families.add(new EnchantFamily(toEnchantment(enchantName).get(), maxLevel, startingPrice));
							itemToFamilies.put(match, families);
						}
					}

				}
			}
			else
			{
				Material match = Material.valueOf(itemTypeName);
				
				List<EnchantFamily> families = new ArrayList<>();
				ConfigurationSection enchantmentSection = itemSection.getConfigurationSection("enchantments");

				for(String enchantName : enchantmentSection.getKeys(false))
				{
					ConfigurationSection enchantData = enchantmentSection.getConfigurationSection(enchantName);
					
					int maxLevel = enchantData.getInt("maxlevel");
					int startingPrice = enchantData.getInt("startingprice");
					
					if(toEnchantment(enchantName).isPresent())
					{
						families.add(new EnchantFamily(toEnchantment(enchantName).get(), maxLevel, startingPrice));
						itemToFamilies.put(match, families);
					}
				}
			}
			
		}
	}

	public Optional<Enchantment> toEnchantment(String enchantName)
	{
		return Arrays.stream(Enchantment.values())
			.filter(enchant -> enchant.getName().equalsIgnoreCase(enchantName))
			.findFirst();
	}
	
	public List<Material> containsName(String itemTypeName)
	{
		List<Material> materials = new ArrayList<>();
		
		for(Material material : Material.values())
		{
			if(material.name().contains(itemTypeName))
			{
				materials.add(material);
			}
		}
		return materials;
	}

	public Map<Material, List<EnchantFamily>> getItemToFamilies() {
		return itemToFamilies;
	}
}
