package net.dblsaiko.hctm.neoforge.init.net;

import net.dblsaiko.hctm.init.net.PacketSender;
import net.neoforged.neoforge.network.handling.IReplyHandler;

public record NeoForgePacketSender(IReplyHandler handler) implements PacketSender {
}
