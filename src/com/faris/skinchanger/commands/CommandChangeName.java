package com.faris.skinchanger.commands;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.Main;
import com.faris.skinchanger.command.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public class CommandChangeName extends CommandBase {
    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission(this.getPlugin().changeName) || sender.hasPermission(this.getPlugin().changeNameOther)) {
            if (args.length == 1) {
                if (!this.isConsole(sender)) {
                    Player player = (Player) sender;
                    if (player.hasPermission(this.getPlugin().changeName)) {
                        String targetName = args[0];
                        String skin = Main.getInstance().getDisplayFactory().getSkin(player);
                        Main.getInstance().getDisplayFactory().removeChanges(player);
                        Main.getInstance().getDisplayFactory().changeDisplay(player, skin, Lang.replaceColours(targetName));
                        Main.getInstance().getDisplayFactory().refreshPlayer(player);
                        Lang.sendMessage(sender, Lang.COMMAND_CHANGED_NAME, targetName);

                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_INGAME, command.toLowerCase());
                }
            } else if (args.length == 2) {
                if (sender.hasPermission(this.getPlugin().changeNameOther)) {
                    String targetName = args[0];
                    Player targetPlayer = this.getPlayer(args, 1);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        String skin = Main.getInstance().getDisplayFactory().getSkin(targetPlayer);
                        Main.getInstance().getDisplayFactory().removeChanges(targetPlayer);
                        Main.getInstance().getDisplayFactory().changeDisplay(targetPlayer, skin, Lang.replaceColours(targetName));
                        Main.getInstance().getDisplayFactory().refreshPlayer(targetPlayer);
                        Lang.sendMessage(sender, Lang.COMMAND_CHANGED_NAME_OTHER, targetPlayer.getName(), Lang.replaceColours(targetName));
                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOTONLINE, args[1]);
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                }
            } else {
                Lang.sendMessage(sender, Lang.COMMAND_GEN_USAGE, command.toLowerCase() + " <username> [<player>]");
            }
        }
        return true;
    }
}
