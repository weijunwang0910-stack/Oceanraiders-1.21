package me.cryptidyy.oceanraiders.events;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ActiveStateListenerProvider extends StateListenerProvider {

    private static List<Listener> registeredListeners = new ArrayList<>();

    @Override
    public void onEnable(Main plugin)
    {
        String fullPackageName = plugin.getClass().getPackage().getName();

        //register every event in package
        for(Class<?> clazz : new Reflections(fullPackageName + ".activelisteners")
                .getSubTypesOf(Listener.class))
        {
            try {
                Listener listener = (Listener) clazz
                        .getDeclaredConstructor()
                        .newInstance();
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                registeredListeners.add(listener);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }

            try {
                Listener listener = (Listener) clazz
                        .getDeclaredConstructor(Main.class)
                        .newInstance(plugin);
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                registeredListeners.add(listener);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    @Override
    List<Listener> getRegisteredListeners() {
        return this.registeredListeners;
    }

}
