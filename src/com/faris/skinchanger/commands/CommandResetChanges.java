package com.faris.skinchanger.commands;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.Main;
import com.faris.skinchanger.command.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public class CommandResetChanges extends CommandBase {
    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission(this.getPlugin().resetChanges) || sender.hasPermission(this.getPlugin().resetChangesOther)) {
            if (args.length == 0) {
                if (!this.isConsole(sender)) {
                    Player player = (Player) sender;
                    if (player.hasPermission(this.getPlugin().resetChanges)) {
                        //Main.getInstance().getDisplayFactory().removeChanges(player);
                        //Main.getInstance().getDisplayFactory().refreshPlayer(player);
                        Main.getInstance().getSkinFactory().removeSkinChange(player);
                        Lang.sendMessage(sender, Lang.COMMAND_RESET);
                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_INGAME, command.toLowerCase());
                }
            } else if (args.length == 1) {
                if (sender.hasPermission(this.getPlugin().resetChangesOther)) {
                    Player targetPlayer = this.getPlayer(args, 0);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        //Main.getInstance().getDisplayFactory().removeChanges(targetPlayer);
                        //Main.getInstance().getDisplayFactory().refreshPlayer(targetPlayer);
                        Main.getInstance().getSkinFactory().removeSkinChange(targetPlayer);
                        Lang.sendMessage(sender, Lang.COMMAND_RESET_OTHER, targetPlayer.getName());
                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOTONLINE, args[0]);
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                }
            } else {
                Lang.sendMessage(sender, Lang.COMMAND_GEN_USAGE, command.toLowerCase() + " [<player>]");
            }
        } else {
            Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
        }
        return true;
    }
}
