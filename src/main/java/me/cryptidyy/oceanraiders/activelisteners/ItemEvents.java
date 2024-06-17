package me.cryptidyy.oceanraiders.activelisteners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import me.cryptidyy.oceanraiders.customitems.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public class ItemEvents implements Listener {

	private Main plugin;
	
	private List<UUID> players = new ArrayList<>();
	private List<TeleportTridentTask> tridentTasks = new ArrayList<>();
	
	public ItemEvents(Main plugin)
	{
		this.plugin = plugin;
		
		players = plugin.getGameManager().getPlayingPlayers();
		
		for(UUID uuid : players)
		{
			Player source = Bukkit.getPlayer(uuid);
			TeleportTridentTask task = new TeleportTridentTask(plugin, source);
			tridentTasks.add(task);
			
			task.runTaskTimer(plugin, 0, 0);
		}
	}
	
	@EventHandler
	public void onThrow(ProjectileLaunchEvent event)
	{
		if(!(event.getEntity().getShooter() instanceof Player)) return;
		Player source = (Player) event.getEntity().getShooter();

		if(!(event.getEntity() instanceof Trident)) return;
		if(source == null) return;
		
		event.setCancelled(true);
		
		Optional<TeleportTridentTask> optionalTask = tridentTasks
				.stream()
				.filter(task -> task.getSource().getUniqueId().equals(source.getUniqueId()))
				.findFirst();
		
		if(!optionalTask.isPresent()) return;
		
		if(optionalTask.get().getTarget() == null) return;
		
		Player target = optionalTask.get().getTarget();
		target.setGlowing(false);
		optionalTask.get().getTargetTeam().removePlayer(target);
		
		FlyingTrident trident = new FlyingTrident(target, 1, source, source.getInventory().getItemInMainHand());
		source.getInventory().removeItem(source.getInventory().getItemInMainHand());
		
		trident.runTaskTimer(plugin, 0, 2);
	}
	
	//When player clicks on an item
	@EventHandler
	public void onUse(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if(player.getInventory().getItemInMainHand() == null) return;
		if(!player.getInventory().getItemInMainHand().hasItemMeta()) return;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if(toOceanItemType(item) == null) return;
		
		switch(toOceanItemType(item))
		{
			case FIRE_PLACER:
				if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
				if(!event.getBlockFace().equals(BlockFace.UP)) return;
				if(event.getHand().equals(EquipmentSlot.HAND)) 
				{		
					useItem(player, event.getClickedBlock(), event.getAction(), item);
				}
				break;
				
			case COBWEB_WALL:
				if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
				if(event.getHand().equals(EquipmentSlot.HAND))
				{	
					useItem(player, event.getClickedBlock(), event.getAction(), item);
				}
				break;
				
			case BOAT_SINKER:
				if(!event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
				
				if(event.getHand().equals(EquipmentSlot.HAND))
				{	
					useItem(player, event.getClickedBlock(), event.getAction(), item);
				}
				break;
			case WHOLE_CAKE:
				if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

				if(event.getHand().equals(EquipmentSlot.HAND))
				{
					useItem(player, event.getClickedBlock(), event.getAction(), item);
				}
				break;
				
			default:
				break;
			
		}
	}
	
	//When Player Consumes Milk
	@EventHandler
	public void onDrink(PlayerItemConsumeEvent event)
	{
		if(!plugin.getGameManager().getPlayingPlayers().contains(event.getPlayer().getUniqueId())) return;
		if(!event.getItem().getType().equals(Material.MILK_BUCKET)) return;
		if(!event.getItem().getItemMeta().getDisplayName().contains("Immunity")) return;
		
		Player player = event.getPlayer();
		useItem(player, null, null, event.getItem());
		
		Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BUCKET)
			{
				event.getPlayer().getInventory().getItemInMainHand().setAmount(0);
			}
			else
			{
				event.getPlayer().getInventory().remove(Material.BUCKET);
			}
			
		}, 1);
	}

	//When Player Consumes potion
	@EventHandler
	public void onDrinkPotion(PlayerItemConsumeEvent event)
	{
		if(!plugin.getGameManager().getPlayingPlayers().contains(event.getPlayer().getUniqueId())) return;
		if(!event.getItem().getType().equals(Material.POTION)) return;
		if(!((PotionMeta)event.getItem().getItemMeta()).getCustomEffects()
				.stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.INVISIBILITY))) return;

		Player player = event.getPlayer();
		useItem(player, null, null, event.getItem());

		Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BUCKET)
			{
				event.getPlayer().getInventory().getItemInMainHand().setAmount(0);
			}
			else
			{
				event.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
			}

		}, 1);
	}
	
	public OceanItemType toOceanItemType(ItemStack item)
	{		
		return Arrays.stream(OceanItemType.values())
			.filter(type -> type.material == item.getType())
			.filter(type -> ChatColor.stripColor(type.displayName).equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName())))
			.findAny()
			.orElse(null);
	}
	
	private void useItem(Player user, Block clickedBlock, Action action, ItemStack currentItem)
	{
		//setup item
		OceanItem currentOceanItem = plugin.getItemManager()
				.findOceanItem(currentItem.getItemMeta().getDisplayName()).get();
		
		//setup user
		CustomItemUser itemUser = null;
		
		if(clickedBlock != null)
		{
			itemUser = new CustomItemUser(user, clickedBlock, 
					clickedBlock.getLocation(), 
					action,
					currentOceanItem);
		}
		else
		{
			itemUser = new CustomItemUser(user,
					action,
					currentOceanItem);
		}
		
		itemUser.setItemInMainHand(currentItem);
		itemUser.useItem();
	}
}
