package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.shop.ItemEntry;
import me.cryptidyy.oceanraiders.utility.DataConfigUtil;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OceanItemManager {

	private Main plugin;
	private final DataManager itemData;
	
	private Set<OceanItem> oceanItems = new HashSet<>();
	private List<ItemEntry> itemEntries = new ArrayList<>();
	
	public OceanItemManager(Main plugin)
	{
		this.plugin = plugin;
		this.itemData = new DataManager(this.plugin, "customitems.yml");
		this.itemData.reloadConfig();
		
		List<String> removedLore = 
				DataConfigUtil.readLore(itemData.getConfig().getConfigurationSection("globalsettings"), "removedlore");
		
		for(String itemName : this.itemData.getConfig().getKeys(false))
		{
			ConfigurationSection section = this.itemData.getConfig().getConfigurationSection(itemName);
			
			if(itemName.equalsIgnoreCase("globalsettings"))
			{
				continue;
			}

			List<String> lore = DataConfigUtil.addLore(DataConfigUtil.readLore(section, "lore"), removedLore);

			ItemStack currency = DataConfigUtil.getItemStack(section.getConfigurationSection("currency"));
			int amount = section.getInt("amount");
			int slot = section.getInt("slot");
			
			ChatColor priceColor = ChatColor.valueOf(section.getString("currency.color"));

			String inventoryName = section.getString("inventoryname");
			OceanItem item = new FirePlacer(lore, slot);
			
			switch(itemName)
			{
				case "FirePlacer":
					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);
					
					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;
					
				case "TeleportTrident":
					item = new TeleportTrident(lore, slot);
					
					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);
					
					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;
					
				case "CobwebWall":
					item = new CobwebWall(lore, slot);
					
					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);
					
					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;
					
				case "ImmunityMilk":
					item = new ImmunityMilk(lore, slot);
					
					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);
					
					item.getMeta().addEnchant(Enchantment.DURABILITY, 1, true);
					item.getMeta().getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
					
					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;
					
				case "BoatSinker":
					item = new BoatSinker(lore, slot);
					
					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);
					
					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;

				case "WholeCake":
					item = new WholeCake(lore, slot);

					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);

					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));
					break;

				case "InvisibilityPotion":
					item = new InvisibilityPotion(lore, slot);

					item.setAmount(amount);
					item.setCurrency(currency);
					item.setInventoryName(inventoryName);

					PotionMeta meta = (PotionMeta) item.getItem().getItemMeta();

					PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, 45 * 20, 1);
					meta.addCustomEffect(invis, true);
					meta.setColor(PotionEffectType.INVISIBILITY.getColor());
					meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

					item.getItem().setItemMeta(meta);

					oceanItems.add(item);
					itemEntries.add(new ItemEntry(item.getItem(), currency, lore, removedLore, slot, priceColor, item.getName()));


				default:
					break;
			}
		}
	}
	
	public Set<OceanItem> getOceanItems()
	{
		return oceanItems;
	}
	
	public Optional<OceanItem> findOceanItem(String itemName)
	{
		return oceanItems.stream().filter(item -> ChatColor.stripColor(item.getName()).equalsIgnoreCase(ChatColor.stripColor(itemName))).findFirst();
	}

	public List<ItemEntry> getItemEntries() {
		return itemEntries;
	}
	
	public Optional<ItemEntry> findItemEntry(OceanItem item)
	{
		return itemEntries.stream().filter(entry ->
				ChatColor.stripColor(entry.getDisplayName()).equals(ChatColor.stripColor(item.getItem().getItemMeta().getDisplayName()))).findFirst();
	}

	public void setItemEntries(List<ItemEntry> itemEntries) {
		this.itemEntries = itemEntries;
	}
}
