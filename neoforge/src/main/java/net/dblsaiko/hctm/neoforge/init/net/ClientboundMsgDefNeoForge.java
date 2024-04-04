package net.dblsaiko.hctm.neoforge.init.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.dblsaiko.hctm.init.net.ClientMessageHandler;
import net.dblsaiko.hctm.init.net.ClientboundMsgDef;
import net.dblsaiko.hctm.init.net.ClientboundMsgSender;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

public class ClientboundMsgDefNeoForge<T> implements ClientboundMsgDef<T> {
    private static final Method HANDLER_METHOD;
    
    static {
        // extra reflection stuff because I don't trust the JVM
        Method handlerMethod = null;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            try {
                Class<?> clientUtils = Class.forName("net.dblsaiko.hctm.neoforge.init.net.ClientUtils");
                handlerMethod = clientUtils.getMethod("handle", MsgPayload.class, PlayPayloadContext.class, Codec.class, ClientMessageHandler.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        HANDLER_METHOD = handlerMethod;
    }
    
    private final Identifier id;
    private final Codec<T> codec;
    private final ClientboundMsgSender<T> sender;
    private ClientMessageHandler<T> handler;
    
    public ClientboundMsgDefNeoForge(Identifier id, Codec<T> codec) {
        this.id = id;
        this.codec = codec;
        this.sender = new ClientboundMsgSenderNeoForge<>(id, codec);
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
        return sender;
    }
    
    public void handle(MsgPayload payload, PlayPayloadContext ctx) {
        try {
            HANDLER_METHOD.invoke(null, payload, ctx, codec, handler);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
