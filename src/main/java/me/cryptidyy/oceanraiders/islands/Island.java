package me.cryptidyy.oceanraiders.islands;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Island {
	
	public Island (String displayName, 
			Location cornerOne, 
			Location cornerTwo, 
			Location spawnLoc, 
			Location dropLoc, 
			Location chestLoc,
			Location lootChestOne,
			Location lootChestTwo,
			Location lootChestThree,
			Location blackSmith, 
			Location librarian,
			Location farmer,
			Location witch,
		    List<Location> dockLocations)
	{
		this.configName = displayName.replace(" ", "_").toUpperCase();
		this.setDisplayName(displayName);
		this.setCornerOne(cornerOne);
		this.setCornerTwo(cornerTwo);
		this.setDropLoc(dropLoc);
		this.setSpawnLoc(spawnLoc);
		this.chestLoc = chestLoc;
		this.setLootChestOne(lootChestOne);
		this.setLootChestTwo(lootChestTwo);
		this.setLootChestThree(lootChestThree);
		this.setBlackSmith(blackSmith);
		this.setLibrarian(librarian);
		this.setFarmer(farmer);
		this.setWitch(witch);
		this.setDockLocations(dockLocations);
	}
	
	private String displayName;
	private String configName;
	
	private Location spawnLoc;
	private Location dropLoc;
	
	private Location cornerOne;
	private Location cornerTwo;
	
	private Location chestLoc;
	
	private Location lootChestOne;
	private Location lootChestTwo;
	private Location lootChestThree;
	
	private Location blackSmith, librarian, farmer, witch;

	private List<Location> docks;
	
	//Shops:
	//Farmer
	//Blacksmith
	//Librarian
	//Witch
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getConfigName() {
		return configName;
	}
	
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	public Location getCornerOne() {
		return cornerOne;
	}
	
	public void setCornerOne(Location cornerOne) {
		this.cornerOne = cornerOne;
	}
	
	public Location getCornerTwo() {
		return cornerTwo;
	}
	
	public void setCornerTwo(Location cornerTwo) {
		this.cornerTwo = cornerTwo;
	}

	public Location getSpawnLoc() {
		return spawnLoc;
	}

	public void setSpawnLoc(Location spawnLoc) {
		this.spawnLoc = spawnLoc;
	}

	public Location getDropLoc() {
		return dropLoc;
	}

	public void setDropLoc(Location dropLoc) {
		this.dropLoc = dropLoc;
	}

	public Location getChestLoc() {
		return chestLoc;
	}

	public Location getLootChestOne() {
		return lootChestOne;
	}

	public void setLootChestOne(Location lootChestOne) {
		this.lootChestOne = lootChestOne;
	}

	public Location getLootChestTwo() {
		return lootChestTwo;
	}

	public void setLootChestTwo(Location lootChestTwo) {
		this.lootChestTwo = lootChestTwo;
	}

	public Location getLootChestThree() {
		return lootChestThree;
	}

	public void setLootChestThree(Location lootChestThree) {
		this.lootChestThree = lootChestThree;
	}

	public Location getBlackSmith() {
		return blackSmith;
	}

	public void setBlackSmith(Location blackSmith) {
		this.blackSmith = blackSmith;
	}

	public Location getLibrarian() {
		return librarian;
	}

	public void setLibrarian(Location librarian) {
		this.librarian = librarian;
	}

	public Location getFarmer() {
		return farmer;
	}

	public void setFarmer(Location farmer) {
		this.farmer = farmer;
	}

	public Location getWitch() {
		return witch;
	}

	public void setWitch(Location witch) {
		this.witch = witch;
	}

	public List<Location> getDockLocations() {return this.docks;}

	public void setDockLocations(List<Location> dockLocations) {this.docks = dockLocations;}
	
}
