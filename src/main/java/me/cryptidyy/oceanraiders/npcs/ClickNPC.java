package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ClickNPC implements Listener {
	
	NPCShopManager manager;
	
	public ClickNPC(NPCShopManager manager)
	{
		this.manager = manager;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity villager = event.getRightClicked(); //entity type doesn't matter
		
		if(villager.getCustomName().contains("BLACKSMITH"))
		{
			event.setCancelled(true);
			player.openInventory(manager.getBlacksmithShop(player).getArmorShop());
		}
		else if(villager.getCustomName().contains("LIBRARIAN"))
		{
			event.setCancelled(true);
			player.openInventory(manager.getEnchantShop(player).getEnchantInv());
		}
		else if(villager.getCustomName().contains("FARMER"))
		{
			event.setCancelled(true);
			player.openInventory(manager.getFoodShop(player).getFoodShop());
		}
		else if(villager.getCustomName().contains("POTION"))
		{
			event.setCancelled(true);
			player.openInventory(manager.getPotionShop(player).getDrinkingShop());
		}
		
	}
}
