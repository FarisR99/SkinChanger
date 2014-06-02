package com.faris.skinchanger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.List;
import java.util.Random;

/**
 * @author KingFaris10
 */
public class EventListener implements Listener {
    private static final Random random = new Random();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Main.getInstance().isPluginEnabled() && Main.getInstance().getChangeSkinsOnJoin() && event.getPlayer().hasPermission(Main.getInstance().joinSkinPermission)) {
            List<String> joinSkins = Main.getInstance().getJoinSkins();
            if (!joinSkins.isEmpty()) {
                Main.getInstance().getDisplayFactory().changeDisplay(event.getPlayer(), joinSkins.get(random.nextInt(joinSkins.size())), event.getPlayer().getName());
                Main.getInstance().getDisplayFactory().refreshPlayer(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerRightClickPlayer(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            if (Main.getInstance().isPluginEnabled() && Main.getInstance().isClicker(event.getPlayer())) {
                String displayName = Main.getInstance().getDisplayFactory().getDisplayName(event.getPlayer());
                Main.getInstance().getDisplayFactory().removeChanges(event.getPlayer());
                Main.getInstance().getDisplayFactory().changeDisplay(event.getPlayer(), ((Player) event.getRightClicked()).getName(), displayName);
                Main.getInstance().getDisplayFactory().refreshPlayer(event.getPlayer());
                Lang.sendMessage(event.getPlayer(), Lang.COMMAND_CHANGED_SKIN, event.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (Main.getInstance().isPluginEnabled()) {
            Main.getInstance().removeClicker(event.getPlayer());
            Main.getInstance().getDisplayFactory().removeChanges(event.getPlayer());
            Main.getInstance().getDisplayFactory().refreshPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("ProtocolLib")) {
            Main.getInstance().disablePlugin();
            Main.getInstance().setPluginEnabled(false);
        }
    }

}
