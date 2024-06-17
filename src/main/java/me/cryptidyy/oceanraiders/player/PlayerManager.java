package me.cryptidyy.oceanraiders.player;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerManager {

	private static List<OceanPlayer> oceanPlayers = new ArrayList<>();
	private GameManager manager;
	
	public PlayerManager(GameManager manager)
	{	
		this.manager = manager;
	}
	
	public void setupPlayers()
	{
		manager.getPlayingPlayers().forEach(uuid ->
		{
			Player player = Bukkit.getPlayer(uuid);
			
			if(manager.getTeamRed().getPlayers().contains(uuid))
				oceanPlayers.add(new OceanPlayer(player, manager.getTeamRed()));
			else
				oceanPlayers.add(new OceanPlayer(player, manager.getTeamBlue()));
		});
	}
	
	public static OceanPlayer toOceanPlayer(Player player)
	{
		return oceanPlayers
				.stream()
				.filter(oceanPlayer -> oceanPlayer.getPlayerUUID().equals(player.getUniqueId()))
				.findFirst()
				.orElse(null);
	}
	
	public static void addPlayer(Player player, OceanTeam playerTeam, GameManager manager)
	{
		oceanPlayers.add(new OceanPlayer(player, playerTeam));
		toOceanPlayer(player).initPlayer(manager);
	}

	public static void clearPlayers()
	{
		oceanPlayers.clear();
	}
}
