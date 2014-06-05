package com.faris.skinchanger;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.faris.skinchanger.utils.UUIDFetcher;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

// For ProtocolLib 3.3.1 and lower
// For ProtocolLib 3.4.0
//import com.comphenix.protocol.wrappers.WrappedSignedProperty;

/**
 * @author aadnk
 */
public class PlayerDisplayModifier {
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final int WORKER_THREADS = 4;

    private ProtocolManager protocolManager;
    private ConcurrentMap<String, String> skinNames = Maps.newConcurrentMap();
    private ConcurrentMap<String, String> displayNames = Maps.newConcurrentMap();

    private Cache<String, String> profileCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(4, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        public String load(String name) throws Exception {
            return getProfileJson(name);
        }
    });

    public PlayerDisplayModifier(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        // Packet we are modifying
                        PacketType.Play.Server.NAMED_ENTITY_SPAWN,

                        // Packets that must be sent AFTER the entity spawn packet
                        PacketType.Play.Server.ENTITY_EFFECT,
                        PacketType.Play.Server.ENTITY_EQUIPMENT,
                        PacketType.Play.Server.ENTITY_METADATA,
                        PacketType.Play.Server.UPDATE_ATTRIBUTES,
                        PacketType.Play.Server.ATTACH_ENTITY,
                        PacketType.Play.Server.BED) {

                    // This will be executed on an asynchronous thread
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // We only care about the entity spawn packet
                        if (event.getPacketType() != PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                            return;
                        }

                        Player toDisplay = (Player) event.getPacket().getEntityModifier(event).read(0);
                        String skinName = skinNames.get(toDisplay.getName());
                        String displayName = displayNames.get(toDisplay.getName());

                        if (skinName == null && displayName == null) {
                            return;
                        }
                        StructureModifier<WrappedGameProfile> profiles = event.getPacket().getGameProfiles();
                        WrappedGameProfile original = profiles.read(0);
                        WrappedGameProfile result = null;
                        try {
                            UUID extractedUUID = extractUUID(original.getName());
                            result = new WrappedGameProfile(extractedUUID, displayName != null ? displayName : original.getName());
                        } catch (Exception ex) {
                            try {
                                UUID nullUUID = extractUUID("Steve");
                                skinName = "Steve";
                                result = new WrappedGameProfile(nullUUID, displayName != null ? displayName : original.getName());
                            } catch (Exception ex2) {
                                result = null;
                            }
                        } finally {
                            if (result != null) {
                                updateSkin(result, skinName != null ? skinName : result.getName());
                                profiles.write(0, result);
                            }
                        }
                    }
                }
        ).start(WORKER_THREADS);
    }

    @SuppressWarnings("deprecation")
    private UUID extractUUID(final String playerName) {
        try {
            return UUIDFetcher.getUUIDOf(playerName);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot fetch UUID.", ex);
        }
    }

    // This will be cached by Guava
    private String getProfileJson(String name) throws IOException {
        try {
            final URL url = new URL(PROFILE_URL + extractUUID(name).toString().replace("-", ""));
            final URLConnection uc = url.openConnection();

            return CharStreams.toString(new InputSupplier<InputStreamReader>() {
                @Override
                public InputStreamReader getInput() throws IOException {
                    return new InputStreamReader(uc.getInputStream(), Charsets.UTF_8);
                }
            });
        } catch (Exception ex) {
            try {

                final URL url = new URL(PROFILE_URL + extractUUID("Steve").toString().replace("-", ""));
                final URLConnection uc = url.openConnection();

                return CharStreams.toString(new InputSupplier<InputStreamReader>() {
                    @Override
                    public InputStreamReader getInput() throws IOException {
                        return new InputStreamReader(uc.getInputStream(), Charsets.UTF_8);
                    }
                });
            } catch (Exception ex2) {
                return "Steve";
            }
        }
    }

    public String getDisplayName(Player player) {
        return player != null ? this.getDisplayName(player.getName(), player.getCustomName()) : "";
    }

    public String getDisplayName(String playerName) {
        return playerName != null ? (this.displayNames.containsKey(playerName) ? this.displayNames.get(playerName) : playerName) : "";
    }

    public String getDisplayName(String playerName, String defaultDisplayName) {
        return playerName != null ? (this.displayNames.containsKey(playerName) ? this.displayNames.get(playerName) : defaultDisplayName) : defaultDisplayName;
    }

    public String getSkin(Player player) {
        return player != null ? this.getSkin(player.getName()) : "";
    }

    public String getSkin(String playerName) {
        return playerName != null ? (this.skinNames.containsKey(playerName) ? this.skinNames.get(playerName) : playerName) : "";
    }

    private void updateSkin(WrappedGameProfile profile, String skinOwner) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(profileCache.get(skinOwner));
            JSONArray properties = (JSONArray) json.get("properties");

            for (int i = 0; i < properties.size(); i++) {
                JSONObject property = (JSONObject) properties.get(i);
                String name = (String) property.get("name");
                String value = (String) property.get("value");
                String signature = (String) property.get("signature"); // May be NULL

                // Uncomment for ProtocolLib 3.4.0
                //profile.getProperties().put(name, new WrappedSignedProperty(name, value, signature));
                ((GameProfile) profile.getHandle()).getProperties().put(name, new Property(name, value, signature));
            }
        } catch (Exception e) {
            // ProtocolLib will throttle the number of exceptions printed to the console log
        }
    }

    public void changeDisplay(String string, String toSkin) {
        changeDisplay(string, toSkin, null);
    }

    public void changeDisplayName(String string, String toName) {
        changeDisplay(string, null, toName);
    }

    public void changeDisplay(Player player, String toSkin, String toName) {
        if (updateMap(skinNames, player.getName(), toSkin) |
                updateMap(displayNames, player.getName(), toName)) {
            refreshPlayer(player);
        }
    }

    public void changeDisplay(String playerName, String toSkin, String toName) {
        if (updateMap(skinNames, playerName, toSkin) |
                updateMap(displayNames, playerName, toName)) {
            refreshPlayer(playerName);
        }
    }

    public void removeChanges(Player player) {
        changeDisplay(player.getName(), null, null);
    }

    public void removeChanges(String playerName) {
        changeDisplay(playerName, null, null);
    }

    /**
     * Update the map with the new key-value pair.
     *
     * @param map - the map.
     * @param key - the key of the pair.
     * @param value - the new value, or NULL to remove the pair.
     * @return TRUE if the map was updated, FALSE otherwise.
     */
    private <T, U> boolean updateMap(Map<T, U> map, T key, U value) {
        if (value == null) {
            return map.remove(key) != null;
        } else {
            return !Objects.equal(value, map.put(key, value));
        }
    }

    @SuppressWarnings("deprecation")
    private void refreshPlayer(String name) {
        Player player = Bukkit.getPlayer(name);

        if (player != null) {
            refreshPlayer(player);
        }
    }

    public void refreshPlayer(Player player) {
        // Refresh the displayed entity
        protocolManager.updateEntity(player, protocolManager.getEntityTrackers(player));
    }
}