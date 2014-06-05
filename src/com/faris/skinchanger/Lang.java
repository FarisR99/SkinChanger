package com.faris.skinchanger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author 1Rogue
 */
public enum Lang {
    COMMAND_GEN_INGAME("Command.General.In-game", "&cYou must be a player to use that command!"), COMMAND_GEN_USAGE("Command.General.Usage", "&cUsage: &4/%s"), COMMAND_GEN_NOTONLINE("Command.General.Not online", "%s is not online!"), COMMAND_GEN_NOPERMISSION("Command.General.No permission", "&4You do not have access to that command."), COMMAND_SKIN_NO_EXIST("Command.Changed.Skin does not exist", "&c%s is not a valid player skin."), COMMAND_CHANGED_SKIN("Command.Changed.Skin", "&6Successfully changed your skin to %s's skin."), COMMAND_CHANGED_SKIN_OTHER("Command.Changed.Skin Other", "&6Successfully changed %s's skin to %s's skin."), COMMAND_CHANGED_NAME("Command.Changed.Name", "&6Successfully changed your display name to %s."), COMMAND_CHANGED_NAME_OTHER("Command.Changed.Name Other", "&6Successfully changed %s's display name to %s."), COMMAND_SKINCLICKER_STOP_SUCCESS("Command.Skin clicker.Stop success", "&6You turned off skin clicker."), COMMAND_SKINCLICKER_STOP_FAILED("Command.Skin clicker.Stop failed", "&cFailed to turn off skin clicker!"), COMMAND_SKINCLICKER_START_SUCCESS("Command.Skin clicker.Start success", "&6You turned on skin clicker."), COMMAND_SKINCLICKER_START_FAILED("Command.Skin clicker.Start failed", "&cFailed to turn on skin clicker!"), COMMAND_RESET("Command.Reset.Self", "&6You reset all the changes made to you."), COMMAND_RESET_OTHER("Command.Reset.Other", "&6You reset all the changes made to %s.");

    private static FileConfiguration messagesConfig;
    private final String def;
    private final String path;

    /**
     * {@link com.faris.skinchanger.Lang} private constructor
     *
     * @param path The path to the value
     * @param def The default value
     */
    private Lang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    /**
     * Formats a {@link com.faris.skinchanger.Lang} enum constant with the supplied arguments
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    public String format(Object... args) {
        return Lang.replaceColours(String.format(messagesConfig.getString(this.path, this.def), args));
    }

    /**
     * Will format a string with "PLURAL" or "PLURALA" tokens in them.
     * <br /><br /><ul>
     * <li> <em>PLURALA</em>: Token that will evaluate gramatically. An int
     * value of 1 will return "is &lt;amount&gt; 'word'", otherwise it will be
     * "are &lt;amount&gt; 'word'".
     * </li><li> <em>PLURAL</em>: Token that will evaluate the word. An int
     * value of 1 will return the first word, value of 2 the second word.
     *
     * @param amount The amount representative of the data token
     * @param args The arguments to replace any other tokens with.
     * @return The plural format.
     */
    public String pluralFormat(int amount, Object... args) {
        String repl = messagesConfig.getString(this.path);
        repl = repl.replaceAll("\\{PLURALA (.*)\\|(.*)\\}", amount == 1 ? "is " + amount + " $1" : "are " + amount + " $2");
        repl = repl.replaceAll("\\{PLURAL (.*)\\|(.*)\\}", amount == 1 ? "$1" : "$2");
        return Lang.replaceColours(String.format(repl, args));
    }

    /**
     * Converts pre-made strings to have chat colors in them
     *
     * @param color String with unconverted color codes
     * @return string with correct chat colors included
     */
    public static String replaceColours(String color) {
        return ChatColor.translateAlternateColorCodes('&', color);
    }

    /**
     * Loads the lang values from the configuration file. Safe to use for
     * reloading.
     *
     * @param plugin The {@link org.bukkit.plugin.Plugin} to load lang information from
     * @throws java.io.IOException If the file cannot be read
     */
    public static void init(Plugin plugin) {
        File ref = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(ref);
        for (Lang l : Lang.values()) {
            if (!messagesConfig.isSet(l.getPath())) {
                messagesConfig.set(l.getPath(), l.getDefault());
            }
        }
        try {
            messagesConfig.save(ref);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a formatted string and prepends the prefix to it.
     *
     * @param target The target to send to
     * @param message The message to format and send
     * @deprecated
     */
    public static void sendMessage(CommandSender target, String message) {
        target.sendMessage(String.format("&7[&5SkinChanger&7] %s", message));
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @deprecated
     */
    public static void sendRawMessage(CommandSender target, String message) {
        target.sendMessage(replaceColours(message));
    }

    /**
     * Sends a formatted string and prepends the prefix to it.
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link com.faris.skinchanger.Lang} message
     */
    public static void sendMessage(CommandSender target, Lang message, Object... args) {
        String s = String.format(replaceColours("&7[&5SkinChanger&7] %s"), message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link com.faris.skinchanger.Lang} message
     */
    public static void sendRawMessage(CommandSender target, Lang message, Object... args) {
        String s = replaceColours(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * The YAML path to store this value in
     *
     * @return The path to the YAML value
     */
    private String getPath() {
        return this.path;
    }

    /**
     * The default value of this YAML string
     *
     * @return The default value
     */
    private String getDefault() {
        return this.def;
    }
}
