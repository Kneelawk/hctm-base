package net.dblsaiko.hctm.fabric.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.dblsaiko.hctm.init.ItemRegistry;
import net.dblsaiko.hctm.init.RegistryObject;

import org.jetbrains.annotations.NotNull;

public class ItemRegistryFabric implements ItemRegistry {
    private final String modId;
    private final Settings blockDefault;

    private List<InternalRegistryObject<? extends Item>> all = new ArrayList<>();

    public ItemRegistryFabric(String modId) {
        this(modId, new Item.Settings());
    }

    public ItemRegistryFabric(String modId, Item.Settings blockDefault) {
        this.modId = modId;
        this.blockDefault = blockDefault;
    }

    @Override
    @NotNull
    public <T extends Item> RegistryObject<T> create(String name, Supplier<T> item) {
        var o = new DeferredRegistryObjectImpl<>(new Identifier(this.modId, name), item);
        this.all.add(o);
        return o;
    }

    @Override
    @NotNull
    public RegistryObject<BlockItem> create(String name, RegistryObject<? extends Block> block) {
        return this.create(name, () -> new BlockItem(block.get(), this.blockDefault));
    }

    public void register() {
        this.all.forEach(InternalRegistryObject::register);
    }

    public void unregister() {
        this.all.forEach(InternalRegistryObject::unregister);
    }

    private static final class DeferredRegistryObjectImpl<T extends Item> extends AbstractRegistryObject<T> {
        private final Supplier<T> item;

        private DeferredRegistryObjectImpl(Identifier id, Supplier<T> item) {
            super(id);
            this.item = item;
        }

        @Override
        protected T registerNew() {
            return Registry.register(Registries.ITEM, this.id(), this.item.get());
        }
    }
}