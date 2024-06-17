package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class InvisWalkEvent implements Listener {

    private int counter = 0;
    @EventHandler
    public void onInvisWalk(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if(!Main.getPlugin(Main.class).getGameManager().isStarted()) return;
        if(!PlayerManager.toOceanPlayer(player).isInvisible()) return;
        //spawn particles under feet

        counter++;

        if(counter >= 8)
        {
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().clone().add(0, 0.1, 0), 0);
            counter = 0;
        }
    }
}
