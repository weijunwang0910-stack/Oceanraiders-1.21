package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class FoodNPC {

	private final Villager NPC;
	public FoodNPC(Location loc) 
	{
		NPC = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

		NPC.setAI(false);
		NPC.setCustomName(ChatColor.YELLOW + "FARMER");
		NPC.setCustomNameVisible(true);
		NPC.setInvulnerable(true);

		NPC.setRotation(loc.getYaw(), loc.getPitch());
		NPC.setProfession(Profession.FARMER);

	}

	public Villager getNPC()
	{
		return NPC;
	}
	
}
