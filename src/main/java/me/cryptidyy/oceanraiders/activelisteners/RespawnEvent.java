package me.cryptidyy.oceanraiders.activelisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RespawnEvent implements Listener {

	private static List<UUID> respawningPlayers = new ArrayList<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if(!respawningPlayers.contains(event.getPlayer().getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(PlayerInteractEvent event)
	{
		if(!respawningPlayers.contains(event.getPlayer().getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(PlayerInteractEvent event)
	{
		if(!respawningPlayers.contains(event.getPlayer().getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event)
	{
		if(!(event.getDamager() instanceof Player)) return;
		if(!respawningPlayers.contains(event.getDamager().getUniqueId())) return;
		
		event.setCancelled(true);
	}
	
	public static void addPlayer(Player player)
	{
		respawningPlayers.add(player.getUniqueId());
	}
	
	public static void removePlayer(Player player)
	{
		respawningPlayers.remove(player.getUniqueId());
	}
	
	
}
