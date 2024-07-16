package net.dblsaiko.hctm.common.wire

import com.kneelawk.graphlib.api.graph.GraphUniverse
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.mojang.serialization.Codec
import net.dblsaiko.hctm.HctmBase
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

typealias NetNode = NodeHolder<BlockNode>

val WIRE_NETWORK = GraphUniverse.builder().build(Identifier.of(HctmBase.MOD_ID, "wirenet"))

interface BlockPartProvider {
    fun getPartsInBlock(world: World, pos: BlockPos, state: BlockState): Set<BlockNode>
}

fun <N : BlockNode> simpleBaseWireCodec(constructor: (Direction) -> N, getter: (N) -> Direction): Codec<N> {
    return Codec.BYTE.xmap({ constructor(Direction.byId(it.toInt())) }, { getter(it).id.toByte() })
}

fun register() {
    WIRE_NETWORK.register()

    WIRE_NETWORK.addDiscoverer { world, pos ->
        val state = world.getBlockState(pos)
        (state.block as? BlockPartProvider)?.getPartsInBlock(world, pos, state).orEmpty()
    }
}
