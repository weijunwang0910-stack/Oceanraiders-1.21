package me.cryptidyy.oceanraiders.state;

import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.events.StateListenerProvider;
import me.cryptidyy.oceanraiders.events.WaitingStateListenerProvider;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.sql.QueueListener;
import me.cryptidyy.oceanraiders.sql.ServerSQL;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class StartCountdownState extends GameState {

    private GameManager manager;
    private BukkitTask countdown;

    private QueueListener queueListener;

    private int secUntilStart = 20;

    @Override
    public void onEnable(Main plugin)
    {
        super.onEnable(plugin);

        manager = plugin.getGameManager();
        countdown = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if(!manager.canGameStart()) manager.setState(new WaitingArenaState());

            if(secUntilStart <= 0)
            {
                manager.setState(new ActiveArenaState());
            }
            Bukkit.broadcastMessage(ChatUtil.format("&bGame starts in " + secUntilStart + "..."));

            secUntilStart--;

        }, 0, 20);

        API.getInstance().getStatusUpdater().setWaiting(true);

        manager = plugin.getGameManager();
        scheduleQueueListener();

        //ServerSQL.updateGameState("StartCountdownState");
    }

    public void scheduleQueueListener()
    {
        queueListener = manager.getQueueListener();
        queueListener.setListening(true);
    }
    @Override
    public void onDisable()
    {
        super.onDisable();
        countdown.cancel();
        API.getInstance().getStatusUpdater().setWaiting(false);
        queueListener.setListening(false);
    }

    @Override
    public StateListenerProvider getStateListenerProvider()
    {
        return new WaitingStateListenerProvider();
    }
}
