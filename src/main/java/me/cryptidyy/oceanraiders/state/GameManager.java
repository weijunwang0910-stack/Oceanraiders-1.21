package me.cryptidyy.oceanraiders.state;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.customitems.OceanItemManager;
import me.cryptidyy.oceanraiders.gamemap.GameMap;
import me.cryptidyy.oceanraiders.gamemap.LocalGameMap;
import me.cryptidyy.oceanraiders.sql.QueueListener;
import me.cryptidyy.oceanraiders.tickers.GameLoop;
import me.cryptidyy.oceanraiders.npcs.GameNPCSetupManager;
import me.cryptidyy.oceanraiders.player.OceanTeam;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.loot.LootChestManager;
import me.cryptidyy.oceanraiders.npcs.NPCShopManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.scoreboard.HealthBar;
import me.cryptidyy.oceanraiders.scoreboard.ScoreboardManager;
import me.cryptidyy.oceanraiders.shop.EnchantManager;
import me.cryptidyy.oceanraiders.shop.ItemEntryManager;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;
import me.cryptidyy.oceanraiders.utility.PlayerRollbackManager;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class GameManager {
	private final Main plugin;
	private GameState state;
	private final int MAX_PLAYERS = 16;
	private final int MIN_PLAYERS = 2;

	private List<UUID> lobbyPlayers = new ArrayList<>();
	private List<UUID> playingPlayers = new ArrayList<>();
	private List<UUID> allPlayers = new ArrayList<>();

	private OceanTeam teamRed = new OceanTeam("Red Team");
	private OceanTeam teamBlue = new OceanTeam("Blue Team");

	private ScoreboardManager boardManager;
	private final HealthBar HEALTH_BAR = new HealthBar();
	private NPCShopManager shopManager = new NPCShopManager();
	private JScoreboardTeam targetTeam;

	private LootChestManager lootChestManager;
	private IslandManager islandManager;
	private OceanItemManager oceanItemManager;

	private Map<UUID, EnchantManager> enchantManagers = new HashMap<>();

	private boolean isStarted = false;

	private ArmorStand redTreasureDrop;
	private ArmorStand blueTreasureDrop;

	private Chest redTreasureChest;
	private Chest blueTreasureChest;

	private ItemStack redTreasure;
	private ItemStack blueTreasure;

	private World gameWorld;
	private GameLoop gameLoop;

	private ItemEntryManager itemEntryManager;

	private Location lobbyCornerOne;
	private Location lobbyCornerTwo;

	private QueueListener queueListener;

	public GameManager(Main plugin)
	{
		this.plugin = plugin;
		//this.gameWorld = Bukkit.getWorld("world");
		this.gameWorld = plugin.getCurrentMap().getWorld();

		this.redTreasure = new ItemBuilder(Material.HEART_OF_THE_SEA)
				.setName(ChatColor.RED + "Red Treasure")
				.addEnchant(Enchantment.DURABILITY, 1)
				.addItemFlags(ItemFlag.HIDE_ENCHANTS)
				.addLoreLine(ChatColor.AQUA + "The treasure that's desired by every Raider")
				.toItemStack();

		this.blueTreasure = new ItemBuilder(Material.HEART_OF_THE_SEA)
				.setName(ChatColor.BLUE + "Blue Treasure")
				.addEnchant(Enchantment.DURABILITY, 1)
				.addItemFlags(ItemFlag.HIDE_ENCHANTS)
				.addLoreLine(ChatColor.AQUA + "The treasure that's desired by every Raider")
				.toItemStack();

		this.lobbyCornerOne = new Location(this.gameWorld, -152, 111, -18);
		this.lobbyCornerTwo = new Location(this.gameWorld, -144, 106, -10);
	}

	public void onEnable()
	{
		this.queueListener = new QueueListener(plugin);
		queueListener.runTaskTimer(plugin, 0, 10);

		this.oceanItemManager = plugin.getItemManager();
		this.itemEntryManager = new ItemEntryManager(plugin);
		this.islandManager = plugin.getIslandManager();

		this.setState(new WaitingArenaState());
	}

	public void onDisable()
	{
		plugin.getCurrentMap().unload();
		this.queueListener.cancel();
	}

	public QueueListener getQueueListener() {
		return queueListener;
	}

	public void setState(GameState state)
	{
		if(this.state != null)
			this.state.onDisable();

		this.state = state;
		this.state.onEnable(plugin);

	}

	//Accepts solo players, or party members/leaders
	public void join(GameJoiner joiner)
	{
		//remove joiner from queue once it has joined
		joiner.removeFromQueue();

		Player leader = Bukkit.getPlayer(joiner.getUUID());

		//join before game
		if(allPlayers.contains(leader.getUniqueId()) || lobbyPlayers.contains(leader.getUniqueId()))
		{
			leader.sendMessage(org.bukkit.ChatColor.RED + "You are already in a game!");
			return;
		}

		//OceanTeam teamToJoin = teamBlue;
		OceanTeam teamToJoin = teamRed.getPlayers().size() <= teamBlue.getPlayers().size() ? teamRed : teamBlue;

		List<UUID> allPlayers = joiner.getAllPlayers();
		for(UUID uuid : allPlayers)
		{
			Player player = Bukkit.getPlayer(uuid);
			preparePlayer(player, teamToJoin);
			API.getInstance().alreadyQueuedPlayers.remove(uuid);
		}
	}

	private void preparePlayer(Player player, OceanTeam teamToJoin)
	{
		if(state instanceof WaitingArenaState || state instanceof StartCountdownState)
			lobbyPlayers.add(player.getUniqueId());

		//teleport player to lobby
		player.teleport(new Location(this.gameWorld, -147.5, 109, -13.5));

		teamToJoin.addPlayer(player);
		this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
				+ teamToJoin.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");
	}

	//Do not rejoin the entire party
	public void rejoin(GameJoiner joiner)
	{
		Player player = Bukkit.getPlayer(joiner.getUUID());

		if(!allPlayers.contains(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "There's no game for you to rejoin!");
			return;
		}

		//for(UUID memberID : joiner.getAllPlayers())
		//{
			Player member = player;
			//Add members back to their team
			if(PlayerManager.toOceanPlayer(member).getPlayerTeam().getTeamName().equals("Red Team"))
			{
				teamRed.getPlayers().add(member.getUniqueId());

				this.getBoardManager().addPlayerToTeamRed(member);
				this.sendGameMessage(ChatColor.RED + member.getName() + ChatColor.GRAY + " has rejoined.");
			}
			else
			{
				teamBlue.getPlayers().add(member.getUniqueId());

				this.getBoardManager().addPlayerToTeamBlue(member);
				this.sendGameMessage(ChatColor.BLUE + member.getName() + ChatColor.GRAY + " has rejoined.");
			}

			PlayerManager.toOceanPlayer(member).killPlayer(null);

			//Register player again
			try
			{
				GameNPCSetupManager.registerPlayer(member);
			}
			catch(Exception e)
			{

			}
			if(!playingPlayers.contains(member.getUniqueId()))
				playingPlayers.add(member.getUniqueId());

			//PlayerManager.toOceanPlayer(player).respawnPlayer();

			this.getBoardManager().addPlayer(member.getUniqueId());
			LootChestManager.allLootChests
					.stream()
					.filter(chest -> chest.getUUID().equals(member.getUniqueId()))
					.forEach(lootChest -> lootChest.displayHologram(member));
		//}
	}

	//Note: quitting does not affect all players in a party
	public void quit(Player quitter)
	{
		System.out.println(ChatColor.GOLD + quitter.getName() + " is quitting the game");
		Player player = quitter;
		if(isStarted)
		{
			Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has quit.");
			playingPlayers.remove(player.getUniqueId());
			GameNPCSetupManager.unRegisterPlayer(player);

			//remove player from their team
			this.getBoardManager().removePlayerFromAllTeams(player);

			if(teamRed.getPlayers().contains(player.getUniqueId()))
			{
				teamRed.removePlayer(player);
			}
			else
			{
				teamBlue.removePlayer(player);
			}

			lootChestManager.getAllLootChests()
					.stream()
					.filter(lootChest -> lootChest.getUUID().equals(player))
					.forEach(lootChest -> lootChest.stopRefresh());
		}
		else
		{
			//for(UUID uuid : getAllPartyPlayers(quitter))
			//{
				//Player player = Bukkit.getPlayer(uuid);
			lobbyPlayers.remove(player.getUniqueId());
				GameNPCSetupManager.unRegisterPlayer(player);

				if(teamRed.getPlayers().contains(player.getUniqueId()))
				{
					teamRed.removePlayer(player);
				}
				else
				{
					teamBlue.removePlayer(player);
				}

				Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has quit.");

				if(this.getBoardManager() == null) return;
				this.getBoardManager().removePlayer(player);
			//}
		}
	}

	private List<UUID> getAllPartyPlayers(Player player)
	{
		List<UUID> result = new ArrayList<>();
		PlayerParty playerParty = PartyManager.getInstance().getParty(PAFPlayerManager.getInstance().getPlayer(player.getUniqueId()));
		if(playerParty == null)
		{
			result.add(player.getUniqueId());
			return result;
		}

		playerParty.getAllPlayers().stream().forEach(member -> {
			result.add(member.getUniqueId());
		});

		return result;
	}

	public void joinPlayer(Player player)
	{
		if(allPlayers.size() >= MAX_PLAYERS)
		{
			player.sendMessage(org.bukkit.ChatColor.RED + "This game is full!");
			return;
		}

		if(!isStarted)
		{
			//join before game
			if(allPlayers.contains(player.getUniqueId()) || lobbyPlayers.contains(player.getUniqueId()))
			{
				player.sendMessage(org.bukkit.ChatColor.RED + "You are already in a game!");
				return;
			}

			lobbyPlayers.add(player.getUniqueId());
			//teleport player to lobby
			player.teleport(new Location(this.gameWorld, -147.5, 109, -13.5));

			if(teamRed.getPlayers().size() <= teamBlue.getPlayers().size())
			{
				teamRed.addPlayer(player);
				this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
						+ teamRed.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");
			}

			else
			{
				teamBlue.addPlayer(player);
				this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
						+ teamBlue.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");
			}
		}
		else
		{
			//join midgame
			if(allPlayers.contains(player.getUniqueId()) || lobbyPlayers.contains(player.getUniqueId()))
			{
				player.sendMessage(org.bukkit.ChatColor.RED + "You are already in a game!");
				return;
			}

			allPlayers.add(player.getUniqueId());
			playingPlayers.add(player.getUniqueId());

			if(teamRed.getPlayers().size() <= teamBlue.getPlayers().size())
			{
				teamRed.addPlayer(player);
				this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
						+ teamRed.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");

				PlayerManager.addPlayer(player, teamRed, this);
			}

			else
			{
				teamBlue.addPlayer(player);
				this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
						+ teamBlue.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");

				PlayerManager.addPlayer(player, teamBlue, this);
			}
		}
		PlayerRollbackManager.save(player);
	}

	public void rejoinPlayer(Player player)
	{
		//player rejoin midgame
		if(playingPlayers.contains(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "You are already in the game!");
			return;
		}

		if(!allPlayers.contains(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "There's no game for you to rejoin!");
			return;
		}


		//Add player back to their team
		if(PlayerManager.toOceanPlayer(player).getPlayerTeam().getTeamName().equals("Red Team"))
		{
			teamRed.getPlayers().add(player.getUniqueId());

			this.getBoardManager().addPlayerToTeamRed(player);
			this.sendGameMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has rejoined.");
		}
		else
		{
			teamBlue.getPlayers().add(player.getUniqueId());

			this.getBoardManager().addPlayerToTeamBlue(player);
			this.sendGameMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY + " has rejoined.");
		}

		PlayerManager.toOceanPlayer(player).killPlayer(null);

		//Register player again
		try
		{
			GameNPCSetupManager.registerPlayer(player);
		}
		catch(Exception e)
		{

		}
		playingPlayers.add(player.getUniqueId());

		//PlayerManager.toOceanPlayer(player).respawnPlayer();

		this.getBoardManager().addPlayer(player.getUniqueId());
		LootChestManager.allLootChests
				.stream()
				.filter(chest -> chest.getUUID().equals(player.getUniqueId()))
				.forEach(lootChest -> lootChest.displayHologram(player));
	}

	public void quitPlayer(Player player)
	{
		if(isStarted)
		{
			//player quit mid-game
			if(!playingPlayers.contains(player.getUniqueId()))
			{
				player.sendMessage(ChatColor.RED + "You are not in a game!");
				return;
			}

			Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has quit.");
			playingPlayers.remove(player.getUniqueId());
			GameNPCSetupManager.unRegisterPlayer(player);

			//remove player from their team
			this.getBoardManager().removePlayerFromAllTeams(player);

			if(teamRed.getPlayers().contains(player.getUniqueId()))
			{
				teamRed.removePlayer(player);
			}
			else
			{
				teamBlue.removePlayer(player);
			}

		}
		else
		{
			//player quit before/after game
			if(!lobbyPlayers.contains(player.getUniqueId()))
			{
				player.sendMessage(ChatColor.RED + "You are not in a game!");
				return;
			}

			lobbyPlayers.remove(player.getUniqueId());

			GameNPCSetupManager.unRegisterPlayer(player);

			if(teamRed.getPlayers().contains(player.getUniqueId()))
			{
				teamRed.removePlayer(player);
			}
			else
			{
				teamBlue.removePlayer(player);
			}

			Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " has quit.");
		}

		if(this.getBoardManager() == null) return;
		this.getBoardManager().removePlayer(player);
	}

	public boolean canGameStart()
	{
		//return lobbyPlayers.size() >= MIN_PLAYERS && teamRed.getPlayers().size() > 0 && teamBlue.getPlayers().size() > 0;
		return lobbyPlayers.size() >= MIN_PLAYERS;
	}

	public boolean canJoinServer()
	{
		return allPlayers.size() <= MAX_PLAYERS | isStarted;
	}

	public void sendGameMessage(String message)
	{
		for(UUID uuid : lobbyPlayers)
		{
			Bukkit.getPlayer(uuid).sendMessage(message);
		}

		for(UUID uuid : playingPlayers)
		{
			Bukkit.getPlayer(uuid).sendMessage(message);
		}
	}

	public void sendGameMessages(String[] messages)
	{
		for(UUID uuid : lobbyPlayers)
		{
			Bukkit.getPlayer(uuid).sendMessage(messages);
		}

		for(UUID uuid : playingPlayers)
		{
			Bukkit.getPlayer(uuid).sendMessage(messages);
		}
	}
	
	public GameState getGameState()
	{
		return this.state;
	}

	public List<UUID> getLobbyPlayers()
	{
		return this.lobbyPlayers;
	}

	public List<UUID> getPlayingPlayers()
	{
		return this.playingPlayers;
	}

	public List<UUID> getAllPlayers() {
		return allPlayers;
	}

	public boolean isStarted()
	{
		return this.isStarted;
	}

	public void setStarted(boolean isStarted)
	{
		this.isStarted = isStarted;
	}

	public HealthBar getHealthBar()
	{
		return this.HEALTH_BAR;
	}

	public NPCShopManager getShopManager()
	{
		return this.shopManager;
	}

	public EnchantManager getEnchantManager(Player player) {
		return enchantManagers.get(player.getUniqueId());
	}

	public Map<UUID, EnchantManager> getEnchantManagers() {
		return enchantManagers;
	}

	public ScoreboardManager getBoardManager()
	{
		return this.boardManager;
	}

	public void setBoardManager(ScoreboardManager manager)
	{
		this.boardManager = manager;
	}

	public OceanTeam getTeamRed()
	{
		return this.teamRed;
	}

	public OceanTeam getTeamBlue()
	{
		return this.teamBlue;
	}

	public JScoreboardTeam getTargetTeam()
	{
		return this.targetTeam;
	}

	public void setTargetTeam(JScoreboardTeam team)
	{
		this.targetTeam = team;
	}

	public LootChestManager getLootChestManager()
	{
		return this.lootChestManager;
	}

	public void setLootChestManager(LootChestManager manager)
	{
		this.lootChestManager = manager;
	}

	public ArmorStand getRedTreasureDrop() {
		return this.redTreasureDrop;
	}

	public ArmorStand getBlueTreasureDrop() {
		return blueTreasureDrop;
	}

	public void setRedTreasureDrop(ArmorStand stand) {
		this.redTreasureDrop = stand;
	}

	public void setBlueTreasureDrop(ArmorStand stand) {
		this.blueTreasureDrop = stand;
	}

	public IslandManager getIslandManager()
	{
		return this.islandManager;
	}

	public void setRedTreasureChest(Chest redChest) {
		this.redTreasureChest = redChest;
	}

	public void setBlueTreasureChest(Chest blueChest)
	{
		this.blueTreasureChest = blueChest;
	}

	public Chest getRedTreasureChest()
	{
		return this.redTreasureChest;
	}

	public Chest getBlueTreasureChest()
	{
		return this.blueTreasureChest;
	}

	public ItemStack getRedTreasure()
	{
		return this.redTreasure;
	}

	public ItemStack getBlueTreasure()
	{
		return this.blueTreasure;
	}

	public World getGameWorld()
	{
		return this.gameWorld;
	}

	public void setGameWorld()
	{
		this.gameWorld = gameWorld;
	}

	public GameLoop getGameLoop()
	{
		return this.gameLoop;
	}

	public void setGameLoop(GameLoop gameLoop)
	{
		this.gameLoop = gameLoop;
	}

	public OceanItemManager getOceanItemManager()
	{
		return this.oceanItemManager;
	}

	public void setOceanItemManager(OceanItemManager itemManager)
	{
		this.oceanItemManager = itemManager;
	}

	public ItemEntryManager getEntryManager()
	{
		return this.itemEntryManager;
	}

	public void setEntryManager(ItemEntryManager manager)
	{
		this.itemEntryManager = manager;
	}

	public Location[] getLobbyLocations()
	{
		return new Location[]{this.lobbyCornerOne, lobbyCornerTwo};
	}
}
