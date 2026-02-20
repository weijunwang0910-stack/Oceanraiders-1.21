package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;

public class ImmunityMilk extends OceanItem implements Listener {

	public static List<UUID> immunePlayers = new ArrayList<>();
	
	private BukkitTask timer;
	
	public ImmunityMilk(List<String> lore, int slot) 
	{
		super(OceanItemType.IMMUNITY_MILK, lore, slot);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	@Override
	public void useItem(CustomItemUser user, GameManager manager)
	{
		Player player = user.getUser();
		
		if(immunePlayers.contains(player.getUniqueId())) immunePlayers.remove(player.getUniqueId());
		
		immunePlayers.add(player.getUniqueId());
		
		player.sendMessage(ChatColor.AQUA + "You are now immune to splash potions for 01:30 seconds!");
		
		if(timer != null) timer.cancel();
		
		timer = new BukkitRunnable()
		{
			@Override
			public void run() 
			{
				immunePlayers.remove(user.getUser().getUniqueId());
				user.getUser().sendMessage(ChatColor.RED + "You are no longer immune to splash potions!");
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20 * 90);
		
	}
	
	@EventHandler
	public void onSplashed(PotionSplashEvent event)
	{
		for(LivingEntity entity : event.getAffectedEntities())
		{
			if(!(entity instanceof Player)) return;
			
			Player player = (Player) entity;
			if(!immunePlayers.contains(player.getUniqueId())) return;
			
			//remove effect for that player
			event.setIntensity(player, 0);
			
			//player.sendMessage("You blocked a splash potion!");
			player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().clone().add(0,0,0), 50, 0.5d, 2d, 0.5d, 20,
					new Particle.DustOptions(Color.WHITE, 2));
			
		}
	}

}
