package me.cryptidyy.oceanraiders.islands;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TemporaryIsland {

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

	private Location waitCornerOne;
	private Location waitCornerTwo;
	private Location respawnLoc;

	private List<Location> lootContainers;
	
	//Shops:
	//Farmer
	//Blacksmith
	//Librarian
	//Witch
	
	
	public TemporaryIsland(String displayName)
	{
		this.displayName = displayName;
		this.spawnLoc = null;
		this.dropLoc = null;
		this.cornerOne = null;
		this.cornerTwo = null;
		this.chestLoc = null;
		this.lootChestOne = null;
		this.lootChestTwo = null;
		this.lootChestThree = null;
		this.blackSmith = null;
		this.librarian = null;
		this.farmer = null;
		this.witch = null;
		this.docks = new ArrayList<>();
		this.waitCornerOne = null;
		this.waitCornerTwo = null;
		this.respawnLoc = null;
		this.lootContainers = new ArrayList<>();
	}
	
	public TemporaryIsland(Island island)
	{
		this.displayName = island.getDisplayName();
		this.configName = island.getConfigName();
		this.spawnLoc = island.getSpawnLoc();
		this.dropLoc = island.getDropLoc();
		this.cornerOne = island.getCornerOne();
		this.cornerTwo = island.getCornerTwo();
		this.chestLoc = island.getChestLoc();
		this.lootChestOne = island.getLootChestOne();
		this.lootChestTwo = island.getLootChestTwo();
		this.lootChestThree = island.getLootChestThree();
		this.blackSmith = island.getBlackSmith();
		this.librarian = island.getLibrarian();
		this.farmer = island.getFarmer();
		this.witch = island.getWitch();
		this.docks = island.getDockLocations();
		this.waitCornerOne = island.getWaitCornerOne();
		this.waitCornerTwo = island.getWaitCornerTwo();
		this.respawnLoc = island.getRespawnLoc();
		this.lootContainers = island.getLootContainers();
	}
	
	public Island toIsland()
	{
		if(spawnLoc == null || dropLoc == null || cornerOne == null || cornerTwo == null || chestLoc == null 
				|| lootChestOne == null || lootChestTwo == null || lootChestThree == null || librarian == null
				|| blackSmith == null || farmer == null || witch == null || docks == null || waitCornerOne == null
				|| waitCornerTwo == null || respawnLoc == null || lootContainers == null)
		{
			return null;
		}
		
		return new Island(displayName, 
				cornerOne, 
				cornerTwo, 
				spawnLoc, 
				dropLoc, 
				chestLoc, 
				lootChestOne, 
				lootChestTwo, 
				lootChestThree,
				blackSmith,
				librarian,
				farmer,
				witch,
				docks,
				waitCornerOne,
				waitCornerTwo,
				respawnLoc,
				lootContainers);
	}
	
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

	public void setChestLoc(Location chestLoc) {
		this.chestLoc = chestLoc;
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
	
	public void setBlacksmith(Location blackSmith)
	{
		this.blackSmith = blackSmith;
	}
	
	public void setLibrarian(Location librarian)
	{
		this.librarian = librarian;
	}
	
	public void setFarmer(Location farmer)
	{
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

	public Location getWaitCornerOne()
	{
		return this.waitCornerOne;
	}

	public void setWaitCornerOne(Location loc)
	{
		this.waitCornerOne = loc;
	}

	public Location getWaitCornerTwo()
	{
		return this.waitCornerTwo;
	}

	public void setWaitCornerTwo(Location loc)
	{
		this.waitCornerTwo = loc;
	}

	public Location getRespawnLoc()
	{
		return this.respawnLoc;
	}

	public void setRespawnLoc(Location loc)
	{
		this.respawnLoc = loc;
	}


	public List<Location> getLootContainers()
	{
		return this.lootContainers;
	}

	public void setLootContainers(List<Location> lootContainers)
	{
		this.lootContainers = lootContainers;
	}
}
