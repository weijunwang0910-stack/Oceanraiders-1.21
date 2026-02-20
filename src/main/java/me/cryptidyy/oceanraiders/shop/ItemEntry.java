package me.cryptidyy.oceanraiders.shop;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.utility.ItemNames;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class ItemEntry {

	private ItemStack item;
	private ItemStack currency;
	private String name;
	private String displayName;
	private String priceLore;
	private String description;
	
	private List<String> removedLore = new ArrayList<>();
	
	private boolean isPersistent = false;
	
	private int slot = 0;

	public ItemEntry(ItemStack item, ItemStack currency, List<String> lore, List<String> removedLore,
					 int slot, ChatColor priceColor, String displayName)
	{
		this.setCurrency(currency);
		this.setPriceLore(currency.getAmount() == 1 ? ChatColor.valueOf(priceColor.name()) + "Cost: "
				+ currency.getAmount() + " " + currency.getType().name().toLowerCase() :
				ChatColor.valueOf(priceColor.name()) + "Cost: " + currency.getAmount() + " " + currency.getType().name().toLowerCase() + "s");

		this.removedLore = removedLore;
		this.setDescription(description);
		this.displayName = (displayName == null ? ItemNames.getItemName(item) : displayName);
		this.name = item.getType().name();
		this.slot = slot;

		this.item = new ItemBuilder(item).setName(this.displayName).setLore(lore).addLoreLine(priceLore, 0).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack();

		return;
	}

	public ItemEntry(ItemStack item, ItemStack currency, List<String> lore, List<String> removedLore, 
			int slot, ChatColor priceColor)
	{
		this.setCurrency(currency);
		this.setPriceLore(currency.getAmount() == 1 ? ChatColor.valueOf(priceColor.name()) + "Cost: " 
				+ currency.getAmount() + " " + currency.getType().name().toLowerCase() :
					ChatColor.valueOf(priceColor.name()) + "Cost: " + currency.getAmount() + " " + currency.getType().name().toLowerCase() + "s");
		
		this.removedLore = removedLore;
		this.setDescription(description);
		this.displayName = ItemNames.getItemName(item);
		this.name = item.getType().name();
		this.slot = slot;
		
		this.item = new ItemBuilder(item).setName(displayName).setLore(lore).addLoreLine(priceLore, 0).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack();
		
		return;
	}

	public ItemEntry(ItemStack item, ItemStack currency, List<String> lore, List<String> removedLore, int slot, String displayName)
	{
		this.setCurrency(currency);
		this.setPriceLore(currency.getAmount() == 1 ? ChatColor.GREEN + "Cost: "
				+ currency.getAmount() + " " + currency.getType().name().toLowerCase() :
				ChatColor.GREEN + "Cost: " + currency.getAmount() + " " + currency.getType().name().toLowerCase() + "s");

		this.removedLore = removedLore;
		this.setDescription(description);
		this.displayName = (displayName == null ? ItemNames.getItemName(item) : displayName);
		this.name = item.getType().name();
		this.slot = slot;

		this.item = new ItemBuilder(item).setName(this.displayName).setLore(lore).addLoreLine(priceLore, 0).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack();

		return;
	}

	public ItemEntry(ItemStack item, ItemStack currency, List<String> lore, List<String> removedLore, int slot)
	{
		this.setCurrency(currency);
		this.setPriceLore(currency.getAmount() == 1 ? ChatColor.GREEN + "Cost: " 
				+ currency.getAmount() + " " + currency.getType().name().toLowerCase() :
				ChatColor.GREEN + "Cost: " + currency.getAmount() + " " + currency.getType().name().toLowerCase() + "s");
		
		this.removedLore = removedLore;
		this.setDescription(description);
		this.displayName = ItemNames.getItemName(item);
		this.name = item.getType().name();
		this.slot = slot;
		
		this.item = new ItemBuilder(item).setName(displayName).setLore(lore).addLoreLine(priceLore, 0).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack();
		
		return;
	}
	
//	public ItemEntry(ItemStack item, ItemStack currency, List<String> lore)
//	{
//		this.setCurrency(currency);
//		this.setPriceLore(currency.getAmount() == 1 ? ChatColor.RED + "Cost: " 
//				+ currency.getAmount() + " " + currency.getType().name().toLowerCase() :
//				ChatColor.RED + "Cost: " + currency.getAmount() + " " + currency.getType().name().toLowerCase() + "s");
//		
//		this.setDescription(description);
//		this.name = item.getType().name().toLowerCase();
//		
//		this.item = new ItemBuilder(item).setLore(lore).addLoreLine(priceLore, 0).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
//				.toItemStack();
//		
//		return;
//	}
	
	/*public ItemEntry addPriceLore(String line)
	{
		ItemMeta im = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(im.getLore().contains(priceLore)) return this;
		
		lore.add(line);
		im.setLore(lore);
		item.setItemMeta(im);
		return this;
	}*/
	
	public ItemEntry addLoreLine(String line)
	{
		ItemMeta im = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(im.hasLore())lore = new ArrayList<>(im.getLore());
		lore.add(line);
		im.setLore(lore);
		item.setItemMeta(im);
		return this;
	}

	public ItemEntry addItemFlags(ItemFlag itemFlag)
	{
		ItemMeta im = item.getItemMeta();
		im.addItemFlags(itemFlag);
		item.setItemMeta(im);
		return this;
	}

	public ItemEntry setItemMeta(ItemMeta meta)
	{
		item.setItemMeta(meta);
		return this;
	}
	
	public ItemEntry setName(String name)
	{
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		return this;
	}
	
	public ItemEntry addUnsafeEnchantment(Enchantment ench, int level)
	{
	     item.addUnsafeEnchantment(ench, level);
	     return this;
	}
	
	public ItemEntry addEnchant(Enchantment ench, int level)
	{
	     ItemMeta im = item.getItemMeta();
	     im.addEnchant(ench, level, true);
	     item.setItemMeta(im);
	     return this;
	}
	
	public ItemStack getItem() {
		return item.clone();
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public ItemStack getCurrency() {
		return currency;
	}

	public void setCurrency(ItemStack currency) {
		this.currency = currency;
	}

	public String getName() {
		return name;
	}

	public String getPriceLore() {
		return priceLore;
	}

	public void setPriceLore(String priceLore) {
		this.priceLore = priceLore;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isPersistent() {
		return isPersistent;
	}

	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

	public List<String> getRemovedLore() {
		return removedLore;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

}
