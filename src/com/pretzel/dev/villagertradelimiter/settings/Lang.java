package com.pretzel.dev.villagertradelimiter.settings;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class Lang {
    private final FileConfiguration def;
    private FileConfiguration cfg;

    /**
     * @param plugin The Bukkit/Spigot/Paper plugin instance
     * @param reader The file reader for the default messages.yml file (located in the src/main/resources)
     * @param path The file path for the active messages.yml file (located on the server in plugins/[plugin name])
     */
    public Lang(final Plugin plugin, final Reader reader, final String path) {
        //Gets the default values, puts them in a temp file, and loads them as a FileConfiguration
        String[] defLines = Util.readFile(reader);
        String def = "";
        if(defLines == null) defLines = new String[0];
        for(String line : defLines) def += line+"\n";
        final File defFile = new File(path, "temp.yml");
        Util.writeFile(defFile, def);
        this.def = YamlConfiguration.loadConfiguration(defFile);

        //Gets the active values and loads them as a FileConfiguration
        File file = new File(path,"messages.yml");
        try {
            if(file.createNewFile()) Util.writeFile(file, def);
        } catch (Exception e) {
            Util.errorMsg(e);
        }

        this.cfg = null;
        try {
            ConfigUpdater.update(plugin, "messages.yml", file);
        } catch (IOException e) {
            Util.errorMsg(e);
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
        defFile.delete();
    }

    /**
     * @param key The key (or path) of the section in messages.yml (e.g, common.reloaded)
     * @return The String value in messages.yml that is mapped to the given key
     */
    public String get(final String key) {
        return get(key, def.getString("help", ""));
    }

    /**
     * @param key The key (or path) of the section in messages.yml (e.g, common.reloaded)
     * @param def The default value to return if the key is not found
     * @return The String value in messages.yml that is mapped to the given key, or the given default value if the key was not found
     */
    public String get(final String key, final String def) {
        return Util.replaceColors(this.cfg.getString(key, def));
    }
}