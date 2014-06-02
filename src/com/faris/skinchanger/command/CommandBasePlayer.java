package com.faris.skinchanger.command;

import com.faris.skinchanger.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public abstract class CommandBasePlayer extends CommandBase {

    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        if (this.isConsole(sender)) {
            Lang.sendMessage(sender, Lang.COMMAND_GEN_INGAME);
            return true;
        }
        return this.onCommand((Player) sender, command, args);
    }

    protected abstract boolean onCommand(Player player, String command, String[] args);
}
