package me.cryptidyy.oceanraiders.activelisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.cryptidyy.oceanraiders.player.OceanPlayer;
import me.cryptidyy.oceanraiders.player.PlayerManager;

public class PlayerAttackEvent implements Listener {

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event)
	{
		if(!(event.getDamager() instanceof Player)) return;
		if(!(event.getEntity() instanceof Player)) return;
		OceanPlayer attacker = PlayerManager.toOceanPlayer((Player) event.getDamager());

		if(attacker.isSpawnProtected())
		{
			attacker.setSpawnProtected(false);
			attacker.getPlayer().sendMessage(ChatColor.RED + "You attacked someone! You are no longer spawn protected!");
		}

		OceanPlayer damaged = PlayerManager.toOceanPlayer((Player) event.getEntity());

		if(damaged.isSpawnProtected())
		{
			event.setCancelled(true);
		}

		if(attacker.getPlayerTeam().equals(damaged.getPlayerTeam())) event.setCancelled(true);
	}

	@EventHandler
	public void onShot(EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getDamager() instanceof Projectile)) return;
		if(((Projectile) event.getDamager()).getShooter() instanceof Player) return;

		Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
		OceanPlayer attacker = PlayerManager.toOceanPlayer(shooter);
		OceanPlayer damaged = PlayerManager.toOceanPlayer((Player) event.getEntity());

		if(attacker.getPlayerTeam().equals(damaged.getPlayerTeam())) event.setCancelled(true);
	}
}
