package net.dblsaiko.hctm.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public interface BlockEntityTypeRegistry {
    <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, Factory<T> factory,
                                                                      RegistryObject<? extends Block>... blocks);

    <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, FactoryWithType<T> factory,
                                                                      RegistryObject<? extends Block>... blocks);

    @FunctionalInterface
    interface Factory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    interface FactoryWithType<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
