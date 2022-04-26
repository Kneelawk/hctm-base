package common.wire

import net.dblsaiko.hctm.common.wire.BlockPartProvider
import net.dblsaiko.hctm.common.wire.PartExt
import net.dblsaiko.hctm.common.wire.PartExtFinder
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object BlockPartExtFinder : PartExtFinder {
    override fun getPartsInBlock(world: ServerWorld, pos: BlockPos): Iterable<PartExt> {
        val blockState = world.getBlockState(pos)
        val block = blockState.block as? BlockPartProvider ?: return emptySet()
        return block.getPartsInBlock(world, pos, blockState)
    }
}