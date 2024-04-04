package net.dblsaiko.hctm.init;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemGroup;

public interface ItemGroupRegistry {
    @NotNull <T extends ItemGroup> RegistryObject<T> create(String name, Supplier<T> itemGroup);
}
