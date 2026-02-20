package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.coreapi.api.QueueListener;
import me.cryptidyy.coreapi.api.RejoinHandler;
import me.cryptidyy.oceanraiders.loot.LootChestManager;
import me.cryptidyy.oceanraiders.npcs.GameNPCSetupManager;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OceanRejoinHandler extends RejoinHandler {

    private GameManager manager;
    public OceanRejoinHandler(QueueListener queueListener, Server server, GameManager manager) {
        super(queueListener, server);
        this.manager = manager;
    }

    @Override
    public List<UUID> getAllPlayers() {
        return manager.getAllPlayers();
    }

    @Override
    public void rejoin(GameJoiner joiner)
    {
        Player player = Bukkit.getPlayer(joiner.getUUID());

        if(!getAllPlayers().contains(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You weren't a part of this game!");
            return;
        }

        Player member = player;
        if(PlayerManager.toOceanPlayer(member).getPlayerTeam().getTeamName().equals("Red Team"))
        {
            manager.getTeamRed().getPlayers().add(member.getUniqueId());

            manager.getBoardManager().addPlayerToTeamRed(member);
            manager.sendGameMessage(ChatColor.RED + member.getName() + ChatColor.GRAY + " has rejoined.");
            player.sendMessage(ChatColor.RED + member.getName() + ChatColor.GRAY + " has rejoined.");
        }
        else
        {
            manager.getTeamBlue().getPlayers().add(member.getUniqueId());

            manager.getBoardManager().addPlayerToTeamBlue(member);
            manager.sendGameMessage(ChatColor.BLUE + member.getName() + ChatColor.GRAY + " has rejoined.");
            player.sendMessage(ChatColor.BLUE + member.getName() + ChatColor.GRAY + " has rejoined.");
        }

        PlayerManager.toOceanPlayer(member).killPlayer(null,10);

        if(!manager.getPlayingPlayers().contains(member.getUniqueId()))
            manager.getPlayingPlayers().add(member.getUniqueId());

        //PlayerManager.toOceanPlayer(player).respawnPlayer();

        manager.getBoardManager().addPlayer(member);
        LootChestManager.allLootChests
                .stream()
                .filter(chest -> chest.getUUID().equals(member.getUniqueId()))
                .forEach(lootChest -> lootChest.displayHologram(member));
        //}
    }
}
