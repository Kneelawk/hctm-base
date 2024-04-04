package net.dblsaiko.hctm.fabric.init;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.dblsaiko.hctm.init.BlockRegistry;
import net.dblsaiko.hctm.init.RegistryObject;

import org.jetbrains.annotations.NotNull;

public class BlockRegistryFabric implements BlockRegistry {
    private final String modId;

    private List<InternalRegistryObject<? extends Block>> all = new ArrayList<>();

    public BlockRegistryFabric(String modId) {
        this.modId = modId;
    }

    @Override
    @NotNull
    public <T extends Block> RegistryObject<T> create(String name, Supplier<T> block) {
        RegistryObjectImpl<T> o = new RegistryObjectImpl<>(new Identifier(this.modId, name), block);
        this.all.add(o);
        return o;
    }

    public void register() {
        this.all.forEach(InternalRegistryObject::register);
    }

    public void unregister() {
        this.all.forEach(InternalRegistryObject::unregister);
    }

    private static final class RegistryObjectImpl<T extends Block> extends AbstractRegistryObject<T> {
        private final Supplier<T> block;

        private RegistryObjectImpl(Identifier id, Supplier<T> block) {
            super(id);
            this.block = block;
        }

        @Override
        protected T registerNew() {
            return Registry.register(Registries.BLOCK, this.id(), this.block.get());
        }
    }
}
