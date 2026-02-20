package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class BlackSmithNPC {

	private final Villager NPC;
	public BlackSmithNPC(Location loc)
	{
		NPC = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

		NPC.setAI(false);
		NPC.setCustomName(ChatColor.YELLOW + "BLACKSMITH");
		NPC.setCustomNameVisible(true);
		NPC.setInvulnerable(true);

		NPC.setRotation(loc.getYaw(), loc.getPitch());
		NPC.setProfession(Profession.ARMORER);

	}

	public Villager getNPC()
	{
		return NPC;
	}
}	