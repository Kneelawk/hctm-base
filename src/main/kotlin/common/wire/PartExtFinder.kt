package net.dblsaiko.hctm.common.wire

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface PartExtFinder {
    fun getPartsInBlock(world: ServerWorld, pos: BlockPos): Iterable<PartExt>
}