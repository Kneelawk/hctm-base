package net.dblsaiko.hctm.neoforge.init;

import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemGroupRegistryNeoForge implements ItemGroupRegistry {
    private final DeferredRegister<ItemGroup> register;

    public ItemGroupRegistryNeoForge(String modId) {
        register = DeferredRegister.create(Registries.ITEM_GROUP, modId);
    }

    @Override
    public @NotNull <T extends ItemGroup> RegistryObject<T> create(String name, T itemGroup) {
        return new RegistryObjectImpl<>(register.register(name, () -> itemGroup));
    }

    public void register(IEventBus modBus) {
        register.register(modBus);
    }

    private static final class RegistryObjectImpl<T extends ItemGroup> implements RegistryObject<T> {
        private final DeferredHolder<ItemGroup, T> holder;

        private RegistryObjectImpl(DeferredHolder<ItemGroup, T> holder) {
            this.holder = holder;
        }

        @Override
        public Identifier id() {
            return holder.getId();
        }

        @Override
        public @NotNull T get() {
            return holder.get();
        }
    }
}
