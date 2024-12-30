package me.cryptidyy.oceanraiders.activelisteners;

import me.cryptidyy.oceanraiders.state.ActiveArenaState;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import me.cryptidyy.oceanraiders.utility.TreasureMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.OceanPlayer;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;

public class TreasureDropEvent implements Listener {
	
	private Main plugin;
	
	private GameManager manager;
	
	public TreasureDropEvent(Main plugin)
	{
		this.plugin = plugin;
		this.manager = this.plugin.getGameManager();
	}
	@EventHandler
	public void onTreasureRemove(InventoryClickEvent e)
	{
		if (!(e.getWhoClicked() instanceof Player)) return;

		final Player p = (Player) e.getWhoClicked();

		Inventory inv = e.getInventory();
		Inventory clickedInv = e.getClickedInventory();
		ItemStack current = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
		InventoryAction action = e.getAction();
		ClickType clickType = e.getClick();
		int slot = e.getRawSlot();

		if(!(inv.getHolder() instanceof Chest) && !(inv.getHolder() instanceof Barrel)) return;
		if(isTreasureChest(inv, p)) return;

		if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
		{
			if(!(clickedInv instanceof PlayerInventory)) return;
		}
		else
		{
			if(clickedInv instanceof PlayerInventory) return;
		}

		if(clickType.equals(ClickType.NUMBER_KEY))
		{
			if(p.getInventory().getItem(e.getHotbarButton()) == null) return;
			if(p.getInventory().getItem(e.getHotbarButton()).getType().equals(Material.HEART_OF_THE_SEA))
			{
				e.setCancelled(true);
			}
		}
		if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
		{
			if(current.getType().equals(Material.HEART_OF_THE_SEA))
			{
				e.setCancelled(true);
			}
		}
		else if(action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME))
		{
			if(cursor.getType().equals(Material.HEART_OF_THE_SEA))
			{
				e.setCancelled(true);
			}
		}
		else if(action.equals(InventoryAction.SWAP_WITH_CURSOR))
		{
			if(cursor.getType().equals(Material.HEART_OF_THE_SEA))
			{
				e.setCancelled(true);
			}
		}
	}
	private boolean isTreasureChest(Inventory inventory, Player player)
	{
		if(!(inventory.getHolder() instanceof Container)) return false;
		Location loc = ((Container) (inventory.getHolder())).getLocation();

		boolean isRedChest = loc.getX() == manager.getRedTreasureChest().getX()
				&& loc.getY() == manager.getRedTreasureChest().getY()
				&& loc.getZ() == manager.getRedTreasureChest().getZ();

		boolean isBlueChest = loc.getX() == manager.getBlueTreasureChest().getX()
				&& loc.getY() == manager.getBlueTreasureChest().getY()
				&& loc.getZ() == manager.getBlueTreasureChest().getZ();

		return (PlayerManager.toOceanPlayer(player).getPlayerTeam().getTeamName().equals("Red Team") && isRedChest)
				|| (PlayerManager.toOceanPlayer(player).getPlayerTeam().getTeamName().equals("Blue Team") && isBlueChest);
	}

	@EventHandler
	public void onDrag(InventoryDragEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player)) return;
		Inventory inv = event.getInventory();
		ItemStack item = event.getOldCursor();
		if(!(inv.getHolder() instanceof Chest) && !(inv.getHolder() instanceof Barrel)) return;
		if(isTreasureChest(inv, (Player) event.getWhoClicked())) return;
		//if drag happened outside a container
		if(inv instanceof DoubleChestInventory)
		{
			if(event.getNewItems().keySet().stream().allMatch(slot -> slot > 53)) return;
		}
		else
		{
			if(event.getNewItems().keySet().stream().allMatch(slot -> slot > 26)) return;
		}
		if(item.getType().equals(Material.HEART_OF_THE_SEA))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		
		if(!manager.getPlayingPlayers().contains(player.getUniqueId())) return;
		
		if(!event.getItemDrop().getItemStack().equals(manager.getBlueTreasure())
				&& !event.getItemDrop().getItemStack().equals(manager.getRedTreasure())) return;
		
		OceanPlayer oceanPlayer = PlayerManager.toOceanPlayer(player);
		oceanPlayer.setHasDroppedTreasure(true);
		
		new BukkitRunnable()
		{
			@Override
			public void run() 
			{
				if(!(manager.getGameState() instanceof ActiveArenaState))
				{
					this.cancel();
					return;
				}
				if(!manager.getGameWorld().getEntities().contains(event.getItemDrop()))
				{
					oceanPlayer.setHasDroppedTreasure(false);
					this.cancel();
					return;
				}

				event.getItemDrop().remove();

				if(player.getInventory().firstEmpty() == -1)
				{
					resetTreasure(event.getItemDrop().getItemStack());
					player.sendMessage(ChatUtil.format("&cYou dropped the treasure at an invalid spot, " +
							"and you didn't have space in your inventory."));
					this.cancel();
					return;
				}

				player.getInventory().addItem(event.getItemDrop().getItemStack().equals(manager.getRedTreasure()) ?
						manager.getRedTreasure() : manager.getBlueTreasure());

				player.sendMessage(ChatColor.RED + "You dropped the treasure at an invalid spot!");
				oceanPlayer.setHasDroppedTreasure(false);
				oceanPlayer.setDroppedTreasure(event.getItemDrop().getItemStack().equals(manager.getRedTreasure()) ?
						manager.getRedTreasure() : manager.getBlueTreasure());

				this.cancel();
				return;

			}
			
		}.runTaskLater(plugin, 20 * 3);
	}

	public void resetTreasure(ItemStack treasure)
	{
		if(!treasure.equals(manager.getRedTreasure())
				&& !treasure.equals(manager.getBlueTreasure())) return;

		//put treasure back to treasure chest
		boolean isRedTreasure = treasure.equals(manager.getRedTreasure());

		Chest treasureChest;

		treasureChest = isRedTreasure ?
				manager.getRedTreasureChest() :
				manager.getBlueTreasureChest();

		treasureChest.getBlockInventory().setItem(13, treasure);

		manager.sendGameMessages(isRedTreasure ?
				TreasureMessages.getReplacedMessage("Red team", true) :
				TreasureMessages.getReplacedMessage("Blue team", true));

		if(isRedTreasure)
		{
			manager.getTeamRed().setTreasureStolen(false);
		}
		else
		{
			manager.getTeamBlue().setTreasureStolen(false);
		}
		return;
	}

}
