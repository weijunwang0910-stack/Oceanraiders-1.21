package me.cryptidyy.oceanraiders.npcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.cryptidyy.oceanraiders.shop.BlackSmithShop;
import me.cryptidyy.oceanraiders.shop.EnchantShop;
import me.cryptidyy.oceanraiders.shop.FoodShop;
import me.cryptidyy.oceanraiders.shop.PotionShop;

public class NPCShopManager {

	private static List<Player> shopUsers = new ArrayList<>();
	
	private static Map<UUID, BlackSmithShop> playerToBlacksmithMap = new HashMap<>();
	private static Map<UUID, EnchantShop> playerToEnchantMap = new HashMap<>();
	private static Map<UUID, FoodShop> playerToFoodMap = new HashMap<>();
	private static Map<UUID, PotionShop> playerToPotionMap = new HashMap<>();
	
	public NPCShopManager addPlayer(Player player)
	{
		shopUsers.add(player);
		playerToBlacksmithMap.put(player.getUniqueId(), new BlackSmithShop(player));
		playerToEnchantMap.put(player.getUniqueId(), new EnchantShop(player));
		playerToFoodMap.put(player.getUniqueId(), new FoodShop(player));
		playerToPotionMap.put(player.getUniqueId(), new PotionShop(player));
		return this;
	}
	
	public BlackSmithShop getBlacksmithShop(Player player)
	{
		if(playerToBlacksmithMap.containsKey(player.getUniqueId()))
			return playerToBlacksmithMap.get(player.getUniqueId());
		
		return null;
	}
	
	public EnchantShop getEnchantShop(Player player)
	{
		if(playerToEnchantMap.containsKey(player.getUniqueId()))
			return playerToEnchantMap.get(player.getUniqueId());
		
		return null;
	}
	
	public FoodShop getFoodShop(Player player)
	{
		if(playerToFoodMap.containsKey(player.getUniqueId()))
			return playerToFoodMap.get(player.getUniqueId());
		
		return null;
	}
	
	public PotionShop getPotionShop(Player player)
	{
		if(playerToPotionMap.containsKey(player.getUniqueId()))
			return playerToPotionMap.get(player.getUniqueId());
		
		return null;
	}

	public void resetAllShop()
	{
		for(UUID id : playerToBlacksmithMap.keySet())
		{
			playerToBlacksmithMap.get(id).getPlayerPurchasedItems().clear();
			playerToFoodMap.get(id).getPlayerPurchasedItems().clear();
			playerToPotionMap.get(id).getPlayerPurchasedItems().clear();
		}

	}
	
}
