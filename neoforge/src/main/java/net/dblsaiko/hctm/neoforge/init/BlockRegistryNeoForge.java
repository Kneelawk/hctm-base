package net.dblsaiko.hctm.neoforge.init;

import java.util.function.Supplier;

import net.dblsaiko.hctm.init.BlockRegistry;
import net.dblsaiko.hctm.init.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockRegistryNeoForge implements BlockRegistry {
    private final DeferredRegister.Blocks register;
    
    public BlockRegistryNeoForge(String modId) {
        register = DeferredRegister.createBlocks(modId);
    }

    @Override
    public @NotNull <T extends Block> RegistryObject<T> create(String name, Supplier<T> block) {
        return new RegistryObjectImpl<>(register.register(name, block));
    }
    
    public void register(IEventBus modBus) {
        register.register(modBus);
    }
    
    private static final class RegistryObjectImpl<T extends Block> implements RegistryObject<T> {
        private final DeferredBlock<T> block;

        private RegistryObjectImpl(DeferredBlock<T> block) {
            this.block = block;
        }

        @Override
        public Identifier id() {
            return block.getId();
        }

        @Override
        public @NotNull T get() {
            return block.get();
        }
    }
}
