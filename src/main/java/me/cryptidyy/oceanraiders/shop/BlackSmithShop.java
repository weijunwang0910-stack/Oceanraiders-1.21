package me.cryptidyy.oceanraiders.shop;

import java.util.Optional;

import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.customitems.OceanItem;
import me.cryptidyy.oceanraiders.customitems.OceanItemManager;
import me.cryptidyy.oceanraiders.customitems.OceanItemType;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;
import org.bukkit.inventory.PlayerInventory;

public class BlackSmithShop extends InventoryShop implements Listener {

	private Inventory blackSmithArmorShop = Bukkit.createInventory(null, 54, "BLACKSMITH SHOP");
	private Inventory blackSmithSwordShop = Bukkit.createInventory(null, 54, "BLACKSMITH SHOP");
	private Inventory blackSmithBowShop = Bukkit.createInventory(null, 54, "BLACKSMITH SHOP");
	
	private static Main plugin = Main.getPlugin(Main.class);
	
	private ItemEntryManager entryManager = plugin.getGameManager().getEntryManager();
	private OceanItemManager oceanItemManager = plugin.getItemManager();
	private ItemStack[] menuItems;
	
	public BlackSmithShop(Player player)
	{
		//firePlacer = toOceanItem(OceanItemType.FIRE_PLACER).toItemStack();
		
		for(ItemEntry entry : entryManager.getItemEntriesArmor())
		{
			putConfigItems(blackSmithArmorShop, entry, entry.getSlot());
		}
		
		for(ItemEntry entry : entryManager.getItemEntriesSword())
		{
			putConfigItems(blackSmithSwordShop, entry, entry.getSlot());
		}
		for(ItemEntry entry : entryManager.getItemEntriesBow())
		{
			putConfigItems(blackSmithBowShop, entry, entry.getSlot());
		}
		
		//put custom items
		for(OceanItem item : oceanItemManager.getOceanItems())
		{
			Optional<ItemEntry> itemEntry = oceanItemManager.findItemEntry(item);
			
			switch(item.getInventoryName())
			{
				case "armor":
					if(itemEntry.isPresent())
					{
						putConfigItems(blackSmithArmorShop, itemEntry.get(), item.getSlot());
					}
					break;
				case "sword":
					if(itemEntry.isPresent())
					{
						putConfigItems(blackSmithSwordShop, itemEntry.get(), item.getSlot());
					}
					break;
				case "bow":
					if(itemEntry.isPresent())
					{
						putConfigItems(blackSmithBowShop, itemEntry.get(), item.getSlot());
					}
					break;
				default: 
					break;
			}
		}

		menuItems = new ItemStack[] {new ItemBuilder(Material.IRON_CHESTPLATE)
				.setName(ChatColor.AQUA + "Armors")
				.setLore(" ", ChatUtil.format("&eClick to view!"))
				.addItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack(),
				new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.AQUA + "Weapons")
						.setLore(" ", ChatUtil.format("&eClick to view!"))
						.addItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack(),
				new ItemBuilder(Material.BOW).setName(ChatColor.AQUA + "Archeries")
						.setLore(" ", ChatUtil.format("&eClick to view!"))
						.addItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(),
				new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close Shop").toItemStack()};

		int[] menuSlots = {3,4,5,9,10,11,12,13,14,15,16,17,53};
		putMenuItems(menuItems, menuSlots);
		
		setupShop(player, blackSmithArmorShop);
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

		switch(event.getSlot())
		{
			case 3:
				player.openInventory(blackSmithArmorShop);
				setupShop(player, blackSmithArmorShop);
				break;
			case 4:
				player.openInventory(blackSmithSwordShop);
				setupShop(player, blackSmithSwordShop);
				break;
			case 5:
				player.openInventory(blackSmithBowShop);
				setupShop(player, blackSmithBowShop);
				break;
			case 53:
				player.closeInventory();
				break;
			default:

		}
		if(event.getInventory().equals(blackSmithArmorShop))
		{
			if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesArmor(), player).isPresent())
			{
				if(event.getClick().equals(ClickType.RIGHT))
				{
					armorPurchase(player, toEntry(event.getSlot(), event.getCurrentItem(),
							entryManager.getItemEntriesArmor(), player).get(),
							blackSmithArmorShop);
					return;
				}

				purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesArmor(), player).get(), blackSmithArmorShop);
			}

		}
		else if(event.getInventory().equals(blackSmithSwordShop))
		{
			if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesSword(), player).isPresent())
			{
				purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesSword(), player).get(), blackSmithSwordShop);
			}
			else if(oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName()).isPresent())
			{
				purchase(player,
						oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName())
						.get().toItemEntry(), blackSmithSwordShop);
			}

		}
		else if(event.getInventory().equals(blackSmithBowShop))
		{

			if(toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesBow(), player).isPresent())
				purchase(player, toEntry(event.getSlot(), event.getCurrentItem(), entryManager.getItemEntriesBow(), player).get(), blackSmithBowShop);

			else if(oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName()).isPresent())
			{
				purchase(player,
						oceanItemManager.findOceanItem(event.getCurrentItem().getItemMeta().getDisplayName())
						.get().toItemEntry(), blackSmithBowShop);
			}
		}
	}
	
	public OceanItem toOceanItem(OceanItemType type)
	{
		return oceanItemManager.getOceanItems()
				.stream()
				.filter(item -> item.getType().equals(OceanItemType.FIRE_PLACER))
				.findFirst().get();
	}
	
	public Inventory getArmorShop() {
		return blackSmithArmorShop;
	}

	public Inventory getSwordShop() {
		return blackSmithSwordShop;
	}

	public Inventory getBowShop() {
		return blackSmithBowShop;
	}
	
	public void putMenuItems(ItemStack[] items, int[] slots) throws IllegalArgumentException
	{
		if(items.length != slots.length)
		{
			throw new IllegalArgumentException();
		}

		for(int x = 0; x < items.length; x++)
		{
			blackSmithArmorShop.setItem(slots[x], items[x]);
			blackSmithSwordShop.setItem(slots[x], items[x]);
			blackSmithBowShop.setItem(slots[x], items[x]);
		}
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

	private boolean isValidInventory(InventoryClickEvent event)
	{

		if(event.getClickedInventory() == null) return false;
		return (event.getClickedInventory().equals(blackSmithArmorShop)
				|| event.getClickedInventory().equals(blackSmithSwordShop)
				|| event.getClickedInventory().equals(blackSmithBowShop));
	}

	private boolean isOpenInventory(InventoryClickEvent event)
	{
		return (blackSmithArmorShop.getViewers().contains(event.getWhoClicked())
				|| blackSmithBowShop.getViewers().contains(event.getWhoClicked())
				|| blackSmithSwordShop.getViewers().contains(event.getWhoClicked()));
	}
}
