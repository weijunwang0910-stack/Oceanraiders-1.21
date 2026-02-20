package me.cryptidyy.oceanraiders.scoreboard;

import me.cryptidyy.oceanraiders.Main;
import me.cryptidyy.oceanraiders.state.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameScoreboardManager {

    private Team redTeam;
    private Team blueTeam;
    private BukkitTask updateTask;
    private Main plugin;
    private List<UUID> viewers = new ArrayList<>();
    private GameManager manager;
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private Scoreboard board;

    private Objective objective;
    private Team line2;
    private Team line4;
    private Team line5;

    public GameScoreboardManager(Main plugin, List<UUID> players)
    {
        this.viewers = players;
        this.plugin = plugin;
        this.manager = plugin.getGameManager();
        this.board = scoreboardManager.getNewScoreboard();
        objective = board.registerNewObjective("gameinfo", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Oceanraiders");
        Score line1 = objective.getScore(ChatColor.BLUE.toString());
        line1.setScore(7);
        Score line3 = objective.getScore(ChatColor.BOLD.toString());
        line3.setScore(5);
        Score line6 = objective.getScore(ChatColor.DARK_AQUA.toString());
        line6.setScore(2);
        Score line7 = objective.getScore(ChatColor.YELLOW + "Thanks for playing!");
        line7.setScore(1);
        line4 = board.registerNewTeam("line4");
        line4.addEntry(ChatColor.RED.toString());
        line5 = board.registerNewTeam("line5");
        line5.addEntry(ChatColor.AQUA.toString());
        line2 = board.registerNewTeam("line2");
        line2.addEntry(ChatColor.BLACK.toString());
        line2.setPrefix(ChatColor.GRAY + "Time elapsed: ");
    }

    public void makeTeams(List<UUID> red, List<UUID> blue)
    {
        redTeam = board.registerNewTeam("redteam");
        blueTeam = board.registerNewTeam("blueteam");

        redTeam.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "Red ");
        blueTeam.setPrefix(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Blue ");
        //redTeam = board.createTeam("Red", "&c&lR ", ChatColor.RED);
        //blueTeam = board.createTeam("Blue", "&1&lB ", ChatColor.DARK_BLUE);
        red.forEach(uuid -> redTeam.addEntry(Bukkit.getOfflinePlayer(uuid).getName()));
        blue.forEach(uuid -> blueTeam.addEntry(Bukkit.getOfflinePlayer(uuid).getName()));

    }

    public void addPlayerToTeamRed(Player player)
    {
        redTeam.addEntry(player.getName());
    }

    public void addPlayerToTeamBlue(Player player)
    {
        blueTeam.addEntry(player.getName());
    }

    public void removePlayerFromAllTeams(Player player)
    {
        redTeam.removeEntry(player.getName());
        blueTeam.removeEntry(player.getName());
    }

    public void show()
    {
        ScoreboardTimer timer = new ScoreboardTimer(3600, true);
        timer.count();
        startUpdateTask(timer);
    }

    public void startUpdateTask(ScoreboardTimer timer)
    {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                String redTreasureLine = ChatColor.RED + "Red Treasure: " + ChatColor.GREEN + "INTACT";
                String blueTreasureLine = ChatColor.BLUE + "Blue Treasure: " + ChatColor.GREEN + "INTACT";
                if(manager.getTeamRed().isTreasureStolen())
                {
                    redTreasureLine = ChatColor.RED + "Red Treasure: " + ChatColor.RED + "STOLEN";
                }
                if(manager.getTeamBlue().isTreasureStolen())
                {
                    blueTreasureLine = ChatColor.BLUE + "Blue Treasure: " + ChatColor.RED + "STOLEN";
                }

                line4.setPrefix(redTreasureLine);
                objective.getScore(ChatColor.RED.toString()).setScore(4);

                line5.setPrefix(blueTreasureLine);
                objective.getScore(ChatColor.AQUA.toString()).setScore(3);

                line2.setSuffix(ChatColor.GRAY+ timer.getCurrentTimeLabel());
                objective.getScore(ChatColor.BLACK.toString()).setScore(6);

                updateScoreboard();
            }

        }, 0, 5);
    }
    private void updateScoreboard()
    {
        this.viewers.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
                player.setScoreboard(board);
        });
    }
    public Scoreboard getScoreboard()
    {
        return board;
    }
    public Team findTeam(Player player)
    {
        return redTeam.getEntries().contains(player.getName()) ? redTeam
                : blueTeam.getEntries().contains(player.getName()) ? blueTeam : null;
    }

    public List<UUID> getViewers()
    {
        return this.viewers;
    }

    public void cancelUpdateTask()
    {
        if(!updateTask.isCancelled())
            updateTask.cancel();
    }
    public void addPlayer(Player player)
    {
        player.setScoreboard(board);
    }
    public void removePlayer(Player player)
    {
        player.setScoreboard(scoreboardManager.getNewScoreboard());
    }
}
