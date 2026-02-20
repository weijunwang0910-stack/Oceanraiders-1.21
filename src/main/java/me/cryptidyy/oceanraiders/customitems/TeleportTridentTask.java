package me.cryptidyy.oceanraiders.customitems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TeleportTridentTask extends BukkitRunnable {
	
	private Player source;
	private Player target;
	
	private Main plugin;
	
	private boolean isTargetAlreadyGlowing = false;
	private OceanItemManager itemManager;
	private OceanItem teleportTrident;
	
	private Team targetTeam;
	
	private String oldTargetName;
	private String oldPlayerName;
	
	public TeleportTridentTask(Main plugin, Player player)
	{
		this.plugin = plugin;
		this.source = player;
		
		this.itemManager = plugin.getItemManager();
		targetTeam = plugin.getGameManager().getTargetTeam();
		
		this.teleportTrident = itemManager.getOceanItems()
				.stream().filter(item -> item.getType().equals(OceanItemType.TELEPORT_TRIDENT)).findFirst().get();

	}
	
	private Team oldTeam;
	
	@Override
	public void run() 
	{
		//If player is holding teleport trident
		if(!source.getInventory().getItemInMainHand().getType().equals(teleportTrident.getItem().getType())
			|| !source.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
				.equalsIgnoreCase(teleportTrident.getItem().getItemMeta().getDisplayName()))
		{
			//player stopped holding trident
			if(target != null)
				resetTargetGlow();
			return;
		}

		//Define ray size and ray trace
		double size = 0.5;
		
		if(target != null)
		{
			double currentSize = raysize(target.getLocation().distance(source.getLocation()));
			size = currentSize < 0.5 ? 0.5 : currentSize;
		}
		
		RayTraceResult result = source.getWorld()
				.rayTraceEntities(source.getEyeLocation().add(source.getLocation().getDirection().normalize()
						.multiply(size + 1)), 
						source.getLocation().getDirection(), 50, size);
		
		//Reset target glow
		if(result == null)
		{
			if(target == null) return;
			resetTargetGlow();
			return;
		}
		
		//Aim at target
		if(result.getHitEntity() != null)
		{
			if(!(result.getHitEntity() instanceof Player)) return;
			
			target = (Player) result.getHitEntity();
			if(target.getUniqueId().equals(source.getUniqueId())) return;

			oldTeam = plugin.getGameManager().getBoardManager().findTeam(target);

			//Before target
			if(!targetTeam.getEntries().contains(target.getName()))
			{
				isTargetAlreadyGlowing = target.isGlowing();
				
				oldPlayerName = PlayerManager.toOceanPlayer(target).getDisplayName();
				oldTargetName = ChatColor.translateAlternateColorCodes('&', oldTeam.getDisplayName() 
						+ oldPlayerName);

				targetTeam.addPlayer(target);
			}

			//setPlayerName(oldTargetName, target);
			target.setGlowing(true);
			
			source.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
					new TextComponent(ChatColor.GREEN + "Targeting " + oldTargetName));
			return;
		}
		else
		{
			if(target == null) return;
			resetTargetGlow();
		}
		
	}
	
	private void resetTargetGlow()
	{
		source.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
		
		if(target != null && !isTargetAlreadyGlowing)
		{
			target.setGlowing(false);
		}
		
		if(targetTeam != null && target != null)
		{
			targetTeam.removePlayer(target);
			target = null;
		}
	}
	
	public Player getSource()
	{
		return this.source;
	}
	
	public Player getTarget()
	{
		return this.target;
	}
	
	public Team getTargetTeam()
	{
		return this.targetTeam;
	}
	
	double erf(double x)
	{
	    // constants
	    double a1 =  0.254829592;
	    double a2 = -0.284496736;
	    double a3 =  1.421413741;
	    double a4 = -1.453152027;
	    double a5 =  1.061405429;
	    double p  =  0.3275911;

	    // Save the sign of x
	    int sign = 1;
	    if (x < 0)
	        sign = -1;
	    x = Math.abs(x);

	    // A&S formula 7.1.26
	    double t = 1.0/(1.0 + p*x);
	    double y = 1.0 - (((((a5*t + a4)*t) + a3)*t + a2)*t + a1)*t*Math.exp((-x*x));

	    return sign*y;
	}
	
	double raysize(double x)
	{
		return erf((x / 15) - 1) + 0.7;
	}

}
