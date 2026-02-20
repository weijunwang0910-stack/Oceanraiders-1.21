package me.cryptidyy.oceanraiders.serverlisteners;

import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class WorldGuardEvent implements Listener {

	private Main plugin;
	private GameManager manager;
	
	public WorldGuardEvent(Main plugin)
	{
		this.plugin = plugin;
		manager = plugin.getGameManager();
	}

	@EventHandler
	public void onFrameBreak(HangingBreakByEntityEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onMapBreak(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof ItemFrame) event.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{
		if(event.getPlayer().isOp() && !manager.isStarted()) return;

		if(event.getBlock().getType().equals(Material.TALL_GRASS)) return;
		if(event.getBlock().getType().equals(Material.GRASS_BLOCK)) return;
		if(event.getBlock().getType().equals(Material.FERN)) return;
		if(event.getBlock().getType().equals(Material.LARGE_FERN)) return;
		
		Player player = event.getPlayer();
		
		event.setCancelled(true);
		player.sendMessage(ChatColor.RED + "You cannot break this here!");
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event)
	{
		if(event.getPlayer().isOp() && !manager.isStarted()) return;

		Player player = event.getPlayer();
		
		event.setCancelled(true);
		player.sendMessage(ChatColor.RED + "You cannot place that here!");
	}
	
	@EventHandler
	public void onExplode(BlockExplodeEvent event)
	{
		if(!event.getBlock().getWorld().equals(manager.getGameWorld())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		if(!manager.isStarted()) return;
		if(!manager.getPlayingPlayers().contains(event.getPlayer().getUniqueId())) return;
		if(PlayerManager.toOceanPlayer(event.getPlayer()).isRespawning()) return;
		
		//if player gets out of arena
		if(event.getTo().getY() > 92)
		{
			if(event.getFrom().getY() > 92)
			{
				//big problem
				if(PlayerManager.toOceanPlayer(event.getPlayer()).getPlayerTeam().getTeamName().contains("Red"))
				{
					event.getPlayer().teleport(plugin.getIslandManager().findIsland("Red Island").get().getSpawnLoc());
					event.getPlayer().sendMessage(ChatColor.RED + "You have went out of bounds, teleporting you to spawn...");
					return;
				}
				else
				{
					event.getPlayer().teleport(plugin.getIslandManager().findIsland("Blue Island").get().getSpawnLoc());
					event.getPlayer().sendMessage(ChatColor.RED + "You have went out of bounds, teleporting you to spawn...");
					return;
				}

			}

			event.getPlayer().teleport(event.getFrom());
			event.getPlayer().sendMessage(ChatColor.RED + "You have reached the height limit!");

		}

		Location arenaCornerOne  = new Location(manager.getGameWorld(), 245, 100, -106);
		Location arenaCornerTwo = new Location(manager.getGameWorld(), -551, 0, 237);

		if(!isInRect(event.getPlayer().getLocation(), arenaCornerOne, arenaCornerTwo))
		{
			Vector tpDirection = event.getTo().toVector().subtract(event.getFrom().toVector()).normalize().multiply(-1.5);

			if (event.getFrom().getZ() == event.getTo().getZ() && event.getFrom().getX() == event.getTo().getX())
			{
				tpDirection = event.getPlayer().getLocation().getDirection().normalize().multiply(-1.5);
			}

			if(!isInRect(event.getFrom().add(tpDirection), arenaCornerOne, arenaCornerTwo))
			{
				//big problem
				if(PlayerManager.toOceanPlayer(event.getPlayer()).getPlayerTeam().getTeamName().contains("Red"))
				{
					event.getPlayer().teleport(plugin.getIslandManager().findIsland("Red Island").get().getSpawnLoc());
					event.getPlayer().sendMessage(ChatColor.RED + "You have went out of bounds, teleporting you to spawn...");
					return;
				}
				else
				{
					event.getPlayer().teleport(plugin.getIslandManager().findIsland("Blue Island").get().getSpawnLoc());
					event.getPlayer().sendMessage(ChatColor.RED + "You have went out of bounds, teleporting you to spawn...");
					return;
				}
			}
			else if(event.getPlayer().isInsideVehicle())
			{
				teleportWithPassenger((Vehicle) event.getPlayer().getVehicle(), event.getFrom().add(tpDirection));
			}
			else
			{
				event.getPlayer().teleport(event.getFrom().add(tpDirection));
			}
			event.getPlayer().sendMessage(ChatUtil.format("&cYou cannot go here!"));
		}
	}
	
	@EventHandler
	public void onDrinkPotion(PlayerItemConsumeEvent event)
	{
		if(!event.getItem().getItemMeta().getDisplayName().toLowerCase().contains("potion")) return;
		
		Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE)
			{
				event.getPlayer().getInventory().getItemInMainHand().setAmount(0);
			}
			else
			{
				event.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
			}


		}, 1);
	}
	
//	@EventHandler
//	public void onAnvilDrop(EntitySpawnEvent event)
//	{
//		if(!(event.getEntity() instanceof Item)) return;
//		Item item = (Item) event.getEntity();
//
//		if(item.getItemStack().getType().equals(Material.ANVIL))
//		{
//			event.setCancelled(true);
//		}
//
//
//	}

	public boolean isInRect(Location playerLoc, Location loc1, Location loc2)
	{
		double[] dim = new double[2];

		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);

		if(playerLoc.getX() > dim[1] || playerLoc.getX() < dim[0])
			return false;

		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);

		if(playerLoc.getZ() > dim[1] || playerLoc.getZ() < dim[0])
			return false;

		dim[0] = loc1.getY();
		dim[1] = loc2.getY();
		Arrays.sort(dim);

		if(playerLoc.getY() > dim[1] || playerLoc.getY() < dim[0])
			return false;


		return true;

	}

	private void teleportWithPassenger(Vehicle vehicle, Location location)
	{
		List<Entity> passengers = vehicle.getPassengers();
		if(passengers.size() == 0) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> passengers.forEach(passenger -> vehicle.addPassenger(passenger)), 5);

		passengers.forEach(Entity::leaveVehicle);
		passengers.forEach(vehicle::removePassenger);

		passengers.forEach(passenger -> {
			passenger.teleport(location);
		});

		vehicle.teleport(location);
	}
	
}
