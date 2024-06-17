package me.cryptidyy.oceanraiders.activelisteners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class ItemBurnEvent implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) 
	{
		if(event.getEntity().getType() != EntityType.DROPPED_ITEM) return;
		
		Item droppedItem = (Item) event.getEntity();
		
		if(droppedItem.getItemStack().getType() == Material.HEART_OF_THE_SEA)
			event.setCancelled(true);
	}
}
