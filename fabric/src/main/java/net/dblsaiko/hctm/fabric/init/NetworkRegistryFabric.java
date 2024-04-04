package net.dblsaiko.hctm.fabric.init;

import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;

import java.util.HashMap;
import java.util.Map;

import net.dblsaiko.hctm.fabric.init.net.ClientboundMsgDefFabric;
import net.dblsaiko.hctm.fabric.init.net.ServerboundMsgDefFabric;
import net.dblsaiko.hctm.init.NetworkRegistry;
import net.dblsaiko.hctm.init.net.ClientboundMsgDef;
import net.dblsaiko.hctm.init.net.ServerboundMsgDef;

public class NetworkRegistryFabric implements NetworkRegistry {
    private final String modId;
    private final Map<String, ServerboundMsgDefFabric<?>> sbDefs = new HashMap<>();
    private final Map<String, ClientboundMsgDefFabric<?>> cbDefs = new HashMap<>();

    public NetworkRegistryFabric(String modId) {
        this.modId = modId;
    }

    @Override public <T> ServerboundMsgDef<T> registerServerbound(String name, Codec<T> codec) {
        this.checkUnregistered(this.sbDefs, name);
        Identifier id = new Identifier(this.modId, name);
        ServerboundMsgDefFabric<T> def = new ServerboundMsgDefFabric<>(id, codec);
        this.sbDefs.put(name, def);
        return def;
    }

    @Override public <T> ClientboundMsgDef<T> registerClientbound(String name, Codec<T> codec) {
        this.checkUnregistered(this.cbDefs, name);
        Identifier id = new Identifier(this.modId, name);
        ClientboundMsgDefFabric<T> def = new ClientboundMsgDefFabric<>(id, codec);
        this.cbDefs.put(name, def);
        return def;
    }

    private void checkUnregistered(Map<String, ?> defs, String name) {
        if (defs.containsKey(name)) {
            throw new IllegalStateException("Duplicate registering of packet '%s'".formatted(name));
        }
    }

    public void registerClientHandlers() {
        for (ClientboundMsgDefFabric<?> value : this.cbDefs.values()) {
            value.registerHandler();
        }
    }

    public void registerServerHandlers() {
        for (ServerboundMsgDefFabric<?> value : this.sbDefs.values()) {
            value.registerHandler();
        }
    }

    public enum Side {
        SERVER,
        CLIENT,
    }
}
