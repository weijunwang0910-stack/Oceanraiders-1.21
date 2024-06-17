package me.cryptidyy.oceanraiders.shop;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.ItemNames.ItemNames;

public abstract class InventoryShop implements Listener {

	private Map<UUID, List<ItemEntry>> playerPurchasedItems = new HashMap<>();
	
	@EventHandler
	public abstract void onClick(InventoryClickEvent event);
	
	public void putItems(Inventory inv, ItemEntry[] items, int[] slots) throws IllegalArgumentException
	{
		if(items.length != slots.length)
		{
			throw new IllegalArgumentException();
		}
		
		for(int x = 0; x < items.length; x++)
		{
			inv.setItem(slots[x], items[x].getItem());
		}
	}
	
	public void putConfigItems(Inventory inv, ItemEntry entry, int slot)
	{
		inv.setItem(slot, entry.getItem());
	}
	
	public abstract void putMenuItems(ItemStack[] items, int[] slots) throws IllegalArgumentException;

	//run this before player recieves item, don't make change to ACTUAL item entry
	public void setupShop(Player player, Inventory shop)
	{
		if(!playerPurchasedItems.containsKey(player.getUniqueId())) return;

		for(ItemEntry entry : playerPurchasedItems.get(player.getUniqueId()))
		{
			ItemMeta purchasedMeta = entry.getItem().getItemMeta().clone();

			//only modify item in shop inventory, not in player inventory
			if(shop.contains(entry.getItem()))
			{
				purchasedMeta.addEnchant(Enchantment.DURABILITY, 1, true);
				purchasedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				purchasedMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				
				purchasedMeta.setLore(Arrays.asList(ChatColor.RED + "You already have purchased this item!"));

				Arrays.stream(shop.getContents())
				.filter(item -> item != null)
				.filter(item -> ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(entry.getDisplayName())))
				.findFirst().get().setItemMeta(purchasedMeta);
			}
		}
	}
	
	private void saveItem(Player player, ItemEntry item)
	{
		if(item.getItem().getType().name().contains("CHESTPLATE") 
				|| item.getItem().getType().name().contains("LEGGINGS") 
				|| item.getItem().getType().name().contains("BOOTS")
				|| item.getItem().getType().name().contains("SWORD")
				|| item.getItem().getType().name().contains("AXE"))
		{
			List<ItemEntry> purchasedItems =
					playerPurchasedItems.containsKey(player.getUniqueId()) ?
							new ArrayList<>(playerPurchasedItems.get(player.getUniqueId())) : new ArrayList<>();

			purchasedItems.add(item);
			playerPurchasedItems.put(player.getUniqueId(), purchasedItems);
		}

	}
	
	public boolean canPurchase(Player player, ItemEntry item)
	{
		//If player already purchased this item
		if(playerPurchasedItems.containsKey(player.getUniqueId()) && playerPurchasedItems.get(player.getUniqueId())
				.stream()
				.map(ItemEntry::getName)
				.anyMatch(name -> name.equalsIgnoreCase(item.getName())))
		{
			//already purchased
			player.sendMessage(ChatColor.RED + "You already have purchased this item!");
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
			return false;
		}

		//Has money
		if(((Arrays.stream(player.getInventory().getStorageContents())
				.filter(currentItem -> currentItem != null)
				.filter(currentItem -> currentItem.getType() == item.getCurrency().getType())
				.map(ItemStack::getAmount))
				.reduce(0, Integer::sum)) - item.getCurrency().getAmount() >= 0)
		{
			return true;
		}

		//not enough money
		player.sendMessage(ChatColor.RED + "Not enough " + item.getCurrency().getType().name().toLowerCase() + "s, need " 
				+ (item.getCurrency().getAmount() - (Arrays.stream(player.getInventory().getContents())
				.filter(currentItem -> currentItem != null)
				.filter(currency -> currency.getType().equals(item.getCurrency().getType()))
				.map(ItemStack::getAmount))
				.reduce(0, Integer::sum)) + " more.");
		
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
		
		return false;
		
	}
	
	public void purchase(Player player, ItemEntry item, Inventory inv)
	{
		if(!canPurchase(player, item)) return;
		
		player.getInventory().removeItem(item.getCurrency());

		//give original item, not with purchased lore
		ItemStack purchasedItem = item.getItem();
		ItemMeta im = purchasedItem.getItemMeta();
	    List<String> lore = new ArrayList<>(im.getLore());
	    
	    //remove lores
	    if(lore.contains(item.getPriceLore()))
	    {
		    lore.remove(item.getPriceLore());
	    }
	    
	    for(String removedLoreLine : item.getRemovedLore())
	    {
	    	if(!lore.contains(removedLoreLine)) continue;
	    	
	    	lore.remove(removedLoreLine);
	    }

		saveItem(player, item);
		setupShop(player, inv);
	    im.setLore(lore);
	    purchasedItem.setItemMeta(im);

		giveItem(player, purchasedItem);
		//player.getInventory().addItem(purchasedItem);


		player.sendMessage(ChatColor.GOLD + "You purchased " + item.getDisplayName() + " x" + item.getItem().getAmount());
		player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.7f, 0.5f);
		return;
	}
	
	public ItemStack armorPurchase(Player player, ItemEntry item, Inventory inv)
	{
		if(!canPurchase(player, item)) return null;
		
		player.getInventory().removeItem(item.getCurrency());
		
		ItemStack purchasedItem = item.getItem();
		ItemMeta im = purchasedItem.getItemMeta();
	    List<String> lore = new ArrayList<>(im.getLore());
	    
	    if(lore.contains(item.getPriceLore()))
	    {
		    lore.remove(item.getPriceLore());
	    }
	    
	    for(String removedLoreLine : item.getRemovedLore())
	    {
	    	if(!lore.contains(removedLoreLine)) continue;
	    	
	    	lore.remove(removedLoreLine);
	    }
	    
	    im.setLore(lore);
	    purchasedItem.setItemMeta(im);
		saveItem(player, item);
		setupShop(player, inv);
	    
	    for(int i = 36; i <= 38 ; i++)
		{
			ItemStack existingArmor = player.getInventory().getItem(i);
			
			if(existingArmor == null)
			{
				if(i == 38 && getArmorType(purchasedItem).equals("CHESTPLATE"))
					player.getInventory().setItem(i, purchasedItem);
				if(i == 37 && getArmorType(purchasedItem).equals("LEGGINGS"))
					player.getInventory().setItem(i, purchasedItem);
				if(i == 36 && getArmorType(purchasedItem).equals("BOOTS"))
					player.getInventory().setItem(i, purchasedItem);
			}
			else if(getArmorType(existingArmor).equals(getArmorType(purchasedItem)))
			{
				//replace the armor
				player.getInventory().setItem(i, purchasedItem);
				
				if(!existingArmor.getType().name().contains("LEATHER"))
				{
					//player.getInventory().addItem(existingArmor);
					giveItem(player, existingArmor);
				}
			}
		}

		player.sendMessage(ChatColor.GOLD + "You purchased " + item.getName() + " x" + item.getItem().getAmount());
		player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2f, 1f);
		return purchasedItem; 
	}
	
	public String getArmorType(ItemStack armor)
	{
		if(armor == null) return "";
		
		if(armor.getType().name().contains("HELMET")) return "HELMET";
		else if(armor.getType().name().contains("CHESTPLATE")) return "CHESTPLATE";
		else if(armor.getType().name().contains("LEGGINGS")) return "LEGGINGS";
		else if(armor.getType().name().contains("BOOTS")) return "BOOTS";
		
		return "";
	}

	public Optional<ItemEntry> toEntry(int itemSlot, ItemStack item, List<ItemEntry> entries, Player purchaser)
	{
		return entries.stream().filter(entry ->
				ChatColor.stripColor(entry.getDisplayName()).equals(ChatColor.stripColor(item.getItemMeta().getDisplayName())))
				.filter(entry -> entry.getSlot() == itemSlot)
				.findFirst();
	}

	public Optional<ItemEntry> toEntryFromString(String name, ItemEntry[] entries)
	{
		return Arrays.stream(entries).filter(entry -> entry.getName().equals(name)).findFirst();
	}

	public Map<UUID, List<ItemEntry>> getPlayerPurchasedItems()
	{
		return this.playerPurchasedItems;
	}

	public void giveItem(Player player, ItemStack item)
	{
		if(player.getInventory().firstEmpty() == -1)
		{
			player.getWorld().dropItemNaturally(player.getLocation(), item);
			return;
		}

		player.getInventory().addItem(item);
	}
}
