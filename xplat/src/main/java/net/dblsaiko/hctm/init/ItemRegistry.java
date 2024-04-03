package net.dblsaiko.hctm.init;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public interface ItemRegistry {
    @NotNull <T extends Item> RegistryObject<T> create(String name, T item);

    @NotNull <T extends Item> RegistryObject<T> createThen(String name, Supplier<T> item);

    @NotNull RegistryObject<BlockItem> create(String name, RegistryObject<? extends Block> block);
}
