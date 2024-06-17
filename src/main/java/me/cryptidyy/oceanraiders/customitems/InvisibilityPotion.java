package me.cryptidyy.oceanraiders.customitems;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.player.OceanTeam;
import me.cryptidyy.oceanraiders.player.PlayerManager;
import me.cryptidyy.oceanraiders.state.GameManager;
import me.cryptidyy.oceanraiders.utility.ChatUtil;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.IMaterial;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
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

        PacketPlayOutEntityEquipment showHeadPacket
                = new PacketPlayOutEntityEquipment(
                        target.getEntityId(),
                EnumItemSlot.HEAD,
                CraftItemStack.asNMSCopy(target.getInventory().getHelmet()));
        PacketPlayOutEntityEquipment showChestPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.CHEST,
                CraftItemStack.asNMSCopy(target.getInventory().getChestplate()));
        PacketPlayOutEntityEquipment showLegsPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.LEGS,
                CraftItemStack.asNMSCopy(target.getInventory().getLeggings()));
        PacketPlayOutEntityEquipment showFeetPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.FEET,
                CraftItemStack.asNMSCopy(target.getInventory().getBoots()));

        hideArmorTask.cancel();

        for(UUID opponentID : opponentTeam.getOnlinePlayers())
        {
            Player opponent = Bukkit.getPlayer(opponentID);
            ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(showHeadPacket);
            ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(showChestPacket);
            ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(showLegsPacket);
            ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(showFeetPacket);
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

        PacketPlayOutEntityEquipment hideHeadPacket
                = new PacketPlayOutEntityEquipment(
                        target.getEntityId(),
                EnumItemSlot.HEAD,
                CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment hideChestPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.CHEST,
                CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment hideLegsPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.LEGS,
                CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment hideFeetPacket
                = new PacketPlayOutEntityEquipment(
                target.getEntityId(),
                EnumItemSlot.FEET,
                CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));

        //target.sendMessage("You are hidden!");

        //testing
        hideArmorTask = Bukkit.getScheduler()
            .runTaskTimer(Main.getPlugin(Main.class), () -> {
                for(UUID opponentID : opponentTeam.getOnlinePlayers())
                {
                    Player opponent = Bukkit.getPlayer(opponentID);
                    ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(hideHeadPacket);
                    ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(hideChestPacket);
                    ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(hideLegsPacket);
                    ((CraftPlayer) opponent).getHandle().playerConnection.networkManager.sendPacket(hideFeetPacket);
                }

            }, 0, 1);

        wasTargetGlowing = target.isGlowing();

        if(target.isGlowing())
        {
            target.setGlowing(false);
        }

        PlayerManager.toOceanPlayer(target).setInvisible(true);
    }
}
