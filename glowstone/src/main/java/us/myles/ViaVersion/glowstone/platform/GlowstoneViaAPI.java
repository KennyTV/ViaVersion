package us.myles.ViaVersion.glowstone.platform;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.GlowstonePlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@AllArgsConstructor
public class GlowstoneViaAPI implements ViaAPI<Player> {
    private GlowstonePlugin plugin;

    @Override
    public int getPlayerVersion(@NonNull Player player) {
        if (!isPorted(player.getUniqueId()))
            return ProtocolRegistry.SERVER_PROTOCOL;
        return getPortedPlayers().get(player.getUniqueId()).get(ProtocolInfo.class).getProtocolVersion();
    }

    @Override
    public int getPlayerVersion(@NonNull UUID uuid) {
        if (!isPorted(uuid))
            return ProtocolRegistry.SERVER_PROTOCOL;
        return getPortedPlayers().get(uuid).get(ProtocolInfo.class).getProtocolVersion();
    }

    @Override
    public boolean isPorted(UUID playerUUID) {
        return getPortedPlayers().containsKey(playerUUID);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf packet) throws IllegalArgumentException {
        if (!isPorted(uuid)) throw new IllegalArgumentException("This player is not controlled by ViaVersion!");
        UserConnection ci = getPortedPlayers().get(uuid);
        ci.sendRawPacket(packet);
    }

    @Override
    public void sendRawPacket(Player player, ByteBuf packet) throws IllegalArgumentException {
        sendRawPacket(player.getUniqueId(), packet);
    }

    @Override
    public BossBar createBossBar(String title, BossColor color, BossStyle style) {
        return new GlowstoneBossBar(title, 1F, color, style);
    }

    @Override
    public BossBar createBossBar(String title, float health, BossColor color, BossStyle style) {
        return new GlowstoneBossBar(title, health, color, style);
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
        outputSet.removeAll(Via.getPlatform().getConf().getBlockedProtocols());

        return outputSet;
    }

    public Map<UUID, UserConnection> getPortedPlayers() {
        return Via.getManager().getPortedPlayers();
    }
}
