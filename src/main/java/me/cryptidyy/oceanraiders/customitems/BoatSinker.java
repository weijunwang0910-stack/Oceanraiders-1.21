package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.cryptidyy.oceanraiders.Main;

public class BoatSinker extends OceanItem implements Listener {

	private List<FallingBlock> launchedAnvils = new ArrayList<>();
	
	public BoatSinker(List<String> lore, int slot) 
	{
		super(OceanItemType.BOAT_SINKER, lore, slot);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}

	@Override
	public void useItem(CustomItemUser user, GameManager manager)
	{
		//launch a projectile
		Player player = user.getUser();
		FallingBlock anvil = player.getWorld()
				.spawnFallingBlock(player.getEyeLocation()
				.add(player.getLocation().getDirection().normalize().multiply(1)), Material.ANVIL.createBlockData());
		
		anvil.setInvulnerable(true);
		anvil.setDropItem(false);
		anvil.setVelocity(player.getLocation().getDirection().multiply(1.5f));
		
		launchedAnvils.add(anvil);
		
		player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		
		Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), (bukkitTask) -> {

			if(launchedAnvils.size() == 0)
			{
				bukkitTask.cancel();
				return;
			}

			//detect if anvil hits player/boat
			for(FallingBlock currentAnvil : launchedAnvils)
			{
				for(Entity target : currentAnvil.getNearbyEntities(4, 4, 4))
				{
					if(!currentAnvil.getBoundingBox().overlaps(target.getBoundingBox().clone().expand(1.2))) continue;
					
					Player hitPlayer = null;
					
					if(target instanceof Player && !target.getUniqueId().equals(player.getUniqueId()))
					{
						hitPlayer = (Player) target;
						//Bukkit.broadcastMessage(player.getName() + " hit " + hitPlayer.getName() + " with an anvil!");
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								launchedAnvils.remove(anvil);
								anvil.remove();
							}

						}.runTaskLater(Main.getPlugin(Main.class), 10);


					}
					else if(target instanceof Boat)
					{
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								launchedAnvils.remove(currentAnvil);
								anvil.remove();
							}

						}.runTaskLater(Main.getPlugin(Main.class), 10);

						if(!target.getPassengers().stream().anyMatch(passenger -> passenger instanceof Player)) continue;
						
						hitPlayer = (Player) target.getPassengers()
									.stream()
									.filter(passenger -> passenger instanceof Player)
									.findFirst().get();

						if(hitPlayer.getUniqueId().equals(player.getUniqueId()) || target.equals(player.getVehicle())) continue;

						//Bukkit.broadcastMessage(player.getName() + " hit " + hitPlayer.getName() + "'s boat with an anvil!");
					}
					
					if(hitPlayer == null) continue;
					if(!hitPlayer.isInsideVehicle()) continue;
					if(!hitPlayer.getVehicle().getType().equals(EntityType.BOAT)) continue;
					
					if(!hitPlayer.getUniqueId().equals(player.getUniqueId()))
					{
						hitPlayer.getVehicle().remove();
						hitPlayer.getWorld().playSound(hitPlayer.getLocation(), Sound.BLOCK_ANVIL_HIT, 2, 1);

						if(anvil != null)
						{
							new BukkitRunnable()
							{
								@Override
								public void run()
								{
									anvil.remove();
									launchedAnvils.remove(currentAnvil);
								}

							}.runTaskLater(Main.getPlugin(Main.class), 10);
						}
						sinkPlayer(hitPlayer);
						continue;
					}
				}

			}
		}, 0, 1);
	}
	
	@EventHandler
	public void onHitGround(EntityChangeBlockEvent event)
	{
		if(!launchedAnvils.contains(event.getEntity())) return;
		event.getEntity().remove();
		event.setCancelled(true);

		launchedAnvils.remove(event.getEntity());

	}

	
	private void sinkPlayer(Player hitPlayer)
	{
		Location lastLocation = hitPlayer.getLocation().add(0, -100, 0).clone();
		
		new BukkitRunnable()
		{
			double ticks = 0;
			
			@Override
			public void run() 
			{
				if(ticks >= 20 * 8) this.cancel();
				
				ticks += 1;
				hitPlayer.setVelocity((hitPlayer.getLocation().toVector()
						.subtract(lastLocation.toVector())).normalize().multiply(-0.3));
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
	}
}
