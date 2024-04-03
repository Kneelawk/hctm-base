package net.dblsaiko.hctm.fabric.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dblsaiko.hctm.init.BlockEntityTypeRegistry;
import net.dblsaiko.hctm.init.RegistryObject;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntityTypeRegistryFabric implements BlockEntityTypeRegistry {
    private final String modId;

    private final List<InternalRegistryObject<BlockEntityType<? extends BlockEntity>>> all = new ArrayList<>();

    public BlockEntityTypeRegistryFabric(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, Factory<T> factory,
                                                                             RegistryObject<? extends Block>... blocks) {
        RegistryObjectImpl<T> o = new RegistryObjectImpl<>(new Identifier(this.modId, name), factory, blocks);
        // noinspection unchecked
        this.all.add((InternalRegistryObject<BlockEntityType<? extends BlockEntity>>) (Object) o);
        return o;
    }

    @Override
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> create(String name, FactoryWithType<T> factory,
                                                                             RegistryObject<? extends Block>... blocks) {
        RegistryObjectImplWithType<T> o =
            new RegistryObjectImplWithType<>(new Identifier(this.modId, name), factory, blocks);
        // noinspection unchecked
        this.all.add((InternalRegistryObject<BlockEntityType<? extends BlockEntity>>) (Object) o);
        return o;
    }

    public void register() {
        this.all.forEach(InternalRegistryObject::register);
    }

    public void unregister() {
        this.all.forEach(InternalRegistryObject::unregister);
    }

    private static final class RegistryObjectImpl<T extends BlockEntity> extends
        AbstractRegistryObject<BlockEntityType<T>> {
        private final Factory<T> factory;
        private final RegistryObject<? extends Block>[] blocks;

        private RegistryObjectImpl(Identifier id, Factory<T> factory, RegistryObject<? extends Block>[] blocks) {
            super(id);
            this.factory = factory;
            this.blocks = blocks;
        }

        @Override
        public BlockEntityType<T> registerNew() {
            return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                this.id(),
                FabricBlockEntityTypeBuilder.create(
                    this.factory::create,
                    Arrays.stream(this.blocks).map(RegistryObject::get).toArray(Block[]::new)
                ).build(null)
            );
        }
    }

    private static final class RegistryObjectImplWithType<T extends BlockEntity>
        extends AbstractRegistryObject<BlockEntityType<T>> {
        private final FactoryWithType<T> factory;
        private final RegistryObject<? extends Block>[] blocks;

        private RegistryObjectImplWithType(Identifier id, FactoryWithType<T> factory,
                                           RegistryObject<? extends Block>[] blocks) {
            super(id);
            this.factory = factory;
            this.blocks = blocks;
        }

        @Override
        public BlockEntityType<T> registerNew() {
            return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                this.id(),
                FabricBlockEntityTypeBuilder.create(
                    (pos, state) -> this.factory.create(this.get(), pos, state),
                    Arrays.stream(this.blocks).map(RegistryObject::get).toArray(Block[]::new)
                ).build(null)
            );
        }
    }
}
