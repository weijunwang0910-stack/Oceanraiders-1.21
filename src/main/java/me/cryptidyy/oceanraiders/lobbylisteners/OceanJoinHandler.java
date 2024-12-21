package me.cryptidyy.oceanraiders.lobbylisteners;

import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.coreapi.api.JoinHandler;
import me.cryptidyy.oceanraiders.sql.OceanQueueListener;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OceanJoinHandler extends JoinHandler {
    private GameManager manager;
    public OceanJoinHandler(OceanQueueListener queueListener, Server server, GameManager manager)
    {
        super(queueListener, server);
        this.manager = manager;
    }

    @Override
    public List<UUID> getLobbyPlayers() {
        return manager.getLobbyPlayers();
    }

    @Override
    public void join(GameJoiner joiner) {

        Player player = Bukkit.getPlayer(joiner.getUUID());
        manager.getJoiners().add(joiner);

        //join before game
        if(manager.getAllPlayers().contains(player.getUniqueId()) || manager.getLobbyPlayers().contains(player.getUniqueId()))
        {
            player.sendMessage(org.bukkit.ChatColor.RED + "You are already in a game!");
            return;
        }

        List<UUID> allPlayers = joiner.getAllPlayers();
        manager.preparePlayer(player);
    }
}
