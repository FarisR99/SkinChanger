package com.faris.skinchanger.commands;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.Main;
import com.faris.skinchanger.command.CommandBase;
import com.faris.skinchanger.utils.UUIDFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author KingFaris10
 */
public class CommandChangeSkin extends CommandBase {
    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission(this.getPlugin().changeSkin) || sender.hasPermission(this.getPlugin().changeSkinOther)) {
            if (args.length == 1) {
                if (!this.isConsole(sender)) {
                    Player player = (Player) sender;
                    if (player.hasPermission(this.getPlugin().changeSkin)) {
                        String targetSkin = args[0];
                        try {
                            UUID targetUUID = UUIDFetcher.getUUIDOf(targetSkin);
                            if (targetUUID != null) {
                                String displayName = Main.getInstance().getDisplayFactory().getDisplayName(player);
                                Main.getInstance().getDisplayFactory().removeChanges(player);
                                Main.getInstance().getDisplayFactory().changeDisplay(player, targetSkin, displayName);
                                Main.getInstance().getDisplayFactory().refreshPlayer(player);
                                Lang.sendMessage(sender, Lang.COMMAND_CHANGED_SKIN, targetSkin);
                            } else {
                                Lang.sendMessage(sender, Lang.COMMAND_SKIN_NO_EXIST, targetSkin);
                            }
                        } catch (Exception ex) {
                            Lang.sendMessage(sender, Lang.COMMAND_SKIN_NO_EXIST, targetSkin);
                        }
                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_INGAME, command.toLowerCase());
                }
            } else if (args.length == 2) {
                if (sender.hasPermission(this.getPlugin().changeSkinOther)) {
                    String targetSkin = args[0];
                    Player targetPlayer = this.getPlayer(args, 1);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        try {
                            UUID targetUUID = UUIDFetcher.getUUIDOf(targetSkin);
                            if (targetUUID != null) {
                                String displayName = Main.getInstance().getDisplayFactory().getDisplayName(targetPlayer);
                                Main.getInstance().getDisplayFactory().removeChanges(targetPlayer);
                                Main.getInstance().getDisplayFactory().changeDisplay(targetPlayer, targetSkin, displayName);
                                Main.getInstance().getDisplayFactory().refreshPlayer(targetPlayer);
                                Lang.sendMessage(sender, Lang.COMMAND_CHANGED_SKIN_OTHER, targetPlayer.getName(), targetSkin);
                            } else {
                                Lang.sendMessage(sender, Lang.COMMAND_SKIN_NO_EXIST, targetSkin);
                            }
                        } catch (Exception ex) {
                            Lang.sendMessage(sender, Lang.COMMAND_SKIN_NO_EXIST, targetSkin);
                        }
                    } else {
                        Lang.sendMessage(sender, Lang.COMMAND_GEN_NOTONLINE, args[1]);
                    }
                } else {
                    Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
                }
            } else {
                Lang.sendMessage(sender, Lang.COMMAND_GEN_USAGE, command.toLowerCase() + " <username> [<player>]");
            }
        } else {
            Lang.sendMessage(sender, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
        }
        return true;
    }
}
