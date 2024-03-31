package net.dblsaiko.hctm.init;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {
    private final String modId;

    private List<InternalRegistryObject<? extends ItemGroup>> all = new ArrayList<>();

    public ItemGroupRegistry(String modId) {
        this.modId = modId;
    }

    @NotNull
    public <T extends ItemGroup> RegistryObject<T> create(String name, T itemGroup) {
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
        private final T group;

        private RegistryObjectImpl(Identifier id, T group) {
            super(id);
            this.group = group;
        }

        @Override
        protected T registerNew() {
            return Registry.register(Registries.ITEM_GROUP, this.id(), this.group);
        }
    }
}
