package me.cryptidyy.oceanraiders.commands;

import java.util.Optional;
import java.util.Set;

import me.cryptidyy.oceanraiders.state.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.customitems.OceanItem;

public class TestCommands implements CommandExecutor {

	private Main plugin;
	
	//private List<LootChest> lootChests = new ArrayList<>();
	
	public TestCommands(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		
		if(label.equalsIgnoreCase("ortest"))
		{
			if(!(sender instanceof Player)) return true;
			
			//BlackSmithShop shop = new BlackSmithShop();
			Player player = (Player) sender;
			
			if(args.length > 0)
			{
				switch(args[0])
				{
					case "give":
						
						if(!plugin.getGameManager().isStarted())
						{
							player.sendMessage(ChatColor.RED + "The game has not started!");
							break;
						}
						
						if(args.length < 2)
						{
							player.sendMessage(ChatColor.RED + "Usage: /ortest give <itemName> [amount]");
							break;
						}
						
						String itemName = args[1];
						int amount = 1;

						if(args.length == 3)
						{
							if(!isNum(args[2]))
							{
								player.sendMessage(ChatColor.RED + "Invalid item amount: " + args[2]);
								break;
							}
							amount = Integer.parseInt(args[2]);
						}
						
						Set<OceanItem> items = plugin.getGameManager()
													.getOceanItemManager()
													.getOceanItems();
						
						Optional<OceanItem> optionalItem = items.stream()
								.filter(item -> item.getType().name().equalsIgnoreCase(itemName))
								.findFirst();
						
						if(!optionalItem.isPresent())
						{
							player.sendMessage(ChatColor.RED + "Invalid item name: " + itemName);
							break;
						}
						
						ItemStack addedItem = optionalItem.get().getItem();
						addedItem.setAmount(amount);
						
						player.getInventory().addItem(addedItem);
						break;
				}
			}
			
			
			//player.openInventory(shop.getBlackSmithArmorShop());
			
			//EnchantShop shop = new EnchantShop();
			//player.openInventory(shop.getEnchantInv());
//			
//			BlackSmithNPC blackSmith = new BlackSmithNPC(player.getLocation());
//			//EnchantNPC librarian = new EnchantNPC(player.getLocation());
//			
//			WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
//			world.addEntity(blackSmith);
//			
//			PacketReader reader = new PacketReader();
//			
//			reader.create(player);
			
			//Entity spawnedEntity = spawnEntity(new BlackSmithNPC(null, null, null), ((Player) sender).getLocation());
			
			/*stand = (ArmorStand) world.spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			
			stand.setInvulnerable(true);
			
			stand.setVisible(false);
			
			ItemDropTicker ticker = new ItemDropTicker();
			ticker.runTaskTimer(Main.getPlugin(Main.class), 0, 20);*/

			return true;
		}
		
		if(label.equalsIgnoreCase("setstate"))
		{
			if(args.length < 1)
			{
				sender.sendMessage(ChatColor.RED + "Please specify a game state! (waiting | countdown | active | end)");
				return true;
			}

			GameManager manager = plugin.getGameManager();
			switch(args[0])
			{
				case "waiting":
					manager.setState(new WaitingArenaState());
					break;
				case "countdown":
					manager.setState(new StartCountdownState());
					break;
				case "active":
					manager.setState(new ActiveArenaState());
					break;
				case "end":
					manager.setState(new EndState());
					break;
				default:
					sender.sendMessage(ChatColor.RED + "Please specify a game state! (waiting | countdown | active | end)");
			}
			return true;
		}
		return false;
	}
	
	private boolean isNum(String num)
	{
		try
		{
			Integer.parseInt(num);
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}

}
