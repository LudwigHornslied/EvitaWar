package com.tistory.hornslied.evitaonline.war;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.tistory.hornslied.evitaonline.commands.WarCommand;
import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.listeners.ConquestListener;
import com.tistory.hornslied.evitaonline.listeners.KoTHListener;
import com.tistory.hornslied.evitaonline.listeners.RaidListener;
import com.tistory.hornslied.evitaonline.listeners.SiegeListener;
import com.tistory.hornslied.evitaonline.listeners.TotalWarListener;
import com.tistory.hornslied.evitaonline.listeners.TownyListener;
import com.tistory.hornslied.evitaonline.war.timer.WarTimerHandler;

public class EvitaWarMain extends JavaPlugin {
	private static EvitaWarMain instance;
	
	private FileConfiguration config;
	private FileConfiguration confronts;
	private FileConfiguration conquests;

	public static EvitaWarMain getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		loadConfig();
		createTables();
		WarManager.getInstance();
		WarTimerHandler.getInstance();
		
		registerEvents();
		initCommands();
	}
	
	private void loadConfig() {
		if (!(new File(getDataFolder(), "config.yml").exists()))
			saveDefaultConfig();
		config = getConfig();
		
		if (!(new File(getDataFolder(), "confronts.yml").exists()))
			saveResource("confronts.yml", false);
		confronts = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "confronts.yml"));
		
		if (!(new File(getDataFolder(), "conquests.yml").exists()))
			saveResource("conquests.yml", false);
		conquests = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "conquests.yml"));
	}
	
	private void createTables() {
		DB db = EvitaCoreMain.getInstance().getDB();
		
		db.query("CREATE TABLE IF NOT EXISTS sidgelogs (" 
				+ "id int NOT NULL PRIMARY KEY AUTO_INCREMENT,"
				+ "date varchar(255) NOT NULL,"
				+ "attacker varchar(255) NOT NULL,"
				+ "defender varchar(255) NOT NULL,"
				+ "iswin tinyint(1) NOT NULL DEFAULT 0"
				+ ");");
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new RaidListener(), this);
		pm.registerEvents(new SiegeListener(), this);
		pm.registerEvents(new ConquestListener(), this);
		pm.registerEvents(new KoTHListener(), this);
		pm.registerEvents(new TotalWarListener(), this);
		pm.registerEvents(new TownyListener(), this);
	}
	
	private void initCommands() {
		WarCommand warCommand = new WarCommand();
		
		getCommand("totalwar").setExecutor(warCommand);
		getCommand("raid").setExecutor(warCommand);
		getCommand("koth").setExecutor(warCommand);
		getCommand("conquest").setExecutor(warCommand);
	}
	
	public FileConfiguration getConfiguration() {
		return config;
	}
	
	public FileConfiguration getConfronts() {
		return confronts;
	}
	
	public FileConfiguration getConquest() {
		return conquests;
	}
}
