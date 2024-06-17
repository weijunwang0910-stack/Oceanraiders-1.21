package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FirePlacer extends OceanItem {

	private final int initialItemDamage;
	public FirePlacer(List<String> lore, int slot)
	{
		super(OceanItemType.FIRE_PLACER, lore, slot);
		initialItemDamage = 64;
	}


	@Override
	public void useItem(CustomItemUser user, GameManager manager)
	{
		Location center = user.getClickedLocation().add(0,1,0);
		List<Location> locs = new ArrayList<>();

		for(double x = center.getX() - 1; x <= center.getX() + 1; x++)
		{
			for(double z = center.getZ() - 1; z <= center.getZ() + 1; z++)
			{
				if(new Location(center.getWorld(), x, center.getY(), z).getBlock().getType().isAir())
				{
					locs.add(new Location(center.getWorld(), x, center.getY(), z));
					new Location(center.getWorld(), x, center.getY(), z).getBlock().setType(Material.FIRE);
				}
			}
		}

		ItemStack flintAndSteel = user.getItemInMainHand();

		if(flintAndSteel == null) return;

		ItemMeta meta = flintAndSteel.getItemMeta();
		if(meta instanceof Damageable)
		{
			Damageable damageable = (Damageable) meta;
			damageable.setDamage(damageable.getDamage() + 8);
			flintAndSteel.setItemMeta(meta);

			if(damageable.getDamage()> flintAndSteel.getType().getMaxDurability())
			{
				user.getUser().getInventory().setItemInMainHand(null);
				user.getUser().playSound(user.getUser().getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			}
		}
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
			for(Location loc : locs)
			{
				loc.getBlock().setType(Material.AIR);
				center.getBlock().setType(Material.AIR);
			}
		}, 20 * 2);
	}
}
