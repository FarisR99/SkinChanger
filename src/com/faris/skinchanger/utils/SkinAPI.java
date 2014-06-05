package com.faris.skinchanger.utils;

import com.faris.skinchanger.Lang;
import com.faris.skinchanger.Main;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

/**
 * @author KingFaris10
 */
public class SkinAPI {

    /**
     * Get the display name of a player.
     *
     * @param player - The player.
     * @return The player's display name.
     */
    public static String getDisplayName(Player player) {
        Validate.notNull(player);
        return Main.getInstance().getDisplayFactory().getDisplayName(player);
    }


    /**
     * Get the skin of a player.
     *
     * @param player - The player.
     * @return The player's skin name.
     */
    public static String getSkin(Player player) {
        Validate.notNull(player);
        return Main.getInstance().getDisplayFactory().getSkin(player);
    }


    /**
     * Set the skin and display name of a player.
     *
     * @param player - The player.
     * @param skinUsername - The skin holder's username.
     * @param displayName - The player's new display name.
     */
    public static boolean set(Player player, String skinUsername, String displayName) {
        Validate.notNull(player);
        boolean result = false;
        if (skinUsername == null) resetSkin(player);
        else result = setSkin(player, skinUsername);
        if (skinUsername == null) resetDisplayName(player);
        else result = setDisplayName(player, displayName);
        return result;
    }

    /**
     * Set the skin of a player.
     *
     * @param player - The player.
     * @param skinUsername - The skin holder's username.
     */
    public static boolean setSkin(Player player, String skinUsername) {
        Validate.notNull(player);
        if (skinUsername == null) {
            resetSkin(player);
            return true;
        } else {
            if (!skinUsername.isEmpty())
                Main.getInstance().getDisplayFactory().changeDisplay(player.getName(), skinUsername, getDisplayName(player));
            return skinUsername.isEmpty();
        }
    }

    /**
     * Set the display name of a player.
     *
     * @param player - The player.
     * @param displayName - The player's new display name.
     */
    public static boolean setDisplayName(Player player, String displayName) {
        Validate.notNull(player);
        if (displayName == null) {
            resetDisplayName(player);
            return true;
        } else {
            if (!displayName.isEmpty())
                Main.getInstance().getDisplayFactory().changeDisplay(player.getName(), getSkin(player), Lang.replaceColours(displayName));
            return displayName.isEmpty();
        }
    }

    /**
     * Reset the player's skin and display name.
     *
     * @param player - The player.
     */
    public static void reset(Player player) {
        Validate.notNull(player);
        Main.getInstance().getDisplayFactory().removeChanges(player);
    }


    /**
     * Reset the player's display name.
     *
     * @param player - The player.
     */
    public static void resetDisplayName(Player player) {
        Validate.notNull(player);
        String currentSkin = Main.getInstance().getDisplayFactory().getSkin(player);
        Main.getInstance().getDisplayFactory().removeChanges(player);
        if (!currentSkin.equals(player.getName()))
            Main.getInstance().getDisplayFactory().changeDisplay(player.getName(), currentSkin);
    }


    /**
     * Reset the player's skin.
     *
     * @param player - The player.
     */
    public static void resetSkin(Player player) {
        Validate.notNull(player);
        String currentDisplay = Main.getInstance().getDisplayFactory().getDisplayName(player);
        Main.getInstance().getDisplayFactory().removeChanges(player);
        if (!currentDisplay.equals(player.getCustomName()))
            Main.getInstance().getDisplayFactory().changeDisplayName(player.getName(), currentDisplay);
    }
}
