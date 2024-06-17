package me.cryptidyy.oceanraiders.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DataConfigUtil {

	public static void saveLocation(Location loc, ConfigurationSection section)
	{
		//section.set("world", loc.getWorld().getName());
		section.set("x", loc.getX());
		section.set("y", loc.getY());
		section.set("z", loc.getZ());
		section.set("yaw", loc.getYaw());
		section.set("pitch", loc.getPitch());
	}

	public static void saveLocationList(List<Location> locations, ConfigurationSection section)
	{
		for(int i = 1; i <= locations.size(); i++)
		{
			Location location = locations.get(i-1);
			saveLocation(location, section.createSection(".dock" + i));
		}
	}

	public static Location readLocation(ConfigurationSection section)
	{
		return new Location(Main.getPlugin(Main.class).getGameManager().getGameWorld(),
				section.getDouble("x"),
				section.getDouble("y"),
				section.getDouble("z"),
				(float) section.getDouble("yaw"),
				(float) section.getDouble("pitch"));
	}

	public static List<Location> readLocationList(ConfigurationSection section)
	{
		List<Location> result = new ArrayList<>();

		for(String locationName : section.getKeys(false))
		{
			result.add(readLocation(section.getConfigurationSection(locationName)));
		}

		return result;
	}

	public static List<String> readLore(ConfigurationSection section, String loreListName)
	{
		List<String> lore = new ArrayList<>();

		for(String line : section.getStringList(loreListName))
		{
			lore.add(ChatUtil.format(line));
		}
		return lore;
	}

	@SafeVarargs
	public static List<String> addLore(List<String> existingLore, List<String>... newLores)
	{
		List<String> copyOfExistingLore = new ArrayList<>(existingLore);

		for(List<String> newLore : newLores)
		{
			for(String line : newLore)
			{
				copyOfExistingLore.add(ChatColor.translateAlternateColorCodes('&', line));
			}

		}

		return copyOfExistingLore;

	}

	public static ItemStack getItemStack(ConfigurationSection section)
	{
		if(toMaterial(section.getString("material")).isPresent())
		{
			return new ItemStack(toMaterial(section.getString("material")).get(), section.getInt("price"));
		}

		return new ItemStack(Material.AIR);

	}

	public static PotionEffect readPotionEffect(ConfigurationSection section)
	{
		String typeString = section.getString("type");
		int duration = section.getInt("duration");
		int amplifier = section.getInt("amplifier");
		boolean ambient = section.getBoolean("ambient");
		boolean particles = section.getBoolean("particles");

		Optional<PotionEffectType> effectType = Arrays.stream(PotionEffectType.values())
				.filter(effect -> effect.getName().equals(typeString))
				.findFirst();

		if(!effectType.isPresent())
		{
			Bukkit.getLogger().log(Level.SEVERE, "Invalid potion effect at " + section.getName() + "!");
			return null;
		}

		return new PotionEffect(effectType.get(), duration, amplifier, ambient, particles);
	}

	private static Optional<Material> toMaterial(String configName)
	{
		return Arrays.stream(Material.values()).filter(mat -> mat.toString().equalsIgnoreCase(configName)).findFirst();
	}
}
