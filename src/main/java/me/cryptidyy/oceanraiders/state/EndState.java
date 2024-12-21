package me.cryptidyy.oceanraiders.state;

import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.events.StateListenerProvider;
import me.cryptidyy.oceanraiders.npcs.GameNPCSetupManager;
import me.cryptidyy.oceanraiders.loot.LootChestManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.shop.ItemEntryManager;
import me.cryptidyy.oceanraiders.sql.ServerSQL;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import me.cryptidyy.oceanraiders.utility.PluginMessenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndState extends GameState {

    private GameManager manager;

    @Override
    public void onEnable(Main plugin)
    {
        super.onEnable(plugin);
        //Bukkit.broadcastMessage(ChatUtil.format("&cGame over!"));

        manager = plugin.getGameManager();
        manager.setStarted(false);

        //reset players/game data
        manager.getPlayingPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);

            if(player != null)
                PlayerManager.toOceanPlayer(player).resetPlayer(manager);
        });

        manager.getBoardManager().hide();

        GameNPCSetupManager npcSetupManager = new GameNPCSetupManager(plugin);
        npcSetupManager.unSetNPCs();

        manager.getLootChestManager().getAllLootChests().forEach(lootChest -> {
            lootChest.hideHologram();
        });

        LootChestManager.allLootChests.clear();
        manager.getShopManager().resetAllShop();
        manager.setEntryManager(new ItemEntryManager(plugin));

        manager.getGameLoop().cancelPodiumParticles();
        manager.getGameLoop().cancel();

        manager.getGameWorld().getEntities().forEach(entity -> {
            if(entity instanceof Player) return;
            entity.remove();
        });

        //reset islands
        manager.getRedTreasureDrop().remove();
        manager.getRedTreasureChest().getBlockInventory().clear();

        manager.getBlueTreasureDrop().remove();
        manager.getRedTreasureChest().getBlockInventory().clear();

        manager.getPlayingPlayers().forEach(uuid -> {
            manager.getLobbyPlayers().add(uuid);
        });

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            manager.getPlayingPlayers().forEach(uuid -> {

                Player player = Bukkit.getPlayer(uuid);
                if(player != null)
                    //teleport player to another game
                    queueNextGame(player);
            });

            manager.getPlayingPlayers().clear();
            manager.getAllPlayers().clear();

            PlayerManager.clearPlayers();
            //plugin.getCurrentMap().unload();
        }, 5 * 20);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            //shutdown server
            Bukkit.shutdown();
        }, 15 * 20);

    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    @Override
    public StateListenerProvider getStateListenerProvider() {
        return null;
    }

    public void queueNextGame(Player player)
    {
        API.getInstance().queueGame(player.getUniqueId(), "oceanraiders");
    }
}
