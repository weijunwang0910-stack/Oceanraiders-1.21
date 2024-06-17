package me.cryptidyy.oceanraiders.lobbylisteners;

import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.coreapi.sql.GameJoinerSQL;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinEvent implements Listener {
	
	private GameManager manager;
	
	public JoinEvent(Main plugin)
	{
		this.manager = plugin.getGameManager();
	}
	private List<UUID> playersJoined = new ArrayList<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		playersJoined.add(player.getUniqueId());

		if(manager.getLobbyPlayers().contains(player.getUniqueId()))
		{
			System.out.println(ChatColor.RED + "Player " + player.getName() + " has already joined the game!");
			//return;
		}

		event.setJoinMessage("");

		if(!manager.getQueueListener().getQueuedPlayers().stream().map(GameJoiner::getUUID).anyMatch(player.getUniqueId()::equals))
		{
			//allow party members to join still
			System.out.println(ChatColor.RED + player.getName() + " joined without queue!");
		}

		GameJoiner gameJoiner = manager.getQueueListener().getQueuedPlayers().stream().filter(joiner -> joiner.getUUID().equals(player.getUniqueId()))
				.findFirst().get();

		gameJoiner.setPlayerConnected(true);

		if(gameJoiner.getParty() == null)
		{
			manager.join(gameJoiner);
			playersJoined.remove(gameJoiner.getUUID());
			return;
		}

		if(gameJoiner.getParty().getAllPlayers().stream().allMatch(member -> playersJoined.contains(member.getUniqueId())))
		{
			manager.join(gameJoiner);
			gameJoiner.getParty().getAllPlayers().stream().forEach(member -> {
				playersJoined.remove(member.getUniqueId());
			});
		}

		//player then gets removed from queue after bungee receives this message
	}
}
