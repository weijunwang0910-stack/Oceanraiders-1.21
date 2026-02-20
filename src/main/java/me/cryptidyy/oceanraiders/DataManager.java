package me.cryptidyy.oceanraiders;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

	private Main plugin;
	private File configFile;
	private String filename;
	private YamlConfiguration dataConfig = null;
	
	public DataManager(Main plugin, String filename)
	{
		if(!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		
		this.plugin = plugin;
		this.filename = filename;
		saveDefaultConfig();
	}
	
	public void reloadConfig()
	{
		if(configFile == null)
			configFile = new File(plugin.getDataFolder(), filename);
		
		dataConfig = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defaultStream = plugin.getResource(filename);
		
		if(defaultStream != null)
		{
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public YamlConfiguration getConfig()
	{
		if(configFile == null)
			reloadConfig();
		
		return dataConfig;	
	}
	
	public void saveConfig()
	{
		if(dataConfig == null || configFile == null)
			return;
		
		try 
		{
			this.getConfig().save(configFile);
		} 
		catch (IOException e) 
		{
			plugin.getLogger().log(Level.SEVERE, "Error saving to " + configFile, e);
		}
	}
	
	public void saveDefaultConfig()
	{
		if(configFile == null)
			configFile = new File(plugin.getDataFolder(), filename);
		
		if(!configFile.exists())
		{
			//plugin.saveResource(filename, false);
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		dataConfig = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defaultStream = plugin.getResource(filename);
		
		if(defaultStream != null)
		{
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}
}
