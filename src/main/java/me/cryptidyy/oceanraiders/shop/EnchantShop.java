package me.cryptidyy.oceanraiders.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class EnchantShop implements Listener {

	private Inventory enchantInv;
	
	private int inputSlot = 29;
	private int outputSlot = 33;
	
	private EnchantManager manager;
	
	private final Map<Material, List<EnchantFamily>> itemToFamilies;
	private static BukkitTask reduceGlitch;
	private final Player inventoryOnwer;

	private ItemStack inputItem = new ItemStack(Material.AIR);
	private ItemStack outputItem = new ItemStack(Material.AIR, 0);
	
	private ItemStack[] menuItems = {
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(),
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").toItemStack(), 
			new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close Shop").toItemStack()};

	private ItemStack outputSlotItem = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName(" ").toItemStack();
	
	public EnchantShop(Player player)
	{	
		manager = PlayerManager.toOceanPlayer(player).getEnchantManager();
		itemToFamilies = manager.getItemToFamilies();
		
		enchantInv = Bukkit.createInventory(player, 54, "ENCHANTING");
		
		for(int x = 0; x <= 53; x++)
		{
			enchantInv.setItem(x, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack());
		}
		int[] menuSlots = {19,20,21,28,30,37,38,39,23,24,25,32,34,41,42,43,53};
		putMenuItems(menuItems, menuSlots);
		
		for(int x = 10; x <= 16; x++)
		{
			enchantInv.setItem(x, 
					new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setName("Enchants will appear here").toItemStack());
		}
		enchantInv.setItem(outputSlot, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName(" ").toItemStack());
		enchantInv.setItem(inputSlot, null);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		
		reduceGlitch = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getPlugin(Main.class), () -> {

			if(enchantInv.getItem(inputSlot) != null)
			{
				placedMethod(enchantInv);
			}
		}, 0, 3);

		this.inventoryOnwer = player;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		Inventory clickedInv = event.getClickedInventory();

		//player places item into the shop
		if(event.getClickedInventory() instanceof PlayerInventory)
		{
			if(event.getClick().equals(ClickType.SHIFT_LEFT))
			{
				if(enchantInv.getItem(inputSlot) != null)
				{
					event.setCancelled(true);
					return;
				}
				//itemToFamilies.put(event.getCurrentItem().getType(), createList(event.getCurrentItem()));
				//displayBooks(createList(event.getCurrentItem()), event.getInventory());
				if(event.getCurrentItem() == null) return;

				Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
					placedMethod(enchantInv);

					Player player = (Player) event.getWhoClicked();
					player.updateInventory();
				},0);

				return;
			}
			return;
		}
		
		if(event.getClickedInventory() == null) return;
		if(event.getClickedInventory().equals(enchantInv))
		{
			if(event.getSlot() == inputSlot)
			{
				//placed item in the input slot
				if(event.getAction().equals(InventoryAction.PLACE_ALL)
						|| event.getAction().equals(InventoryAction.PLACE_ONE)
						|| event.getAction().equals(InventoryAction.PLACE_SOME))
				{
					placedMethod(clickedInv);
					return;
				}

				//took out item at inputSlot
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), bukkitTask -> {
					resetInv();
				}, 1);
				return;
			}

			if(event.getCurrentItem() == null)
			{
				event.setCancelled(true);
				return;
			}
			//if clicked on one of the books
			if(event.getCurrentItem().getType().toString().contains("ENCHANTED_BOOK"))
			{
				//If this book is a gapple book
				if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RESET + "Enchanted Golden Apple Book"))
				{
					Player player = (Player) event.getWhoClicked();
					event.setCancelled(true);

					//Pay price
					if(!canPurchase(player, 2))
					{
						player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
						event.setCancelled(true);
						return;
					}

					player.getInventory().removeItem(new ItemStack(Material.LAPIS_LAZULI, 2));

					//Decrease original item and increase enchanted item
					inputItem = enchantInv.getItem(inputSlot);
					outputItem.setType(Material.ENCHANTED_GOLDEN_APPLE);

					outputItem.setAmount(outputItem.getAmount() + 1);
					inputItem.setAmount(inputItem.getAmount() - 1);

					if(clickedInv.getItem(outputSlot).getType() != outputSlotItem.getType()
						&& clickedInv.getItem(outputSlot).getType() != Material.ENCHANTED_GOLDEN_APPLE)
					{
						player.getInventory().addItem(clickedInv.getItem(outputSlot));
					}

					clickedInv.setItem(outputSlot, outputItem);

					player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 1f);
					undisplayBooks();
					return;
				}
				//if this book is from EnchantFamily
				else if(toEnchantFamily(enchantInv.getItem(inputSlot).getType(), event.getCurrentItem()).isPresent())
				{
					EnchantFamily bookFamily = 
							toEnchantFamily(enchantInv.getItem(inputSlot).getType(), event.getCurrentItem()).get();
					
					Player player = (Player) event.getWhoClicked();
					ItemStack item = clickedInv.getItem(inputSlot);
					
					//Pay price
					if(!canPurchase(player, bookFamily.getPrice()))
					{
						player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
						event.setCancelled(true);
						return;
					}
					
					player.getInventory().removeItem(new ItemStack(Material.LAPIS_LAZULI, bookFamily.getPrice()));
					
					//Remove original item and make enchanted item
					item.addEnchantment(bookFamily.getEnchant(), 
							bookFamily.getCurrentLevel());

					if(clickedInv.getItem(outputSlot).getType() != outputSlotItem.getType())
					{
						player.getInventory().addItem(clickedInv.getItem(outputSlot));
					}

					clickedInv.setItem(outputSlot, item);
					clickedInv.setItem(inputSlot, null);
					
					//upgrade enchant for next purchase
					player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 1f);
					bookFamily.upgrade();
					undisplayBooks();
				}
			}
			
			//Check output slot
			if(event.getSlot() == 33)
			{
				if(clickedInv.getItem(event.getSlot()).getType() == Material.YELLOW_STAINED_GLASS_PANE)
				{
					event.setCancelled(true);
					return;
				}
				
				//If result got taken out
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), bukkitTask -> {
					
					resetInv();

				}, 0);
				return;
				
			}
			
			if(event.getSlot() == 53)
				event.getWhoClicked().closeInventory();
			
			event.setCancelled(true);
			return;
		}

		//Player player = (Player) event.getWhoClicked();
		//createList(event.getCurrentItem());
	}

	//TODO: handle inventory closing

	public void placedMethod(Inventory clickedInv)
	{
		if(clickedInv.getItem(inputSlot) == null) return;
		
		//itemToFamilies.put(clickedInv.getItem(inputSlot).getType(), createList(clickedInv.getItem(inputSlot)));
		//displayBooks(createList(clickedInv.getItem(inputSlot)), clickedInv);
		ItemStack inputtedItem = clickedInv.getItem(inputSlot);

		if(Material.GOLDEN_APPLE.equals(inputtedItem.getType()))
		{
			//player placed in golden apple
			displayAppleBooks(clickedInv);
		}

		displayBooks(itemToFamilies.get(clickedInv.getItem(inputSlot).getType()), clickedInv);
	}
	
	public Optional<EnchantFamily> toEnchantFamily(Material material, ItemStack book)
	{
		return itemToFamilies.get(material).stream().filter(family -> family.toBook().equals(book)).findFirst();
	}
	
	public boolean canPurchase(Player player, int price)
	{
		//Has money
		if(((Arrays.stream(player.getInventory().getStorageContents())
				.filter(currentItem -> currentItem != null)
				.filter(currentItem -> currentItem.getType() == Material.LAPIS_LAZULI)
				.map(ItemStack::getAmount))
				.reduce(0, Integer::sum)) - price >= 0)
		{
			return true;
		}

		//not enough money
		player.sendMessage(ChatColor.RED + "Not enough lapides, need "
				+ (price - (Arrays.stream(player.getInventory().getContents())
				.filter(currentItem -> currentItem != null)
				.filter(currency -> currency.getType().equals(Material.LAPIS_LAZULI))
				.map(ItemStack::getAmount))
				.reduce(0, Integer::sum)) + " more.");
		
		return false;
	}

	public void resetInv()
	{
		for(int x = 0; x <= 53; x++)
		{
			if(x == inputSlot) continue;
			if(x == outputSlot) continue;
			enchantInv.setItem(x, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack());
		}
		int[] menuSlots = {19,20,21,28,30,37,38,39,23,24,25,32,34,41,42,43,53};
		putMenuItems(menuItems, menuSlots);
		
		undisplayBooks();
		
		//enchantInv.setItem(29, null);
		if(enchantInv.getItem(outputSlot) == null)
			enchantInv.setItem(33, outputSlotItem);

		outputItem.setAmount(0);
		inventoryOnwer.updateInventory();

	}
	
	public void undisplayBooks()
	{
		for(int x = 10; x <= 16; x++)
		{
			enchantInv.setItem(x, new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setName("Enchants will appear here!").toItemStack());
		}
		inventoryOnwer.updateInventory();
	}
	
	public void displayBooks(List<EnchantFamily> books, Inventory inv)
	{
		for(int x = 10; x <= 16; x++)
		{
			try
			{
				if(books.get(x - 10).getCurrentLevel() != -1)
				{
					inv.setItem(x, books.get(x - 10).toBook());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void displayAppleBooks(Inventory inv)
	{
		if(inv.getItem(10).equals(getGodAppleBook())) return;

		inv.setItem(10, getGodAppleBook());
		inventoryOnwer.updateInventory();
	}

	public ItemStack getGodAppleBook()
	{
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "Enchanted Golden Apple Book");
		meta.setLore(Arrays.asList(ChatColor.BLUE + "Price: " + 2 + " lapides",
				" ",
				ChatColor.GRAY + "Restores 2 Food Bars + 9.6 Saturation",
				ChatColor.BLUE + "Absorption IV (2:00)",
				ChatColor.BLUE + "Regeneration II (0:20)",
				ChatColor.BLUE + "Fire Resistance (5:00)",
				ChatColor.BLUE + "Resistance (5:00)",
				" "));
		book.setItemMeta(meta);
		return book;
	}
	
	public void putMenuItems(ItemStack[] items, int[] slots) throws IllegalArgumentException
	{
		if(items.length != slots.length)
			throw new IllegalArgumentException();
		
		for(int x = 0; x < items.length; x++)
		{
			enchantInv.setItem(slots[x], items[x]);
		}
	}
	
	public Inventory getEnchantInv()
	{
		return enchantInv;
	}


	public static BukkitTask getReduceGlitch() {
		return reduceGlitch;
	}
	
	public Map<Material, List<EnchantFamily>> getItemToFamilies()
	{
		return this.itemToFamilies;
	}
}
