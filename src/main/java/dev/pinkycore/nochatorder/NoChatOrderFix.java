package dev.pinkycore.nochatorder;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class NoChatOrderFix extends JavaPlugin {

    private static final String PREFIX = ChatColor.DARK_AQUA + "[NoChatOrderFix] " + ChatColor.RESET;

    @Override
    public void onEnable() {
        sendConsole(ChatColor.LIGHT_PURPLE, "PinkyCore magic activated — rerouting signed chat directly through the server pipeline.");

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketType[] chatPackets = resolveChatPackets();

        if (chatPackets.length == 0) {
            getLogger().log(Level.SEVERE, "No compatible chat packet types found for this server version.");
            sendConsole(ChatColor.RED, "No compatible chat packet types found for this server version.");
            return;
        }

        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                chatPackets
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    handleIncomingChat(event);
                } catch (Exception ex) {
                    getLogger().warning("Failed to relay chat payload: " + ex.getMessage());
                    sendConsole(ChatColor.YELLOW, "Failed to relay chat payload: " + ex.getMessage());
                }
            }
        });
    }

    private void handleIncomingChat(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        String payload = readFirstString(packet);

        if (payload == null || payload.isBlank()) {
            return;
        }

        event.setCancelled(true);

        boolean commandPacket = isCommandPacket(event.getPacketType());
        boolean startsWithSlash = payload.startsWith("/");
        boolean shouldRunAsCommand = commandPacket || startsWithSlash;

        if (shouldRunAsCommand) {
            String command = startsWithSlash ? payload.substring(1) : payload;
            Bukkit.getScheduler().runTask(this, () -> event.getPlayer().performCommand(command));
        } else {
            String message = payload;
            Bukkit.getScheduler().runTask(this, () -> event.getPlayer().chat(message));
        }
    }

    private String readFirstString(PacketContainer packet) {
        try {
            StructureModifier<String> modifier = packet.getStrings();
            if (modifier.size() > 0) {
                return modifier.readSafely(0);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private PacketType[] resolveChatPackets() {
        List<PacketType> packetTypes = new ArrayList<>();
        addIfSupported(packetTypes, PacketType.Play.Client.CHAT);
        addIfSupported(packetTypes, getPacketType("CHAT_COMMAND"));
        addIfSupported(packetTypes, getPacketType("CHAT_COMMAND_SIGNED"));
        return packetTypes.toArray(new PacketType[0]);
    }

    private void addIfSupported(List<PacketType> packetTypes, PacketType type) {
        if (type != null && type.isSupported()) {
            packetTypes.add(type);
        }
    }

    private PacketType getPacketType(String fieldName) {
        try {
            Field field = PacketType.Play.Client.class.getField(fieldName);
            Object value = field.get(null);
            if (value instanceof PacketType packetType) {
                return packetType;
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return null;
    }

    private boolean isCommandPacket(PacketType type) {
        return Objects.equals(type, PacketType.Play.Client.CHAT_COMMAND)
                || Objects.equals(type, getPacketType("CHAT_COMMAND_SIGNED"));
    }

    private void sendConsole(ChatColor color, String message) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(PREFIX + color + message + ChatColor.RESET);
    }
}
