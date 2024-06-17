package me.cryptidyy.oceanraiders.activelisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
		Player attacker = (Player) event.getDamager();
		
		
		if(PlayerManager.toOceanPlayer(attacker).isSpawnProtected())
		{
			OceanPlayer player = PlayerManager.toOceanPlayer(attacker);
			player.setSpawnProtected(false);
			attacker.sendMessage(ChatColor.RED + "You attacked someone! You are no longer spawn protected!");
		}
	}
}
