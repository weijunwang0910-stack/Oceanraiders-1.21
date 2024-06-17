package me.cryptidyy.oceanraiders.utility;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

public class NMSHelper {

	public static Object getHandle(Player player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return player.getClass().getMethod("getHandle").invoke(player);
	}
	
	
	public static GameProfile getProfile(Player player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Object handle = getHandle(player);
		
		return (GameProfile) handle.getClass()
				.getSuperclass()
				.getDeclaredMethod("getProfile")
				.invoke(handle);
	}
}
