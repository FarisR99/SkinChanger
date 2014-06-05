package com.faris.skinchanger.command;

import com.faris.skinchanger.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public abstract class CommandBase implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            return Main.getInstance().isPluginEnabled() && this.onCommand(sender, cmd.getName(), args);
        } catch (Exception ex) {
            ex.printStackTrace();
            Lang.sendMessage(sender, Lang.COMMAND_ERROR, label.toLowerCase());
            return true;
        }
    }

    protected abstract boolean onCommand(CommandSender sender, String command, String[] args);

    protected boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    protected boolean isOnline(String[] args, int index) {
        return getPlayer(args, index) != null;
    }

    protected Player getPlayer(String[] args, int index) {
        if (args.length > index) {
            return Bukkit.getPlayer(args[index]);
        }
        return null;
    }

    protected Main getPlugin() {
        return Main.getInstance();
    }

}
