package me.cryptidyy.oceanraiders.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.utility.DataConfigUtil;

public class ItemEntryManager {

	private Main plugin;
	private DataManager itemData;
	
	private List<ItemEntry> itemEntriesArmor = new ArrayList<>();
	private List<ItemEntry> itemEntriesSword = new ArrayList<>();
	private List<ItemEntry> itemEntriesBow = new ArrayList<>();
	
	private List<ItemEntry> itemEntriesFood = new ArrayList<>();
	
	private List<ItemEntry> itemEntriesPotion = new ArrayList<>();
	private List<ItemEntry> itemEntriesSplashPotion = new ArrayList<>();
	
	public static List<List<ItemEntry>> allShopEntries = new ArrayList<>();

	//make new lists of item entry for all shops
	
	public ItemEntryManager(Main plugin)
	{
		this.plugin = plugin;
		
		//armors.yml
		this.itemData = new DataManager(this.plugin, "armors.yml");
		this.itemData.reloadConfig();

		List<String> armorGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> armorGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : this.itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), armorGlobalLore, armorGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			
			if(!toMaterial(itemName).isPresent()) continue;
			
			ItemEntry item = new ItemEntry(new ItemStack(toMaterial(itemName).get(), amount), currency, lore, armorGlobalRemovedLore, slot);
			//Bukkit.broadcastMessage(lore + "");

			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			itemEntriesArmor.add(item);
		}
		
		//swords.yml
		this.itemData = new DataManager(this.plugin, "swords.yml");
		this.itemData.reloadConfig();

		List<String> swordGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> swordGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : this.itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), swordGlobalLore, swordGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			
			if(!toMaterial(itemName).isPresent()) continue;
			
			ItemEntry item = new ItemEntry(new ItemStack(toMaterial(itemName).get(), amount), currency, lore, swordGlobalRemovedLore, slot);
			
			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			itemEntriesSword.add(item);
		}
		
		//bows.yml
		this.itemData = new DataManager(this.plugin, "bows.yml");
		this.itemData.reloadConfig();

		List<String> bowGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> bowGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : this.itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), bowGlobalLore, bowGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			int damage = section.getInt("damage");
			String displayName = section.getString("displayname");

			if(!toMaterial(itemName).isPresent()) continue;
			
			String effectName = section.getString("arroweffect");
			ItemStack is = new ItemStack(toMaterial(itemName).get(), amount);
			Damageable meta = (Damageable) is.getItemMeta();
			if(damage > 0) meta.setDamage(damage);

			is.setItemMeta((ItemMeta) meta);
			
			if(toPotionType(effectName).isPresent())
			{
				PotionType type = toPotionType(effectName).get();
				setPotionMeta(type, is);
			}
			
			ItemEntry item = new ItemEntry(is, currency, lore, bowGlobalRemovedLore, slot, displayName);
			
			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			itemEntriesBow.add(item);
		}
		
		//food.yml
		itemData = new DataManager(plugin, "food.yml");
		itemData.reloadConfig();

		List<String> foodGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> foodGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), foodGlobalLore, foodGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			
			if(Material.valueOf(itemName) == null) continue;

			ItemEntry item = null;
			if(itemName.equals("SUSPICIOUS_STEW"))
			{
				ItemStack stew = new ItemStack(Material.valueOf(itemName), amount);
				SuspiciousStewMeta meta = (SuspiciousStewMeta) stew.getItemMeta();

				meta.addCustomEffect(DataConfigUtil.readPotionEffect(section.getConfigurationSection("effect")), true);
				stew.setItemMeta(meta);
				item = new ItemEntry(stew, currency, lore, foodGlobalRemovedLore, slot);
				itemEntriesFood.add(item);
			}
			else
			{
				item = new ItemEntry(new ItemStack(Material.valueOf(itemName), amount), currency, lore, foodGlobalRemovedLore, slot);
			}
			
			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			itemEntriesFood.add(item);
		}
		
		//potion.yml
		itemData = new DataManager(plugin, "potion.yml");
		itemData.reloadConfig();

		List<String> potionGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> potionGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), potionGlobalLore, potionGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			String displayName = section.getString("displayname");

			Material mat = Material.valueOf(section.getString("material"));
			ChatColor priceColor = ChatColor.valueOf(section.getString("currency.color"));
			
			if(mat == null) continue;
			
			ItemStack potionItem = new ItemStack(mat);
			
			//if potion
			if(mat == Material.POTION)
			{
				//get effects section
				ConfigurationSection effectSection = section.getConfigurationSection("effects");
				
				if(effectSection == null)
				{
					Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with potion.yml! Please check the config");
				}
				
				PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
				
				//add all potion effects
				for(String effectName : effectSection.getKeys(false))
				{	
					ConfigurationSection potionSection = effectSection.getConfigurationSection(effectName);
					
					int level = potionSection.getInt("level") - 1;
					int duration = (potionSection.getInt("duration") * 20);
					
					PotionEffectType effectType = PotionEffectType.getByName(effectName);

					if(effectType == null)
					{
						Bukkit.getLogger().log(Level.SEVERE, "Invalid potion name " + effectName + " in potion.yml!");
						continue;
					}
			
					PotionEffect effect = new PotionEffect(effectType, duration, level);
					
					potionMeta.setColor(effectType.getColor());

					potionMeta.addCustomEffect(effect, true);
					potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				}
				
				potionItem.setItemMeta(potionMeta);
			}
			
			potionItem.setAmount(amount);
			ItemEntry item = null;
			
			item = new ItemEntry(potionItem, currency, lore, potionGlobalRemovedLore, slot, priceColor, displayName);
			
			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			
			itemEntriesPotion.add(item);
		}
		
		//splash_potion.yml
		itemData = new DataManager(plugin, "splash_potion.yml");
		itemData.reloadConfig();

		List<String> splashPotionGlobalLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "lore");
		List<String> splashPotionGlobalRemovedLore = DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}
			
			List<String> lore = DataConfigUtil
					.addLore(DataConfigUtil.readLore(section, "lore"), splashPotionGlobalLore, splashPotionGlobalRemovedLore);
			
			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			String displayName = section.getString("displayname");
			Material mat = Material.valueOf(section.getString("material"));
			
			ChatColor priceColor = ChatColor.valueOf(section.getString("currency.color"));
			
			if(mat == null) continue;
			
			ItemStack potionItem = new ItemStack(mat);
			
			//if potion
			if(mat == Material.SPLASH_POTION)
			{
				//get effects section
				ConfigurationSection effectSection = section.getConfigurationSection("effects");
				
				if(effectSection == null)
				{
					Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with potion.yml! Please check the config");
				}
				
				PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
				
				//add all potion effects
				for(String effectName : effectSection.getKeys(false))
				{	
					ConfigurationSection potionSection = effectSection.getConfigurationSection(effectName);
					
					int level = potionSection.getInt("level") - 1;
					int duration = (potionSection.getInt("duration") * 20);

					PotionEffectType effectType = PotionEffectType.getByName(effectName);

					if(effectType == null)
					{
						Bukkit.getLogger().log(Level.SEVERE, "Invalid potion name " + effectName + " in potion.yml!");
						continue;
					}
			
					PotionEffect effect = new PotionEffect(effectType, duration, level);
					
					potionMeta.setColor(effectType.getColor());

					potionMeta.addCustomEffect(effect, true);
					potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				}
				
				potionItem.setItemMeta(potionMeta);
			}
			
			potionItem.setAmount(amount);
			ItemEntry item = new ItemEntry(potionItem, currency, lore, splashPotionGlobalRemovedLore, slot, priceColor, displayName);
			
			if(section.getBoolean("persistent"))
			{
				item.setPersistent(true);
				ItemMeta im = item.getItem().getItemMeta();
				im.setUnbreakable(true);
				item.setItemMeta(im);
			}
			
			itemEntriesSplashPotion.add(item);
		}
		
		allShopEntries.add(itemEntriesArmor);
		allShopEntries.add(itemEntriesSword);
		allShopEntries.add(itemEntriesBow);
		allShopEntries.add(itemEntriesFood);
		allShopEntries.add(itemEntriesPotion);
		allShopEntries.add(itemEntriesSplashPotion);
	}
	
	public void setPotionMeta(PotionType type, ItemStack is)
	{
		PotionMeta meta = (PotionMeta) is.getItemMeta();
		meta.setBasePotionData(new PotionData(type));
		is.setItemMeta(meta); 
	}
	
	public List<ItemEntry> getItemEntriesArmor()
	{
		return itemEntriesArmor;
	}
	
	public List<ItemEntry> getItemEntriesSword()
	{
		return itemEntriesSword;
	}
	
	public Optional<Material> toMaterial(String configName)
	{
		return Arrays.stream(Material.values()).filter(mat -> mat.toString().equalsIgnoreCase(configName)).findFirst();
	}
	
	public Optional<PotionType> toPotionType(String effectName)
	{
		return Arrays.stream(PotionType.values()).filter(potion -> potion.toString().equalsIgnoreCase(effectName)).findFirst();
	}

	public List<ItemEntry> getItemEntriesBow() {
		return itemEntriesBow;
	}

	public List<List<ItemEntry>> getAllShopEntries() {
		return allShopEntries;
	}

	public List<ItemEntry> getItemEntriesFood() {
		return itemEntriesFood;
	}

	public List<ItemEntry> getItemEntriesPotion() {
		return itemEntriesPotion;
	}
	
	public List<ItemEntry> getItemEntriesSplashPotion()
	{
		return this.itemEntriesSplashPotion;
	}

	public void setItemEntriesPotion(List<ItemEntry> itemEntriesPotion) {
		this.itemEntriesPotion = itemEntriesPotion;
	}
}
