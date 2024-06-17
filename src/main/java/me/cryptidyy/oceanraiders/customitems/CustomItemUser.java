package me.cryptidyy.oceanraiders.customitems;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;

public class CustomItemUser implements Listener {

	private static Main plugin = Main.getPlugin(Main.class);
	
	private final Player user;
	
	private Block clickedBlock;
	private Location clickedLocation;
	private Action useAction;
	
	private ItemStack itemInMainHand;
	private OceanItem item;
	
	public CustomItemUser(Player user, Block clickedBlock, Location clickedLocation, Action useAction, OceanItem item)
	{
		this(user, clickedLocation, useAction, item);
		this.setClickedBlock(clickedBlock);
	}
	
	public CustomItemUser(Player user, Location clickedLocation, Action useAction, OceanItem item)
	{
		this(user, useAction, item);
		this.setClickedLocation(clickedLocation);
	}
	
	public CustomItemUser(Player user, Action useAction, OceanItem item)
	{
		this.setItem(item);
		this.user = user;
		this.setUseAction(useAction);
	}

	public void useItem()
	{
		item.useItem(this, plugin.getGameManager());
	}

	public Player getUser() {
		return user;
	}

	public Block getClickedBlock() {
		return clickedBlock;
	}

	public void setClickedBlock(Block clickedBlock) {
		this.clickedBlock = clickedBlock;
	}

	public Location getClickedLocation() {
		return clickedLocation;
	}

	public void setClickedLocation(Location clickedLocation) {
		this.clickedLocation = clickedLocation;
	}

	public Action getUseAction() {
		return useAction;
	}

	public void setUseAction(Action useAction) {
		this.useAction = useAction;
	}

	public OceanItem getItem() {
		return item;
	}

	public void setItem(OceanItem item) {
		this.item = item;
	}

	public ItemStack getItemInMainHand() {
		return itemInMainHand;
	}

	public void setItemInMainHand(ItemStack itemInMainHand) {
		this.itemInMainHand = itemInMainHand;
	}
	
}
