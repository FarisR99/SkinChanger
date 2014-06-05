package com.faris.skinchanger.commands;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.command.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public class CommandSkinChanger extends CommandBase {
    @Override
    protected boolean onCommand(CommandSender sender, String command, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission(this.getPlugin().reloadPermission)) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (!this.getPlugin().getChangeSkinsOnJoin()) Lang.sendMessage(player, Lang.COMMAND_GEN_RELOAD_CONFIG);
                this.getPlugin().getDisplayFactory().removeChanges(player);
            }

            this.getPlugin().reloadConfig();
            this.getPlugin().loadConfiguration();
            this.getPlugin().checkUpdates();

            Lang.sendMessage(sender, Lang.COMMAND_RELOAD);
        } else {
            sender.sendMessage(ChatColor.GOLD + "SkinChanger " + this.getPlugin().getDescription().getVersion() + " by KingFaris10");
        }
        return true;
    }
}
