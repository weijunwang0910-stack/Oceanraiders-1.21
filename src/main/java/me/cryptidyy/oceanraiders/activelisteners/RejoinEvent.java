package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.cryptidyy.oceanraiders.Main;

public class RejoinEvent implements Listener {

	private GameManager manager;

	public RejoinEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;
		//if(!QuitEvent.quitPlayers.contains(event.getPlayer().getUniqueId())) return;
		event.setJoinMessage("");

//		Optional<GameJoiner> joiner = manager.getQueuedJoiners()
//				.stream()
//				.filter(gameJoiner -> gameJoiner.containsPlayer(player.getUniqueId()))
//				.findFirst();
//
//		if(!joiner.isPresent()) return;
//		manager.rejoin(joiner.get());
		GameJoiner joiner =
				manager.getQueueListener().getQueuedPlayers()
						.stream()
						.filter(queued -> queued.getUUID().equals(player.getUniqueId()))
						.findFirst()
						.get();

		if(joiner == null)
		{
			player.sendMessage("You joined without a queue!");
			return;
		}

		manager.rejoin(joiner);
		joiner.setPlayerConnected(true);
	}
}
