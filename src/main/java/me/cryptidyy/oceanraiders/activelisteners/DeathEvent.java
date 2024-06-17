package me.cryptidyy.oceanraiders.activelisteners;

import java.util.List;
import java.util.Optional;

import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.player.OceanPlayer;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.shop.ItemEntry;
import net.md_5.bungee.api.ChatColor;

public class DeathEvent implements Listener {
	
	private Main plugin;
	
	private GameManager manager;
	
	private IslandManager islandManager;
	
	private Player damager;
	private Player target;
	
	private String treasureReplacedHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE REPLACED > ";
	
	public DeathEvent(Main plugin)
	{
		this.plugin = plugin;
		this.islandManager = this.plugin.getIslandManager();
		this.manager = plugin.getGameManager();
	}
	
	@EventHandler
	public void onKill(EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Player)) return;
		
		Player target = (Player) event.getEntity();
		
		if(!manager.getPlayingPlayers().contains(target.getUniqueId())) return;
		
		//If player is damaged by arrow
		if(!(event.getDamager() instanceof Player))
		{
			if(event.getDamager() instanceof Arrow)
			{
				//Killed by arrow
				if(event.getFinalDamage() >= target.getHealth())
				{
					event.setCancelled(true);
					killTarget(damager, target);
					return;
				}
				damager.sendMessage(manager.getTeamRed().getPlayers().contains(target.getUniqueId()) ?
						ChatColor.RED + target.getName() + ChatColor.GRAY + " is on " + ChatColor.RED + Math.round(target.getHealth() - event.getFinalDamage()) + " HP!"
						: ChatColor.BLUE + target.getName() + ChatColor.GRAY + " is on " + ChatColor.RED + Math.round(target.getHealth() - event.getFinalDamage())  + " HP!");
			}
			return;
		}
		
		Player damager = (Player) event.getDamager();
		
		if(!manager.getPlayingPlayers().contains(damager.getUniqueId())) return;

		//If damager is on same team
		if(manager.getTeamRed().getPlayers().contains(target.getUniqueId())
				&& manager.getTeamRed().getPlayers().contains(damager.getUniqueId()))
		{
			event.setCancelled(true);
			return;
		}
		
		if(manager.getTeamBlue().getPlayers().contains(target.getUniqueId())
				&& manager.getTeamBlue().getPlayers().contains(damager.getUniqueId()))
		{
			event.setCancelled(true);
			return;
		}
		//If target is spawn protected
		if(PlayerManager.toOceanPlayer(target).isSpawnProtected())
		{
			event.setCancelled(true);
			return;
		}
		
		
		//If target is killed
		if(event.getFinalDamage() >= target.getHealth())
		{
			event.setCancelled(true);
			killTarget(damager, target);
			PlayerManager.toOceanPlayer(target).setOnDamageCheckCooldown(true);
			
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event)
	{
		if(!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		
		if(!manager.getPlayingPlayers().contains(player.getUniqueId())) return;

		//If player is killed by non entity
		if(event.getCause().equals(DamageCause.ENTITY_ATTACK) 
				|| event.getCause().equals(DamageCause.ENTITY_SWEEP_ATTACK)
				|| event.getCause().equals(DamageCause.PROJECTILE)) return;
		
		if(event.getFinalDamage() >= player.getHealth())
		{
			event.setCancelled(true);
			
			if(manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
			{
				manager.sendGameMessage(ChatColor.RED + player.getName() + ChatColor.GRAY +  " died." );

				resetTreasure(null, player);
				player.teleport(islandManager.findIsland("Red Island").get().getSpawnLoc());
				resetPlayer(player, null);

			}
			else
			{
				manager.sendGameMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY +  " died." );

				resetTreasure(null, player);
				player.teleport(islandManager.findIsland("Blue Island").get().getSpawnLoc());
				resetPlayer(player, null);

			}

			PlayerManager.toOceanPlayer(target).setOnDamageCheckCooldown(true);
		}
		
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!(event.getEntity().getShooter() instanceof Player)) return;
		if(!(event.getHitEntity() instanceof Player)) return;
		
		damager = (Player) event.getEntity().getShooter();
		target = (Player) event.getHitEntity();
		
		if(!manager.getPlayingPlayers().contains(damager.getUniqueId())
				|| !manager.getPlayingPlayers().contains(target.getUniqueId())) return;
	}
	
	public void resetTreasure(Player damager, Player target)
	{
		if(!target.getInventory().contains(manager.getRedTreasure()) && !target.getInventory().contains(manager.getBlueTreasure())) return;

		if(PlayerManager.toOceanPlayer(target).hasDroppedTreasure())
		{
			OceanPlayer oceanPlayer = PlayerManager.toOceanPlayer(target);
			//put treasure back to treasure chest	
			
			if(oceanPlayer == null)
				Bukkit.broadcastMessage("ocean player is null!");
				
			boolean isRedTreasure = oceanPlayer.getDroppedTreasure().equals(manager.getRedTreasure());
			
			Chest treasureChest;
			ItemStack treasure = oceanPlayer.getDroppedTreasure();
					
			treasureChest = isRedTreasure ? 
					manager.getRedTreasureChest() :
					manager.getBlueTreasureChest();
			
			treasureChest.getBlockInventory().setItem(13, treasure);
			
			target.sendMessage(ChatColor.RED + "You died, you've lost your treasure");
			manager.sendGameMessage(" ");
//			manager.sendGameMessage(isRedTreasure ?
//					treasureReplacedHeader + ChatColor.AQUA + "Red team's treasure is replaced!":
//					treasureReplacedHeader + ChatColor.AQUA + "Blue team's treasure is replaced!");
//			manager.sendGameMessage(" ");

			manager.sendGameMessages(isRedTreasure ?
					TreasureMessages.getReplacedMessage("Red team", true) :
					TreasureMessages.getReplacedMessage("Blue team", true));
			
			if(isRedTreasure)
			{
				manager.getTeamRed().setTreasureStolen(false);
			}
			else
			{
				manager.getTeamBlue().setTreasureStolen(false);
			}
			
			PlayerManager.toOceanPlayer(target).setHasDroppedTreasure(false);
			return;

		}
		
		if(!target.getInventory().contains(manager.getRedTreasure())
				&& !target.getInventory().contains(manager.getBlueTreasure())) return;
			
		if(damager == null)
		{
			//put treasure back to treasure chest	
			boolean isRedTreasure = target.getInventory().contains(manager.getRedTreasure());
			
			Chest treasureChest;
			ItemStack treasure;
			
			treasure = isRedTreasure ? 
					manager.getRedTreasure() :
					manager.getBlueTreasure();
					
			treasureChest = isRedTreasure ? 
					manager.getRedTreasureChest() :
					manager.getBlueTreasureChest();
			
			treasureChest.getBlockInventory().setItem(13, treasure);
			
			target.sendMessage(ChatColor.RED + "You died, you've lost your treasure");
//			manager.sendGameMessage(" ");
//			manager.sendGameMessage(isRedTreasure ?
//					treasureReplacedHeader + ChatColor.AQUA + "Red team's treasure is replaced!":
//					treasureReplacedHeader + ChatColor.AQUA + "Blue team's treasure is replaced!");
//			manager.sendGameMessage(" ");

			manager.sendGameMessages(isRedTreasure ?
					TreasureMessages.getReplacedMessage("Red team", true) :
					TreasureMessages.getReplacedMessage("Blue team", true));
			
			if(isRedTreasure)
			{
				manager.getTeamRed().setTreasureStolen(false);
			}
			else
			{
				manager.getTeamBlue().setTreasureStolen(false);
			}
			return;
		}
		
		//give killer the treasure
		damager.getInventory().addItem(target.getInventory().contains(manager.getRedTreasure()) ?
				manager.getRedTreasure() : manager.getBlueTreasure());
		
		if(damager.getInventory().contains(manager.getRedTreasure()))
		{
//			manager.sendGameMessage(" ");
//			manager.sendGameMessage(manager.getTeamRed().getPlayers().contains(damager.getUniqueId()) ?
//					ChatColor.RED + damager.getName() + ChatColor.GRAY + " now has Red team's treasure!" :
//				ChatColor.BLUE + damager.getName() + ChatColor.GRAY + " now has Red team's treasure!");
//			manager.sendGameMessage(" ");

			manager.sendGameMessages(TreasureMessages.getTransactedMessage("Red team", damager.getName()));
		}
		else
		{
//			manager.sendGameMessage(" ");
//			manager.sendGameMessage(manager.getTeamRed().getPlayers().contains(damager.getUniqueId()) ?
//					ChatColor.RED + damager.getName() + ChatColor.GRAY + " now has Blue team's treasure!" :
//				ChatColor.BLUE + damager.getName() + ChatColor.GRAY + " now has Blue team's treasure!");
//			manager.sendGameMessage(" ");

			manager.sendGameMessages(TreasureMessages.getTransactedMessage("Blue team", damager.getName()));
		}

		
	}
	
	public void killTarget(Player damager, Player target)
	{
		if(manager.getTeamRed().getPlayers().contains(target.getUniqueId()))
		{
			manager.sendGameMessage(ChatColor.RED + target.getName() + ChatColor.GRAY + " was killed by "
					+ ChatColor.BLUE + damager.getName());
		}
		else
		{
			manager.sendGameMessage(ChatColor.BLUE + target.getName() + ChatColor.GRAY +  " was killed by "
					+ ChatColor.RED + damager.getName());
		}
		damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, (float) 2, (float) 2);
		
		resetTreasure(damager, target);
		
		Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
			resetPlayer(target, damager);
		}, 2);
		

		//target.sendTitle(ChatColor.RED + "YOU DIED!", "You'll respawn at your team island", 5, 20 * 2, 20 * 1);
	}
	
	public void resetPlayer(Player player, Player killer)
	{
		PlayerManager.toOceanPlayer(player).killPlayer(killer);
		PlayerManager.toOceanPlayer(player).setOnDamageCheckCooldown(false);
	}
}
