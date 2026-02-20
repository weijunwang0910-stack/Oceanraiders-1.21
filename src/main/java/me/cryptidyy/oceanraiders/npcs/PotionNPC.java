package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class PotionNPC {

	private final Witch NPC;
	public PotionNPC(Location loc)
	{
		NPC = (Witch) loc.getWorld().spawnEntity(loc, EntityType.WITCH);

		NPC.setAI(false);
		NPC.setCustomName(ChatColor.YELLOW + "POTION");
		NPC.setCustomNameVisible(true);
		NPC.setInvulnerable(true);

		NPC.setRotation(loc.getYaw(), loc.getPitch());

	}

	public Witch getNPC()
	{
		return NPC;
	}
}
