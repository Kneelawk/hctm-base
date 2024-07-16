package net.dblsaiko.hctm.neoforge.init.net;

import java.util.function.Consumer;

import net.dblsaiko.hctm.init.net.PacketSender;

import net.minecraft.network.packet.CustomPayload;

public record NeoForgePacketSender(Consumer<CustomPayload> handler) implements PacketSender {
}
