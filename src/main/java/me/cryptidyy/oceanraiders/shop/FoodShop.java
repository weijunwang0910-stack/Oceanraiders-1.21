package me.cryptidyy.oceanraiders.shop;

import me.cryptidyy.oceanraiders.customitems.OceanItem;
import me.cryptidyy.oceanraiders.customitems.OceanItemManager;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;

import java.util.Optional;

public class FoodShop extends InventoryShop{

	private Inventory foodShop = Bukkit.createInventory(null, 54, "FARMER SHOP");
	
	private static Main plugin = Main.getPlugin(Main.class);
	
	private ItemEntryManager entryManager = plugin.getGameManager().getEntryManager();

	private OceanItemManager oceanItemManager = plugin.getItemManager();
	
	public FoodShop(Player player)
	{
		for(ItemEntry entry : entryManager.getItemEntriesFood())
		{
			putConfigItems(foodShop, entry, entry.getSlot());
		}

		//put custom items
		for(OceanItem item : oceanItemManager.getOceanItems())
		{
			Optional<ItemEntry> itemEntry = oceanItemManager.findItemEntry(item);

			switch(item.getInventoryName())
			{
				case "food":
					if(itemEntry.isPresent())
					{
						putConfigItems(foodShop, itemEntry.get(), item.getSlot());
					}
					break;
				default:
					break;
			}
		}
		setupShop(player, foodShop);
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) 
	{
		Player player = (Player) event.getWhoClicked();

		if(!isOpenInventory(event)) return;
		if(!isValidInventory(event))
		{
			event.setCancelled(true);
			return;
		}
		if(event.getCurrentItem() == null) return;
		
		event.setCancelled(true);
		
		if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesFood(), player).isPresent())
		{
			purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesFood(), player).get(), foodShop);
			setupShop(player, foodShop);
			return;
		}
		else if(oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName()).isPresent())
		{
			purchase(player,
					oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName())
							.get().toItemEntry(), foodShop);
		}
	}

	@Override
	public void putMenuItems(ItemStack[] items, int[] slots) throws IllegalArgumentException 
	{

	}
	
	public Inventory getFoodShop()
	{
		return this.foodShop;
	}

	private boolean isValidInventory(InventoryClickEvent event)
	{
		if(event.getClickedInventory() == null) return false;
		return (event.getClickedInventory().equals(foodShop));
	}

	private boolean isOpenInventory(InventoryClickEvent event)
	{
		return foodShop.getViewers().contains(event.getWhoClicked());
	}
}
