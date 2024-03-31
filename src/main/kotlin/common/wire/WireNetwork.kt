package net.dblsaiko.hctm.common.wire

import com.kneelawk.graphlib.api.graph.GraphUniverse
import com.kneelawk.graphlib.api.graph.NodeHolder
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.graph.user.BlockNodeDecoder
import net.dblsaiko.hctm.HctmBase
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

typealias NetNode = NodeHolder<BlockNode>

val WIRE_NETWORK = GraphUniverse.builder().build(Identifier(HctmBase.MOD_ID, "wirenet"))

interface BlockPartProvider {
    fun getPartsInBlock(world: World, pos: BlockPos, state: BlockState): Set<BlockNode>
}

class SimpleBaseWireDecoder<N : BlockNode>(private val constructor: (Direction) -> N) : BlockNodeDecoder {
    override fun decode(tag: NbtElement?): BlockNode? {
        return (tag as? NbtByte)
            ?.takeIf { it.intValue() in 0 until 6 }
            ?.let { constructor(Direction.byId(it.intValue())) }
    }
}

