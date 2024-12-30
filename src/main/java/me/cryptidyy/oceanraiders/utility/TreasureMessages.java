package me.cryptidyy.oceanraiders.utility;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.OceanTeam;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TreasureMessages {

    private static final String treasureStolenHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE STOLEN > ";
    private static final String treasureReplacedHeader = ChatColor.WHITE + "" + ChatColor.BOLD + "TREASURE REPLACED > ";
    private static final String treasureSwapHeader = org.bukkit.ChatColor.WHITE + "" + org.bukkit.ChatColor.BOLD + "TREASURE TRANSFERRED > ";

    public static String[] getStolenMessage(String teamName, boolean playSound)
    {
        if(playSound)
        {
            if(teamName.contains("Red"))
            {
                playStolenSound(Main.getPlugin(Main.class).getGameManager().getTeamRed());
            }
            else
            {
                playStolenSound(Main.getPlugin(Main.class).getGameManager().getTeamBlue());
            }
        }

        return new String[]{"", treasureStolenHeader + ChatUtil.format("&c" + teamName + "'s treasure was stolen!"), ""};
    }

    public static String[] getReplacedMessage(String teamName, boolean playSound)
    {
        if(playSound)
        {
            if(teamName.contains("Red"))
            {
                playReplacedSound(Main.getPlugin(Main.class).getGameManager().getTeamRed());
            }
            else
            {
                playReplacedSound(Main.getPlugin(Main.class).getGameManager().getTeamBlue());
            }
        }

        return new String[]{"", treasureReplacedHeader + ChatUtil.format("&b" + teamName + "'s treasure was replaced!"), ""};
    }

    public static String[] getTransactedMessage(String teamName, String playerName)
    {
        return new String[]{"",
                treasureSwapHeader + ChatUtil.format(toChatColorOfPlayer(playerName) + playerName + " &fnow has " + teamName + "'s treasure!"),
                ""};
    }

    private static ChatColor toChatColorOfPlayer(String playerName)
    {
        Player player = Bukkit.getPlayer(playerName);

        ChatColor result = PlayerManager.toOceanPlayer(player).getPlayerTeam().getTeamName().contains("Red") ? ChatColor.RED : ChatColor.BLUE;
        return result;
    }

    private static void playStolenSound(OceanTeam teamStolen)
    {
        OceanTeam otherTeam
                = teamStolen.getTeamName().contains("Red") ? Main.getPlugin(Main.class).getGameManager().getTeamBlue() :
                Main.getPlugin(Main.class).getGameManager().getTeamRed();

        teamStolen.getOnlinePlayers().forEach(id -> {
            Player player = Bukkit.getPlayer(id);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        });

        otherTeam.getOnlinePlayers().forEach(id -> {
            Player player = Bukkit.getPlayer(id);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1f);
        });
    }

    private static void playReplacedSound(OceanTeam teamReplaced)
    {
        OceanTeam otherTeam
                = teamReplaced.getTeamName().contains("Red") ? Main.getPlugin(Main.class).getGameManager().getTeamBlue() :
                Main.getPlugin(Main.class).getGameManager().getTeamRed();

        teamReplaced.getOnlinePlayers().forEach(id -> {
            Player player = Bukkit.getPlayer(id);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        });

        otherTeam.getOnlinePlayers().forEach(id -> {
            Player player = Bukkit.getPlayer(id);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1f);
        });
    }
}
