package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.cryptidyy.oceanraiders.Main;

public class FlyingTrident extends BukkitRunnable implements Listener {

	private Player targetPlayer;
	private Player sourcePlayer;
	
	private Location target;
	private Location source;

	private double speed;
	private float knockback = 0.6f;
	
	private Trident flyingTrident;
	private ItemStack trident;
	private boolean tridentFailed = false;
	
	public FlyingTrident(Player target, double speed, Player source, ItemStack trident)
	{
		this.target = target.getLocation().add(0,1,0);
		this.targetPlayer = target;
		this.speed = speed;
		this.source = source.getLocation();
		this.sourcePlayer = source;
		this.trident = trident;
		
		flyingTrident = (Trident) target.getWorld().spawnEntity(sourcePlayer.getEyeLocation()
				.add(sourcePlayer.getLocation().getDirection().normalize()), EntityType.TRIDENT);
		flyingTrident.setGravity(false);
		flyingTrident.setInvulnerable(true);
		flyingTrident.setBounce(false);
		flyingTrident.setDamage(0.0D);

		targetDirectionFromSource = new Vector(this.target.getX() - this.source.getX(),
				this.target.getY() - this.source.getY(),
				this.target.getZ() - this.source.getZ());
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		//set initial velocity
		flyingTrident.setVelocity(targetDirectionFromSource.normalize().multiply(speed));
	}

	Vector targetDirectionFromSource;
	Location newTargetLocation;
	Location currentSourceLocation;
	
	List<Location> particles = new ArrayList<>();
	
	@Override
	public void run() {	

		//fly to target
		newTargetLocation = targetPlayer.getLocation().add(0,1,0);
		currentSourceLocation = flyingTrident.getLocation();
		
		targetDirectionFromSource = new Vector(newTargetLocation.getX() - currentSourceLocation.getX(),
				newTargetLocation.getY() - currentSourceLocation.getY(),
				newTargetLocation.getZ() - currentSourceLocation.getZ());
		
		//set trident velocity
		flyingTrident.setVelocity(targetDirectionFromSource.normalize().multiply(speed));


		//Display cool particles
		Vector particleLocation = flyingTrident.getBoundingBox().getCenter().subtract(targetDirectionFromSource.multiply(4));
		particles.add(particleLocation.toLocation(flyingTrident.getWorld()));
		
		for(Location particle : particles)
		{
			flyingTrident.getWorld().spawnParticle(Particle.END_ROD, particle, 0, 0D, 0D, 0D, 0, null, true);
		}

		//avoid blocks

		//Hit block
		if(flyingTrident.isInBlock())
		{
			tridentFailed = true;
			flyingTrident.remove();
			sourcePlayer.sendMessage("The trident missed!");
			sourcePlayer.getInventory().addItem(trident);
			
			this.cancel();
			return;
		}
		
		if(flyingTrident.getLocation().distance(newTargetLocation) < 2)
		{
			flyingTrident.remove();
			particles.clear();
			newTargetLocation.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, newTargetLocation, 50, 1D, 1D, 1D, 0, null, true);
			
			Location teleportLoc = newTargetLocation.clone();
			teleportLoc.setYaw(sourcePlayer.getLocation().getYaw());
			teleportLoc.setPitch(sourcePlayer.getLocation().getPitch());
			
			sourcePlayer.teleport(teleportLoc);

			if(targetPlayer.getHealth() > 2)
				targetPlayer.damage(0);
			
			targetPlayer.setInvulnerable(true);
			targetPlayer.setVelocity((targetPlayer.getLocation().toVector()
					.subtract(targetDirectionFromSource)
					.normalize().multiply(knockback).setY(0.4f)));
			
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), (bukkitTask) -> {
				targetPlayer.setInvulnerable(false);
			}, 10);
			
			HandlerList.unregisterAll(this);
			this.cancel();
			return;
		}
		
	}

	public boolean isTridentFailed() {
		return tridentFailed;
	}
}
