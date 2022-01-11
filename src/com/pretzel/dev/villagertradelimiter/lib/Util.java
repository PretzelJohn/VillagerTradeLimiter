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
    /**
     * Sends a message to the sender of a command
     * @param msg The message to send
     * @param player The player (or console) to sent the message to
     */
    public static void sendMsg(String msg, Player player) {
        if(player == null) consoleMsg(msg);
        else player.sendMessage(msg);
    }

    //Sends a message to a player if they have permission
    /**
     * Sends a message to a player if they have permission
     * @param perm The name of the permission to check
     * @param msg The message to send
     * @param player The player (or console) to sent the message to
     */
    public static void sendIfPermitted(String perm, String msg, Player player) {
        if(player.hasPermission(perm)) player.sendMessage(msg);
    }

    /**
     * Sends a message to the console
     * @param msg The message to send
     */
    public static void consoleMsg(String msg) {
        if(msg != null) Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    /**
     * Replaces the color tags to bukkit color tags
     * @param in The string to replace color tags for
     * @return The string with replaced colors
     */
    public static String replaceColors(String in) {
        if(in == null) return null;
        return in.replace("&", "\u00A7");
    }

    /**
     * Sends an error message to the console
     * @param e The error to send a message for
     */
    public static void errorMsg(Exception e) {
        String error = e.toString();
        for(StackTraceElement x : e.getStackTrace()) {
            error += "\n\t"+x.toString();
        }
        consoleMsg(ChatColor.RED+"ERROR: "+error);
    }

    /**
     * Checks whether a player is a Citizens NPC or not
     * @param player The player to check
     * @return True if the player is an NPC, false otherwise
     */
    public static boolean isNPC(Player player) {
        return player.hasMetadata("NPC");
    }

    /**
     * Returns whether a villager is a Citizens NPC or not
     * @param villager The villager to check
     * @return True if the villager is an NPC, false otherwise
     */
    public static boolean isNPC(Villager villager) {
        return villager.hasMetadata("NPC");
    }

    /**
     * Combines the elements of an int[] into a string
     * @param arr The int[] to combine
     * @param separator (optional) The string to place between elements of the int[]
     * @return The combined string of the int[]
     */
    public static String intArrayToString(int[] arr, String separator) {
        String res = "";
        for(int a : arr) { res += a+separator; }
        return res;
    }
    public static String intArrayToString(int[] arr) {
        return intArrayToString(arr, "");
    }

    /**
     * Reads the lines of a file into a String[]
     * @param reader The file reader
     * @return The lines of the file, as a String[]
     */
    public static String[] readFile(Reader reader) {
        String out = "";
        BufferedReader br;
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

    /**
     * Reads the lines of a file into a String[]
     * @param file The file
     * @return The lines of the file, as a String[]
     */
    public static String[] readFile(File file) {
        try {
            return readFile(new FileReader(file));
        } catch(Exception e) {
            errorMsg(e);
            return null;
        }
    }

    /**
     * Writes a String to a file
     * @param file The file to write the String to
     * @param out The String to write to the file
     */
    public static void writeFile(File file, String out) {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(out);
            bw.close();
        } catch(Exception e) {
            errorMsg(e);
        }
    }
}
