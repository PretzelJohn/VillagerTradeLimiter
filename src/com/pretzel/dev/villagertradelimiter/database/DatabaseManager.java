package com.pretzel.dev.villagertradelimiter.database;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.data.Cooldown;
import com.pretzel.dev.villagertradelimiter.data.PlayerData;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Villager;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class DatabaseManager {
    private static final String CREATE_TABLE_COOLDOWN =
            "CREATE TABLE IF NOT EXISTS vtl_cooldown("+
                    "uuid CHAR(36) NOT NULL,"+
                    "item VARCHAR(255) NOT NULL,"+
                    "time TEXT NOT NULL,"+
                    "PRIMARY KEY(uuid, item));";
    private static final String SELECT_ITEMS = "SELECT * FROM vtl_cooldown;";
    private static final String INSERT_ITEM = "INSERT OR IGNORE INTO vtl_cooldown(uuid,item,time) VALUES?;"; //INSERT IGNORE INTO for MySQL
    private static final String DELETE_ITEMS = "DELETE FROM vtl_cooldown WHERE uuid='?';";

    private final VillagerTradeLimiter instance;
    private Database database;

    public DatabaseManager(final VillagerTradeLimiter instance) {
        this.instance = instance;
    }

    public void load() {
        final ConfigurationSection cfg = instance.getCfg().getConfigurationSection("database");
        if(cfg == null) {
            Util.consoleMsg("Database settings missing from config.yml!");
            this.database = null;
            return;
        }
        boolean mysql = cfg.getBoolean("mysql", false);
        if(this.database != null && ((mysql && this.database.isMySQL()) || (!mysql && !this.database.isMySQL()))) this.database.load(cfg);
        else this.database = (mysql?new MySQL(instance, cfg):new SQLite(instance));
        this.database.execute(CREATE_TABLE_COOLDOWN, false);

        //Loads all the data
        this.database.execute(SELECT_ITEMS, true, (result,args) -> {
            if(result != null) {
                for(String row : result) {
                    final String[] tokens = row.split(",");

                    UUID uuid = UUID.fromString(tokens[0]);
                    String item = tokens[1];
                    final Date date = Cooldown.parseTime(tokens[2]);
                    if(date == null) continue;
                    long time = date.getTime();

                    PlayerData data = instance.getPlayerData().get(uuid);
                    if(data == null) {
                        data = new PlayerData();
                        instance.getPlayerData().put(uuid, data);
                    }

                    String key = (Bukkit.getEntity(uuid) instanceof Villager ? "Restock" : "Cooldown");
                    String cooldownStr = instance.getCfg().getString(key, "0");
                    cooldownStr = instance.getCfg().getString("Overrides."+item+"."+key, cooldownStr);
                    long cooldown = Cooldown.parseCooldown(cooldownStr);

                    final Date now = Date.from(Instant.now());
                    if(cooldown != 0 && now.getTime()/1000L < time/1000L + cooldown) {
                        data.getTradingCooldowns().put(item, Cooldown.formatTime(date));
                    }
                }
            }
        });
    }

    public void savePlayer(final UUID uuid, boolean async) {
        if(this.database == null) return;

        //Delete existing rows for player
        final String uuidStr = uuid.toString();
        if(async) this.database.execute(DELETE_ITEMS.replace("?", uuidStr), false, (result,args) -> save(uuid, true));
        else {
            this.database.execute(DELETE_ITEMS.replace("?", uuidStr), false);
            save(uuid, false);
        }
    }
    private void save(final UUID uuid, boolean async) {
        //Insert new rows for player pages
        final PlayerData playerData = instance.getPlayerData().get(uuid);
        if(playerData == null) return;

        String values = "";
        for(String item : playerData.getTradingCooldowns().keySet()) {
            final String time = playerData.getTradingCooldowns().get(item);

            if(!values.isEmpty()) values += ",";
            values += "('"+uuid+"','"+item+"','"+time+"')";
        }
        if(values.isEmpty()) return;
        String sql = INSERT_ITEM.replace("?", values);
        if(this.database.isMySQL()) sql = sql.replace(" OR ", " ");
        if(async) this.database.execute(sql, false, (result,args) -> {});
        else this.database.execute(sql, false);
    }
}

