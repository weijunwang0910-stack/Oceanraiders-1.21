package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Witch;

import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityWitch;

public class PotionNPC extends EntityWitch {

	public PotionNPC(Location loc)
	{
		super(EntityTypes.WITCH, ((CraftWorld)loc.getWorld()).getHandle());
		
		Witch witch = (Witch) this.getBukkitEntity();
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		this.setNoAI(true);
		this.setCustomName(new ChatComponentText(ChatColor.YELLOW + "POTION"));
		this.setCustomNameVisible(true);
		this.setInvulnerable(true);
		
		witch.setRotation(loc.getYaw(), loc.getPitch());
		witch.setRemoveWhenFarAway(false);
	}
}
