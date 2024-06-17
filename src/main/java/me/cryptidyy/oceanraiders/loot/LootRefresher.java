package me.cryptidyy.oceanraiders.loot;

import me.cryptidyy.oceanraiders.Main;

public class LootRefresher {

	private LootChest lootChest;
	private LootChestManager lootManager;
	
	public LootRefresher(Main plugin, LootChest lootChest, LootTable lootTable, LootChestManager manager)
	{
		this.lootChest = lootChest;
		this.lootManager = manager;
		
		/*lootTable = new LootTable.LootTableBuilder()
				.add(new ItemStack(Material.IRON_INGOT), 3)
				.add(new ItemStack(Material.GOLD_INGOT), 1)		
				.build();*/
	}
	
	public void forceRefresh()
	{
		lootChest.generateInv(lootManager.getGenerateTimes());
	}



}
