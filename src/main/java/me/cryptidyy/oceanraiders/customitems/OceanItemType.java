package me.cryptidyy.oceanraiders.customitems;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum OceanItemType {

	FIRE_PLACER(Material.FLINT_AND_STEEL, ChatColor.GOLD + "Fire Placer"), 
	COBWEB_WALL(Material.COBWEB, ChatColor.GOLD + "Cobweb Wall"), 
	TELEPORT_TRIDENT(Material.TRIDENT, ChatColor.GOLD + "Teleport Trident"),
	BOAT_SINKER(Material.ANVIL, ChatColor.GOLD + "Boat Sinker"), 
	IMMUNITY_MILK(Material.MILK_BUCKET, ChatColor.GOLD + "Immunity Milk"),
	WHOLE_CAKE(Material.CAKE, ChatColor.GOLD + "Whole Cake"),
	INVISIBILITY(Material.POTION, ChatColor.WHITE + "Invisibility Potion");
	
	public Material material;
	public String displayName;
	
	OceanItemType(Material material, String name)
	{
		this.material = material;
		this.displayName = name;
	}
}
