package net.dblsaiko.hctm.init.net;

import net.minecraft.client.MinecraftClient;

@FunctionalInterface
public interface ClientMessageHandler<T> {
    void handle(MinecraftClient client, T message, PacketSender sender);
}
