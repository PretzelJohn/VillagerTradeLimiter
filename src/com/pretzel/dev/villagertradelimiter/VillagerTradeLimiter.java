package com.pretzel.dev.villagertradelimiter;

import com.pretzel.dev.villagertradelimiter.commands.CommandManager;
import com.pretzel.dev.villagertradelimiter.commands.CommandBase;
import com.pretzel.dev.villagertradelimiter.data.PlayerData;
import com.pretzel.dev.villagertradelimiter.database.DatabaseManager;
import com.pretzel.dev.villagertradelimiter.listeners.InventoryListener;
import com.pretzel.dev.villagertradelimiter.settings.ConfigUpdater;
import com.pretzel.dev.villagertradelimiter.lib.Metrics;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.listeners.PlayerListener;
import com.pretzel.dev.villagertradelimiter.settings.Lang;
import com.pretzel.dev.villagertradelimiter.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VillagerTradeLimiter extends JavaPlugin {
    public static final String PLUGIN_NAME = "VillagerTradeLimiter";
    public static final String PREFIX = ChatColor.GOLD+"["+PLUGIN_NAME+"] ";
    private static final int BSTATS_ID = 9829;

    //Settings
    private FileConfiguration cfg;
    private Lang lang;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private PlayerListener playerListener;
    private HashMap<UUID, PlayerData> playerData;

    /** Initial plugin load/unload */
    public void onEnable() {
        //Initialize instance variables
        this.cfg = null;
        this.commandManager = new CommandManager(this);
        this.playerData = new HashMap<>();

        //Copy default settings & load settings
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.loadSettings();
        this.loadBStats();

        //Register commands and listeners
        this.registerCommands();
        this.registerListeners();

        //Send enabled message
        Util.consoleMsg(PREFIX+PLUGIN_NAME+" is running!");
    }

    /** Save database on plugin stop, server stop */
    public void onDisable() {
        for(UUID uuid : playerData.keySet()) {
            this.databaseManager.savePlayer(uuid, false);
        }
        this.playerData.clear();
    }

    /** Loads or reloads config.yml and messages.yml */
    public void loadSettings() {
        final String mainPath = this.getDataFolder().getPath()+"/";
        final File file = new File(mainPath, "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", file, Collections.singletonList("Overrides"));
        } catch (IOException e) {
            Util.errorMsg(e);
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.lang = new Lang(this, this.getTextResource("messages.yml"), mainPath);

        //Load/reload database manager
        if(this.databaseManager == null) this.databaseManager = new DatabaseManager(this);
        this.databaseManager.load();
    }

    /** Load and initialize the bStats class with the plugin id */
    private void loadBStats() {
        if(this.cfg.getBoolean("bStats", true)) {
            new Metrics(this, BSTATS_ID);
        }
    }

    /** Registers plugin commands */
    private void registerCommands() {
        final CommandBase cmd = this.commandManager.getCommands();
        this.getCommand("villagertradelimiter").setExecutor(cmd);
        this.getCommand("villagertradelimiter").setTabCompleter(cmd);
    }

    /** Registers plugin listeners */
    private void registerListeners() {
        final Settings settings = new Settings(this);
        this.playerListener = new PlayerListener(this, settings);
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this, settings), this);
    }


    // ------------------------- Getters -------------------------
    /** Returns the settings from config.yml */
    public FileConfiguration getCfg() { return this.cfg; }

    /** Returns a language setting from messages.yml */
    public String getLang(final String path) { return this.lang.get(path); }

    /** Returns this plugin's player listener */
    public PlayerListener getPlayerListener() { return this.playerListener; }

    /** Returns a player's data container */
    public HashMap<UUID, PlayerData> getPlayerData() { return this.playerData; }
}
