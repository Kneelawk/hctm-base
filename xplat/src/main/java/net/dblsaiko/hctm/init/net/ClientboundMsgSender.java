package net.dblsaiko.hctm.init.net;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ClientboundMsgSender<T> {
    void send(ServerPlayerEntity player, T message);

    void send(PacketSender sender, T message);
}
