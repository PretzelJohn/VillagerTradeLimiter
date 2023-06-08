package com.pretzel.dev.villagertradelimiter.database;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.bukkit.configuration.ConfigurationSection;

public class SQLite extends Database {

    private Path path;

    public SQLite(final VillagerTradeLimiter instance) {
        super(instance);
        this.load(null);
    }

    public void load(final ConfigurationSection cfg) {
        path = instance.getDataFolder().toPath().resolve("database.db");
        this.test();
    }

    public boolean isMySQL() { return false; }

    @Override
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }

}
