package me.cryptidyy.oceanraiders.npcs;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.server.v1_15_R1.Entity;

public class OpenShopEvent extends Event implements Cancellable {

	private final Entity npc;
	private final Player player;
	private boolean isCancelled;
	private static final HandlerList HANDLERS = new HandlerList();
	
	public OpenShopEvent(Entity npc, Player player)
	{
		this.player = player;
		this.npc = npc;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg) {
		
		isCancelled = arg;
	}

	public Player getPlayer() {
		return player;
	}

	public Entity getNpc() {
		return npc;
	}

}
