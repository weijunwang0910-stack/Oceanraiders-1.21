package me.cryptidyy.oceanraiders.state;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.events.StateListenerProvider;

public abstract class GameState {

	public void onEnable(Main plugin)
	{
		if(getStateListenerProvider() != null)
		{
			getStateListenerProvider().onEnable(plugin);
		}
	}
	
	public void onDisable()
	{
		if(getStateListenerProvider() != null)
		{
			getStateListenerProvider().onDisable();
		}
	}
	
	public abstract StateListenerProvider getStateListenerProvider();
	
}
