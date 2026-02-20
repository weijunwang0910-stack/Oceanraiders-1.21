package me.cryptidyy.oceanraiders.shop;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.customitems.OceanItem;
import me.cryptidyy.oceanraiders.customitems.OceanItemManager;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class PotionShop extends InventoryShop implements Listener {

	private static Main plugin = Main.getPlugin(Main.class);
	
	private ItemEntryManager entryManager = plugin.getGameManager().getEntryManager();
	private OceanItemManager oceanItemManager = plugin.getItemManager();
	
	private Inventory drinkingShop = Bukkit.createInventory(null, 54, "Potion Shop");
	private Inventory splashShop = Bukkit.createInventory(null, 54, "Potion Shop");
	
	ItemStack[] menuItems = 
	{
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
		new ItemBuilder(Material.SPLASH_POTION)
			.setName(ChatColor.RESET + "Splash Potions")
			.setPotionMeta(PotionType.HARMING)
			.addEnchant(Enchantment.UNBREAKING, 1)
			.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
			.addItemFlags(ItemFlag.HIDE_ENCHANTS)
			.addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setName("Splash Potions")
			.setLore("", ChatColor.YELLOW + "Click to view!")
			.toItemStack(),
		new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close Shop").toItemStack()
	};
	
	public PotionShop(Player player)
	{
		//setup all inventories
		for(ItemEntry entry : entryManager.getItemEntriesPotion())
		{
			putConfigItems(drinkingShop, entry, entry.getSlot());
		}
		
		//setup all inventories
		for(ItemEntry entry : entryManager.getItemEntriesSplashPotion())
		{
			putConfigItems(splashShop, entry, entry.getSlot());
		}
		
		
		//put custom items
		for(OceanItem item : oceanItemManager.getOceanItems())
		{
			Optional<ItemEntry> itemEntry = oceanItemManager.findItemEntry(item);
			
			switch(item.getInventoryName())
			{
				case "drinkShop":
					if(itemEntry.isPresent())
					{
						putConfigItems(drinkingShop, itemEntry.get(), item.getSlot());
					}
					break;
				case "splashShop":
					if(itemEntry.isPresent())
					{
						putConfigItems(splashShop, itemEntry.get(), item.getSlot());
					}
					break;
				default: 
					break;
			}

		}
		
		//put menu items
		int[] menuSlots = {9,10,11,12,13,14,15,16,17,4,53};
		putMenuItems(menuItems, menuSlots);

		//register click event
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) 
	{
		if(!isOpenInventory(event)) return;

		if(!isValidInventory(event))
		{
			event.setCancelled(true);
			return;
		}
		
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		
		//if clicked on splash potion
		if(event.getSlot() == 4)
		{
			if(event.getCurrentItem().getType() == Material.SPLASH_POTION)
				player.openInventory(splashShop);
			
			if(event.getCurrentItem().getType() == Material.POTION)
				player.openInventory(drinkingShop);
		}
		else if(event.getSlot() == 53)
		{
			player.closeInventory();
		}
		
		//if bought potion
		if(event.getCurrentItem() == null) return;
		
		if(event.getInventory().equals(drinkingShop))
		{
			if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesPotion(), player).isPresent())
			{		
				purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesPotion(), player).get(), drinkingShop);
			}
			else if(oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName()).isPresent())
			{
				purchase(player,
						oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName())
								.get().toItemEntry(), drinkingShop);
			}
			
		}
		else
		{
			if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesSplashPotion(), player).isPresent())
			{		
				purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesSplashPotion(), player).get(), splashShop);
			}
			else if(oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName()).isPresent())
			{
				purchase(player, 
						oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName())
						.get().toItemEntry(), splashShop);
			}
		}

	}

	@Override
	public void putMenuItems(ItemStack[] items, int[] slots) throws IllegalArgumentException 
	{
		for(int i = 0; i < items.length; i++)
		{
			ItemStack item = items[i];
			int slot = slots[i];
			
			drinkingShop.setItem(slot, item);
			splashShop.setItem(slot, item);
			
			splashShop.setItem(4, new ItemBuilder(Material.POTION).setName(ChatColor.RESET + "Normal Potions")
					.setPotionMeta(PotionType.HEALING)
					.addEnchant(Enchantment.UNBREAKING, 1)
					.addItemFlags(ItemFlag.HIDE_ENCHANTS)
					.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
					.setLore("", ChatColor.YELLOW + "Click to view!")
					.toItemStack());
		}
	}
	
	public Inventory getDrinkingShop()
	{
		return this.drinkingShop;
	}
	
	public Inventory getSplashShop()
	{
		return this.splashShop;
	}

	private boolean isValidInventory(InventoryClickEvent event)
	{
		if(event.getClickedInventory() == null) return false;

		return (event.getClickedInventory().equals(drinkingShop)
				|| event.getClickedInventory().equals(splashShop));
	}

	private boolean isOpenInventory(InventoryClickEvent event)
	{
		return (drinkingShop.getViewers().contains(event.getWhoClicked())
				|| splashShop.getViewers().contains(event.getWhoClicked()));
	}

}
