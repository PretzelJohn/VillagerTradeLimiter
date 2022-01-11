package com.pretzel.dev.villagertradelimiter.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MySQL extends Database {
    private final MysqlConnectionPoolDataSource source;

    public MySQL(final JavaPlugin instance, final ConfigurationSection cfg) {
        super(instance);
        this.source = new MysqlConnectionPoolDataSource();
        this.load(cfg);
    }

    public void load(final ConfigurationSection cfg) {
        this.source.setServerName(cfg.getString("host", "localhost"));
        this.source.setPort(cfg.getInt("port", 3306));
        this.source.setDatabaseName(cfg.getString("database", "sagas_holo"));
        this.source.setUser(cfg.getString("username", "root"));
        this.source.setPassword(cfg.getString("password", "root"));
        try {
            this.source.setCharacterEncoding(cfg.getString("encoding", "utf8"));
            this.source.setUseSSL(cfg.getBoolean("useSSL", false));
        } catch (SQLException e) {}

        this.test();
    }

    public boolean isMySQL() { return true; }
    public DataSource getSource() { return this.source; }
}
