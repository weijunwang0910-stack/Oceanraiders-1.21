package me.cryptidyy.oceanraiders.loot;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.utility.Hologram;
import me.cryptidyy.oceanraiders.utility.Timer;
import net.md_5.bungee.api.ChatColor;

public class LootChest {

	private final Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Loot Chest");
	private final Location chestLoc;
	private final UUID id;
	
	private final int index;
	
	private static ItemStack[] invContents;

	private final Hologram hologram;
	private final LootChestManager manager;
	
	private final LootRefresher lootRefresher;
	private final LootTable lootTable;
	
	private final String islandName;
	private final String chestName;

	private Timer timer;
	
	public LootChest(Location chest, Player player, LootTable table, LootChestManager manager, String islandName, 
			String chestName, int index)
	{
		this.id = player.getUniqueId();
		this.chestLoc = chest.clone().add(chest.getX() > 0 ? 0.5 : 0.5, -1.0, chest.getZ() > 0 ? 0.5 : 0.5);
		this.manager = manager;
		this.lootTable = table;
		this.lootRefresher = new LootRefresher(Main.getPlugin(Main.class), this, lootTable, manager);
		this.islandName = islandName;
		this.index = index;
		this.chestName = chestName;
		
		hologram = new Hologram(chestLoc, "default hologram");
		generateInv(manager.getGenerateTimes());
	}
	
//	public Location getCenter(Location loc) {
//	    return new Location(loc.getWorld(),
//	        getRelativeCoord(loc.getBlockX()),
//	        loc.getY(),
//	        getRelativeCoord(loc.getBlockZ()));
//	}
//
//	private double getRelativeCoord(double i) {
//	    double d = i;
//	    d = d <= 0 ? d + .5 : d - .5;
//	    return d;
//	}
	
	public void generateInv(int amount)
	{
		if(inv == null)
			Bukkit.broadcastMessage("inv is null!");

		//Add at least 1 item
		inv.addItem(lootTable.getRandomItem());

		for(int i = 0; i < amount; i++)
		{
			inv.addItem(lootTable.getRandom());
		}
		
		//Convert array to arraylist
		invContents = Arrays
				.stream(inv.getContents())
				.filter(item -> item != null)
				.toArray(ItemStack[]::new);
		
		//invContents = new ItemStack[] {
		//	iron
		//};
		

		for(int i = 0; i < invContents.length; i++)
		{
			if(invContents[i] == null)
			{
				Bukkit.broadcastMessage("Null");
			}
			else
			{
				//Bukkit.broadcastMessage(invContents[i].getType().name() + invContents[i].getAmount());
			}
			

		}
		
		shuffleItems(invContents);

	}
	
	public void shuffleItems(ItemStack[] items)
	{
		inv.clear();
		//Bukkit.broadcastMessage(items.length + "");
		//Loop thru every itemstack
		for(int i = 0; i <= items.length - 1; i++)
		{
			ItemStack item = invContents[i];
			if(item == null) continue;
			
			//int divideAmount = (int) (item.getAmount() * ((double)items.length / (double)inv.getSize()));
			

			int totalItemsAmount = Arrays.stream(items).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
			double divideAmount = ((double)item.getAmount() / (double)totalItemsAmount) * inv.getSize();
			
			if(divideAmount > item.getAmount() || divideAmount == 0)
			{
				divideAmount = item.getAmount();
			}
			
			int partialAmount = (int) (item.getAmount() / divideAmount);
			
			ItemStack partialItem = item;
			partialItem.setAmount(partialAmount);
			
			//Loop thru all the partial items
			for(int index = 1; index <= divideAmount; index++)
			{
				//Pick a random slot
				int randSlot = (int)(Math.random() * (inv.getSize() - 1));
				
				if(inv.getItem(randSlot) == null)
				{
					inv.setItem(randSlot, partialItem);
				}
				else if(inv.getItem(randSlot).getType().equals(item.getType()))
				{
					inv.setItem(randSlot, partialItem);
					inv.getItem(randSlot).setAmount(partialItem.getAmount() + inv.getItem(randSlot).getAmount());
				}
				else
				{
					inv.addItem(partialItem);
				}
				
			}	
			
		}
	}
	
	public void displayHologram(Player player)
	{
		hologram.display(player);
	}
	
	public void setupRefreshHologram()
	{
		Player player = Bukkit.getPlayer(id);
		hologram.display(player);
		
		timer = new Timer(this, manager.getRefreshSeconds());
		timer.countDown();
	}

	public void stopRefresh()
	{
		timer.stop();
	}

	public void hideHologram()
	{
		if(hologram == null) return;
		
		Player player = Bukkit.getPlayer(id);
		hologram.hide(player);

		if(!timer.isCancelled())
			timer.cancel();
	}
	
	public boolean isEmpty(Inventory inv)
	{
		for(ItemStack content : inv.getContents())
		{
			if(content != null)
				return false;
		}
		return true;
	}
	
	public Player getPlayer() {
		
		Player player = Bukkit.getPlayer(id);
		return player;
	}

	public UUID getUUID()
	{
		return id;
	}
	
	public Inventory getInventory()
	{
		return inv;
	}
	public Hologram getHologram()
	{
		return this.hologram;
	}

	public Location getChestLoc() {
		return chestLoc;
	}

	public LootChestManager getManager() {
		return manager;
	}

	public LootRefresher getLootRefresher() {
		return lootRefresher;
	}
	
	public LootTable getLootTable()
	{
		return this.lootTable;
	}

	public String getIslandName() {
		return islandName;
	}

	public int getIndex() {
		return index;
	}

	public String getChestName() {
		return chestName;
	}
}
