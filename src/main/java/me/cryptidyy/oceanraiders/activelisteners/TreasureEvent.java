package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import net.md_5.bungee.api.ChatColor;

public class TreasureEvent implements Listener {

	private GameManager manager;

	public TreasureEvent(Main plugin)
	{
		this.plugin = plugin;
		this.manager = plugin.getGameManager();
	}
	
	private Main plugin;
	
	private IslandManager islandManager;
	
	private String treasureStoleHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE STOLEN > ";
	private String treasureReplacedHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE REPLACED > ";
//
//	@EventHandler
//	public void onTreasureTake2(InventoryClickEvent event)
//	{
//		if(!(event.getWhoClicked() instanceof Player)) return;
//
//		Player player = (Player) event.getWhoClicked();
//		islandManager = plugin.getIslandManager();
//
//		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;
//
//		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
//		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();
//
//
//		Inventory redTreasureInv = redChest.getBlockInventory();
//		Inventory blueTreasureInv = blueChest.getBlockInventory();
//
//		//Red chest handler
//		if(event.getInventory().equals(redTreasureInv))
//		{
//			if(manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
//			{
//				if(manager.getTeamRed().isTreasureStolen())
//				{
//					if(!(event.getClickedInventory() instanceof PlayerInventory)
//							|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
//					{
//						//manager.sendGameMessage(treasureReplacedHeader + ChatColor.AQUA + "Red team's treasure is replaced!");
//						manager.sendGameMessages(TreasureMessages.getReplacedMessage("Red team", true));
//						return;
//					}
//					return;
//				}
//
//				event.setCancelled(true);
//				player.sendMessage(ChatColor.RED + "You cannot take your own team treasure!");
//				return;
//			}
//
//			//if treasure isn't stolen
//			if(!manager.getTeamRed().isTreasureStolen())
//			{
//				if(!(event.getClickedInventory() instanceof PlayerInventory)
//						|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
//				{
//					if(event.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA))
//					{
//						//manager.sendGameMessage(treasureStoleHeader + ChatColor.RED + "Red team's treasure is stolen!");
//						manager.sendGameMessages(TreasureMessages.getStolenMessage("Red team", true));
//						return;
//					}
//				}
//			}
//			//Blue put back treasure handler
//			if(manager.getTeamRed().isTreasureStolen() && manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
//			{
//				if(event.getClickedInventory() instanceof PlayerInventory)
//				{
//					event.setCancelled(true);
//				}
//
//			}
//
//		}
//
//		//Blue chest handler
//		else if(event.getInventory().equals(blueTreasureInv))
//		{
//			if(manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
//			{
//				if(manager.getTeamBlue().isTreasureStolen())
//				{
//					if(!(event.getClickedInventory() instanceof PlayerInventory)
//							|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
//					{
////						manager.sendGameMessage(treasureReplacedHeader + ChatColor.AQUA + "Blue team's treasure is replaced!");
//						manager.sendGameMessages(TreasureMessages.getReplacedMessage("Blue team", true));
//						return;
//					}
//
//					return;
//				}
//
//				event.setCancelled(true);
//				player.sendMessage(ChatColor.RED + "You cannot take your own team treasure!");
//				return;
//
//			}
//
//			if(!manager.getTeamBlue().isTreasureStolen())
//			{
//				if (!(event.getClickedInventory() instanceof PlayerInventory)
//						|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
//				{
//					if (event.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA))
//					{
//						manager.sendGameMessages(TreasureMessages.getStolenMessage("Blue team", true));
//						return;
//					}
//				}
//			}
//
//			//Red put back treasure handler
//			if(manager.getTeamBlue().isTreasureStolen() && manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
//			{
//				if(event.getClickedInventory() instanceof PlayerInventory)
//				{
//					event.setCancelled(true);
//				}
//
//			}
//
//		}
//
//	}

	//Handles when enemy team takes a treasure and tries to put it back
	@EventHandler
	public void onTreasureTake(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return;
		final Player player = (Player) e.getWhoClicked();

		Inventory inv = e.getInventory();
		Inventory clickedInv = e.getClickedInventory();
		ItemStack current = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
		InventoryAction action = e.getAction();
		ClickType clickType = e.getClick();

		islandManager = plugin.getIslandManager();

		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;

		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();

		Inventory redTreasureInv = redChest.getBlockInventory();
		Inventory blueTreasureInv = blueChest.getBlockInventory();

		if(!(inv.getHolder() instanceof Chest) && !(inv.getHolder() instanceof Barrel)) return;
		String teamName = inv.equals(redTreasureInv) ? "Red Team" : "Blue Team";
		//Red Chest with blue team player OR blue chest with red team player
		if(inv.equals(redTreasureInv) && manager.getTeamBlue().getPlayers().contains(player.getUniqueId())
			|| inv.equals(blueTreasureInv) && manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
		{
			//player clicks in chest
			if(!(clickedInv instanceof PlayerInventory))
			{
				//players takes from chest
				if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || action.equals(InventoryAction.PICKUP_ALL)
						|| action.equals(InventoryAction.PICKUP_HALF) || action.equals(InventoryAction.PICKUP_ONE)
						|| action.equals(InventoryAction.PICKUP_SOME))
				{
					if(current == null) return;

					if(current.getType().equals(Material.HEART_OF_THE_SEA))
					{
						manager.sendGameMessages(TreasureMessages.getStolenMessage(teamName, true));
					}
				}
				//player uses a number key
				else if(clickType.equals(ClickType.NUMBER_KEY))
				{
					//Player uses number key on an empty space
					if(current == null)
					{
						e.setCancelled(true);
					}
					//Player uses number key on the treasure
					if(current.getType().equals(Material.HEART_OF_THE_SEA))
					{
						ItemStack hotbarItem = player.getInventory().getItem(e.getHotbarButton());
						//player has an empty hotbar space
						if(hotbarItem == null || hotbarItem.getType().equals(Material.AIR))
						{
							manager.sendGameMessages(TreasureMessages.getStolenMessage(teamName, true));
						}
						else
						{
							e.setCancelled(true);
						}
					}
					//Player uses number key on an item that's not a treasure
					else
					{
						e.setCancelled(true);
					}
				}
				//player puts in chest
				else if(action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME))
				{
					e.setCancelled(true);
				}
				//player swaps in chest
				else if(action.equals(InventoryAction.SWAP_WITH_CURSOR))
				{
					e.setCancelled(true);
				}
				return;
			}
			//player clicks in the player inventory
			if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
			{
				e.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onTreasureReplace(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return;
		final Player player = (Player) e.getWhoClicked();

		Inventory inv = e.getInventory();
		Inventory clickedInv = e.getClickedInventory();
		ItemStack current = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
		InventoryAction action = e.getAction();
		ClickType clickType = e.getClick();

		islandManager = plugin.getIslandManager();

		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;

		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();

		Inventory redTreasureInv = redChest.getBlockInventory();
		Inventory blueTreasureInv = blueChest.getBlockInventory();

		if(!(inv.getHolder() instanceof Chest) && !(inv.getHolder() instanceof Barrel)) return;
		String teamName = inv.equals(redTreasureInv) ? "Red Team" : "Blue Team";
		//Same chest with same team
		if(inv.equals(redTreasureInv) && manager.getTeamRed().getPlayers().contains(player.getUniqueId())
				|| inv.equals(blueTreasureInv) && manager.getTeamBlue().getPlayers().contains(player.getUniqueId()))
		{
			//player clicks in chest
			if(!(clickedInv instanceof PlayerInventory))
			{
				//players takes from chest
				if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || action.equals(InventoryAction.PICKUP_ALL)
						|| action.equals(InventoryAction.PICKUP_HALF) || action.equals(InventoryAction.PICKUP_ONE)
						|| action.equals(InventoryAction.PICKUP_SOME))
				{
					if(current == null) return;

					if(current.getType().equals(Material.HEART_OF_THE_SEA))
					{
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You cannot take your own team's treasure!");
					}
				}
				//player uses a number key
				else if(clickType.equals(ClickType.NUMBER_KEY))
				{
					//Player uses number key on an empty space
					if(current == null)
					{
						ItemStack hotbar = player.getInventory().getItem(e.getHotbarButton());

						if(hotbar == null) return;
						boolean isTeamTreasure = hotbar.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
								|| hotbar.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

						if(isTeamTreasure)
						{
							manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
						}
						else
						{
							e.setCancelled(true);
						}
					}
					//Player uses number key on the treasure
					if(current.getType().equals(Material.HEART_OF_THE_SEA))
					{
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You cannot take your own team's treasure!");
					}
					//Player uses number key on an item that's not a treasure
					else
					{
						ItemStack hotbar = player.getInventory().getItem(e.getHotbarButton());

						if(hotbar == null) return;
						boolean isTeamTreasure = hotbar.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
								|| hotbar.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

						if(isTeamTreasure)
						{
							manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
						}
						else
						{
							e.setCancelled(true);
						}
					}
				}
				//player puts in chest
				else if(action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME))
				{
					boolean isTeamTreasure = cursor.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
							|| cursor.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

					if(isTeamTreasure)
					{
						manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
					}
					else
					{
						e.setCancelled(true);
					}
				}
				//player swaps in chest
				else if(action.equals(InventoryAction.SWAP_WITH_CURSOR))
				{
					boolean isTeamTreasure = cursor.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
							|| cursor.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

					if(isTeamTreasure)
					{
						manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
					}
					else
					{
						e.setCancelled(true);
					}
				}
				return;
			}
			//player clicks in the player inventory
			if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
			{
				boolean isTeamTreasure = current.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
						|| current.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

				if(isTeamTreasure)
				{
					manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
				}
				else
				{
					e.setCancelled(true);
				}
			}

		}
	}

	//Handles when enemy team drags a treasure back into the treasure chest
	@EventHandler
	public void onDragPlace(InventoryDragEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player)) return;
		Inventory inv = event.getInventory();
		ItemStack item = event.getOldCursor();
		Player player = (Player) event.getWhoClicked();

		islandManager = plugin.getIslandManager();

		if(!manager.getAllPlayers().contains(player.getUniqueId())) return;

		Chest redChest = (Chest) islandManager.findIsland("Red Island").get().getChestLoc().getBlock().getState();
		Chest blueChest = (Chest) islandManager.findIsland("Blue Island").get().getChestLoc().getBlock().getState();

		Inventory redTreasureInv = redChest.getBlockInventory();
		Inventory blueTreasureInv = blueChest.getBlockInventory();
		String teamName = inv.equals(redTreasureInv) ? "Red Team" : "Blue Team";
		if(!(inv.getHolder() instanceof Chest) && !(inv.getHolder() instanceof Barrel)) return;
		if(inv.equals(redTreasureInv) && manager.getTeamBlue().getPlayers().contains(player.getUniqueId())
				|| inv.equals(blueTreasureInv) && manager.getTeamRed().getPlayers().contains(player.getUniqueId()))
		{
			//if drag happened outside treasure chest
			if(event.getNewItems().keySet().stream().allMatch(slot -> slot > 26)) return;

			event.setCancelled(true);
		}
		else
		{
			//if drag happened outside treasure chest
			if(event.getNewItems().keySet().stream().allMatch(slot -> slot > 26)) return;

			boolean isTeamTreasure = item.getItemMeta().getDisplayName().contains("Blue Treasure") && inv.equals(blueTreasureInv)
					|| item.getItemMeta().getDisplayName().contains("Red Treasure") && inv.equals(redTreasureInv);

			if(isTeamTreasure)
			{
				manager.sendGameMessages(TreasureMessages.getReplacedMessage(teamName, true));
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}
}
