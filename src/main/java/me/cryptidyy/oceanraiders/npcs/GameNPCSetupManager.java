package me.cryptidyy.oceanraiders.npcs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityVillager;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.Island;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.npcs.BlackSmithNPC;
import me.cryptidyy.oceanraiders.npcs.ClickNPC;
import me.cryptidyy.oceanraiders.npcs.EnchantNPC;
import me.cryptidyy.oceanraiders.npcs.FoodNPC;
import me.cryptidyy.oceanraiders.npcs.NPCShopManager;
import me.cryptidyy.oceanraiders.npcs.PacketReader;
import me.cryptidyy.oceanraiders.npcs.PotionNPC;

public class GameNPCSetupManager {

	private Main plugin;
	
	private BlackSmithNPC blacksmith;
	private EnchantNPC librarian;
	private NPCShopManager shopManager = new NPCShopManager();
	
	private static PacketReader packetReader;
	
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
		WorldServer world = ((CraftWorld) plugin.getGameManager().getGameWorld()).getHandle();
		
		BlackSmithNPC redBlacksmith = new BlackSmithNPC(redIsland.getBlackSmith());
		EnchantNPC redLibrarian = new EnchantNPC(redIsland.getLibrarian());
		FoodNPC redFarmer = new FoodNPC(redIsland.getFarmer());
		PotionNPC redPotion = new PotionNPC(redIsland.getWitch());
		
		npcs.add(redBlacksmith);
		npcs.add(redLibrarian);
		npcs.add(redFarmer);
		npcs.add(redPotion);
		
		world.addEntity(redBlacksmith);
		world.addEntity(redLibrarian);
		world.addEntity(redFarmer);
		world.addEntity(redPotion);
		
		Island blueIsland = islandManager.findIsland("Blue Island").get();
		
		BlackSmithNPC blueBlacksmith = new BlackSmithNPC(blueIsland.getBlackSmith());
		EnchantNPC blueLibrarian = new EnchantNPC(blueIsland.getLibrarian());
		FoodNPC blueFarmer = new FoodNPC(blueIsland.getFarmer());
		PotionNPC bluePotion = new PotionNPC(blueIsland.getWitch());
		
		npcs.add(blueBlacksmith);
		npcs.add(blueLibrarian);
		npcs.add(blueFarmer);
		npcs.add(bluePotion);
		
		world.addEntity(blueBlacksmith);
		world.addEntity(blueLibrarian);
		world.addEntity(blueFarmer);
		world.addEntity(bluePotion);
		
		registerPlayers();

	}
	
	public void unSetNPCs()
	{
		plugin.getGameManager().getGameWorld().getEntities().stream().filter(entity -> entity instanceof EntityVillager).forEach(npc -> {
			npc.remove();
		});
		
		unRegisterNPCs();
		
	}
	
	public void registerPlayers()
	{
		packetReader = new PacketReader();
		
		plugin.getGameManager().getAllPlayers().forEach(uuid -> {
			packetReader.create(Bukkit.getPlayer(uuid));

		});

		Bukkit.getServer().getPluginManager().registerEvents(new ClickNPC(shopManager), plugin);
	}
	
	public void unRegisterNPCs()
	{
		plugin.getGameManager().getPlayingPlayers().forEach(uuid -> {
			if(packetReader == null) return;
			
			packetReader.remove(Bukkit.getPlayer(uuid));
			
		});
	}
	
	public static void unRegisterPlayer(Player player)
	{
		if(packetReader != null)
			packetReader.remove(player);
	}
	
	public static void registerPlayer(Player player)
	{
		if(packetReader == null) return;
		
		try
		{
			packetReader.create(player);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public BlackSmithNPC getBlacksmith() {
		return blacksmith;
	}

	public EnchantNPC getLibrarian() {
		return librarian;
	}
}
