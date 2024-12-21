package me.cryptidyy.oceanraiders.state;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.activelisteners.OceanRejoinHandler;
import me.cryptidyy.oceanraiders.customitems.OceanItemManager;
import me.cryptidyy.oceanraiders.lobbylisteners.OceanJoinHandler;
import me.cryptidyy.oceanraiders.sql.OceanQueueListener;
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
	private List<GameJoiner> gameJoiners = new ArrayList<>();

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
	private OceanQueueListener queueListener;
	private OceanJoinHandler joinHandler;
	private OceanRejoinHandler rejoinHandler;

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
	}

	public void onEnable()
	{
		this.queueListener = new OceanQueueListener();
		queueListener.runTaskTimer(plugin, 0, 10);

		this.joinHandler = new OceanJoinHandler(queueListener, plugin.getServer(), this);
		this.rejoinHandler = new OceanRejoinHandler(queueListener, plugin.getServer(), this);
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

	public OceanQueueListener getQueueListener() {
		return queueListener;
	}

	public void setState(GameState state)
	{
		if(this.state != null)
			this.state.onDisable();

		this.state = state;
		this.state.onEnable(plugin);

	}

	public void preparePlayer(Player player)
	{
		if(state instanceof WaitingArenaState || state instanceof StartCountdownState)
			lobbyPlayers.add(player.getUniqueId());

		//teleport player to lobby
		player.teleport(new Location(this.gameWorld, -147.5, 109, -13.5));
		player.setHealth(20);
		player.setFoodLevel(20);
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.getScoreboard().getTeams().forEach(team -> team.unregister());

//		teamToJoin.addPlayer(player);
//		this.sendGameMessage(org.bukkit.ChatColor.AQUA + player.getName() + org.bukkit.ChatColor.GRAY + " has joined "
//				+ teamToJoin.getTeamName() + " (" + lobbyPlayers.size() + "/" + MAX_PLAYERS + ")");
	}

	public void quit(Player quitter)
	{
		Player player = quitter;
		Optional<GameJoiner> toQuit = gameJoiners.stream().filter(joiner -> joiner.getUUID().equals(quitter.getUniqueId())).findFirst();
		if(toQuit.isPresent())
		{
			gameJoiners.remove(toQuit.get());
		}

		if(isStarted)
		{
			Bukkit.broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has quit.");
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

				Bukkit.broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has quit.");

				if(this.getBoardManager() == null) return;
				this.getBoardManager().removePlayer(player);
			//}
		}
	}

	public void assignOptimalTeam() {
		List<GameJoiner> gameJoinerLeaders = new ArrayList<>();
		for(GameJoiner joiner : gameJoiners)
		{
			PAFPlayer pPlayer = PAFPlayerManager.getInstance().getPlayer(joiner.getUUID());
			if(joiner.getParty() == null)
			{
				gameJoinerLeaders.add(joiner);
			}
			else if(joiner.getParty().isLeader(pPlayer))
			{
				gameJoinerLeaders.add(joiner);
			}
		}

		int n = gameJoinerLeaders.size();
		if (n == 0)
		{
			return;
		}

		int totalSum = 0;
		for (GameJoiner joiner : gameJoinerLeaders)
		{
			totalSum += joiner.getAllPlayers().size();
		}

		boolean[][] dp = new boolean[n + 1][totalSum / 2 + 1];
		dp[0][0] = true;

		for (int i = 1; i <= n; i++)
		{
			int num = gameJoinerLeaders.get(i - 1).getAllPlayers().size();
			for (int j = 0; j <= totalSum / 2; j++)
			{
				dp[i][j] = dp[i - 1][j];
				if (j >= num)
				{
					dp[i][j] = dp[i][j] || dp[i - 1][j - num];
				}
			}
		}

		int bestSum = 0;
		for (int j = totalSum / 2; j >= 0; j--)
		{
			if (dp[n][j]) {
				bestSum = j;
				break;
			}
		}

		//Backtrack
		List<UUID> result = new ArrayList<>();
		int w = bestSum;
		for (int i = n; i > 0; i--)
		{
			if (w == 0) break;
			if (!dp[i - 1][w])
			{
				gameJoinerLeaders.get(i - 1).getAllPlayers().forEach(uuid -> result.add(uuid));
				w -= gameJoinerLeaders.get(i - 1).getAllPlayers().size();
			}
		}

		for(GameJoiner joiner : gameJoinerLeaders)
		{
			if(result.contains(joiner.getUUID()))
			{
				for(UUID uuid : joiner.getAllPlayers())
				{
					teamRed.addPlayer(Bukkit.getPlayer(uuid));
				}
			}
			else
			{
				for(UUID uuid : joiner.getAllPlayers())
				{
					teamBlue.addPlayer(Bukkit.getPlayer(uuid));
				}
			}
		}
	}

	public boolean canGameStart()
	{
		if(gameJoiners.size() == 0) return false;
		if(lobbyPlayers.size() < MIN_PLAYERS) return false;

		if(gameJoiners.size() >= 2)
		{
			for(int i = 0; i < gameJoiners.size() - 1; i++)
			{
				if(!gameJoiners.get(i).getParty().getLeader().getUniqueId().equals(gameJoiners.get(i + 1).getParty().getLeader().getUniqueId()))
				{
					return true;
				}
			}
		}
		return false;
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

	public List<GameJoiner> getJoiners()
	{
		return this.gameJoiners;
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
		return new Location[] {this.getIslandManager().getIslands().get(0).getWaitCornerOne(),
				this.getIslandManager().getIslands().get(0).getWaitCornerTwo()};
	}

	public OceanJoinHandler getJoinHandler()
	{
		return this.joinHandler;
	}

	public OceanRejoinHandler getRejoinHandler()
	{
		return this.rejoinHandler;
	}
}
