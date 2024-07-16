package net.dblsaiko.hctm.neoforge.init.net;

import net.dblsaiko.hctm.init.net.ClientMessageHandler;
import net.dblsaiko.hctm.init.net.MsgPayload;
import net.dblsaiko.hctm.init.net.Utils;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mojang.serialization.Codec;

import net.minecraft.client.MinecraftClient;

public class ClientUtils {
    public static <T> void handle(MsgPayload payload, IPayloadContext ctx, Codec<T> codec,
                                  ClientMessageHandler<T> handler) {
        handler.handle(MinecraftClient.getInstance(), Utils.fromNbt(codec, payload.element()),
            new NeoForgePacketSender(ctx::reply));
    }
}
