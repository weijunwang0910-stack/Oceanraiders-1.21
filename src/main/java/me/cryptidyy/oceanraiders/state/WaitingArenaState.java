package me.cryptidyy.oceanraiders.state;

import me.cryptidyy.coreapi.api.API;
import me.cryptidyy.oceanraiders.events.WaitingStateListenerProvider;
import me.cryptidyy.oceanraiders.lobbylisteners.OceanJoinHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.events.StateListenerProvider;
import net.md_5.bungee.api.ChatColor;

public class WaitingArenaState extends GameState {


	private BukkitTask startCheck;
	private GameManager manager;
	private OceanJoinHandler joinHandler;
	
	@Override
	public void onEnable(Main plugin)
	{
		super.onEnable(plugin);
		Bukkit.broadcastMessage(ChatColor.AQUA + "Waiting for players!");
		API.getInstance().getStatusUpdater().setWaiting(true);

		manager = plugin.getGameManager();
		joinHandler = manager.getJoinHandler();
		joinHandler.setListening(true);
		//scheduleQueueListener();

		startCheck = Bukkit.getScheduler().runTaskTimer(plugin, () ->
		{
			API.getInstance().getStatusUpdater().setJoinedPlayerNum(manager.getLobbyPlayers().size());
			if(manager.canGameStart())
			{
				//API.getInstance().getStatusUpdater().setJoinedPlayerNum(manager.getLobbyPlayers().size());
				manager.setState(new StartCountdownState());
				startCheck.cancel();
			}
			
		}, 0, 8);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		joinHandler.setListening(false);
		startCheck.cancel();
		//API.getInstance().getStatusUpdater().setWaiting(false);
	}
	
	@Override
	public StateListenerProvider getStateListenerProvider()
	{
		return new WaitingStateListenerProvider();
	}

}
