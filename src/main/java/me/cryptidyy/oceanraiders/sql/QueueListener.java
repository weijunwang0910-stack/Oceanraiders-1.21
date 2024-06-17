package me.cryptidyy.oceanraiders.sql;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.coreapi.sql.GameJoinerSQL;
import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//ran during waiting arena state for first join, and active state for rejoin
public class QueueListener extends BukkitRunnable {

    private Main plugin;
    private boolean isListening = false;

    public QueueListener(Main plugin)
    {
        this.plugin = plugin;
    }

    private List<GameJoiner> queuedPlayers = new ArrayList<>();

    @Override
    public void run()
    {
        if(!isListening) return;
        //check players from queue whether they can join, and update the according tables
        GameJoiner joiner = API.getInstance().checkQueue();
        if(joiner == null) return;
        if(!canJoin(joiner))
        {
            joiner.setAllowJoin(false);
            return;
        }

        //Also add members in the party of the joiner to the queue list
        joiner.getAllPlayers().forEach(member -> {
            GameJoiner memberJoiner = new GameJoinerSQL(member);
            queuedPlayers.add(memberJoiner);
        });

        //Communicate with CoreAPI on which players are already checked
        API.getInstance().setChecked(joiner.getUUID());
        joiner.setAllowJoin(true);
    }

    public List<GameJoiner> getQueuedPlayers() {
        return queuedPlayers;
    }

    private boolean canJoin(GameJoiner joiner)
    {
        //Code to check if player can join
        //if(joiner.getName().equalsIgnoreCase("Cryptidyy")) return false;

        //if players are banned

        for(UUID uuid : joiner.getAllPlayers())
        {
            if(!Bukkit.getBannedPlayers().contains(Bukkit.getOfflinePlayer(uuid))) continue;

            String reason = "PLAYER " + Bukkit.getOfflinePlayer(uuid).getName() + " IS BANNED";
            joiner.setRejectedReason(reason);
            return false;
        }

        return true;
    }

    public void setListening(boolean isListening)
    {
        this.isListening = isListening;
    }
}
