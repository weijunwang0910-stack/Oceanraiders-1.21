package me.cryptidyy.oceanraiders.gamemap;

import org.bukkit.World;

public interface GameMap {

    boolean load();
    void unload();
    boolean restoreFromSource();

    boolean isLoaded();
    World getWorld();

}
