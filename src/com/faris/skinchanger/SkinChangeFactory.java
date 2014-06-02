package com.faris.skinchanger;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author BigTeddy98
 */
public class SkinChangeFactory {

    private ProtocolManager protocolManager;
    private Map<String, String> names = new HashMap<String, String>();
    private List<Packet> packet = new ArrayList<Packet>();

    public SkinChangeFactory(Main plugin) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {

                if (packet.contains(event.getPacket().getHandle())) {
                    packet.remove(event.getPacket().getHandle());
                    return;
                }

                if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                    event.setCancelled(true);
                    Player pl = fromId(event.getPacket().getIntegers().read(0));
                    if (!names.containsKey(pl.getName())) {
                        event.setCancelled(false);
                        return;
                    }
                    setSkin(plugin, pl, event.getPlayer(), names.get(pl.getName()));
                }
            }

            private Player fromId(int id) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getEntityId() == id) {
                        return p;
                    }
                }
                return null;
            }
        });
    }

    public void addSkinChange(Player p, String toSkin) {
        this.names.put(p.getName(), toSkin);

        Packet packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle());
        for (Player pl : p.getWorld().getPlayers()) {
            if (p.equals(pl)) {
                continue;
            }
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void removeSkinChange(Player p) {
        if (this.names.containsKey(p.getName())) {
            this.names.remove(p.getName());
            Packet packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle());
            for (Player pl : p.getWorld().getPlayers()) {
                if (p.equals(pl)) {
                    continue;
                }
                ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    private void setSkin(Plugin plugin, final Player toSet, final Player willSee, final String toSkin) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Packet packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) toSet).getHandle());

                    SkinChangeFactory.this.packet.add(packet);

                    Field gameProfileField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
                    gameProfileField.setAccessible(true);

                    @SuppressWarnings("deprecation")
                    GameProfile profile = new GameProfile(Bukkit.getOfflinePlayer(toSet.getName()).getUniqueId(), toSet.getName());
                    fixSkin(profile, toSkin);

                    gameProfileField.set(packet, profile);

                    ((CraftPlayer) willSee).getHandle().playerConnection.sendPacket(packet);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @SuppressWarnings({"deprecation", "resource"})
    private void fixSkin(GameProfile profile, String skinOwner) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Bukkit.getOfflinePlayer(skinOwner).getUniqueId().toString().replace("-", ""));
            URLConnection uc = url.openConnection();

            System.out.println("connected.");

            Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A");
            String json = scanner.next();

            JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");
            for (int i = 0; i < properties.size(); i++) {
                JSONObject property = (JSONObject) properties.get(i);
                String name = (String) property.get("name");
                String value = (String) property.get("value");
                String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
                if (signature != null) {
                    profile.getProperties().put(name, new Property(name, value, signature));
                } else {
                    profile.getProperties().put(name, new Property(value, name));
                }
            }
        } catch (Exception e) {
        }
    }
}