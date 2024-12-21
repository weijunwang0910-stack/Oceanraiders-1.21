package me.cryptidyy.oceanraiders.serverlisteners;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

	private Main plugin;

	public InteractEvent(Main plugin)
	{
		this.plugin = plugin;
	}

	//prevent players from using blocks
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if(!plugin.getGameManager().isStarted()) return;

		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND)
		{
			event.setCancelled(true);
		}
		
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(!event.getClickedBlock().getType().isInteractable()) return;
		if(event.getClickedBlock().getType() == Material.CHEST) return;
		if(event.getClickedBlock().getType() == Material.BARREL) return;
		if(event.getClickedBlock().getType() == Material.WATER) return;
		
		for(Material type : containsName("_DOOR"))
		{
			if(event.getClickedBlock().getType() == type) return;
		}
		event.setCancelled(true);
	}
	
	
	public List<Material> containsName(String itemTypeName)
	{
		List<Material> materials = new ArrayList<>();
		for(Material material : Material.values())
		{
			if(material.name().contains(itemTypeName))
			{
				materials.add(material);
			}
		}
		return materials;
	}
}
