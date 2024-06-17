package me.cryptidyy.oceanraiders.events;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class WaitingStateListenerProvider extends StateListenerProvider {

    private static List<Listener> registeredListeners = new ArrayList<>();

    @Override
    public void onEnable(Main plugin)
    {
        String fullPackageName = plugin.getClass().getPackage().getName();

        //register every event in package
        for(Class<?> clazz : new Reflections(fullPackageName + ".lobbylisteners")
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
    List<Listener> getRegisteredListeners() {
        return this.registeredListeners;
    }
}
