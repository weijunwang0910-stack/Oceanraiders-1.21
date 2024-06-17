package me.cryptidyy.oceanraiders.state;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.events.*;
import me.cryptidyy.oceanraiders.sql.QueueListener;
import me.cryptidyy.oceanraiders.sql.ServerSQL;
import me.cryptidyy.oceanraiders.tickers.GameLoop;
import me.cryptidyy.oceanraiders.npcs.GameNPCSetupManager;
import me.cryptidyy.oceanraiders.islands.Island;
import me.cryptidyy.oceanraiders.loot.LootChestManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.scoreboard.ScoreboardManager;
import me.cryptidyy.oceanraiders.shop.EnchantManager;
import me.cryptidyy.oceanraiders.utility.PluginMessenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ActiveArenaState extends GameState {

    private Main plugin;
    private QueueListener queueListener = null;

    public void onEnable(Main plugin)
    {
        //TODO: Schedule task timer to fetch data from QueueListener and allow rejoining

        this.plugin = plugin;
        GameManager manager = plugin.getGameManager();

        manager.getLobbyPlayers().forEach(uuid -> {
            manager.getAllPlayers().add(uuid);
            manager.getPlayingPlayers().add(uuid);
        });

        manager.getLobbyPlayers().clear();

        GameNPCSetupManager npcSetupManager = new GameNPCSetupManager(plugin);

        manager.setBoardManager(new ScoreboardManager(plugin, manager.getPlayingPlayers()));
        manager.getBoardManager().show();
        manager.getBoardManager().makeTeams(manager.getTeamRed().getPlayers(), manager.getTeamBlue().getPlayers());

        manager.setTargetTeam(manager.getBoardManager().getScoreboard().createTeam("Target", "", ChatColor.GREEN));

        manager.getPlayingPlayers().forEach(uuid -> {
            manager.getEnchantManagers().put(uuid, new EnchantManager(plugin));
        });

        PlayerManager playerManager = new PlayerManager(manager);
        playerManager.setupPlayers();

        //setup player / teleport them
        manager.getPlayingPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            PlayerManager.toOceanPlayer(player).initPlayer(manager);
        });

        //remove lobby
        removeLobby(manager);

        //LootChestManager is per-player, but there's only one instance of LootChestManager in GameManager
        manager.setLootChestManager(new LootChestManager(plugin));

        initRedIsland(manager);
        initBlueIsland(manager);

        npcSetupManager.setNPCs();

        GameLoop gameLoop = new GameLoop(plugin);
        manager.setGameLoop(gameLoop);
        gameLoop.runTaskTimer(plugin, 0, 5);

        //start loot chest refresh
        manager.getLootChestManager().getAllLootChests().stream().forEach(lootChest -> {
            lootChest.setupRefreshHologram();
        });

        manager.setStarted(true);

        //Setup task timer to listen for rejoin queue
        startRejoinCheck(manager);

        //PluginMessenger.sendGameStartedStateUpdateMessage(true);
        super.onEnable(plugin);


        //ServerSQL.updateGameState("ActiveArenaState");
    }

    private void startRejoinCheck(GameManager manager)
    {
        queueListener = manager.getQueueListener();
        queueListener.setListening(true);
    }
    private void initRedIsland(GameManager manager)
    {
        Island redIsland = manager.getIslandManager().findIsland("Red Island").get();

        Location dropLoc = redIsland.getDropLoc();

        ArmorStand redTreasureDrop = (ArmorStand) dropLoc.getWorld().spawnEntity(dropLoc, EntityType.ARMOR_STAND);
        redTreasureDrop.setInvulnerable(true);
        redTreasureDrop.setVisible(false);
        redTreasureDrop.setCustomName(ChatColor.AQUA + "" + ChatColor.BOLD + "DROP TREASURE HERE!");
        redTreasureDrop.setCustomNameVisible(true);

        manager.setRedTreasureDrop(redTreasureDrop);

        Chest redChest = (Chest) redIsland.getChestLoc().getBlock().getState();
        redChest.getBlockInventory().setItem(13, manager.getRedTreasure());

        manager.setRedTreasureChest(redChest);
    }


    private void initBlueIsland(GameManager manager)
    {
        Location dropLoc = manager.getIslandManager().findIsland("Blue Island").get().getDropLoc();
        ArmorStand blueTreasureDrop = (ArmorStand) dropLoc.getWorld().spawnEntity(dropLoc, EntityType.ARMOR_STAND);
        blueTreasureDrop.setInvulnerable(true);
        blueTreasureDrop.setVisible(false);
        blueTreasureDrop.setCustomName(ChatColor.AQUA + "" + ChatColor.BOLD + "DROP TREASURE HERE!");
        blueTreasureDrop.setCustomNameVisible(true);

        manager.setBlueTreasureDrop(blueTreasureDrop);

        Chest blueChest = (Chest) manager.getIslandManager().findIsland("Blue Island").get().getChestLoc().getBlock().getState();
        blueChest.getBlockInventory().setItem(13, manager.getBlueTreasure());

        manager.setBlueTreasureChest(blueChest);
    }

    @Override
    public StateListenerProvider getStateListenerProvider() {
        return new ActiveStateListenerProvider();
    }

    public void removeLobby(GameManager manager)
    {
        Location[] lobbyLocs = manager.getLobbyLocations();
        double[] dimX = new double[2];
        double[] dimY = new double[2];
        double[] dimZ = new double[2];

        dimX[0] = lobbyLocs[0].getX();
        dimX[1] = lobbyLocs[1].getX();
        Arrays.sort(dimX);

        dimY[0] = lobbyLocs[0].getY();
        dimY[1] = lobbyLocs[1].getY();
        Arrays.sort(dimY);

        dimZ[0] = lobbyLocs[0].getZ();
        dimZ[1] = lobbyLocs[1].getZ();
        Arrays.sort(dimZ);

        for(double x = dimX[0]; x < dimX[1]; x++)
            for(double y = dimY[0]; y < dimY[1]; y++)
                for(double z = dimZ[0]; z < dimZ[1]; z++)
                {
                    new Location(manager.getGameWorld(), x, y, z).getBlock().setType(Material.AIR);
                }
    }
}
