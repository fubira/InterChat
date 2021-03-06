package net.ironingot.interchat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public final class InterChatProtocol {

    public static final void enable(InterChatPlugin plugin) {
        if (!plugin.getConfigHandler().useTotalPlayerCount()) {
            return;
        }

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(
            new PacketAdapter(PacketAdapter.params(plugin, PacketType.Status.Server.SERVER_INFO).optionAsync()) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    ping.setPlayersOnline(((InterChatPlugin)plugin).getMessenger().getTotalPlayers());
                }
            });
    }

    public static final void disable(InterChatPlugin plugin) {
        if (!plugin.getConfigHandler().useTotalPlayerCount()) {
            return;
        }

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.removePacketListeners(plugin);
    }
}