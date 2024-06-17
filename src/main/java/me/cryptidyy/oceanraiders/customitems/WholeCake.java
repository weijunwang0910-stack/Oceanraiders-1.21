package me.cryptidyy.oceanraiders.customitems;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class WholeCake extends OceanItem {

    public WholeCake(List<String> lore, int slot)
    {
        super(OceanItemType.WHOLE_CAKE, lore, slot);
    }

    @Override
    public void useItem(CustomItemUser user, GameManager manager)
    {
        Player player = user.getUser();
        if(player.getFoodLevel() >= 20) return;

        user.getItemInMainHand().setAmount(0);
        player.setFoodLevel(player.getFoodLevel() + 14);

        player.setSaturation(player.getSaturation() + 2.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_WOOL_BREAK, 1f ,1f);
    }
}
