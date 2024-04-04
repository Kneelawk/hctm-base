package net.dblsaiko.hctm.fabric.init.net;

import net.dblsaiko.hctm.init.net.ServerMessageHandler;
import net.dblsaiko.hctm.init.net.ServerboundMsgDef;
import net.dblsaiko.hctm.init.net.ServerboundMsgSender;
import net.dblsaiko.hctm.init.net.Utils;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

public class ServerboundMsgDefFabric<T> implements ServerboundMsgDef<T> {
    private final Identifier id;
    private final Codec<T> codec;
    private final ServerboundMsgSender<T> sender;
    private ServerMessageHandler<T> handler;

    public ServerboundMsgDefFabric(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
        this.sender = new ServerboundMsgSenderFabric<>(id, codec);
    }

    @Override
    public void bind(ServerMessageHandler<T> handler) {
        if (this.handler != null) {
            throw new IllegalStateException("Duplicate bind for serverbound packet '%s'".formatted(this.id));
        }

        this.handler = handler;
    }

    @Override
    public ServerboundMsgSender<T> sender() {
        return this.sender;
    }

    public void registerHandler() {
        var handler = this.handler;

        if (handler == null) {
            throw new IllegalStateException("Handler for packet '%s' not bound".formatted(this.id));
        }

        ServerPlayNetworking.registerGlobalReceiver(this.id, (server, player, handler1, buf, responseSender) -> {
            var message = Utils.readBuffer(this.codec, buf);
            handler.handle(server, player, message, new FabricPacketSender(responseSender));
        });
    }
}
