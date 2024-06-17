package me.cryptidyy.oceanraiders.loot;

import org.bukkit.inventory.ItemStack;

public class Entry {

	private ItemStack item;
	private double chance;
	private int weight;
	
	public Entry(ItemStack item, int weight)
	{
		this.item = item;
		this.weight = weight;
	}

	public ItemStack getItem() {
		return item.clone();
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
