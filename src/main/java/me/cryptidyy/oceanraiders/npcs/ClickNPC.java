package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_15_R1.Entity;

public class ClickNPC implements Listener {
	
	NPCShopManager manager;
	
	public ClickNPC(NPCShopManager manager)
	{
		this.manager = manager;
	}
	
	@EventHandler
	public void onClick(OpenShopEvent event)
	{
		Player player = event.getPlayer();
		
		Entity villager = event.getNpc();
		
		if(villager.getCustomName().getString().contains("BLACKSMITH"))
		{
			player.openInventory(manager.getBlacksmithShop(player).getArmorShop());
		}
		else if(villager.getCustomName().getString().contains("LIBRARIAN"))
		{
			player.openInventory(manager.getEnchantShop(player).getEnchantInv());
		}
		else if(villager.getCustomName().getString().contains("FARMER"))
		{
			player.openInventory(manager.getFoodShop(player).getFoodShop());
		}
		else if(villager.getCustomName().getString().contains("POTION"))
		{
			player.openInventory(manager.getPotionShop(player).getDrinkingShop());
		}
		
	}
}
