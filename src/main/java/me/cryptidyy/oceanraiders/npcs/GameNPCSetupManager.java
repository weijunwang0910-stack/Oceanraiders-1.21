package me.cryptidyy.oceanraiders.npcs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.Island;
import me.cryptidyy.oceanraiders.islands.IslandManager;

public class GameNPCSetupManager {

	private Main plugin;
	
	private BlackSmithNPC blacksmith;
	private EnchantNPC librarian;
	private NPCShopManager shopManager = new NPCShopManager();
	
	public static List<Entity> npcs = new ArrayList<>();
	
	private IslandManager islandManager;
	
	public GameNPCSetupManager(Main plugin)
	{
		this.plugin = plugin;
		this.islandManager = plugin.getIslandManager();
	}
	
	public void setNPCs()
	{
		Island redIsland = islandManager.findIsland("Red Island").get();
		
		BlackSmithNPC redBlacksmith = new BlackSmithNPC(redIsland.getBlackSmith());
		EnchantNPC redLibrarian = new EnchantNPC(redIsland.getLibrarian());
		FoodNPC redFarmer = new FoodNPC(redIsland.getFarmer());
		PotionNPC redPotion = new PotionNPC(redIsland.getWitch());
		
		npcs.add(redBlacksmith.getNPC());
		npcs.add(redLibrarian.getNPC());
		npcs.add(redFarmer.getNPC());
		npcs.add(redPotion.getNPC());
		
		Island blueIsland = islandManager.findIsland("Blue Island").get();
		
		BlackSmithNPC blueBlacksmith = new BlackSmithNPC(blueIsland.getBlackSmith());
		EnchantNPC blueLibrarian = new EnchantNPC(blueIsland.getLibrarian());
		FoodNPC blueFarmer = new FoodNPC(blueIsland.getFarmer());
		PotionNPC bluePotion = new PotionNPC(blueIsland.getWitch());
		
		npcs.add(blueBlacksmith.getNPC());
		npcs.add(blueLibrarian.getNPC());
		npcs.add(blueFarmer.getNPC());
		npcs.add(bluePotion.getNPC());

		Bukkit.getServer().getPluginManager().registerEvents(new ClickNPC(shopManager), plugin);

	}
	
	public void unSetNPCs()
	{
		npcs.forEach(npc -> npc.remove());
	}
	public BlackSmithNPC getBlacksmith() {
		return blacksmith;
	}

	public EnchantNPC getLibrarian() {
		return librarian;
	}
}
