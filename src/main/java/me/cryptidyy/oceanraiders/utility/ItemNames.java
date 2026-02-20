package me.cryptidyy.oceanraiders.utility;

import org.bukkit.inventory.ItemStack;

public class ItemNames {

    public static String getItemName(ItemStack item)
    {
        String materialName = item.getType().name();

        // Format to a more user-friendly string (e.g., "DIAMOND_BLOCK" -> "Diamond Block")
        String userFriendlyName = materialName.replace("_", " ").toLowerCase();
        StringBuilder builder = new StringBuilder(userFriendlyName);
        int i = 0;
        do {
            builder.replace(i, i + 1, builder.substring(i, i + 1).toUpperCase());
            i = builder.indexOf(" ", i) + 1;
        } while (i > 0 && i < builder.length());

        return builder.toString();
    }
}
