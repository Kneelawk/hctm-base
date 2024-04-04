package net.dblsaiko.hctm.neoforge.init.net;

import net.dblsaiko.hctm.init.net.PacketSender;
import net.dblsaiko.hctm.init.net.ServerboundMsgSender;
import net.dblsaiko.hctm.init.net.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

public class ServerboundMsgSenderNeoForge<T> implements ServerboundMsgSender<T> {
    private final Identifier id;
    private final Codec<T> codec;

    public ServerboundMsgSenderNeoForge(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
    }

    @Override
    public void send(T message) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            throw new IllegalStateException("Can't send serverbound message on dedicated server");
        }

        PacketDistributor.SERVER.noArg().send(new MsgPayload(id, Utils.toNbt(codec, message)));
    }

    @Override
    public void send(PacketSender sender, T message) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            throw new IllegalStateException("Can't send serverbound message on dedicated server");
        }

        ((NeoForgePacketSender) sender).handler().send(new MsgPayload(id, Utils.toNbt(codec, message)));
    }
}
