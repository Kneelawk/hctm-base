package net.dblsaiko.hctm.fabric.init.net;

import net.dblsaiko.hctm.init.net.PacketSender;
import net.dblsaiko.hctm.init.net.ServerboundMsgSender;
import net.dblsaiko.hctm.init.net.Utils;

import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class ServerboundMsgSenderFabric<T>implements ServerboundMsgSender<T> {
    private final Identifier id;
    private final Codec<T> codec;

    public ServerboundMsgSenderFabric(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
    }

    @Override public void send(T message) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Can't send serverbound message on dedicated server");
        }

        ClientPlayNetworking.send(this.id, Utils.prepareBuffer(this.codec, message));
    }

    @Override public void send(PacketSender sender, T message) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Can't send serverbound message on dedicated server");
        }

        ((FabricPacketSender) sender).sender().sendPacket(this.id, Utils.prepareBuffer(this.codec, message));
    }
}
