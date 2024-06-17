package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.state.ActiveArenaState;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.OceanPlayer;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;

public class TreasureDropEvent implements Listener {
	
	private Main plugin;
	
	private GameManager manager;
	
	public TreasureDropEvent(Main plugin)
	{
		this.plugin = plugin;
		this.manager = this.plugin.getGameManager();
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		
		if(!manager.getPlayingPlayers().contains(player.getUniqueId())) return;
		
		if(!event.getItemDrop().getItemStack().equals(manager.getBlueTreasure())
				&& !event.getItemDrop().getItemStack().equals(manager.getRedTreasure())) return;
		
		OceanPlayer oceanPlayer = PlayerManager.toOceanPlayer(player);
		oceanPlayer.setHasDroppedTreasure(true);
		
		new BukkitRunnable()
		{
			@Override
			public void run() 
			{
				if(!(manager.getGameState() instanceof ActiveArenaState))
				{
					this.cancel();
					return;
				}
				if(!manager.getGameWorld().getEntities().contains(event.getItemDrop()))
				{
					oceanPlayer.setHasDroppedTreasure(false);
					this.cancel();
					return;
				}

				event.getItemDrop().remove();

				if(player.getInventory().firstEmpty() == -1)
				{
					resetTreasure(event.getItemDrop().getItemStack());
					player.sendMessage(ChatUtil.format("&cYou dropped the treasure at an invalid spot, " +
							"and you didn't have space in your inventory."));
					this.cancel();
					return;
				}

				player.getInventory().addItem(event.getItemDrop().getItemStack().equals(manager.getRedTreasure()) ?
						manager.getRedTreasure() : manager.getBlueTreasure());

				player.sendMessage(ChatColor.RED + "You dropped the treasure at an invalid spot!");
				oceanPlayer.setHasDroppedTreasure(false);
				oceanPlayer.setDroppedTreasure(event.getItemDrop().getItemStack().equals(manager.getRedTreasure()) ?
						manager.getRedTreasure() : manager.getBlueTreasure());

				this.cancel();
				return;

			}
			
		}.runTaskLater(plugin, 20 * 3);
	}

	public void resetTreasure(ItemStack treasure)
	{
		if(!treasure.equals(manager.getRedTreasure())
				&& !treasure.equals(manager.getBlueTreasure())) return;

		//put treasure back to treasure chest
		boolean isRedTreasure = treasure.equals(manager.getRedTreasure());

		Chest treasureChest;

		treasureChest = isRedTreasure ?
				manager.getRedTreasureChest() :
				manager.getBlueTreasureChest();

		treasureChest.getBlockInventory().setItem(13, treasure);

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

}
