package com.pretzel.dev.villagertradelimiter;

import com.pretzel.dev.villagertradelimiter.commands.CommandManager;
import com.pretzel.dev.villagertradelimiter.commands.CommandBase;
import com.pretzel.dev.villagertradelimiter.settings.ConfigUpdater;
import com.pretzel.dev.villagertradelimiter.lib.Metrics;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.listeners.PlayerListener;
import com.pretzel.dev.villagertradelimiter.settings.Lang;
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
    private PlayerListener playerListener;

    //Initial plugin load/unload
    public void onEnable() {
        //Initialize instance variables
        this.cfg = null;
        this.commandManager = new CommandManager(this);

        //Copy default settings & load settings
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.loadSettings();
        this.loadBStats();

        //Register commands and listeners
        this.playerListener = new PlayerListener(this);
        this.registerCommands();
        this.registerListeners();

        //Send enabled message
        Util.consoleMsg(PREFIX+PLUGIN_NAME+" is running!");
    }

    //Loads or reloads config.yml and messages.yml
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
    }

    //Load and initialize the bStats class with the plugin id
    private void loadBStats() {
        if(this.cfg.getBoolean("bStats", true)) {
            new Metrics(this, BSTATS_ID);
        }
    }

    //Registers plugin commands
    private void registerCommands() {
        final CommandBase cmd = this.commandManager.getCommands();
        this.getCommand("villagertradelimiter").setExecutor(cmd);
        this.getCommand("villagertradelimiter").setTabCompleter(cmd);
    }

    //Registers plugin listeners
    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
    }


    // ------------------------- Getters -------------------------
    //Returns the settings from config.yml
    public FileConfiguration getCfg() { return this.cfg; }

    //Returns a language setting from messages.yml
    public String getLang(final String path) { return this.lang.get(path); }

    //Returns this plugin's player listener
    public PlayerListener getPlayerListener() { return this.playerListener; }
}
