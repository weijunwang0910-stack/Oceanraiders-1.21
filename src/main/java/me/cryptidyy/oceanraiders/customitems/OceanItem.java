package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cryptidyy.oceanraiders.shop.ItemEntry;

public abstract class OceanItem {

	private OceanItemType type;
	private Material material = Material.AIR;
	private int amount = 0;
	private int slot = 0;
	private ItemStack currency;
	private String name = "";
	
	private ItemMeta meta;
	private ItemStack item;
	private String inventoryName = "";
	
	private List<String> lore = new ArrayList<>();

	public OceanItem(OceanItemType type, List<String> lore, int slot)
	{
		material = type.material;
		name = type.displayName;
		item = new ItemStack(material);
		meta = item.getItemMeta();
		this.type = type;
		this.setLore(lore);
		this.slot = slot;
		
		this.setName();
		
	}
	
	public abstract void useItem(CustomItemUser user, GameManager manager);
	
	public ItemEntry toItemEntry()
	{
		OceanItemManager itemManager = Main.getPlugin(Main.class).getItemManager();
		return itemManager.getItemEntries().stream().filter(entry ->
				ChatColor.stripColor(entry.getDisplayName()).equals(ChatColor.stripColor(name))).findFirst().get();
	}
	
	public void setName()
	{
		ItemMeta im = item.getItemMeta();
		
		im.setDisplayName(name);
		item.setItemMeta(im);
	}

	public ItemStack toItemStack()
	{
		return item;
	}
	
	public OceanItemType getType() {
		return type;
	}

	public void setType(OceanItemType type) {
		this.type = type;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public ItemMeta getMeta() {
		return meta;
	}

	public void setMeta(ItemMeta meta) {
		this.meta = meta;
	}

	public ItemStack getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getCurrency() {
		return currency;
	}

	public void setCurrency(ItemStack currency) {
		this.currency = currency;
	}

	public String getInventoryName() {
		return inventoryName;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}
	
}
