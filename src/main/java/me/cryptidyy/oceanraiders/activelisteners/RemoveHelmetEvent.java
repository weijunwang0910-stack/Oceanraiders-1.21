package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class RemoveHelmetEvent implements Listener {

	private GameManager manager;
	
	public RemoveHelmetEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}
	
	@EventHandler
	public void onRemove(InventoryClickEvent event)
	{
		if(!manager.getPlayingPlayers().contains(event.getWhoClicked().getUniqueId())) return;
		if(!(event.getWhoClicked() instanceof Player)) return;
		if(!(event.getClickedInventory() instanceof PlayerInventory)) return;
		
		if(event.getSlot() != 39) return;
			
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		player.sendMessage(ChatColor.RED + "You cannot remove your helmet!");
		
	}
}
