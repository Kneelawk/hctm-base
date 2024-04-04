package net.dblsaiko.hctm.fabric.init.net;

import net.dblsaiko.hctm.init.net.ClientMessageHandler;
import net.dblsaiko.hctm.init.net.ClientboundMsgDef;
import net.dblsaiko.hctm.init.net.ClientboundMsgSender;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

public class ClientboundMsgDefFabric<T> implements ClientboundMsgDef<T> {
    private final Identifier id;
    private final Codec<T> codec;
    private final ClientboundMsgSender<T> sender;
    private ClientMessageHandler<T> handler;

    public ClientboundMsgDefFabric(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
        this.sender = new ClientboundMsgSenderFabric<>(id, codec);
    }

    @Override
    public void bind(ClientMessageHandler<T> handler) {
        if (this.handler != null) {
            throw new IllegalStateException("Duplicate bind for clientbound packet '%s'".formatted(this.id));
        }

        this.handler = handler;
    }

    @Override
    public ClientboundMsgSender<T> sender() {
        return this.sender;
    }

    public void registerHandler() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Can't register clientbound message on dedicated server");
        }

        var handler = this.handler;

        if (handler == null) {
            throw new IllegalStateException("Handler for packet '%s' not bound".formatted(this.id));
        }

        ClientUtils.registerHandler(this.id, this.codec, handler);
    }
}
