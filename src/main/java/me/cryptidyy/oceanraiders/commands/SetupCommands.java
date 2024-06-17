package me.cryptidyy.oceanraiders.commands;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.islands.Island;
import me.cryptidyy.oceanraiders.islands.TemporaryIsland;

public class SetupCommands implements CommandExecutor {
	
	private Main plugin;
	
	public SetupCommands(Main plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(label.equalsIgnoreCase("orsetup"))
		{
			if(!(sender instanceof Player)) return true;
			
			Player player = (Player) sender;

			if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("setup"))
				{
					//switch to edit mode

					String islandName = islandNameFromArgs(args);
					
					if(islandName.isEmpty())
					{
						player.sendMessage(ChatColor.RED + "Island name can't be empty");
						return true;
					}
					 
					Optional<Island> optionalIsland = plugin.getIslandManager().findIsland(islandName);
					TemporaryIsland tempIsland = optionalIsland
							.map(TemporaryIsland::new)
							.orElseGet(() -> new TemporaryIsland(islandName));
					
					plugin.getIslandManager().getIslandSetupManager().addToSetup(player, tempIsland);
					return true;
					
				}
				
				else if(args[0].equalsIgnoreCase("delete"))
				{
					//Delete island
					String islandName = islandNameFromArgs(args);
					Optional<Island> optionalIsland = plugin.getIslandManager().findIsland(islandName);
					if(!optionalIsland.isPresent())
					{
						player.sendMessage(ChatColor.RED+ "That island doesn't exist!");
						return true;
					}
					
					plugin.getIslandManager().deleteIsland(optionalIsland.get());
					player.sendMessage("Deleted " + islandName + " from the config!");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("list"))
				{
					//list islands
					if(plugin.getIslandManager().getIslands().size() == 0)
					{
						player.sendMessage(ChatColor.RED+"No islands have been set up!");
						return true;
					}
					
					plugin.getIslandManager().getIslands().forEach(island -> {
						player.sendMessage(island.getDisplayName());
					});
					
					return true;
				}
				
				else
				{
					player.sendMessage(ChatColor.RED+"Usage: /orsetup [setup|delete|list]");
					return true;
				}
				//Spawn location
				//Drop location
			}
			
			player.sendMessage(ChatColor.RED+"Usage: /orsetup [setup|delete|list]");
		}
		
		return false;
	}
	
	private String islandNameFromArgs(String[] args)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
					
			if(i == 0) continue;
			
			stringBuilder.append(arg);
			
			if(i != args.length - 1)
			{
				stringBuilder.append(" ");
			}
				
		}
		return stringBuilder.toString();
	}
}








