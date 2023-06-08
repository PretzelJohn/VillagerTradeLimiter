package com.pretzel.dev.villagertradelimiter.database;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.bukkit.configuration.ConfigurationSection;

public class MySQL extends Database {
    private String hostname;
    private int port;
    private String database;
    private String username;
    private String password;
    private String encoding;
    private boolean useSsl;

    public MySQL(final VillagerTradeLimiter instance, final ConfigurationSection cfg) {
        super(instance);
        this.load(cfg);
    }

    public void load(final ConfigurationSection cfg) {
        this.hostname = cfg.getString("host", "localhost");
        this.port = cfg.getInt("port", 3306);
        this.database = cfg.getString("database", "sagas_holo");
        this.username = cfg.getString("username", "root");
        this.password = cfg.getString("password", "root");
        this.encoding = cfg.getString("encoding", "utf8");
        this.useSsl = cfg.getBoolean("useSSL", false);
        this.test();
    }

    public boolean isMySQL() { return true; }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://" + hostname + ":" + port + "/" + database
                + "?autoReconnect=true&useSSL=" + useSsl + "&characterEncoding=" + encoding,
            username,
            password
        );
    }
}
