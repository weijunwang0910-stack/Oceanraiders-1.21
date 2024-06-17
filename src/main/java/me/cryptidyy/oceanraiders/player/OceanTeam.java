package me.cryptidyy.oceanraiders.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class OceanTeam {
	
	public OceanTeam(String teamName)
	{
		this.teamName = teamName;
		leatherArmorColor = teamName.contains("Red") ? Color.RED : Color.BLUE;
	}

	private List<UUID> players = new ArrayList<>();
	private String teamName;
	private boolean treasureStolen;
	private Color leatherArmorColor;

	public void addPlayer(Player player)
	{
		players.add(player.getUniqueId());
	}
	
	public void removePlayer(Player player)
	{
		players.remove(player.getUniqueId());
	}

	public void sendTeamMessage(String message)
	{
		for(UUID uuid : players)
		{
			Bukkit.getPlayer(uuid).sendMessage(message);
		}
	}
	
	public String getTeamName() {
		return teamName;
	}

	public List<UUID> getPlayers()
	{
		return players;
	}

	public List<UUID> getOnlinePlayers()
	{
		List<UUID> onlinePlayers = new ArrayList<>();

		for(UUID id : players)
		{
			if(Bukkit.getPlayer(id) != null) onlinePlayers.add(id);
		}

		return onlinePlayers;
	}

	public boolean isTreasureStolen() {
		return treasureStolen;
	}

	public void setTreasureStolen(boolean treasureStolen) {
		this.treasureStolen = treasureStolen;
	}

	public Color getLeatherArmorColor() {
		return leatherArmorColor;
	}
}
