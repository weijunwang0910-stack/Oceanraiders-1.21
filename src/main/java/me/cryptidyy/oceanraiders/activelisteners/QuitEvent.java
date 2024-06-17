package me.cryptidyy.oceanraiders.activelisteners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.cryptidyy.oceanraiders.player.OceanPlayer;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.inventory.ItemStack;

public class QuitEvent implements Listener {
	
	private GameManager manager;
	//private GameManager manager;
	
	public static List<UUID> quitPlayers = new ArrayList<>();
	
	public QuitEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if(!manager.getPlayingPlayers().contains(event.getPlayer().getUniqueId())) return;

		//reset player treasure
		resetTreasure(event.getPlayer());

		event.setQuitMessage("");
		quitPlayers.add(event.getPlayer().getUniqueId());
		//manager.getPlayingPlayers().remove(player.getUniqueId());
		manager.quit(player);
//
//		Optional<GameQuitter> quitter = manager.getQueuedQuitters()
//				.stream()
//				.filter(gameJoiner -> gameJoiner.containsPlayer(player.getUniqueId()))
//				.findFirst();
//
//		if(!quitter.isPresent()) return;
//		manager.quit(quitter.get());
		//manager.quit(event.getPlayer());

	}

	public void resetTreasure(Player target)
	{
		if(!target.getInventory().contains(manager.getRedTreasure())
				&& !target.getInventory().contains(manager.getBlueTreasure())) return;

		if(PlayerManager.toOceanPlayer(target).hasDroppedTreasure())
		{
			OceanPlayer oceanPlayer = PlayerManager.toOceanPlayer(target);
			//put treasure back to treasure chest

			if(oceanPlayer == null)
				Bukkit.broadcastMessage("ocean player is null!");

			boolean isRedTreasure = oceanPlayer.getDroppedTreasure().equals(manager.getRedTreasure());

			Chest treasureChest;
			ItemStack treasure = oceanPlayer.getDroppedTreasure();

			treasureChest = isRedTreasure ?
					manager.getRedTreasureChest() :
					manager.getBlueTreasureChest();

			treasureChest.getBlockInventory().setItem(13, treasure);

			manager.sendGameMessage(" ");

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

			PlayerManager.toOceanPlayer(target).setHasDroppedTreasure(false);
			return;

		}

		if(!target.getInventory().contains(manager.getRedTreasure())
				&& !target.getInventory().contains(manager.getBlueTreasure())) return;

		//put treasure back to treasure chest
		boolean isRedTreasure = target.getInventory().contains(manager.getRedTreasure());

		Chest treasureChest;
		ItemStack treasure;

		treasure = isRedTreasure ?
				manager.getRedTreasure() :
				manager.getBlueTreasure();

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
