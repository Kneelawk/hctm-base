package net.dblsaiko.hctm.init.net;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface ServerMessageHandler<T> {
    void handle(MinecraftServer server, ServerPlayerEntity player, T message, PacketSender sender);
}
