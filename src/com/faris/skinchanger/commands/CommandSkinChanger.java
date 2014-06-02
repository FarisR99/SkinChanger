package com.faris.skinchanger.commands;

import com.faris.skinchanger.command.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author KingFaris10
 */
public class CommandSkinChanger extends CommandBase {
    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "SkinChanger " + this.getPlugin().getDescription().getVersion() + " by KingFaris10");
        return true;
    }
}
