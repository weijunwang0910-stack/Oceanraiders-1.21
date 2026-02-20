package me.cryptidyy.oceanraiders.tickers;

import me.cryptidyy.oceanraiders.islands.Island;
import me.cryptidyy.oceanraiders.state.EndState;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Chest;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public class GameLoop extends BukkitRunnable {

	private Main plugin;

	private GameManager manager;
	
	private IslandManager islandManager;
	
	private BukkitTask podiumParticles;
	
	public GameLoop(Main plugin)
	{
		this.plugin = plugin;
		this.manager = plugin.getGameManager();
		
		Location red = manager.getRedTreasureDrop().getLocation();
		Location blue = manager.getBlueTreasureDrop().getLocation();
		
		podiumParticles = this.schedulePodiumParticle(red, blue, 20);
	}
	
	@Override
	public void run() 
	{
		/*red.getWorld().spawnParticle(Particle.BLOCK_CRACK, 
				red.clone().add(0,1.5,0), 50, 0.4, 0.4, 0.4, 
				Material.REDSTONE_BLOCK.createBlockData());*/

		
		// Detect if treasure chest is empty
		islandManager = plugin.getIslandManager();
		
		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();
		
		if(!redChest.getBlockInventory().contains(manager.getRedTreasure()))
		{
			//Red treasure stolen
			/*game.getTeamRed().getPlayers().forEach(uuid -> {
				Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "Your treasure is stolen!"));
			});*/

			manager.getTeamRed().setTreasureStolen(true);
		}
		else
		{
			//Red treasure intact
			/*game.getTeamRed().getPlayers().forEach(uuid -> {
				Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.GREEN + "Your treasure is intact!"));
			});*/

			manager.getTeamRed().setTreasureStolen(false);
		}
		
		if(!blueChest.getBlockInventory().contains(manager.getBlueTreasure()))
		{
			//Blue treasure stolen
			/*game.getTeamBlue().getPlayers().forEach(uuid -> {
				Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "Your treasure is stolen!"));
			});*/

			manager.getTeamBlue().setTreasureStolen(true);
			
		}
		else 
		{
			//Blue treasure intact
			/*game.getTeamBlue().getPlayers().forEach(uuid -> {
				Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.GREEN + "Your treasure is intact!"));
			});*/

			manager.getTeamBlue().setTreasureStolen(false);
		}
		
		// Detect if treasure is dropped
		manager.getRedTreasureDrop().getNearbyEntities(0.5, 0.5, 0.5).forEach(entity ->
		{	
			if(entity instanceof Item)
			{
				Item item = (Item) entity;
				
				if(item.getItemStack().equals(manager.getBlueTreasure()))
				{
					this.scheduleEndParticle(manager.getRedTreasureDrop().getLocation());
					
					//Bukkit.broadcastMessage("Red team won the game!");
					manager.getTeamRed().getOnlinePlayers().forEach(uuid -> {
						Bukkit.getPlayer(uuid).sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD 
								+ "VICTORY!", "You have won the game!", 5, 20 * 2, 20 * 1);
					});

					manager.getTeamBlue().getOnlinePlayers().forEach(uuid -> {
						Bukkit.getPlayer(uuid).sendTitle(ChatColor.RED + "" + ChatColor.BOLD
								+ "GAME OVER", "Red team has won the game.", 5, 20 * 2, 20 * 1);
					});

					item.remove();
					manager.setState(new EndState());
				}
			}
		});

		manager.getBlueTreasureDrop().getNearbyEntities(0.5, 0.5, 0.5).forEach(entity ->
		{		
			if(entity instanceof Item)
			{
				Item item = (Item) entity;
				
				if(item.getItemStack().equals(manager.getRedTreasure()))
				{
					this.scheduleEndParticle(manager.getRedTreasureDrop().getLocation());
					
					//Bukkit.broadcastMessage("Blue team won the game!");
					manager.getTeamBlue().getOnlinePlayers().forEach(uuid -> {
						Bukkit.getPlayer(uuid).sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD 
								+ "VICTORY!", "You have won the game!", 5, 20 * 2, 20 * 1);
					});

					manager.getTeamRed().getOnlinePlayers().forEach(uuid -> {
						Bukkit.getPlayer(uuid).sendTitle(ChatColor.RED + "" + ChatColor.BOLD
								+ "GAME OVER", "Blue team has won the game.", 5, 20 * 2, 20 * 1);
					});
					
					item.remove();
					manager.setState(new EndState());
				}
			}
		});
		
		//Detect if player has treasure
		if(manager.getTeamRed().isTreasureStolen())
		{
			manager.getTeamBlue().getOnlinePlayers().forEach(uuid -> {

				Player player = Bukkit.getPlayer(uuid);
				if(player.getInventory().contains(manager.getRedTreasure()))
				{
					player.setGlowing(true);
					PlayerManager.toOceanPlayer(player).setHasRedTreasure(true);
					return;
				}
				PlayerManager.toOceanPlayer(player).setHasRedTreasure(false);
				player.setGlowing(false);
				
			});
		}
		
		if(manager.getTeamBlue().isTreasureStolen())
		{
			manager.getTeamRed().getOnlinePlayers().forEach(uuid -> {
				
				Player player = Bukkit.getPlayer(uuid);
				if(player.getInventory().contains(manager.getBlueTreasure()))
				{
					player.setGlowing(true);
					PlayerManager.toOceanPlayer(player).setHasBlueTreasure(true);
					return;
				}
				player.setGlowing(false);
				PlayerManager.toOceanPlayer(player).setHasBlueTreasure(false);
				
			});
		}

		for(Island island : islandManager.getIslands())
		{
			for(Location dockLocation : island.getDockLocations())
			{
				attemptToLaunchPlayer(dockLocation);
			}
		}
	}
	
	public void cancelPodiumParticles()
	{
		if(!podiumParticles.isCancelled())
		{
			podiumParticles.cancel();
		}
	}
	
	private void scheduleEndParticle(Location loc)
	{
		new BukkitRunnable()
		{
			double t = Math.PI / 4;
			double amount = 10;
			double dx, dy, dz;
			
			Location treasureLoc = loc;
			
			@Override
			public void run() {
				
				t += Math.PI / amount;
				
				for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 32)
				{
					dx = t * Math.cos(theta);
					dy = 2 * Math.exp(-0.05 * t) * Math.sin(t) + 1;
					dz = t * Math.sin(theta);
					
					treasureLoc.getWorld().spawnParticle(Particle.FIREWORK,
							treasureLoc.clone().add(dx,dy,dz), 0);
				}

				for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 12)
				{
					dx = t * Math.cos(theta);
					dy = 2 * Math.exp(-0.05 * t) * Math.sin(t) + 1;
					dz = t * Math.sin(theta);

					treasureLoc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
							treasureLoc.clone().add(dx,dy,dz), 0, Material.LIGHT_BLUE_CONCRETE_POWDER.createBlockData());
				}

				if(t > 30)
					this.cancel();
				
			}
			
		}.runTaskTimerAsynchronously(plugin, 0, 1);
	}
	
	private BukkitTask schedulePodiumParticle(Location loc1, Location loc2, int amount)
	{
		return new BukkitRunnable()
		{

			@Override
			public void run() {
				loc1.getWorld().spawnParticle(Particle.DUST,
						loc1.clone().add(0,1.2,0), amount, 1d, 1d, 1d, 1,
						new Particle.DustOptions(Color.RED, 1), true);
				
				loc2.getWorld().spawnParticle(Particle.DUST,
						loc2.clone().add(0,1.2,0), amount, 1d, 1d, 1d, 1,
						new Particle.DustOptions(Color.BLUE, 1), true);
				
			}
			
		}.runTaskTimerAsynchronously(plugin, 0, 1);
	}

	private void attemptToLaunchPlayer(Location dockLocation)
	{
		Collection<Entity> nearbyDockEntities = dockLocation.getWorld().getNearbyEntities(dockLocation, 1, 1, 1);
		if(!nearbyDockEntities.stream().anyMatch(entity -> entity instanceof Player)) return;
		if(!nearbyDockEntities.stream().anyMatch(Entity::isInsideVehicle)) return;

		Collection<Entity> passengers = nearbyDockEntities
				.stream()
				.filter(entity -> entity instanceof Player).collect(Collectors.toList());

		for(Entity passenger : passengers)
		{
			if(!(passenger instanceof Player)) continue;

			Player playerPassenger = (Player) passenger;
			if(!playerPassenger.isInsideVehicle()) continue;
			if(!(playerPassenger.getVehicle() instanceof Boat)) continue;
			if(PlayerManager.toOceanPlayer(playerPassenger).isBoatLaunched()) continue;
			if(playerPassenger.getVehicle().getLocation().getY() > 62.53) continue;

			//launch boat
			Boat boat = (Boat) playerPassenger.getVehicle();
			//boat.setRotation(dockLocation.getYaw(), dockLocation.getPitch());
			Vector toCenter = dockLocation.toVector().setY(0).subtract(boat.getLocation().toVector().setY(0)).multiply(0.2975);
			Vector launchDirection = dockLocation.getDirection().normalize().multiply(5).setY(0.14);

			alignBoat(playerPassenger, dockLocation);
			boat.setVelocity(toCenter);

			PlayerManager.toOceanPlayer(playerPassenger).setBoatLaunched(true);
			PlayerManager.toOceanPlayer(playerPassenger).setBoatSpeeding(true);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				boat.setVelocity(launchDirection);
			},  3);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				boat.setVelocity(launchDirection.normalize());
				PlayerManager.toOceanPlayer(playerPassenger).setBoatLaunched(false);
				//PlayerManager.toOceanPlayer(playerPassenger).setBoatSpeeding(false);
			}, 20 * 3);
		}
	}

	private void alignBoat(Player passenger, Location dockLocation)
	{

	}
}
