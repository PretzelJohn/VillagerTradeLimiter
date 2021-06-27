package com.pretzel.dev.villagertradelimiter.lib;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUpdater {
    private final String[] cfgDefault;
    private String[] cfgActive;

    public ConfigUpdater(final Reader def, final File active) {
        this.cfgDefault = Util.readFile(def);
        this.cfgActive = this.cfgDefault;
        try {
            this.cfgActive = Util.readFile(new FileReader(active));
        } catch (Exception e) {
            Util.errorMsg(e);
        }
    }

    private String getVersion(String[] cfg) {
        for(String x : cfg)
            if(x.startsWith("#") && x.endsWith("#") && x.contains("Version: "))
                return x.split(": ")[1].replace("#", "").trim();
        return "0";
    }

    public FileConfiguration updateConfig(File file, String prefix) {
        final FileConfiguration cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
        if(this.isUpdated()) return cfg;
        Util.consoleMsg(prefix+"Updating config.yml...");

        String out = "";
        for(int i = 0; i < cfgDefault.length; i++) {
            String line = cfgDefault[i];
            if(line.startsWith("#") || line.replace(" ", "").isEmpty()) {
                if(!line.startsWith("  ")) out += line+"\n";
            } else if(!line.startsWith(" ")) {
                if(line.contains(": ")) {
                    out += matchActive(line.split(": ")[0], line)+"\n";
                } else if(line.contains(":")) {
                    String set = matchActive(line, "");
                    if(set.contains("none")) {
                        out += set+"\n";
                        continue;
                    }
                    out += line+"\n";
                    boolean found = false;
                    for(int j = 0; j < cfgActive.length; j++) {
                        String line2 = cfgActive[j];
                        if(line2.startsWith("  ") && !line2.replace(" ", "").isEmpty()) {
                            out += line2+"\n";
                            found = true;
                        }
                    }
                    if(found == false) {
                        while(i < cfgDefault.length-1) {
                            i++;
                            String line2 = cfgDefault[i];
                            out += line2+"\n";
                            if(!line2.startsWith("  ")) break;
                        }
                    }
                }
            }
        }
        Util.writeFile(file, out+"\n");
        return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
    }

    public boolean isUpdated() {
        return getVersion(cfgActive).contains(getVersion(cfgDefault));
    }

    private String matchActive(String start, String def) {
        for(String x : cfgActive)
            if(x.startsWith(start))
                return x;
        return def;
    }
}