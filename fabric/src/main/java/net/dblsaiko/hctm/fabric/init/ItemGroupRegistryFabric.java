package net.dblsaiko.hctm.fabric.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.RegistryObject;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemGroupRegistryFabric implements ItemGroupRegistry {
    private final String modId;

    private List<InternalRegistryObject<? extends ItemGroup>> all = new ArrayList<>();

    public ItemGroupRegistryFabric(String modId) {
        this.modId = modId;
    }

    @Override
    @NotNull
    public <T extends ItemGroup> RegistryObject<T> create(String name, Supplier<T> itemGroup) {
        RegistryObjectImpl<T> o = new RegistryObjectImpl<>(new Identifier(this.modId, name), itemGroup);
        this.all.add(o);
        return o;
    }

    public void register() {
        this.all.forEach(InternalRegistryObject::register);
    }

    public void unregister() {
        this.all.forEach(InternalRegistryObject::unregister);
    }

    private static final class RegistryObjectImpl<T extends ItemGroup> extends AbstractRegistryObject<T> {
        private final Supplier<T> group;

        private RegistryObjectImpl(Identifier id, Supplier<T> group) {
            super(id);
            this.group = group;
        }

        @Override
        protected T registerNew() {
            return Registry.register(Registries.ITEM_GROUP, this.id(), this.group.get());
        }
    }
}
