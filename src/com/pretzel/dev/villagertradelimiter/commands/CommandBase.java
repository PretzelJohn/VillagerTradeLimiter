package com.pretzel.dev.villagertradelimiter.commands;

import com.pretzel.dev.villagertradelimiter.lib.Callback;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandBase implements CommandExecutor, TabCompleter {
    private final String name;
    private final String permission;
    private final Callback<Player> callback;
    private final ArrayList<CommandBase> subs;

    /**
     * @param name The name of the command
     * @param permission The permission required to use the command
     * @param callback The callback that is called when the command is executed
     */
    public CommandBase(String name, String permission, Callback<Player> callback) {
        this.name = name;
        this.permission = permission;
        this.callback = callback;
        this.subs = new ArrayList<>();
    }

    /**
     * @param command The child command to add
     * @return The given child command
     */
    public CommandBase addSub(CommandBase command) {
        this.subs.add(command);
        return command;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final Player player = (sender instanceof Player ? (Player)sender : null);
        if(player != null && !player.hasPermission(this.permission) && !this.permission.isEmpty()) return false;

        if(args.length == 0 || (args.length == 1 && subs.size() == 0)) {
            this.callback.call(player, args);
            return true;
        }

        final String[] args2 = getCopy(args);
        for(CommandBase cmd : subs)
            if(cmd.getName().equalsIgnoreCase(args[0]) && cmd.onCommand(sender, command, alias, args2))
                return true;

        sender.sendMessage(Util.replaceColors("&fUnknown command. Type \"/help\" for help."));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final Player player = (sender instanceof Player ? (Player)sender : null);
        if(player == null) return null;

        final List<String> list = new ArrayList<>();
        if(args.length == 0) return null;
        if(args.length == 1) {
            if(subs.size() == 0) return getPlayerList();
            for(CommandBase cmd : subs)
                if(player.hasPermission(cmd.getPermission()))
                    list.add(cmd.getName());
        } else {
            final String[] args2 = getCopy(args);
            for(CommandBase cmd : subs) {
                List<String> list2 = cmd.onTabComplete(sender, command, alias, args2);
                if(list2 != null) list.addAll(list2);
            }
        }
        return list;
    }

    /**
     * @param args The arguments to be copied
     * @return The copied arguments
     */
    private static String[] getCopy(final String[] args) {
        String[] res = new String[args.length-1];
        System.arraycopy(args, 1, res, 0, res.length);
        return res;
    }

    /** @return The current online player list */
    private static List<String> getPlayerList() {
        final List<String> players = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers())
            players.add(p.getName());
        return players;
    }

    /** @return The name of this command */
    public String getName() { return this.name; }

    /** @return The permission required to use this command */
    public String getPermission() { return this.permission; }
}