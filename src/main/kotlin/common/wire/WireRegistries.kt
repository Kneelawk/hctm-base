package net.dblsaiko.hctm.common.wire

import com.mojang.serialization.Lifecycle
import common.wire.BlockPartExtFinder
import net.dblsaiko.hctm.HctmBase
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object WireRegistries {
    private val PART_EXT_FINDERS = mutableListOf<PartExtFinder>()

    private val EXT_PART_TYPE_IDENTIFIER = Identifier("${HctmBase.MOD_ID}:ext_part_type")

    private val EXT_PART_TYPE_KEY: RegistryKey<Registry<PartExtType>> by lazy {
        RegistryKey.ofRegistry(EXT_PART_TYPE_IDENTIFIER)
    }

    @JvmStatic
    fun registerPartExtFinder(finder: PartExtFinder) {
        PART_EXT_FINDERS += finder
    }

    fun getPartExtsInBlock(world: ServerWorld, pos: BlockPos): Set<PartExt> {
        return PART_EXT_FINDERS.asSequence().flatMap { it.getPartsInBlock(world, pos) }.toSet()
    }

    @JvmStatic
    val EXT_PART_TYPE = SimpleRegistry(EXT_PART_TYPE_KEY, Lifecycle.experimental(), null)

    @JvmStatic
    @Suppress("unchecked_cast")
    fun init() {
        Registry.register(Registry.REGISTRIES as Registry<Registry<*>>, EXT_PART_TYPE_IDENTIFIER, EXT_PART_TYPE)

        // Compatibility with things that used to use blocks to load and create their PartExts
        registerPartExtFinder(BlockPartExtFinder)
    }
}