package net.dblsaiko.hctm.fabric.init.net;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public record FabricPacketSender(PacketSender sender) implements net.dblsaiko.hctm.init.net.PacketSender {
}
