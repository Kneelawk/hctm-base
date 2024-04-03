package net.dblsaiko.hctm.neoforge.init;

import java.util.Arrays;

import net.dblsaiko.hctm.init.BlockEntityTypeRegistry;
import net.dblsaiko.hctm.init.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BlockEntityTypeRegistryNeoForge implements BlockEntityTypeRegistry {
    private final DeferredRegister<BlockEntityType<?>> register;

    public BlockEntityTypeRegistryNeoForge(String modId) {
        register = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
    }

    @Override
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, Factory<T> factory,
                                                                             RegistryObject<? extends Block>... blocks) {
        return new RegistryObjectImpl<>(register.register(name, () -> BlockEntityType.Builder.create(factory::create,
            Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null)));
    }

    @Override
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, FactoryWithType<T> factory,
                                                                             RegistryObject<? extends Block>... blocks) {
        RegistryObjectRef<T> ref = new RegistryObjectRef<>();
        RegistryObject<BlockEntityType<T>> obj = new RegistryObjectImpl<>(register.register(name,
            () -> BlockEntityType.Builder.create((pos, state) -> factory.create(ref.object.get(), pos, state),
                Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null)));
        ref.object = obj;
        return obj;
    }
    
    public void register(IEventBus modBus) {
        register.register(modBus);
    }

    private static final class RegistryObjectImpl<T extends BlockEntity> implements RegistryObject<BlockEntityType<T>> {
        private final DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder;

        private RegistryObjectImpl(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder) {
            this.holder = holder;
        }

        @Override
        public Identifier id() {
            return holder.getId();
        }

        @Override
        public @NotNull BlockEntityType<T> get() {
            return holder.get();
        }
    }

    private static final class RegistryObjectRef<T extends BlockEntity> {
        RegistryObject<BlockEntityType<T>> object = null;
    }
}
