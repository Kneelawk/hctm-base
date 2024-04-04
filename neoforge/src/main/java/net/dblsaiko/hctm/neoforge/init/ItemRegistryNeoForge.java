package net.dblsaiko.hctm.neoforge.init;

import java.util.function.Supplier;

import net.dblsaiko.hctm.init.ItemRegistry;
import net.dblsaiko.hctm.init.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemRegistryNeoForge implements ItemRegistry {
    private final DeferredRegister.Items items;
    private final Item.Settings blockDefault;
    
    public ItemRegistryNeoForge(String modId) {
        this(modId, new Item.Settings());
    }
    
    public ItemRegistryNeoForge(String modId, Item.Settings blockDefault) {
        items = DeferredRegister.createItems(modId);
        this.blockDefault = blockDefault;
    }

    @Override
    public @NotNull <T extends Item> RegistryObject<T> create(String name, Supplier<T> item) {
        return new RegistryObjectImpl<>(items.register(name, item));
    }

    @Override
    public @NotNull RegistryObject<BlockItem> create(String name, RegistryObject<? extends Block> block) {
        return new RegistryObjectImpl<>(items.register(name, () -> new BlockItem(block.get(), this.blockDefault)));
    }
    
    public void register(IEventBus modBus) {
        items.register(modBus);
    }
    
    private static final class RegistryObjectImpl<T extends Item> implements RegistryObject<T> {
        private final DeferredItem<T> item;

        private RegistryObjectImpl(DeferredItem<T> item) {
            this.item = item;
        }

        @Override
        public Identifier id() {
            return item.getId();
        }

        @Override
        public @NotNull T get() {
            return item.get();
        }
    }
}
