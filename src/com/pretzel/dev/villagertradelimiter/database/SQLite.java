package com.pretzel.dev.villagertradelimiter.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;

public class SQLite extends Database {
    private final SQLiteConnectionPoolDataSource source;

    public SQLite(final JavaPlugin instance) {
        super(instance);
        this.source = new SQLiteConnectionPoolDataSource();
        this.load(null);
    }

    public void load(final ConfigurationSection cfg) {
        this.source.setUrl("jdbc:sqlite:"+instance.getDataFolder().getPath()+"/database.db");
        this.test();
    }

    public boolean isMySQL() { return false; }
    public DataSource getSource() { return this.source; }
}
