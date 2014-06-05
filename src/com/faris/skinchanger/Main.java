package com.faris.skinchanger;

import com.faris.skinchanger.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main pluginInstance = null;

    private boolean pluginEnabled = true;
    private boolean checkUpdate = false, shouldUpdate = false;
    private boolean joinSkin = false;

    private PlayerDisplayModifier displayFactory = null;

    private List<String> joinSkins = null;
    private List<String> skinClickers = null;

    public Permission reloadPermission = new Permission("skinchanger.reload");
    public Permission changeSkin = new Permission("skinchanger.change.skin");
    public Permission changeSkinOther = new Permission("skinchanger.change.skin.other");
    public Permission changeName = new Permission("skinchanger.change.name");
    public Permission changeNameOther = new Permission("skinchanger.change.name.other");
    public Permission resetChanges = new Permission("skinchanger.reset");
    public Permission resetChangesOther = new Permission("skinchanger.reset.other");
    public Permission skinClicker = new Permission("skinchanger.skinclicker");
    public Permission joinSkinPermission = new Permission("skinchanger.joinskin");

    public void onEnable() {
        pluginInstance = this;

        this.pluginEnabled = this.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
        if (this.pluginEnabled) {
            this.loadConfiguration();
            try {
                Lang.init(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                this.getServer().getLogger().warning("Could not load the messages file! Using default messages...");
            }
            this.checkUpdates();

            this.displayFactory = new PlayerDisplayModifier(this);
            this.skinClickers = new ArrayList<String>();

            this.getServer().getPluginManager().addPermission(this.reloadPermission);
            this.getServer().getPluginManager().addPermission(this.changeSkin);
            this.getServer().getPluginManager().addPermission(this.changeSkinOther);
            this.getServer().getPluginManager().addPermission(this.changeName);
            this.getServer().getPluginManager().addPermission(this.changeNameOther);
            this.getServer().getPluginManager().addPermission(this.resetChanges);
            this.getServer().getPluginManager().addPermission(this.resetChangesOther);
            this.getServer().getPluginManager().addPermission(this.skinClicker);
            this.getServer().getPluginManager().addPermission(this.joinSkinPermission);
            this.getServer().getPluginManager().registerEvents(new EventListener(), this);

            this.getCommand("skinchanger").setExecutor(new CommandSkinChanger());
            this.getCommand("changeskin").setExecutor(new CommandChangeSkin());
            this.getCommand("changename").setExecutor(new CommandChangeName());
            this.getCommand("skinclicker").setExecutor(new CommandSkinClicker());
            this.getCommand("resetchanges").setExecutor(new CommandResetChanges());
        } else {
            this.getServer().getLogger().log(Level.SEVERE, "ProtocolLib is required to use this plugin!");
        }
    }

    public void onDisable() {
        this.disablePlugin();
    }

    public void loadConfiguration() {
        this.getConfig().options().header("SkinChanger configuration");
        this.getConfig().addDefault("Check for updates", true);
        this.getConfig().addDefault("Automatically update", false);
        this.getConfig().addDefault("Change skin on join", false);
        this.getConfig().addDefault("Player join skins", Arrays.asList("KingFaris10"));
        this.getConfig().options().copyDefaults(true);
        this.getConfig().options().copyHeader(true);
        this.saveConfig();

        this.checkUpdate = this.getConfig().getBoolean("Check for updates", true);
        this.shouldUpdate = this.getConfig().getBoolean("Automatically update", false);
        if (this.shouldUpdate && !this.checkUpdate) {
            this.checkUpdate = true;
            this.getConfig().set("Check for updates", true);
            this.saveConfig();
        }

        this.joinSkin = this.getConfig().getBoolean("Change skin on join", false);
        this.joinSkins = this.getConfig().getStringList("Player join skins");
        if (this.joinSkins == null)
            this.joinSkins = new ArrayList<String>();
        if (this.joinSkins.isEmpty() && this.joinSkin) {
            this.getConfig().set("Change skin on join", false);
            this.saveConfig();
            this.joinSkin = false;
        }
    }

    public boolean addClicker(Player player) {
        if (player != null) {
            if (!this.skinClickers.contains(player.getName())) return this.skinClickers.add(player.getName());
        }
        return false;
    }

    public void checkUpdates() {
        if (this.checkUpdate) {
            int projectID = 80599;
            Updater updater = new Updater(this, projectID, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                this.getLogger().info(updater.getLatestName() + " has been found. Your current version is: " + this.getDescription().getFullName());
                if (this.shouldUpdate) {
                    this.getLogger().info("Updating, please wait...");
                    updater = new Updater(this, projectID, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, false);
                    String errorMessage = "Could not update to the latest version: ";
                    Updater.UpdateResult updateResult = updater.getResult();
                    boolean hasError = updateResult != Updater.UpdateResult.SUCCESS;
                    if (updateResult == Updater.UpdateResult.DISABLED) errorMessage += "Updater is disabled.";
                    else if (updateResult == Updater.UpdateResult.FAIL_APIKEY) errorMessage += "Invalid API key.";
                    else if (updateResult == Updater.UpdateResult.FAIL_BADID) errorMessage += "Invalid updater ID.";
                    else if (updateResult == Updater.UpdateResult.FAIL_DBO) errorMessage += "Invalid DBO.";
                    else if (updateResult == Updater.UpdateResult.FAIL_DOWNLOAD)
                        errorMessage += "Failed to download the latest version.";
                    else if (updateResult == Updater.UpdateResult.FAIL_NOVERSION)
                        errorMessage += "There is no latest version.";
                    else errorMessage += "null";
                    if (hasError) this.getLogger().warning(errorMessage);
                    else this.getLogger().info("Successfully updated to " + updater.getLatestName() + "!");
                } else {
                    this.getLogger().info("You can download the latest version at: " + updater.getLatestFileLink());
                }
            }
        }
    }

    public boolean getChangeSkinsOnJoin() {
        return this.joinSkin;
    }

    public List<String> getJoinSkins() {
        return this.joinSkins;
    }

    public boolean isClicker(Player player) {
        return player != null && this.skinClickers.contains(player.getName());
    }

    public boolean isPluginEnabled() {
        return this.pluginEnabled;
    }

    public PlayerDisplayModifier getDisplayFactory() {
        return this.displayFactory;
    }

    public boolean removeClicker(Player player) {
        if (player != null) {
            if (this.skinClickers.contains(player.getName())) return this.skinClickers.remove(player.getName());
        }
        return false;
    }

    public void setPluginEnabled(boolean flag) {
        this.pluginEnabled = flag;
    }

    public void disablePlugin() {
        if (this.pluginEnabled) {
            for (Player player : Bukkit.getServer().getOnlinePlayers())
                this.displayFactory.removeChanges(player);
            this.displayFactory = null;

            HandlerList.unregisterAll(this);

            this.joinSkins.clear();
            this.joinSkins = null;
            this.skinClickers.clear();
            this.skinClickers = null;

            this.getServer().getPluginManager().removePermission(this.reloadPermission);
            this.getServer().getPluginManager().removePermission(this.changeSkin);
            this.getServer().getPluginManager().removePermission(this.changeSkinOther);
            this.getServer().getPluginManager().removePermission(this.changeName);
            this.getServer().getPluginManager().removePermission(this.changeNameOther);
            this.getServer().getPluginManager().removePermission(this.resetChanges);
            this.getServer().getPluginManager().removePermission(this.resetChangesOther);
            this.getServer().getPluginManager().removePermission(this.skinClicker);
            this.getServer().getPluginManager().removePermission(this.joinSkinPermission);
        }
    }

    public static Main getInstance() {
        return pluginInstance;
    }
}
