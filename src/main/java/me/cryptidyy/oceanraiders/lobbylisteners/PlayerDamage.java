package me.cryptidyy.oceanraiders.lobbylisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamage implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            event.setCancelled(true);
        }
    }
}
