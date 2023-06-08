package com.pretzel.dev.villagertradelimiter.database;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.lib.Callback;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.configuration.ConfigurationSection;

public abstract class Database {
    protected final VillagerTradeLimiter instance;

    public Database(final VillagerTradeLimiter instance) {
        this.instance = instance;
    }

    //Tests a DataSource
    public void test() {
        try {
            try (Connection conn = this.getConnection()) {
                if (!conn.isValid(1000)) throw new SQLException("Could not connect to database!");
                else Util.consoleMsg("Connected to database!");
            }
        } catch (SQLException e) {
            Util.consoleMsg("Could not connect to database!");
        }
    }

    //Executes a statement or query in the database
    public ArrayList<String> execute(final String sql, boolean query) {
        try(Connection conn = this.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            if(query) {
                final ResultSet result = statement.executeQuery();
                int columns = result.getMetaData().getColumnCount();
                final ArrayList<String> res = new ArrayList<>();
                while(result.next()){
                    String row = "";
                    for(int j = 0; j < columns; j++)
                        row += result.getString(j+1)+(j < columns-1?",":"");
                    res.add(row);
                }
                return res;
            } else statement.execute();
        } catch (SQLException e) {
            Util.errorMsg(e);
        }
        return null;
    }
    public void execute(final String sql, boolean query, final Callback<ArrayList<String>> callback) {
        instance.getScheduler().runAsync(() -> {
            final ArrayList<String> result = execute(sql, query);
            if(callback != null) instance.getScheduler().runAsync(() -> callback.call(result));
        });
    }

    public abstract void load(final ConfigurationSection cfg);
    public abstract boolean isMySQL();
    protected abstract Connection getConnection() throws SQLException;
}