package me.cryptidyy.oceanraiders.sql;

import me.cryptidyy.oceanraiders.DataManager;
import me.cryptidyy.oceanraiders.Main;

@Deprecated
public class ServerSQL {

    private static SQLHelper sqlHelper = Main.getPlugin(Main.class).getSqlHelper();
    private static final DataManager settingsFile = new DataManager(Main.getPlugin(Main.class), "settings.yml");;

    public static void createEntry()
    {
        sqlHelper.createEntry("servers", "NAME", getServerName(settingsFile));
    }

    public static void updateStartedState(String state)
    {
        sqlHelper.updateTable("servers", "NAME", getServerName(settingsFile), "STATE", state);
    }

    public static void updateGameState(String gameState)
    {
        sqlHelper.updateTable("servers", "NAME", getServerName(settingsFile), "GAMESTATE", gameState);
    }

    public static void updateGameStartedState(boolean isStarted)
    {
        sqlHelper.updateTable("servers", "NAME", getServerName(settingsFile), "ISSTARTED", isStarted);
    }

    private static String getServerName(DataManager dataManager)
    {
        return dataManager.getConfig().getString("servername");
    }
}
