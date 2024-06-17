package me.cryptidyy.oceanraiders.lobbylisteners;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyQuitEvent implements Listener {

	private GameManager manager;

	public LobbyQuitEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		event.setQuitMessage("");

//		Optional<GameQuitter> quitter = manager.getQueuedQuitters()
//				.stream()
//				.filter(gameQuitter -> gameQuitter.containsPlayer(player.getUniqueId()))
//				.findFirst();
//
//		if(!quitter.isPresent()) return;
		manager.quit(player);

	}

}
