package net.dblsaiko.hctm.fabric.init.net;

import net.dblsaiko.hctm.init.net.ClientMessageHandler;
import net.dblsaiko.hctm.init.net.MsgPayload;
import net.dblsaiko.hctm.init.net.Utils;

import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

class ClientUtils {
    // needs to be in separate class due to lambda causing class loading of
    // client-only classes like MinecraftClient
    static <T> void registerHandler(Identifier id, Codec<T> codec, ClientMessageHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(MsgPayload.id(id), (payload, ctx) -> {
            var message = Utils.fromNbt(codec, payload.element());
            handler.handle(ctx.client(), message, new FabricPacketSender(ctx.responseSender()));
        });
    }
}
