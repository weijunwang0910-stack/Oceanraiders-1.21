package me.cryptidyy.oceanraiders.events;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import me.cryptidyy.oceanraiders.Main;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class StateListenerProvider {

	public abstract void onEnable(Main plugin);
	
	public void onDisable()
	{
		getRegisteredListeners().forEach(listener -> {
			HandlerList.unregisterAll(listener);
		});

		getRegisteredListeners().clear();
	}

	abstract List<Listener> getRegisteredListeners();
}
