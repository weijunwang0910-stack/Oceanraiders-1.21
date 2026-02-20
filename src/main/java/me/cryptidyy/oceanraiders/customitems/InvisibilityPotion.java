package me.cryptidyy.oceanraiders.customitems;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.OceanTeam;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class InvisibilityPotion extends OceanItem implements Listener {

    private GameManager manager;

    private BukkitTask hideArmorTask = null;

    private boolean wasTargetGlowing = false;

    public InvisibilityPotion(List<String> lore, int slot)
    {
        super(OceanItemType.INVISIBILITY, lore, slot);
    }

    @Override
    public void useItem(CustomItemUser user, GameManager manager)
    {
        this.manager = manager;

        //hide user from opponents
        hideInvisPlayer(user.getUser());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
    }

    @EventHandler
    public void onHit(EntityDamageEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(((Player) event.getEntity()).getActivePotionEffects()
                .stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.INVISIBILITY)))) return;

        Player target = (Player) event.getEntity();

        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;
        //show player
        showInvisPlayer(target);
        target.removePotionEffect(PotionEffectType.INVISIBILITY);

        target.sendMessage(ChatUtil.format("&cYou took damage and lost your invisibility!"));

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPotionExpire(EntityPotionEffectEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        Player target = (Player) event.getEntity();

        if(!event.getAction().equals(EntityPotionEffectEvent.Action.REMOVED)) return;

        if(!target.getActivePotionEffects().stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.INVISIBILITY))) return;
        if(target.getActivePotionEffects()
                .stream()
                .filter(effect -> effect.getType().equals(PotionEffectType.INVISIBILITY))
                .findFirst()
                .get()
                .getDuration() > 1) return;

        //player ran out of invis
        showInvisPlayer(target);

        target.sendMessage(ChatUtil.format("&cYou are no longer invisible!"));
        HandlerList.unregisterAll(this);
    }

    public void showInvisPlayer(Player target)
    {
        OceanTeam opponentTeam = PlayerManager.toOceanPlayer(target).getPlayerTeam().getTeamName().contains("Red") ?
                manager.getTeamBlue() : manager.getTeamRed();

        hideArmorTask.cancel();

        for(UUID opponentID : opponentTeam.getOnlinePlayers())
        {
            Player opponent = Bukkit.getPlayer(opponentID);
            opponent.showPlayer(manager.getPlugin(), target);
        }

        if(wasTargetGlowing)
        {
            target.setGlowing(true);
        }

        PlayerManager.toOceanPlayer(target).setInvisible(false);

    }

    public void hideInvisPlayer(Player target)
    {
        OceanTeam opponentTeam = PlayerManager.toOceanPlayer(target).getPlayerTeam().getTeamName().contains("Red") ?
                manager.getTeamBlue() : manager.getTeamRed();

        for(UUID opponentID : opponentTeam.getOnlinePlayers())
        {
            Player opponent = Bukkit.getPlayer(opponentID);
            opponent.hidePlayer(manager.getPlugin(), target);
        }

        wasTargetGlowing = target.isGlowing();

        if(target.isGlowing())
        {
            target.setGlowing(false);
        }

        PlayerManager.toOceanPlayer(target).setInvisible(true);
    }
}
