package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVillager;

public class EnchantNPC extends EntityVillager {

	public EnchantNPC(Location loc) 
	{
		super(EntityTypes.VILLAGER, ((CraftWorld)loc.getWorld()).getHandle());
		
		Villager villager = (Villager) this.getBukkitEntity();
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		this.setNoAI(true);
		this.setCustomName(new ChatComponentText(ChatColor.YELLOW + "LIBRARIAN"));
		this.setCustomNameVisible(true);
		this.setInvulnerable(true);
		villager.setProfession(Profession.LIBRARIAN);
		villager.setRotation(loc.getYaw(), loc.getPitch());
	}
}
