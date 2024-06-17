package me.cryptidyy.oceanraiders.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Hologram {
	private static String version;

	static {
		String[] versionArray = Bukkit.getServer().getClass().getName().replace('.', ',').split(",");
		if (versionArray.length >= 4) {
			version = versionArray[3];
		} else {
			version = "";
		}
	}

	private static Class<?> getNMSClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + version + "." + className);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("An error occurred while finding NMS class.", ex);
		}
	}

	private static Class<?> getOBCClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + className);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("An error occurred while finding OBC class.", ex);
		}
	}

	private static Object getHandle(Object object)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		return object.getClass().getMethod("getHandle").invoke(object);
	}

	private static Method sendPacket = null;

	private static void sendPacket(Player player, Object packet) {
		try {
			Object handle = getHandle(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			if (sendPacket == null) {
				sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
			}
			sendPacket.invoke(playerConnection, packet);
		} catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException
				| InvocationTargetException ignored) {
		}
	}

	private static class IChatBaseComponent {

		private static final Logger logger = Logger.getLogger(IChatBaseComponent.class.getName());
		private static final Class<?> IChatBaseComponent = getNMSClass("IChatBaseComponent");
		private static Method newIChatBaseComponent = null;

		static {
			try {
				newIChatBaseComponent = IChatBaseComponent.getDeclaredClasses()[0].getMethod("a", String.class);
			} catch (NoSuchMethodException e) {
				logger.log(Level.SEVERE, "An error occurred while initializing IChatBaseComponent.");
			}
		}

		private static Object of(String string) throws InvocationTargetException, IllegalAccessException {
			return newIChatBaseComponent.invoke(null, "{\"text\": \"" + string + "\"}");
		}

	}

	private static final Class<?> CraftWorld = getOBCClass("CraftWorld"), World = getNMSClass("World"),
			EntityArmorStand = getNMSClass("EntityArmorStand"),
			PacketPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving"),
			PacketPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy"),
			PacketPlayOutEntityMetadata = getNMSClass("PacketPlayOutEntityMetadata"),
			PacketPlayOutEntityTeleport = getNMSClass("PacketPlayOutEntityTeleport"), Entity = getNMSClass("Entity"),
			DataWatcher = getNMSClass("DataWatcher"), EntityLiving = getNMSClass("EntityLiving");
	private static Constructor<?> EntityArmorStandConstructor = null, PacketPlayOutSpawnEntityLivingConstructor = null,
			PacketPlayOutEntityDestroyConstructor = null, PacketPlayOutEntityMetadataConstructor = null,
			PacketPlayOutEntityTeleportConstructor = null;
	private static Method setInvisible = null, setCustomNameVisible = null, setCustomName = null, getId = null,
			getDataWatcher = null, setLocation = null;

	static {
		try {
			EntityArmorStandConstructor = EntityArmorStand.getConstructor(World, double.class, double.class,
					double.class);
			PacketPlayOutSpawnEntityLivingConstructor = PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving);
			PacketPlayOutEntityDestroyConstructor = PacketPlayOutEntityDestroy.getConstructor(int[].class);
			PacketPlayOutEntityMetadataConstructor = PacketPlayOutEntityMetadata.getConstructor(int.class, DataWatcher,
					boolean.class);
			PacketPlayOutEntityTeleportConstructor = PacketPlayOutEntityTeleport.getConstructor(Entity);
			setInvisible = EntityArmorStand.getMethod("setInvisible", boolean.class);
			setCustomNameVisible = EntityArmorStand.getMethod("setCustomNameVisible", boolean.class);
			setLocation = Entity.getMethod("setLocation", double.class, double.class, double.class, float.class,
					float.class);
			try {
				setCustomName = EntityArmorStand.getMethod("setCustomName", String.class);
			} catch (NoSuchMethodException x) {
				setCustomName = EntityArmorStand.getMethod("setCustomName", IChatBaseComponent.IChatBaseComponent);
			}
			getId = EntityArmorStand.getMethod("getId");
			getDataWatcher = Entity.getMethod("getDataWatcher");
		} catch (NoSuchMethodException ignored) {
		}
	}

	private Location location;
	private String text;
	private final Object armorStand;
	private final int id;
	private final Object packetPlayOutSpawnEntityLiving;
	private final Object packetPlayOutEntityDestroy;
	private final Set<Player> viewers = new HashSet<>();

	public Hologram(Location location, String text) {
		this.location = location;
		this.text = text;
		try {
			this.armorStand = EntityArmorStandConstructor.newInstance(getHandle(CraftWorld.cast(location.getWorld())),
					location.getX(), location.getY(), location.getZ());
			setInvisible.invoke(armorStand, true);
			setCustomNameVisible.invoke(armorStand, true);
			if (setCustomName.getParameterTypes()[0].equals(String.class)) {
				setCustomName.invoke(armorStand, text);
			} else {
				setCustomName.invoke(armorStand, IChatBaseComponent.of(text));
			}
			this.id = (int) getId.invoke(armorStand);
			this.packetPlayOutSpawnEntityLiving = PacketPlayOutSpawnEntityLivingConstructor.newInstance(armorStand);
			this.packetPlayOutEntityDestroy = PacketPlayOutEntityDestroyConstructor
					.newInstance((Object) new int[] { id });
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException
				| InstantiationException e) {
			throw new RuntimeException("An error occurred while creating the hologram.", e);
		}
	}

	public void display(Player... players) {
		try {
			for (Player player : players) {
				if (viewers.add(player)) {
					sendPacket(player, packetPlayOutSpawnEntityLiving);
					updateMetadata(player);
				}
			}
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
		}
	}

	public void hide(Player... players) {
		for (Player player : players) {
			if (viewers.remove(player)) {
				sendPacket(player, packetPlayOutEntityDestroy);
			}
		}
	}

	public void setLocation(Location location) {
		try {
			setLocation.invoke(armorStand, location.getX(), location.getY(), location.getZ(), location.getYaw(),
					location.getPitch());
			this.location = location;
			updateLocation();
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
		}
	}

	public void setText(String text) {
		try {
			if (setCustomName.getParameterTypes()[0].equals(String.class)) {
				setCustomName.invoke(armorStand, text);
			} else {
				setCustomName.invoke(armorStand, IChatBaseComponent.of(text));
			}
			this.text = text;
			updateMetadata();
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
		}
	}

	public Location getLocation() {
		return location;
	}

	public String getText() {
		return text;
	}

	private void updateMetadata() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Object packet = PacketPlayOutEntityMetadataConstructor.newInstance(id, getDataWatcher.invoke(armorStand), true);
		for (Player player : viewers) {
			sendPacket(player, packet);
		}
	}

	private void updateMetadata(Player player)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		sendPacket(player,
				PacketPlayOutEntityMetadataConstructor.newInstance(id, getDataWatcher.invoke(armorStand), true));
	}

	private void updateLocation() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		for (Player player : viewers) {
			sendPacket(player, PacketPlayOutEntityTeleportConstructor.newInstance(armorStand));
		}
	}
}
