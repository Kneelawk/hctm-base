package net.dblsaiko.hctm.neoforge.init.net;

import net.dblsaiko.hctm.init.net.ServerMessageHandler;
import net.dblsaiko.hctm.init.net.ServerboundMsgDef;
import net.dblsaiko.hctm.init.net.ServerboundMsgSender;
import net.dblsaiko.hctm.init.net.Utils;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import com.mojang.serialization.Codec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerboundMsgDefNeoForge<T> implements ServerboundMsgDef<T> {
    private final Identifier id;
    private final Codec<T> codec;
    private final ServerboundMsgSender<T> sender;
    private ServerMessageHandler<T> handler;

    public ServerboundMsgDefNeoForge(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
        this.sender = new ServerboundMsgSenderNeoForge<>(id, codec);
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

    public void handle(MsgPayload payload, PlayPayloadContext ctx) {
        PlayerEntity player = ctx.player().get();
        handler.handle(player.getServer(), (ServerPlayerEntity) player, Utils.fromNbt(codec, payload.element()),
            new NeoForgePacketSender(ctx.replyHandler()));
    }
}
