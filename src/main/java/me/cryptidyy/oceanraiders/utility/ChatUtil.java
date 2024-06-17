package me.cryptidyy.oceanraiders.utility;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String format(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
