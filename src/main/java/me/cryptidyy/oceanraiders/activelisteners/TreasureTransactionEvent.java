package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;

public class TreasureTransactionEvent implements Listener {

	private Player treasureDropper;
	
	private ItemStack treasure = null;
	
	private String treasureSwapHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE TRANSACTED > ";
	private GameManager manager;
	
	public TreasureTransactionEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		
		treasureDropper = event.getPlayer();
		if(!manager.getPlayingPlayers().contains(treasureDropper.getUniqueId())) return;
		
		if(event.getItemDrop().getItemStack().equals(manager.getRedTreasure())
				|| event.getItemDrop().getItemStack().equals(manager.getBlueTreasure()))
		{
			//Player dropped the treasure
			treasure = event.getItemDrop().getItemStack();
		}
	}
	
	@EventHandler
	public void onPickup(EntityPickupItemEvent event)
	{
		if(!(event.getEntity() instanceof Player)) return;
		
		
		Player player = (Player) event.getEntity();
		
		if(player.equals(treasureDropper)) return;
		
		if(!manager.getPlayingPlayers().contains(player.getUniqueId())) return;
		
		if(!event.getItem().getItemStack().equals(treasure)) return;
		
		//Another player picked up the treasure
		
		if(treasure.equals(manager.getRedTreasure()))
		{
//			manager.sendGameMessage(treasureSwapHeader + (manager.getTeamRed().getPlayers().contains(player.getUniqueId()) ?
//					ChatColor.RED + player.getName() + ChatColor.WHITE + " now has Red Team's treasure!"
//					: ChatColor.BLUE + player.getName() + ChatColor.WHITE + " now has Red Team's treasure!"));

			manager.sendGameMessages(TreasureMessages.getTransactedMessage("Red team", player.getName()));
		}
		else
		{
//			manager.sendGameMessage(treasureSwapHeader + (manager.getTeamRed().getPlayers().contains(player.getUniqueId()) ?
//					ChatColor.RED + player.getName() + ChatColor.WHITE + " now has Blue Team's treasure!"
//					: ChatColor.BLUE + player.getName() + ChatColor.WHITE + " now has Blue Team's treasure!"));

			manager.sendGameMessages(TreasureMessages.getTransactedMessage("Blue team", player.getName()));
		}
	}

}
