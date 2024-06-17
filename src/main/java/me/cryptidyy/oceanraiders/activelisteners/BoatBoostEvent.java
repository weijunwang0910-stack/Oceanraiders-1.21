package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.player.PlayerManager;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoatBoostEvent implements Listener {
    private static Map<UUID, Float> playerCounter = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if(!(player.getVehicle() instanceof Boat)) return;
        if(!PlayerManager.toOceanPlayer(player).isBoatSpeeding())
        {
            playerCounter.put(player.getUniqueId(), 1.3f);
            return;
        }

        Boat boat = (Boat) player.getVehicle();
        playerCounter.put(player.getUniqueId(), playerCounter.get(player.getUniqueId()) + 0.005f);
        float speed = 1f/playerCounter.get(player.getUniqueId()) < 0.4f ? 0.4f : 1f/playerCounter.get(player.getUniqueId());

        //player.sendMessage("speed: " + speed);
        boostBoat(boat, speed);
    }

    @EventHandler
    public void onPlayerLeaveBoat(VehicleExitEvent event)
    {
        if(!(event.getExited() instanceof Player)) return;
        if(!(event.getVehicle() instanceof Boat)) return;
        Player exited = (Player) event.getExited();
        if(!(PlayerManager.toOceanPlayer(exited).isBoatSpeeding())) return;

        PlayerManager.toOceanPlayer(exited).setBoatSpeeding(false);
    }

    private void boostBoat(Boat boat, float speed)
    {
        boat.setVelocity(boat.getLocation().getDirection().normalize().multiply(speed));
    }
}
