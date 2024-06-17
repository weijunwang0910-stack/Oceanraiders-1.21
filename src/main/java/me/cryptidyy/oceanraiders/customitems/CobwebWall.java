package me.cryptidyy.oceanraiders.customitems;

import java.util.ArrayList;
import java.util.List;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;

public class CobwebWall extends OceanItem {

	public CobwebWall(List<String> lore, int slot)
	{
		super(OceanItemType.COBWEB_WALL, lore, slot);
	}

	@Override
	public void useItem(CustomItemUser user, GameManager manager) {
			
		ItemStack cobwebItem = user.getItem().getItem().clone();
		cobwebItem.setAmount(1);
		
		Location block = user.getClickedBlock().getLocation().clone();
		Location org = user.getClickedBlock().getLocation().clone();
		
		List<Block> cobwebs = new ArrayList<>();
		
		if(user.getUser().getFacing() == BlockFace.NORTH 
				|| user.getUser().getFacing() == BlockFace.SOUTH)
		{
			for(int i = 0; i < 3; i++)
			{
				block.add(0,1,0);
				block.setX(org.getX());
				if(block.getBlock().getType() == Material.AIR)
				{
					block.getBlock().setType(Material.COBWEB);
					cobwebs.add(block.getBlock());
				}

				for(int j = 0; j < 2; j++)
				{
					block.add(1,0,0);
					if(block.getBlock().getType() == Material.AIR)
					{
						block.getBlock().setType(Material.COBWEB);
						cobwebs.add(block.getBlock());
					}
				}
				
				block.setX(org.getX());
				for(int j = 0; j < 2; j++)
				{
					block.add(-1,0,0);
					if(block.getBlock().getType() == Material.AIR)
					{
						block.getBlock().setType(Material.COBWEB);
						cobwebs.add(block.getBlock());
					}

				}
			}
		}
		else
		{
			for(int i = 0; i < 3; i++)
			{
				block.add(0,1,0);
				block.setZ(org.getZ());
				if(block.getBlock().getType() == Material.AIR)
				{
					block.getBlock().setType(Material.COBWEB);
					cobwebs.add(block.getBlock());
				}

				for(int j = 0; j < 2; j++)
				{
					block.add(0,0,1);
					if(block.getBlock().getType() == Material.AIR) 
					{
						block.getBlock().setType(Material.COBWEB);
						cobwebs.add(block.getBlock());
					}
				}
				block.setZ(org.getZ());
				for(int j = 0; j < 2; j++)
				{
					block.add(0,0,-1);
					if(block.getBlock().getType() == Material.AIR) 
					{
						block.getBlock().setType(Material.COBWEB);
						cobwebs.add(block.getBlock());
					}
				}
			}

		}						
		
		Player player = Bukkit.getPlayer(user.getUser().getUniqueId());
		
		int cobwebAmount = player.getInventory().getItemInMainHand().getAmount();
		player.getInventory().getItemInMainHand().setAmount(cobwebAmount - 1);
		
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() 
			{
				for(Block block : cobwebs)
				{
					if(block.getType() == Material.COBWEB)
						block.setType(Material.AIR);
				}
			}
			
		}, 20 * 5);	
	}
}
