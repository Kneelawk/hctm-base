package net.dblsaiko.hctm.neoforge.init.net;

import net.dblsaiko.hctm.init.net.ClientboundMsgSender;
import net.dblsaiko.hctm.init.net.PacketSender;
import net.dblsaiko.hctm.init.net.Utils;
import net.neoforged.neoforge.network.PacketDistributor;

import com.mojang.serialization.Codec;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ClientboundMsgSenderNeoForge<T> implements ClientboundMsgSender<T> {
    private final Identifier id;
    private final Codec<T> codec;

    public ClientboundMsgSenderNeoForge(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
    }

    @Override
    public void send(ServerPlayerEntity player, T message) {
        PacketDistributor.PLAYER.with(player).send(new MsgPayload(id, Utils.toNbt(codec, message)));
    }

    @Override
    public void send(PacketSender sender, T message) {
        ((NeoForgePacketSender) sender).handler().send(new MsgPayload(id, Utils.toNbt(codec, message)));
    }
}
