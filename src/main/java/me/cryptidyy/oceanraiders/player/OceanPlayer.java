package me.cryptidyy.oceanraiders.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.activelisteners.RespawnEvent;
import me.cryptidyy.oceanraiders.npcs.GameNPCSetupManager;
import me.cryptidyy.oceanraiders.islands.IslandManager;
import me.cryptidyy.oceanraiders.shop.EnchantFamily;
import me.cryptidyy.oceanraiders.shop.EnchantManager;
import me.cryptidyy.oceanraiders.shop.ItemEntry;
import me.cryptidyy.oceanraiders.shop.ItemEntryManager;
import me.cryptidyy.oceanraiders.utility.ItemBuilder;

public class OceanPlayer {

	private final UUID playerUUID;
	
	private boolean isIntruding = false;
	
	private boolean hasRedTreasure = false;
	private boolean hasBlueTreasure = false;
	private boolean hasDroppedTreasure = false;
	
	private boolean isSpectating = false;
	private boolean isRespawning = false;
	private boolean isSpawnProtected = false;

	private boolean isBoatLaunched = false;
	private boolean isBoatSpeeding = false;

	private boolean isInvisible = false;

	private boolean isOnDamageCheckCooldown = false;
	
	private BukkitTask respawnTask;
	
	private OceanTeam playerTeam;
	
	private Main plugin = Main.getPlugin(Main.class);
	
	private ItemStack droppedTreasure = null;
	private TransferedItemsManager transferManager;
	
	private final EnchantManager enchantManager;
	
	private final String displayName;
	private IslandManager islandManager = plugin.getIslandManager();
	
	public OceanPlayer(Player player, OceanTeam team)
	{
		this(player.getUniqueId());
		this.playerTeam = team;
	}
	
	public OceanPlayer(UUID uuid)
	{
		this.displayName = Bukkit.getPlayer(uuid).getName();
		this.transferManager = new TransferedItemsManager(plugin);
		
		this.playerUUID = uuid;
		this.enchantManager = plugin.getGameManager().getEnchantManager(Bukkit.getPlayer(playerUUID));
	}

	//called when game starts, teleports player, etc
	public void initPlayer(GameManager manager)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		player.getInventory().clear();
		
		player.getInventory().setItem(8, new ItemBuilder(Material.OAK_BOAT).toItemStack());
		
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20);
		player.setFoodLevel(20);
		
		manager.getHealthBar().setHealthBar(player);
		manager.getShopManager().addPlayer(player);
		
		if(playerTeam.getTeamName().equalsIgnoreCase("Red Team"))
		{
			player.teleport(islandManager.findIsland("Red Island").get().getSpawnLoc());
			
			player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.RED).setInfinityDurability().toItemStack());
			player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.RED).setInfinityDurability().toItemStack());
			player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.RED).setInfinityDurability().toItemStack());
			player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.RED).setInfinityDurability().toItemStack());
		}
		else
		{
			player.teleport(islandManager.findIsland("Blue Island").get().getSpawnLoc());
			
			player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.BLUE).setInfinityDurability().toItemStack());
			player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.BLUE).setInfinityDurability().toItemStack());
			player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.BLUE).setInfinityDurability().toItemStack());
			player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.BLUE).setInfinityDurability().toItemStack());
		}

		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "____________________________________________");
		player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "              Welcome to Oceanraiders!");
		player.sendMessage(ChatColor.YELLOW + "How To Play:");
		player.sendMessage(ChatColor.GRAY + "Get resources from the mines located across the bridge in the village. " +
				"Buy/upgrade items from villagers to raid the other team for their treasure, which is located at the bottom of their mines. " +
				"The first team to place the treasure in the podium across from the docks on their island wins!");
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "____________________________________________");
	}

	public void killPlayer(Player killer, int respawnTimeInSeconds)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		
		Location respawnLocation = player.getLocation().add(0, 50, 0);
		ItemStack[] contents = player.getInventory().getContents();

		if(killer != null)
		{
			for(ItemStack transferedItems : findTransferedItems())
			{
				//give killer the target's items and send message
				killer.getInventory().addItem(transferedItems);
				killer.sendMessage(ChatColor.AQUA + "+" + transferedItems.getAmount()
						+ "x " + transferedItems.getType().toString().toLowerCase());
			}
		}

		isRespawning = true;
		
		player.setGameMode(GameMode.ADVENTURE);
		
		player.setInvulnerable(true);
		player.getInventory().clear();
		player.teleport(respawnLocation);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setHealth(20);
		player.setFoodLevel(20);

		plugin.getGameManager().getPlayingPlayers().forEach(uuid -> {
			Bukkit.getPlayer(uuid).hidePlayer(plugin, player);
		});

		RespawnEvent.addPlayer(player);

		//Respawn the player
		respawnTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
		{
			int seconds = respawnTimeInSeconds;

			@Override
			public void run() {

				if(seconds <= 0)
				{
					respawnTask.cancel();
					player.getInventory().setContents(contents);
					RespawnEvent.removePlayer(player);

					plugin.getGameManager().getPlayingPlayers().forEach(uuid -> {
						Bukkit.getPlayer(uuid).showPlayer(plugin, player);
					});

					respawnPlayer();

					player.sendTitle(ChatColor.GREEN + "Respawned!", "", 0, 20 * 2, 10);

					Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
						player.setFlying(false);
						player.setAllowFlight(false);
						player.setInvulnerable(false);
					}, 5);

					isRespawning = false;
					return;
				}

				player.sendTitle(
						ChatColor.RED + "" + ChatColor.BOLD + "You Died!",
						"Respawning in " + ChatColor.RED + seconds + ChatColor.WHITE + " seconds!", 0, 20 * 2, 10);
				seconds--;
			}

		}, 0, 20 * 1);

	}

	public void respawnPlayer()
	{
		Player player = Bukkit.getPlayer(playerUUID);
		if(player == null) return;
		List<ItemEntry> persistentItems = new ArrayList<>();
		List<ItemEntry> entryInInventory = new ArrayList<>();
		
		List<List<ItemEntry>> allShopEntries = new ArrayList<>(ItemEntryManager.allShopEntries);
		 
		//get all shop items from player inventory
		allShopEntries.forEach(entryList -> {
			Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null && toEntry(item.getType().name(), entryList).isPresent())
			.forEach(entryItem -> {
				entryInInventory.add(toEntry(entryItem.getType().name(), entryList).get());
			});
		});
		
		//get all persistent items
		entryInInventory.stream().filter(entry -> entry.isPersistent())
			.forEach(persistentEntry -> {
			persistentItems.add(persistentEntry);
		});
		
		//clear all items except persistent ones
		Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null)
			.filter(item -> !toEntry(item.getType().name().toLowerCase(), persistentItems).isPresent())
			.forEach(entryItem -> player.getInventory().removeItem(entryItem));
		
		resetEnchants();
		player.setFireTicks(0);
		
		player.getInventory().setItem(8, new ItemBuilder(Material.OAK_BOAT).toItemStack());
		
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20);
		player.setFoodLevel(20);
		this.setSpawnProtected(true);
		player.setInvulnerable(true);
		
		player.setGlowing(false);
		
		if(playerTeam.getTeamName().equalsIgnoreCase("Red Team"))
		{
			if(player.getInventory().getHelmet() == null)
				player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.RED).toItemStack());
			if(player.getInventory().getChestplate() == null)
				player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.RED).toItemStack());
			if(player.getInventory().getLeggings() == null)
				player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.RED).toItemStack());
			if(player.getInventory().getBoots() == null)
				player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.RED).toItemStack());

		}
		else
		{
			if(player.getInventory().getHelmet() == null)
				player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.BLUE).toItemStack());
			if(player.getInventory().getChestplate() == null)
				player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.BLUE).toItemStack());
			if(player.getInventory().getLeggings() == null)
				player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.BLUE).toItemStack());
			if(player.getInventory().getBoots() == null)
				player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.BLUE).toItemStack());

		}
		
		if(playerTeam.getTeamName().equalsIgnoreCase("Red Team"))
		{
			player.teleport(islandManager.findIsland("Red Island").get().getRespawnLoc());
		}
		else
		{
			player.teleport(islandManager.findIsland("Blue Island").get().getRespawnLoc());
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, (bukkitTask) -> {
			this.setSpawnProtected(false);
			player.setInvulnerable(false);
		}, 20 * 8);
	}
	
	public void resetPlayer(GameManager manager)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		
		manager.getTargetTeam().removeEntry(player.getName());
		player.getInventory().clear();
		player.setGlowing(false);
		
		manager.getHealthBar().removeHealthBar(player);
		Arrays.stream(PotionEffectType.values()).forEach(effect -> {
			player.removePotionEffect(effect);
		});

		player.setGameMode(GameMode.ADVENTURE);
		player.setFoodLevel(20);
		player.setHealth(20);
		resetAllEnchants();
	}

	public List<ItemStack> findTransferedItems()
	{
		Player player = Bukkit.getPlayer(playerUUID);
		
		List<ItemStack> transferedItems = new ArrayList<>();
		
		transferManager.getTransferedItems().forEach(transferItemType -> {
			Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null)
			.filter(item -> item.getType().equals(transferItemType))
			.forEach(transferItem -> {
				transferedItems.add(transferItem);
			});
		});
		
		return transferedItems;

	}
	
	public List<ItemEntry> getPersistentItems() 
	{
		Player player = Bukkit.getPlayer(playerUUID);
		
		List<ItemEntry> persistentItems = new ArrayList<>();
		List<ItemEntry> entryInInventory = new ArrayList<>();
		
		List<List<ItemEntry>> allShopEntries = new ArrayList<>(ItemEntryManager.allShopEntries);
		 
		//add all shop items from player inventory
		allShopEntries.forEach(entryList -> {
			Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null && toEntry(item.getType().name(), entryList).isPresent())
			.forEach(entryItem -> {
				entryInInventory.add(toEntry(entryItem.getType().name(), entryList).get());
			});
		});
		
		//get all persistent items
		entryInInventory.stream().filter(entry -> entry.isPersistent())
			.forEach(persistentEntry -> {
			persistentItems.add(persistentEntry);
		});
		
		return persistentItems;
	}
	
	public List<Optional<ItemStack>> persistentItemsInInventory()
	{
		Player player = Bukkit.getPlayer(playerUUID);
		
		List<Optional<ItemStack>> persistentItems = new ArrayList<>();
		List<ItemEntry> entryInInventory = new ArrayList<>();
		
		List<List<ItemEntry>> allShopEntries = new ArrayList<>(ItemEntryManager.allShopEntries);
		 
		//add all shop items from player inventory
		allShopEntries.forEach(entryList -> {
			Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null && toEntry(item.getType().name(), entryList).isPresent())
			.forEach(entryItem -> {
				entryInInventory.add(toEntry(entryItem.getType().name(), entryList).get());
			});
		});
		
		//get all persistent items
		entryInInventory.stream().filter(entry -> entry.isPersistent())
			.forEach(persistentEntry -> {
				
			persistentItems.add(Arrays.stream(player.getInventory().getContents())
					.filter(itemstack -> itemstack != null)
					.filter(itemstack -> itemstack.getType().equals(persistentEntry.getItem().getType()))
					.filter(itemstack -> itemstack.getItemMeta().getDisplayName()
							.equalsIgnoreCase(persistentEntry.getItem().getItemMeta().getDisplayName()))
					.findAny());
		});
		
		return persistentItems;
	}

	public void resetAllEnchants()
	{
		Map<Material, List<EnchantFamily>> itemToFamilies = enchantManager.getItemToFamilies();

		for(Material mat : itemToFamilies.keySet())
		{
			itemToFamilies.get(mat).forEach(family -> {
				family.reset();
			});
		}
	}
	public void resetEnchants()
	{
		Map<Material, List<EnchantFamily>> itemToFamilies = enchantManager.getItemToFamilies();
		for(ItemEntry persistentItems : getPersistentItems())
		{
			//Is part of enchants shop
			if(itemToFamilies.containsKey(persistentItems.getItem().getType()))
			{
				itemToFamilies.get(persistentItems.getItem().getType()).forEach(family -> {
					family.reset();
				});
			}
		}

		for(Optional<ItemStack> optionalPersistentItem : persistentItemsInInventory())
		{
			if(!optionalPersistentItem.isPresent()) continue;
			
			ItemStack persistentItem = optionalPersistentItem.get();

			//Is part of enchants shop
			if(itemToFamilies.containsKey(persistentItem.getType()))
			{
				for(Enchantment enchant : persistentItem.getEnchantments().keySet())
				{
					persistentItem.removeEnchantment(enchant);
				}
				
			}
		}
	}
	
	public EnchantManager getEnchantManager()
	{
		return this.enchantManager;
	}

	public Optional<ItemEntry> toEntry(String name, List<ItemEntry> entries)
	{
		return entries.stream().filter(entry -> entry.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	public Player getPlayer()
	{
		Player player = Bukkit.getPlayer(playerUUID);
		return player;
	}

	public boolean isIntruding() {
		return isIntruding;
	}

	public void setIntruding(boolean isIntruding) {
		this.isIntruding = isIntruding;
	}

	public boolean hasRedTreasure() {
		return hasRedTreasure;
	}

	public void setHasRedTreasure(boolean hasRedTreasure) {
		this.hasRedTreasure = hasRedTreasure;
	}

	public boolean hasBlueTreasure() {
		return hasBlueTreasure;
	}

	public void setHasBlueTreasure(boolean hasBlueTreasure) {
		this.hasBlueTreasure = hasBlueTreasure;
	}

	public boolean hasDroppedTreasure() {
		return hasDroppedTreasure;
	}

	public void setHasDroppedTreasure(boolean hasDroppedTreasure) {
		this.hasDroppedTreasure = hasDroppedTreasure;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemStack getDroppedTreasure() {
		return droppedTreasure;
	}

	public void setDroppedTreasure(ItemStack droppedTreasure) {
		this.droppedTreasure = droppedTreasure;
	}

	public OceanTeam getPlayerTeam() {
		return playerTeam;
	}

	public void setPlayerTeam(OceanTeam playerTeam) {
		this.playerTeam = playerTeam;
	}

	public boolean isSpectating() {
		return isSpectating;
	}

	public void setSpectating(boolean isSpectating) {
		this.isSpectating = isSpectating;
	}

	public boolean isRespawning() {
		return isRespawning;
	}

	public void setRespawning(boolean isRespawning) {
		this.isRespawning = isRespawning;
	}

	public boolean isSpawnProtected() {
		return isSpawnProtected;
	}

	public void setSpawnProtected(boolean isSpawnProtected) {
		this.isSpawnProtected = isSpawnProtected;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public boolean isBoatLaunched()
	{
		return this.isBoatLaunched;
	}

	public void setBoatLaunched(boolean isBoatLaunched)
	{
		this.isBoatLaunched = isBoatLaunched;
	}

	public boolean isBoatSpeeding()
	{
		return this.isBoatSpeeding;
	}

	public void setBoatSpeeding(boolean isBoatSpeeding)
	{
		this.isBoatSpeeding = isBoatSpeeding;
	}

	public boolean isInvisible()
	{
		return this.isInvisible;
	}

	public void setInvisible(boolean isInvisible)
	{
		this.isInvisible = isInvisible;
	}

	public void setOnDamageCheckCooldown(boolean isCooldown)
	{
		this.isOnDamageCheckCooldown = isCooldown;
	}

	public boolean isOnDamageCheckCooldown()
	{
		return this.isOnDamageCheckCooldown;
	}
}
