package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class EnchantNPC {

	private final Villager NPC;
	public EnchantNPC(Location loc)
	{
		NPC = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

		NPC.setAI(false);
		NPC.setCustomName(ChatColor.YELLOW + "LIBRARIAN");
		NPC.setCustomNameVisible(true);
		NPC.setInvulnerable(true);

		NPC.setRotation(loc.getYaw(), loc.getPitch());
		NPC.setProfession(Profession.LIBRARIAN);

	}

	public Villager getNPC()
	{
		return NPC;
	}
}
