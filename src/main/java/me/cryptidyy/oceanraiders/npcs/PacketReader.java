package me.cryptidyy.oceanraiders.npcs;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.cryptidyy.oceanraiders.Main;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;

public class PacketReader {
	
	public void create(Player player)
	{
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {
			
			@Override
			public void channelRead(ChannelHandlerContext context, Object packet) throws Exception
			{	
				if(packet instanceof PacketPlayInUseEntity)
				{
					if(getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
					{
						super.channelRead(context, packet);
						return;
					}

					if(getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
					{	
						super.channelRead(context, packet);
						return;
					}
					if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
					{
						super.channelRead(context, packet);
						return;
					}
					
					int id = (int) getValue(packet, "a");
					if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT"))
					{
						for(Entity npc : GameNPCSetupManager.npcs)
						{
							if(id == npc.getId())
							{
								//PacketPlayInUseEntity useEntity = new PacketPlayInUseEntity();
								Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), bukkitTask -> {
									Bukkit.getPluginManager().callEvent(new OpenShopEvent(npc, player));
								}, 0);
								
								return;
							}

						}
					}			
				}
				super.channelRead(context, packet);
			}
			
			@Override
			public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception
			{
				super.write(context, packet, promise);
			}
		};
		
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

		if(pipeline.get(player.getName()) == null)
			pipeline.addBefore("packet_handler", player.getName(), handler);
	}
	
	public void remove(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }
	
	private Object getValue(Object instance, String name)
	{
		Object result = null;
		
		try
		{
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			result = field.get(instance);
			
			field.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
