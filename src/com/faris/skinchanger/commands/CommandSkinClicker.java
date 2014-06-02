package com.faris.skinchanger.commands;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.Main;
import com.faris.skinchanger.command.CommandBasePlayer;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public class CommandSkinClicker extends CommandBasePlayer {
    @Override
    protected boolean onCommand(Player player, String command, String[] args) {
        if (player.hasPermission(Main.getInstance().skinClicker)) {
            if (args.length == 0) {
                if (Main.getInstance().isClicker(player)) {
                    if (Main.getInstance().removeClicker(player)) Lang.sendMessage(player, Lang.COMMAND_SKINCLICKER_STOP_SUCCESS);
                    else Lang.sendMessage(player, Lang.COMMAND_SKINCLICKER_STOP_FAILED);
                } else {
                    if (Main.getInstance().addClicker(player)) Lang.sendMessage(player, Lang.COMMAND_SKINCLICKER_START_SUCCESS);
                    else Lang.sendMessage(player, Lang.COMMAND_SKINCLICKER_START_FAILED);
                }
            } else {
                Lang.sendMessage(player, Lang.COMMAND_GEN_USAGE, command.toLowerCase());
            }
        } else {
            Lang.sendMessage(player, Lang.COMMAND_GEN_NOPERMISSION, command.toLowerCase());
        }
        return true;
    }
}
