package com.pretzel.dev.villagertradelimiter.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import net.md_5.bungee.api.ChatColor;

public class Util {
    //Sends a message to the sender of a command
    public static void sendMsg(String msg, Player p) {
        if(p == null) consoleMsg(msg);
        else p.sendMessage(msg);
    }

    //Sends a message to a player if they have permission
    public static void sendIfPermitted(String perm, String msg, Player player) {
        if(player.hasPermission(perm)) player.sendMessage(msg);
    }

    //Sends a message to the console
    public static void consoleMsg(String msg) {
        if(msg != null) Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public static String replaceColors(String in) {
        if(in == null) return null;
        return in.replace("&", "\u00A7");
    }

    //Sends an error message to the console
    public static void errorMsg(Exception e) {
        String error = e.toString();
        for(StackTraceElement x : e.getStackTrace()) {
            error += "\n\t"+x.toString();
        }
        consoleMsg(ChatColor.RED+"ERROR: "+error);
    }

    //Returns whether a player is a Citizens NPC or not
    public static boolean isNPC(Player player) {
        return player.hasMetadata("NPC");
    }

    //Returns whether a villager is a Citizens NPC or not
    public static boolean isNPC(Villager villager) {
        return villager.hasMetadata("NPC");
    }

    //Converts an int array to a string
    public static String intArrayToString(int[] arr) {
        String res = "";
        for(int a : arr) { res += a+""; }
        return res;
    }

    public static String[] readFile(Reader reader) {
        String out = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null)
                out += line+"\n";
            br.close();
            return out.split("\n");
        } catch(Exception e) {
            errorMsg(e);
        }
        return null;
    }
    public static String[] readFile(File file) {
        try {
            return readFile(new FileReader(file));
        } catch(Exception e) {
            errorMsg(e);
            return null;
        }
    }

    public static void writeFile(File file, String out) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(out);
            bw.close();
        } catch(Exception e) {
            errorMsg(e);
        }
    }
}
