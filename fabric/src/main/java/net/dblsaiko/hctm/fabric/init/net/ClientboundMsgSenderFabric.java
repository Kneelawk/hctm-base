package net.dblsaiko.hctm.fabric.init.net;

import net.dblsaiko.hctm.init.net.ClientboundMsgSender;
import net.dblsaiko.hctm.init.net.MsgPayload;
import net.dblsaiko.hctm.init.net.PacketSender;
import net.dblsaiko.hctm.init.net.Utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ClientboundMsgSenderFabric<T>implements ClientboundMsgSender<T> {
    private final Identifier id;
    private final Codec<T> codec;

    public ClientboundMsgSenderFabric(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
    }

    @Override public void send(ServerPlayerEntity player, T message) {
        ServerPlayNetworking.send(player, new MsgPayload(id, Utils.toNbt(codec, message)));
    }

    @Override public void send(PacketSender sender, T message) {
        ((FabricPacketSender) sender).sender().sendPacket(new MsgPayload(id, Utils.toNbt(codec, message)));
    }
}
