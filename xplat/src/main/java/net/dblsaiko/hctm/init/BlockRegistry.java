package net.dblsaiko.hctm.init;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;

public interface BlockRegistry {
    @NotNull <T extends Block> RegistryObject<T> create(String name, Supplier<T> block);
}
