package me.cryptidyy.oceanraiders.utility;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Hologram {

    private Location location;
    private String text;
    private final ArmorStand armorStand;
    private Main plugin;

    public Hologram(Location location, String text, Main plugin)
    {
        this.plugin = plugin;
        armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
    }

    public void display(Player... players)
    {
        for(Player player : players)
        {
            player.showEntity(plugin, armorStand);
        }
    }

    public void hide(Player... players)
    {
        for(Player player : players)
        {
            player.hideEntity(plugin, armorStand);
        }
    }
    public void setText(String text)
    {
        this.text = text;
        armorStand.setCustomName(text);
    }

}
