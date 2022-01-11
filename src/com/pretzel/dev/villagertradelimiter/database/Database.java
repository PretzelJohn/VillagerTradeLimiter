package com.pretzel.dev.villagertradelimiter.database;

import com.pretzel.dev.villagertradelimiter.lib.Callback;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Database {
    protected final JavaPlugin instance;

    public Database(final JavaPlugin instance) {
        this.instance = instance;
    }

    //Tests a DataSource
    public void test() {
        try {
            try (Connection conn = this.getSource().getConnection()) {
                if (!conn.isValid(1000)) throw new SQLException("Could not connect to database!");
                else Util.consoleMsg("Connected to database!");
            }
        } catch (SQLException e) {
            Util.consoleMsg("Could not connect to database!");
        }
    }

    //Executes a statement or query in the database
    public ArrayList<String> execute(final String sql, boolean query) {
        try(Connection conn = this.getSource().getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
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
        Bukkit.getScheduler().runTaskAsynchronously(this.instance, () -> {
            final ArrayList<String> result = execute(sql, query);
            if(callback != null) Bukkit.getScheduler().runTask(this.instance, () -> callback.call(result));
        });
    }

    public abstract void load(final ConfigurationSection cfg);
    public abstract boolean isMySQL();
    protected abstract DataSource getSource();
}