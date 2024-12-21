package me.cryptidyy.oceanraiders.sql;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.coreapi.api.GameJoiner;
import me.cryptidyy.coreapi.api.QueueListener;
import me.cryptidyy.coreapi.sql.GameJoinerSQL;
import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//ran during waiting arena state for first join, and active state for rejoin
public class OceanQueueListener extends QueueListener {

    @Override
    public boolean canJoin(GameJoiner gameJoiner) {
        return true;
    }
}
