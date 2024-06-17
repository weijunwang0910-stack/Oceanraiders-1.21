package me.cryptidyy.oceanraiders.commands;

import me.cryptidyy.oceanraiders.state.EndState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import net.md_5.bungee.api.ChatColor;

public class GameCommands implements CommandExecutor {

	private Main plugin;
	
	private GameManager gameManager;
	
	public GameCommands(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(label.equalsIgnoreCase("oceanraiders"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Only a player can run that command!");
				return true;
			}
			
			Player player = (Player) sender;
			gameManager = plugin.getGameManager();
			
			if(args.length >= 1)
			{
				switch(args[0])
				{
					case "join":
						if(gameManager == null)
							player.sendMessage("gamemanager is null!");

						//gameManager.join(new JoinedPlayer(player.getUniqueId()));
						if(args.length == 2)
						{
							Player target = Bukkit.getPlayer(args[1]);
							if(target != null)
							{
								//gameManager.join(new JoinedPlayer(target.getUniqueId()));
							}

						}
						break;

					case "quit":
						//gameManager.quit(new JoinedPlayer(player.getUniqueId()));
						break;
					case "rejoin":
						//gameManager.rejoin(new JoinedPlayer(player.getUniqueId()));
						break;
					case "end":
						if(!gameManager.isStarted())
						{
							player.sendMessage(ChatColor.RED + "Cannot end the game, did you start it yet?");
							return true;
						}

						gameManager.setState(new EndState());
						break;
						
					case "stop":
						if(!gameManager.isStarted())
						{
							player.sendMessage(ChatColor.RED + "Cannot end the game, did you start it yet?");
							return true;
						}
						
						player.sendMessage(ChatColor.RED + "Stopped the game and plugin!");
						gameManager.setState(new EndState());
						break;
					default:
						player.sendMessage(ChatColor.RED + "Usage: /oceanraiders [join|quit|end]");
						break;
				}
				return true;
			}
			player.sendMessage(ChatColor.RED + "Usage: /oceanraiders [join|quit|end]");
			
		}
		return false;
	}

}
