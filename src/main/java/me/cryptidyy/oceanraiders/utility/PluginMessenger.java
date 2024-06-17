package me.cryptidyy.oceanraiders.utility;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PluginMessenger {

    public static final String gameName = "oceanraiders";

    public static final DataManager dataManager = new DataManager(Main.getPlugin(Main.class), "settings.yml");

    public static void sendPlayerCountUpdateMessage(int newPlayerCount)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LobbyPlayerCountUpdateChannel");
        out.writeUTF(gameName);
        out.writeUTF(getServerName(dataManager));
        out.writeInt(newPlayerCount);

        Bukkit.broadcastMessage("Sending player count update!");
        Bukkit.getServer().sendPluginMessage(Main.getPlugin(Main.class), "my:channel", out.toByteArray());
    }

    public static void sendGameStartedStateUpdateMessage(boolean newState)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GameStartedStateUpdateChannel");
        out.writeUTF(gameName);
        out.writeUTF(getServerName(dataManager));
        out.writeBoolean(newState);

        Bukkit.getServer().sendPluginMessage(Main.getPlugin(Main.class), "my:channel", out.toByteArray());
    }

    public static void sendServerJoinableStateMessage(boolean canJoin)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GameJoinableStateUpdateChannel");
        out.writeUTF(gameName);
        out.writeUTF(getServerName(dataManager));
        out.writeBoolean(canJoin);

        Bukkit.getServer().sendPluginMessage(Main.getPlugin(Main.class), "my:channel", out.toByteArray());
    }

    public static void requestGameDisconnect(UUID playerToDisconnect, String lobbyName)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GameDisconnectRequestChannel");
        out.writeUTF(gameName);
        out.writeUTF(getServerName(dataManager));
        out.writeUTF(playerToDisconnect.toString());
        out.writeUTF(lobbyName);

        Bukkit.getPlayer(playerToDisconnect).sendPluginMessage(Main.getPlugin(Main.class), "my:channel", out.toByteArray());
    }

    public static void requestGameSwitch(UUID playerToDisconnect)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GameSwitchRequestChannel");
        out.writeUTF(gameName);
        out.writeUTF(getServerName(dataManager));
        out.writeUTF(playerToDisconnect.toString());

        Bukkit.getServer().sendPluginMessage(Main.getPlugin(Main.class), "my:channel", out.toByteArray());
    }

    private static String getServerName(DataManager dataManager)
    {
        return dataManager.getConfig().getString("servername");
    }
}
