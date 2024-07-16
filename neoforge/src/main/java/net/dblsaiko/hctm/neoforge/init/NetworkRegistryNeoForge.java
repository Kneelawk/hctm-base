package net.dblsaiko.hctm.neoforge.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dblsaiko.hctm.init.NetworkRegistry;
import net.dblsaiko.hctm.init.net.ClientboundMsgDef;
import net.dblsaiko.hctm.init.net.ServerboundMsgDef;
import net.dblsaiko.hctm.neoforge.init.net.ClientboundMsgDefNeoForge;
import net.dblsaiko.hctm.init.net.MsgPayload;
import net.dblsaiko.hctm.neoforge.init.net.ServerboundMsgDefNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import com.mojang.serialization.Codec;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class NetworkRegistryNeoForge implements NetworkRegistry {
    private final String modId;
    private final List<String> defs = new ArrayList<>();
    private final Map<String, ServerboundMsgDefNeoForge<?>> sbDefs = new HashMap<>();
    private final Map<String, ClientboundMsgDefNeoForge<?>> cbDefs = new HashMap<>();

    public NetworkRegistryNeoForge(String modId) {
        this.modId = modId;
    }

    @Override
    public <T> ServerboundMsgDef<T> registerServerbound(String name, Codec<T> codec) {
        this.checkUnregistered(this.sbDefs, name);
        Identifier id = Identifier.of(this.modId, name);
        ServerboundMsgDefNeoForge<T> def = new ServerboundMsgDefNeoForge<>(id, codec);
        this.defs.add(name);
        this.sbDefs.put(name, def);
        return def;
    }

    @Override
    public <T> ClientboundMsgDef<T> registerClientbound(String name, Codec<T> codec) {
        this.checkUnregistered(this.cbDefs, name);
        Identifier id = Identifier.of(this.modId, name);
        ClientboundMsgDefNeoForge<T> def = new ClientboundMsgDefNeoForge<>(id, codec);
        this.defs.add(name);
        this.cbDefs.put(name, def);
        return def;
    }

    private void checkUnregistered(Map<String, ?> defs, String name) {
        if (defs.containsKey(name)) {
            throw new IllegalStateException("Duplicate registering of packet '%s'".formatted(name));
        }
    }

    public void register(IEventBus modBus) {
        modBus.addListener(this::onRegisterPayloadHandlers);
    }

    private void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(modId);

        for (String name : defs) {
            Identifier id = Identifier.of(modId, name);
            if (sbDefs.containsKey(name) && cbDefs.containsKey(name)) {
                registrar.playBidirectional(MsgPayload.id(id), MsgPayload.codec(id), (payload, ctx) -> {
                    if (ctx.flow() == NetworkSide.CLIENTBOUND) {
                        ClientboundMsgDefNeoForge<?> def = cbDefs.get(name);
                        def.handle(payload, ctx);
                    } else {
                        ServerboundMsgDefNeoForge<?> def = sbDefs.get(name);
                        def.handle(payload, ctx);
                    }
                });
            } else if (sbDefs.containsKey(name)) {
                registrar.playToServer(MsgPayload.id(id), MsgPayload.codec(id), (payload, ctx) -> {
                    ServerboundMsgDefNeoForge<?> def = sbDefs.get(name);
                    def.handle(payload, ctx);
                });
            } else if (cbDefs.containsKey(name)) {
                registrar.playToClient(MsgPayload.id(id), MsgPayload.codec(id), (payload, ctx) -> {
                    ClientboundMsgDefNeoForge<?> def = cbDefs.get(name);
                    def.handle(payload, ctx);
                });
            }
        }
    }
}
