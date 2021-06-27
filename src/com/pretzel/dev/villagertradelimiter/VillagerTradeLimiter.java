package com.pretzel.dev.villagertradelimiter;

import com.pretzel.dev.villagertradelimiter.lib.CommandBase;
import com.pretzel.dev.villagertradelimiter.lib.ConfigUpdater;
import com.pretzel.dev.villagertradelimiter.lib.Metrics;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class VillagerTradeLimiter extends JavaPlugin {
    public static final String PLUGIN_NAME = "VillagerTradeLimiter";
    public static final String PREFIX = ChatColor.GOLD+"["+PLUGIN_NAME+"] ";

    //Settings
    private FileConfiguration cfg;

    //Initial plugin load/unload
    public void onEnable() {
        //Initialize instance variables
        this.cfg = null;

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

    //Loads or reloads config.yml settings
    public void loadSettings() {
        //Load config.yml
        final String mainPath = this.getDataFolder().getPath()+"/";
        final File file = new File(mainPath, "config.yml");
        ConfigUpdater updater = new ConfigUpdater(this.getTextResource("config.yml"), file);
        this.cfg = updater.updateConfig(file, PREFIX);
    }

    private void loadBStats() {
        if(this.cfg.getBoolean("bStats", true)) new Metrics(this, 9829);
    }

    //Registers plugin commands
    private void registerCommands() {
        final String reloaded = Util.replaceColors("&eVillagerTradeLimiter &ahas been reloaded!");
        final CommandBase vtl = new CommandBase("villagertradelimiter", "villagertradelimiter.use", p -> this.help(p));
        vtl.addSub(new CommandBase("reload", "villagertradelimiter.reload", p -> {
            loadSettings();
            p.sendMessage(reloaded);
        }));
        this.getCommand("villagertradelimiter").setExecutor(vtl);
        this.getCommand("villagertradelimiter").setTabCompleter(vtl);
    }

    //Registers plugin listeners
    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    // ------------------------- Commands -------------------------
    private void help(final Player p) {
        if(p != null) {
            if(!p.hasPermission("villagertradelimiter.use") && !p.hasPermission("villagertradelimiter.*")) return;
            p.sendMessage(ChatColor.GREEN+"VillagerTradeLimiter commands:");
            p.sendMessage(ChatColor.AQUA+"/vtl "+ChatColor.WHITE+"- shows this help message");
            Util.sendIfPermitted("villagertradelimiter.reload", ChatColor.AQUA+"/vtl reload "+ChatColor.WHITE+"- reloads config.yml", p);
        } else {
            Util.consoleMsg(ChatColor.GREEN+"VillagerTradeLimiter commands:");
            Util.consoleMsg(ChatColor.AQUA+"/vtl "+ChatColor.WHITE+"- shows this help message");
            Util.consoleMsg(ChatColor.AQUA+"/vtl reload "+ChatColor.WHITE+"- reloads config.yml");
        }
    }

    // ------------------------- Getters -------------------------
    //Returns the settings from config.yml
    public FileConfiguration getCfg() { return this.cfg; }
}
