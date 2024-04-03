package net.dblsaiko.hctm.init;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemGroup;

public interface ItemGroupRegistry {
    @NotNull <T extends ItemGroup> RegistryObject<T> create(String name, T itemGroup);
}
