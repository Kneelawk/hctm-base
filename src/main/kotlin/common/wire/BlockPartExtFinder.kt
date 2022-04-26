package common.wire

import net.dblsaiko.hctm.common.wire.PartExt
import net.dblsaiko.hctm.common.wire.PartExtFinder
import net.dblsaiko.hctm.common.wire.PartExtProvider
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

object BlockPartExtFinder : PartExtFinder {
    override fun getPartsInBlock(world: ServerWorld, pos: BlockPos): Sequence<PartExt> {
        val block = world.getBlockState(pos).block as? PartExtProvider ?: return emptySequence()
        return block.partExtType.createExtsForContainer(world, pos, block)
    }
}