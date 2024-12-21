package me.cryptidyy.oceanraiders.loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LootTable {
	
	private List<Entry> entries;
	
	public LootTable(List<Entry> entries2)
	{
		this.entries = entries2;
	}
	
	public ItemStack getRandom()
	{
		double random = Math.random();
		
		for(int i = 0; i < (entries.size() - 1); i++)
		{
			Entry entry = entries.get(i + 1);
			if(entry.getChance() > random) return entries.get(i).getItem();
		}
		return entries.get(entries.size() - 1).getItem();
	}

	//Guarantees at least one item in the loot chest
	public ItemStack getRandomItem()
	{
		for(int i = 0; i < entries.size(); i++)
		{
			Entry entry = entries.get(i);
			if(!entry.getItem().getType().equals(Material.AIR)) return entry.getItem();
		}

		return new ItemStack(Material.AIR);
	}
	
	public static class LootTableBuilder
	{
		private int totalWeight = 0;
		private List<Entry> entries = new ArrayList<>();
		
		public LootTableBuilder add(ItemStack item, int weight)
		{
			totalWeight += weight;
			entries.add(new Entry(item, weight));
			return this;
		}
		
		public LootTableBuilder addList(List<ItemStack> items, List<Integer> weights)
		{
			if(weights.size() != items.size()) throw new IllegalArgumentException();
			
			totalWeight += weights.stream().mapToInt(Integer::intValue).sum();
			
			for(int x = 0; x <= items.size() - 1; x++)
			{
				entries.add(new Entry(items.get(x), weights.get(x)));
			}
			
			return this;
		}
		
		public boolean isBuilt()
		{
			return entries.size() > 0 && totalWeight > 0;
		}
		
		public LootTable build()
		{
			if(!isBuilt()) return null;
			
			double base = 0;
			for(Entry entry : entries)
			{
				double chance = getChance(base);
				entry.setChance(chance);
				base += entry.getWeight();
			}
			
			return new LootTable(entries);
			
		}
		
		private double getChance(double weight)
		{
			return weight/totalWeight;
		}
	}
	
}
