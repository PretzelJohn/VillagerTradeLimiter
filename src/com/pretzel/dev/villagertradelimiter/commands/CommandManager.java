package com.pretzel.dev.villagertradelimiter.commands;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class CommandManager {
    private final VillagerTradeLimiter instance;
    private final ItemStack barrier;

    /** @param instance The instance of VillagerTradeLimiter.java */
    public CommandManager(final VillagerTradeLimiter instance) {
        this.instance = instance;
        this.barrier = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = barrier.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.RED+"Close");
            meta.setLore(Arrays.asList(ChatColor.GRAY+"Click to close", ChatColor.GRAY+"this inventory."));
        }
        barrier.setItemMeta(meta);
    }

    /** @return The root command node, to be registered by the plugin */
    public CommandBase getCommands() {
        //Adds the /vtl command
        final CommandBase cmd = new CommandBase("villagertradelimiter", "villagertradelimiter.use", (p, args) -> showHelp(p, "help"));

        //Adds the /vtl reload command
        cmd.addSub(new CommandBase("reload", "villagertradelimiter.reload", (p,args) -> {
            //Reload the config and lang
            instance.loadSettings();
            Util.sendMsg(instance.getLang("common.reloaded"), p);
        }));

        //Adds the /vtl see <player> command
        cmd.addSub(new CommandBase("see", "villagertradelimiter.see", (p,args) -> {
            //Check if the command was issued via console
            if(p == null) {
                Util.sendMsg(instance.getLang("common.noconsole"), p);
                return;
            }

            //Checks if there are enough arguments
            if(args.length < 1) {
                Util.sendMsg(instance.getLang("common.noargs"), p);
                return;
            }

            //Get the closest villager. If a nearby villager wasn't found, send the player an error message
            Entity closestEntity = getClosestEntity(p);
            if(closestEntity == null) return;

            //Gets the other player by name, using the first argument of the command
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(args[0]);
            if(!otherPlayer.isOnline() && !otherPlayer.hasPlayedBefore()) {
                Util.sendMsg(instance.getLang("see.noplayer").replace("{player}", args[0]), p);
                return;
            }

            //Open the other player's trade view for the calling player
            Util.sendMsg(instance.getLang("see.success").replace("{player}", args[0]), p);
            instance.getPlayerListener().see((Villager)closestEntity, p, otherPlayer);
        }));

        //Adds the /vtl invsee command
        cmd.addSub(new CommandBase("invsee", "villagertradelimiter.invsee", (p, args) -> {
            //Check if the command was issued via console
            if(p == null) {
                Util.sendMsg(instance.getLang("common.noconsole"), p);
                return;
            }

            //Get the closest villager. If a nearby villager wasn't found, send the player an error message
            Entity closestEntity = getClosestEntity(p);
            if(closestEntity == null) return;

            //Open the villager's inventory view for the calling player
            final Villager closestVillager = (Villager)closestEntity;
            final Inventory inventory = Bukkit.createInventory(null, 9, "Villager Inventory");
            for(ItemStack item : closestVillager.getInventory().getContents()) {
                if(item == null) continue;
                inventory.addItem(item.clone());
            }
            inventory.setItem(8, barrier);
            p.openInventory(inventory);
        }));
        return cmd;
    }

    /**
     * @param player The player to get the closest entity for
     * @return The closest entity to the player, that the player is looking at
     */
    private Entity getClosestEntity(final Player player) {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;
        for(Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if(entity instanceof Villager) {
                Location eye = player.getEyeLocation();
                Vector toEntity = ((Villager) entity).getEyeLocation().toVector().subtract(eye.toVector());
                double dot = toEntity.normalize().dot(eye.getDirection());
                double distance = eye.distance(((Villager)entity).getEyeLocation());
                if(dot > 0.99D && distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }
        if(closestEntity == null) {
            Util.sendMsg(instance.getLang("see.novillager"), player);
        }
        return closestEntity;
    }

    /**
     * Sends an interactive help message to a player via chat
     * @param p The player to show the help message to
     * @param key The key of the help message to show (in messages.yml)
     */
    public void showHelp(final Player p, final String key) {
        for(String line : instance.getLang(key).split("\n")) {
            int i = line.indexOf("]");

            final String[] tokens = line.substring(i+1).split(";");
            if(p == null) Util.consoleMsg(tokens[0]);
            else {
                final TextComponent text = new TextComponent(tokens[0]);
                if(tokens.length > 1) text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(tokens[0]+"\n"+tokens[1])));
                text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(tokens[0])));
                if(p.hasPermission(line.substring(1, i))) p.spigot().sendMessage(text);
            }
        }
    }

    /** @return The barrier item */
    public ItemStack getBarrier() {
        return this.barrier;
    }
}
